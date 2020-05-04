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
    private final int decodeAttempt;

    public FileType(Charset charset, int decodeAttempt) {
        this(charset.name(), decodeAttempt);
    }

    public FileType(String charset, int decodeAttempt) {
        this.binary = null;
        this.encoding = charset;
        this.potentialEncodings.add(charset);
        this.decodeAttempt = decodeAttempt;
    }

    public FileType(String charset, Collection<String> potentialEncodings, int decodeAttempt) {
        this.binary = null;
        this.encoding = charset;
        this.potentialEncodings.addAll(potentialEncodings.stream()
                .map(x -> toStandardEncoding(x)).collect(Collectors.toList()));
        this.decodeAttempt = decodeAttempt;
    }

    public FileType(BinaryType binary, int decodeAttempt) {
        this.binary = binary;
        this.encoding = null;
        this.decodeAttempt = decodeAttempt;
    }

    public boolean isBinary() {
        return binary != null;
    }

    public boolean isText() {
        return !isBinary();
    }

    public BinaryType getBinary() {
        if (isText()) {
            throw new IllegalStateException();
        }
        return binary;
    }

    private static String toStandardEncoding(String cs) {
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

    public String getEncoding() {
        if (isBinary()) {
            throw new IllegalStateException();
        }
        return encoding;
    }

    public List<String> getPotentialEncodings() {
        if (isBinary()) {
            throw new IllegalStateException();
        }
        return Collections.unmodifiableList(potentialEncodings);
    }

    @Override
    public String toString() {
        if (binary != null) {
            return "<binary>." + binary + " d:" + decodeAttempt;
        } else {
            return "<text>." + encoding + " " +
                    potentialEncodings.size() + "/" + decodeAttempt;
        }
    }
}
