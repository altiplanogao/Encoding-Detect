package being.gaoyuan;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Command{
    UPDATE_MAGIC_DEFINE,
    CLEAR_MAGIC_DEFINE,
    GENERATE_ENTRY,
    NONE
}

class MagicDefine {
    final boolean usable;
    final int offset;
    final String magicNumbers;

    public MagicDefine(boolean usable, int offset, String magicNumbers) {
        this.usable = usable;
        this.offset = offset;
        this.magicNumbers = magicNumbers;
    }

    private static final Pattern MARKS_PATTERN =
            Pattern.compile("\\[([\\d\\,]+)[\\s\\(\\)\\w]*\\]");
    private static final Pattern HEX_BYTE_PATTERN =
            Pattern.compile("([0-9a-fA-FxXn]{2}\\s*(or)?)+");

    static MagicDefine parse(String input) {
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
                    } catch (Exception e){
                        return new MagicDefine(false, offset, "");
                    }
                    hexPart = input.substring(input.indexOf("]") + 1);
                    try{
                        Hex.decodeHex(hexPart.replaceAll(" ", ""));
                        return new MagicDefine(true, offset, input);
                    } catch (DecoderException e) {
                        return new MagicDefine(false, offset, input);
                    }
                }
            }else {
                throw new IllegalStateException();
            }
        }
        try{
            Hex.decodeHex(hexPart.replaceAll(" ", ""));
            return new MagicDefine(true,0, input);
        } catch (DecoderException e) {
            Matcher matcher = HEX_BYTE_PATTERN.matcher(hexPart);
            if(matcher.matches()){
                return new MagicDefine(false,0, input);
            }
            return null;
        }
    }
}

class LineData {
    final Command command;
    final MagicDefine magicDefine;
    final String[] exts;
    final String description;

    public LineData(Element tr) {
        Elements tds = tr.select("td");
        if (tds.size() != 3) {
            command = Command.NONE;
            magicDefine = null;
            exts =  new String[0];
            description = "";
            return;
        } else {
            String hexOrExts = tds.get(0).text().trim();
            this.description = tds.get(2).text().trim();
            MagicDefine newMagicDefine = MagicDefine.parse(hexOrExts);
            if (newMagicDefine == null) {
                magicDefine = null;
                String extsStr = hexOrExts.toUpperCase();
                if("N/A".equals(extsStr) || StringUtils.isEmpty(hexOrExts)){
                    exts =  new String[0];
                    command = Command.NONE;
                }else {
                    exts = hexOrExts.split(",");
                    command = Command.GENERATE_ENTRY;
                }
            } else {
                exts = new String[0];
                if (newMagicDefine.usable) {
                    magicDefine = newMagicDefine;
                    command = Command.UPDATE_MAGIC_DEFINE;
                } else {
                    magicDefine = null;
                    command = Command.CLEAR_MAGIC_DEFINE;
                }
            }
        }
    }
}

class ResultLine{
    private static final Pattern digitLeading = Pattern.compile("^\\d");
    final MagicDefine magicDefine;
    final String ext;
    final String description;

    public ResultLine(MagicDefine magicDefine, String ext, String description) {
        this.magicDefine = magicDefine;
        ext = ext.replaceAll("\\.","_")
//                .replaceAll("\\d", "_")
                .trim();
        Matcher matcher = digitLeading.matcher(ext);
        if (matcher.find()) {
            ext = "F_" + ext;
        }
        this.ext = ext;
        if(description.contains("\"")) {
            this.description = description.replaceAll("\"", "\\\\\"");
        }else {
            this.description = description;
        }
    }
}
public class FileTypeGeneratorAsTest {

    @Test
    public void generate() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream stream = getClass().getClassLoader()
                .getResource("File Signatures.html").openStream()) {
            String html = IOUtils.toString(stream, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(html);
            Elements trs = doc.select("tbody>tr");
            MagicDefine magicDefine = null;
            Set<String> varNameUsed = new HashSet<>();
            for (Element tr : trs) {
                LineData lineData = new LineData(tr);
                switch (lineData.command) {
                    case NONE:
                        break;
                    case UPDATE_MAGIC_DEFINE:
                        magicDefine = lineData.magicDefine;
                        break;
                    case CLEAR_MAGIC_DEFINE:
                        magicDefine = null;
                    case GENERATE_ENTRY:
                        if (magicDefine == null) {
                            break;
                        }
                        for (String ext : lineData.exts) {
                            ResultLine line = new ResultLine(magicDefine, ext, lineData.description);
                            int offset = 0;
                            String expectVarName = line.ext;
                            if (ext.contains(" ")) {
                                continue;
                            }
                            while (varNameUsed.contains(expectVarName)) {
                                expectVarName = line.ext + "_" + (offset++);
                            }
                            varNameUsed.add(expectVarName);
                            sb.append("public static final FileType " + expectVarName + " = ")
                                    .append("new FileType(")
                                    .append(magicDefine.offset == 0 ? "" : "" + magicDefine.offset + ", ")
                                    .append("\"" + magicDefine.magicNumbers + "\", ")
                                    .append("\"" + ext + "\", ")
                                    .append("\"" + line.description + "\");")
                                    .append("\n");
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }
        sb.toString();
    }
}