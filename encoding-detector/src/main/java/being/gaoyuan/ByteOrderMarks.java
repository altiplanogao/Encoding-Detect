package being.gaoyuan;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ByteOrderMarks {
    public final Charset charset;
    private final byte[] marks;

    private ByteOrderMarks() {
        charset = null;
        marks = null;
    }

    private ByteOrderMarks(Charset charset, byte[] marks) {
        this.charset = charset;
        this.marks = marks;
    }

    public int markLen(){
        return marks.length;
    }

    public static final ByteOrderMarks UTF_8 = new ByteOrderMarks(
            StandardCharsets.UTF_8,
            new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
    public static final ByteOrderMarks UTF_16LE = new ByteOrderMarks(
            StandardCharsets.UTF_16LE,
            new byte[]{(byte) 0xFF, (byte) 0xFE});
    public static final ByteOrderMarks UTF_16BE = new ByteOrderMarks(
            StandardCharsets.UTF_16BE,
            new byte[]{(byte) 0xFE, (byte) 0xFF});
    public static final ByteOrderMarks UTF_32LE = new ByteOrderMarks(
            Charset.forName("UTF-32LE"),
            new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00});
    public static final ByteOrderMarks UTF_32BE = new ByteOrderMarks(
            Charset.forName("UTF-32BE"),
            new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF});

    private static final ByteOrderMarks[] PREDEFINED_BOMS;
    private static final int CHECK_MAX_LEN;

    static {
        List<ByteOrderMarks> predefinedBoms = new ArrayList<>();

        Field[] fields = ByteOrderMarks.class.getFields();
        for (Field field : fields) {
            if (!ByteOrderMarks.class.equals(field.getDeclaringClass())) {
                continue;
            }
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                try {
                    ByteOrderMarks value = (ByteOrderMarks) field.get(null);
                    predefinedBoms.add(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        PREDEFINED_BOMS = predefinedBoms.toArray(new ByteOrderMarks[0]);

        int minByteLen = Integer.MAX_VALUE;
        int maxByteLen = Integer.MIN_VALUE;
        for (ByteOrderMarks bom : predefinedBoms) {
            int len = bom.marks.length;
            maxByteLen = maxByteLen < len ? len : maxByteLen;
            minByteLen = minByteLen > len ? len : minByteLen;
        }
        CHECK_MAX_LEN = maxByteLen;
    }

    private boolean likely(byte[] leadingBytes) {
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] != leadingBytes[i]) {
                return false;
            }
        }
        return true;
    }

    public static Optional<ByteOrderMarks> resolve(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] leadingBytes = new byte[CHECK_MAX_LEN];
            int read = 0;
            do {
                int stepRead = stream.read(leadingBytes, read, leadingBytes.length - read);
                if (stepRead < 0) {
                    break;
                }
                read += stepRead;
            } while (read < leadingBytes.length);

            List<ByteOrderMarks> likely = new ArrayList<>();
            for (ByteOrderMarks bom : PREDEFINED_BOMS) {
                if (bom.marks.length <= read) {
                    if (bom.likely(leadingBytes)) {
                        likely.add(bom);
                    }
                }
            }

            if (likely.isEmpty()) {
                return Optional.empty();
            } else if (likely.size() == 1) {
                return Optional.ofNullable(likely.get(0));
            } else {
                return likely.stream().max(new BomComparator());
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static class BomComparator implements Comparator<ByteOrderMarks> {
        @Override
        public int compare(ByteOrderMarks a, ByteOrderMarks b) {
            int diff = Integer.compare(a.marks.length, b.marks.length);
            if (diff != 0) {
                int len = a.marks.length;
                for (int i = 0; i < len; i++) {
                    diff = Byte.compare(a.marks[i], b.marks[i]);
                    if (diff != 0) {
                        break;
                    }
                }
            }
            return diff;
        }
    }
}