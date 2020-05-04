package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.EncodingDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractEncodingDetector implements EncodingDetector {

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

    protected static DetectSummary tryFit(final File f, Charset charset, int skip) {
        CharsetDetectContext context = new CharsetDetectContext(charset, DetectorSettings.getForbids(),
                Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
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