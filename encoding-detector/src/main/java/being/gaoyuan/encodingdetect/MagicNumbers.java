package being.gaoyuan.encodingdetect;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

public class MagicNumbers {
    private final byte[] marks;

    protected MagicNumbers() {
        marks = null;
    }

    protected MagicNumbers(byte[] marks) {
        this.marks = marks;
    }

    protected MagicNumbers(String hexMarks) {
        try {
            this.marks = Hex.decodeHex(hexMarks);
        } catch (DecoderException e) {
            throw new IllegalArgumentException(hexMarks);
        }
    }

    public int markLen() {
        return marks.length;
    }

    public boolean match(byte[] leadingBytes, final int skips) {
        if ((leadingBytes.length - skips) < marks.length) {
            return false;
        }
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] != leadingBytes[i + skips]) {
                return false;
            }
        }
        return true;
    }

    public boolean match(byte[] leadingBytes) {
        return match(leadingBytes, 0);
    }

    protected boolean match(File file) {
        return match(file, 0);
    }

    protected boolean match(File file, int skips) {
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] buffer = new byte[marks.length + skips];
            int read = read(stream, buffer);
            if (read != buffer.length) {
                return false;
            }
            return match(buffer, skips);
        } catch (IOException e) {
            return false;
        }
    }

    public static int read(InputStream stream, byte[] dest) throws IOException {
        int read = 0;
        do {
            int stepRead = stream.read(dest, read, dest.length - read);
            if (stepRead < 0) {
                break;
            }
            read += stepRead;
        } while (read < dest.length);
        return read;
    }

    protected static class MagicNumbersComparator implements Comparator<MagicNumbers> {
        @Override
        public int compare(MagicNumbers a, MagicNumbers b) {
            int diff = Integer.compare(a.markLen(), b.markLen());
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