package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.EncodingDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractEncodingDetector implements EncodingDetector {
    private static class IntRange {
        public final int first;
        public final int last;

        public IntRange(int first, int last) {
            this.first = first;
            this.last = last;
        }

        public boolean contains(int value) {
            return first <= value && value <= last;
        }
    }

    private static final byte END_CONTROL_BYTE_VALUE = ' ';
    private static final String ALLOWED_CONTROL = "\t\n\r";
    private static final String PARTIAL_FORBID_CONTROL = "\b\f";
    private static final IntRange[] FORBID_RANGES = new IntRange[]{
            new IntRange(0x0080, 0x009F),
            new IntRange(0xFFFC, 0xFFFF)};
    private static final Set<Character> FORBID_CHARS;

    static {
        FORBID_CHARS = new HashSet<>();
        for (int i = 0; i < END_CONTROL_BYTE_VALUE; ++i) {
            FORBID_CHARS.add((char) i);
        }
        for (char c : ALLOWED_CONTROL.toCharArray()) {
            FORBID_CHARS.remove(c);
        }
        for (IntRange range : FORBID_RANGES) {
            for (int i = range.first; i <= range.last; ++i) {
                FORBID_CHARS.add((char) i);
            }
        }
    }

    protected static DetectSummary tryFit(File f, Charset charset) {
        return tryFit(f, charset, 0);
    }

    protected static DetectSummary tryFit(File f, Charset charset, int skip) {
        DetectContext context = new DetectContext(charset, FORBID_CHARS);
        try (FileInputStream stream = new FileInputStream(f);
             InputStreamReader streamReader = new InputStreamReader(stream, charset)) {
            if (skip > 0) {
                for (int i = 0; i < skip; i++) {
                    streamReader.read();
                }
            }
            try (BufferedReader reader = new BufferedReader(streamReader)) {
                while (true) {
                    int ch = reader.read();
                    if (ch < 0) {
                        break;
                    }
                    context.push(ch);
                    if (context.isBroken()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            context.setBroken(true);
        } finally {
            context.commit();
        }
        return context.asSummary();
    }

}
