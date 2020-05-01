package being.gaoyuan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ByteOrderMarks extends MagicNumbers{
    public final Charset charset;

    private ByteOrderMarks() {
        super();
        charset = null;
    }

    private ByteOrderMarks(Charset charset, byte[] marks) {
        super(marks);
        this.charset = charset;
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
            int len = bom.markLen();
            maxByteLen = maxByteLen < len ? len : maxByteLen;
            minByteLen = minByteLen > len ? len : minByteLen;
        }
        CHECK_MAX_LEN = maxByteLen;
    }

    public static Optional<ByteOrderMarks> resolve(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] leadingBytes = new byte[CHECK_MAX_LEN];
            int read = MagicNumbers.read(stream, leadingBytes);

            List<ByteOrderMarks> likely = new ArrayList<>();
            for (ByteOrderMarks bom : PREDEFINED_BOMS) {
                if (bom.markLen() <= read) {
                    if (bom.match(leadingBytes)) {
                        likely.add(bom);
                    }
                }
            }

            if (likely.isEmpty()) {
                return Optional.empty();
            } else if (likely.size() == 1) {
                return Optional.ofNullable(likely.get(0));
            } else {
                return likely.stream().max(new MagicNumbersComparator());
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}