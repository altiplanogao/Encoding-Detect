package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.FileType;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlEncodingDetector extends AbstractEncodingDetector {
    private static final Pattern FIRST_LINE_GENERAL_PATTERN =
            Pattern.compile(
                    "^\\<\\?xml((\\s+(\\w+)\\s*=\\s*((\\\"(\\S+)\\\")|(\'(\\S+)\')))*)\\s*\\?\\>");
    private static final Pattern PROPERTY_PATTERN =
            Pattern.compile(
                    "(\\w+)\\s*=\\s*(\\\"|\\\')(\\S+)(\\\"|\\\')");

    @Override
    public Optional<FileType> detect(File file) {
        final String line = readFirstLine(file);
        if (!StringUtils.startsWith(line, "<?xml")) {
            return Optional.empty();
        }

        Matcher matcher = FIRST_LINE_GENERAL_PATTERN.matcher(line);
        if (matcher.find()) {
            String properties = matcher.group(1);
            Matcher propMatcher = PROPERTY_PATTERN.matcher(properties);
            while (propMatcher.find()) {
                String propName = propMatcher.group(1);
                if ("encoding".equals(propName)) {
                    String propVal = propMatcher.group(3);
                    return Optional.of(new FileType(propVal, 1));
                }
            }
        }
        return Optional.empty();
    }

    protected static String readFirstLine(File f) {
        try (FileInputStream stream = new FileInputStream(f);
             InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.US_ASCII);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line = reader.readLine();
            return line;
        } catch (Exception e) {
            return "";
        }
    }

}
