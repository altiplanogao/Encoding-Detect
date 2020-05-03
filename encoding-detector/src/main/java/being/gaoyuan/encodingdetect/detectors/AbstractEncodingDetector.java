package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.EncodingDetector;
import being.gaoyuan.encodingdetect.utils.ForbidsSet;
import being.gaoyuan.encodingdetect.utils.IntRange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public abstract class AbstractEncodingDetector implements EncodingDetector {
    public static final Set<Integer> CONTROL_EXCEPTS;
    static {
        Set<Integer> controlExcepts = new HashSet<>();
        for (char c : "\t\n\r".toCharArray()) {
            controlExcepts.add((int) c);
        }
        CONTROL_EXCEPTS = Collections.unmodifiableSet(controlExcepts);
    }
    public static Forbids calcSimpleForbids() {
        final IntRange[] forbidRanges = new IntRange[]{
                new IntRange(0x000000, 0x000020),
                new IntRange(0x000080, 0x0000A0),
                new IntRange(0x00FFFC, 0x010000)};
        final Set<Integer> textFileForbids = new HashSet<>();
        for (IntRange range : forbidRanges) {
            for (int i : range) {
                textFileForbids.add(i);
            }
        }
        textFileForbids.removeAll( CONTROL_EXCEPTS);
        return new ForbidsSet(textFileForbids);
    }

    private static Forbids forbids;
    static {
        forbids = calcSimpleForbids();
    }

    public static Forbids getForbids() {
        return forbids;
    }

    public static void setForbids(Forbids forbids) {
        AbstractEncodingDetector.forbids = forbids;
    }

    protected static List<DetectSummary> tryFitSummary(File f,
                                                       Collection<Charset> charsets,
                                                       Collection<Charset> attempt) {
        List<DetectSummary> summaryList = new ArrayList<>();
        for (Charset charset : charsets) {
            if (!attempt.contains(charset)) {
                summaryList.add(tryFit(f, charset));
                attempt.add(charset);
            }
        }
        return summaryList;
    }

    protected static DetectSummary tryFit(File f, Charset charset) {
        return tryFit(f, charset, 0);
    }

    protected static DetectSummary tryFit(File f, Charset charset, int skip) {
        CharsetDetectContext context = new CharsetDetectContext(charset, forbids, 0x10FFFF);
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
                    context.handle(ch);
                    if (context.isBroken()) {
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            context.setBroken(true);
        } finally {
            context.commit();
        }
        return context.asSummary();
    }
}