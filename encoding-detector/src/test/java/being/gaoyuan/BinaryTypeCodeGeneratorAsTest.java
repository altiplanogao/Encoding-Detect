package being.gaoyuan;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SourceLine {
    public final String left;
    public final String right;

    public SourceLine(String left, String right) {
        this.left = left;
        this.right = right.contains("\"") ?
                right.replaceAll("\"", "\\\\\"") : right;
    }

    @Override
    public String toString() {
        return left + ": " + right;
    }
}

class HexPart {
    final boolean usable;
    final int offset;
    final String magicNumbers;

    private HexPart(boolean usable, int offset, String magicNumbers) {
        this.usable = usable;
        this.offset = offset;
        this.magicNumbers = magicNumbers;
    }

    private static final Pattern MARKS_PATTERN =
            Pattern.compile("\\[([\\d\\,]+)[\\s\\(\\)\\w]*\\]");
    private static final Pattern HEX_BYTE_PATTERN =
            Pattern.compile("([0-9a-fA-FxXn]{2}\\s*(or)?)+");

    static HexPart parse(SourceLine sourceLine) {
        String input = sourceLine.left;
        input = input.replaceAll("\n", " ").trim();
        String hexPart = input;
        if (StringUtils.isEmpty(input)) {
            return null;
        } else if (StringUtils.startsWith(input, "[")) {
            if (input.indexOf("]") > 0) {
                Matcher matcher = MARKS_PATTERN.matcher(input);
                while (matcher.find()) {
                    String group = matcher.group(1).replaceAll("\\,", "");
                    int offset = 0;
                    try {
                        offset = Integer.parseInt(group);
                    } catch (Exception e) {
                        return new HexPart(false, offset, "");
                    }
                    hexPart = input.substring(input.indexOf("]") + 1).trim();
                    try {
                        Hex.decodeHex(hexPart.replaceAll(" ", ""));
                        return new HexPart(true, offset, hexPart);
                    } catch (DecoderException e) {
                        return new HexPart(false, offset, hexPart);
                    }
                }
            } else {
                return new HexPart(false, 0, hexPart);
            }
        }
        try {
            Hex.decodeHex(hexPart.replaceAll(" ", ""));
            return new HexPart(true, 0, input);
        } catch (DecoderException e) {
            Matcher matcher = HEX_BYTE_PATTERN.matcher(hexPart);
            if (matcher.matches()) {
                return new HexPart(false, 0, input);
            }
            return null;
        }
    }
}

class ExtensionsPart {
    private static final Pattern DIGIT_LEADING = Pattern.compile("^\\d");
    private static final Pattern EXTENSIONS_STRING_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\,]+)$");

    final boolean usable;
    private final String orignal;
    public final String extensionsString;
    public final String expectFieldName;
    public final String[] extensions;
    public final String description;

    private ExtensionsPart(SourceLine line) {
        orignal = line.left;
        extensionsString = line.left
                .replaceAll("\\.", "_")
                .replaceAll("\\s", "");
        usable = EXTENSIONS_STRING_PATTERN.matcher(extensionsString).find();
        extensions = extensionsString.split(",");
        String tempExpectFieldName = StringUtils.join(extensions, "_")
                .toUpperCase();
        Matcher matcher = DIGIT_LEADING.matcher(tempExpectFieldName);
        if (matcher.find()) {
            tempExpectFieldName = "_" + tempExpectFieldName;
        }
        expectFieldName = tempExpectFieldName;
        description = line.right;
    }

    static ExtensionsPart parse(SourceLine line) {
        try {
            return new ExtensionsPart(line);
        } catch (Exception e) {
            return null;
        }
    }
}

class ResultLine {
    final HexPart hexPart;
    final ExtensionsPart extensionsPart;
    final String expectFieldName;

    public ResultLine(HexPart hexPart, ExtensionsPart extensionsPart,
                      Set<String> nameRegistry) {
        this.hexPart = hexPart;
        this.extensionsPart = extensionsPart;

        int offset = 0;
        String expectVarName = extensionsPart.expectFieldName;
        while (nameRegistry.contains(expectVarName)) {
            expectVarName = extensionsPart.expectFieldName + "_" + (offset++);
        }
        nameRegistry.add(expectVarName);
        this.expectFieldName = expectVarName;
    }

    public void appendTo(StringBuilder sb) {
        sb.append("public static final BinaryType " + expectFieldName + " = ")
                .append("new BinaryType(")
                .append("\"" + extensionsPart.extensionsString + "\", ")
                .append(hexPart.offset == 0 ? "" : "" + hexPart.offset + ", ")
                .append("\"" + hexPart.magicNumbers + "\", ")
                .append("\"" + extensionsPart.description + "\");")
                .append("\n");
    }
}

/**
 * Actually, this class is not designed for test.
 * It's just an entry to generate entries of class PreDefines in the {@link
 * being.gaoyuan.encodingdetect.BinaryTypeCollection} using resource "File Signatures.html"
 *
 * Before running, remember to update file "resources/File Signatures.html" by downloading https://www.garykessler.net/library/file_sigs.html to
 *
 */
@Disabled
public class BinaryTypeCodeGeneratorAsTest {

    @Test
    public void generate() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream stream = getClass().getClassLoader()
                .getResource("File Signatures.html").openStream()) {
            String html = IOUtils.toString(stream, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(html);
            Elements trs = doc.select("tbody>tr");
            List<SourceLine> sourceLines = new ArrayList<>();
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() != 3) {
                    continue;
                } else {
                    sourceLines.add(new SourceLine(tds.get(0).text().trim(),
                            tds.get(2).text().trim()));
                }
            }

            List<ResultLine> toGenerate = new ArrayList<>();
            {
                HexPart hexPart = null;
                final Set<String> nameRegistry = new HashSet<>();
                for (SourceLine source : sourceLines) {
                    HexPart newHexPart = HexPart.parse(source);
                    if (newHexPart != null) {
                        if (newHexPart.usable) {
                            hexPart = newHexPart;
                        } else {
                            hexPart = null;
                        }
                        continue;
                    }
                    ExtensionsPart extensionsPart = ExtensionsPart.parse(source);
                    if (extensionsPart.usable) {
                        if (hexPart != null) {
                            toGenerate.add(new ResultLine(hexPart, extensionsPart, nameRegistry));
                        }
                    }
                }
            }
            toGenerate.sort((o1, o2) -> StringUtils.compare(o1.expectFieldName, o2.expectFieldName));
            for (ResultLine line : toGenerate) {
                line.appendTo(sb);
            }
        }
        sb.toString();
    }
}