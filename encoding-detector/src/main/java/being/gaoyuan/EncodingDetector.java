package being.gaoyuan;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

public class EncodingDetector {
    private static final byte END_CONTROL_BYTE_VALUE = ' ';
    private static final String ALLOWED_CONTROL = "\t\n\r";
    private static final String PARTIAL_FORBID_CONTROL = "\b\f";
    private static final IntRange[] FORBID_RANGES = new IntRange[]{
            new IntRange(0x0080, 0x009F),
            new IntRange(0xFFFC, 0xFFFF)};
    private static final Set<Integer> FORBID_CHARS;

    private static SortedMap<String, Charset> charsets = Charset.availableCharsets();

    static {
        FORBID_CHARS = new HashSet<>();
        for (int i = 0; i < END_CONTROL_BYTE_VALUE; ++i) {
            FORBID_CHARS.add(i);
        }
        for (char c : ALLOWED_CONTROL.toCharArray()) {
            FORBID_CHARS.remove(c);
        }
        for (IntRange range : FORBID_RANGES) {
            for (int i = range.first; i <= range.last; ++i) {
                FORBID_CHARS.add(i);
            }
        }
    }

    public FileType detect(Path file) {
        File f = file.toFile();
        List<DetectSummary> summaryList = new ArrayList<>();

        ByteOrderMarks.resolve(f)
                .ifPresent(bom->summaryList.add(tryFit(f, bom.charset, bom.markLen())));

        for (Charset charset : charsets.values()) {
            summaryList.add(tryFit(f, charset));
        }

        return FileType.bestFit(summaryList).get();
    }

    private DetectSummary tryFit(File f, Charset charset) {
        return tryFit(f, charset, 0);
    }

    private DetectSummary tryFit(File f, Charset charset, int skip) {
        DetectContext context = new DetectContext(charset, FORBID_CHARS);
        try (FileInputStream stream = new FileInputStream(f);
             InputStreamReader streamReader = new InputStreamReader(stream, charset)) {
            if (skip > 0) {
                for (int i = 0; i < skip; i++) {
                    streamReader.read();
                }
            }
            try (BufferedReader reader = new BufferedReader(streamReader)) {
                do {
                    int ch = reader.read();
                    if (ch < 0) {
                        break;
                    }
                    context.push(ch);
                    if(context.isBroken()){
                        break;
                    }
                } while (true);
            }
        } catch (Exception e) {
            context.setBroken(true);
        } finally {
            context.commit();
        }
        return new DetectSummary(context);
    }

    public static List<String> readLines(File f, Charset charset) {
        List<String> lines = new ArrayList<>();
        try (FileInputStream stream = new FileInputStream(f);
             InputStreamReader streamReader = new InputStreamReader(stream, charset);
             BufferedReader reader = new BufferedReader(streamReader)) {

            do {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            } while (true);
        } catch (Exception e) {
        }
        return lines;
    }
}