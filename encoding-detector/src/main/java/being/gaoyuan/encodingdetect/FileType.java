package being.gaoyuan.encodingdetect;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import java.util.stream.Collectors;

public class FileType {
    private static final Set<String> STANDARD_NAMES = new HashSet<String>() {
        {
            Charset.availableCharsets().values().forEach(cs -> add(cs.name()));
        }
    };

    private final BinaryType binary;
    private final String encoding;
    private final List<String> potentialEncodings = new ArrayList<>();

    public FileType(Charset charset) {
        this(charset.name());
    }

    public FileType(String charset) {
        this.binary = null;
        this.encoding = charset;
        this.potentialEncodings.add(charset);
    }

    public FileType(String charset, Collection<String> potentialEncodings) {
        this.binary = null;
        this.encoding = charset;
        this.potentialEncodings.addAll(potentialEncodings.stream()
                .map(x -> toStandardCharset(x)).collect(Collectors.toList()));
    }

    public FileType(BinaryType binary) {
        this.binary = binary;
        this.encoding = null;
    }

    private static String toStandardCharset(String cs) {
        if (STANDARD_NAMES.contains(cs)) {
            return cs;
        } else {
            try {
                Charset charset = Charset.forName(cs);
                return (charset.name());
            } catch (UnsupportedCharsetException | IllegalCharsetNameException exp) {
                return cs;
            }
        }
    }

    @Override
    public String toString() {
        if (binary != null) {
            return "<binary>." + binary;
        } else {
            return "<text>." + encoding + ".in." + potentialEncodings.size();
        }
    }
}
