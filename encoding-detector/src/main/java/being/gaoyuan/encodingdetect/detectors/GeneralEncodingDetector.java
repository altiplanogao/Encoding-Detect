package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.EncodingGuesser;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public class GeneralEncodingDetector extends AbstractEncodingDetector {
    protected static final SortedMap<String, Charset> CHARSETS = Charset.availableCharsets();

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        List<DetectSummary> summaryList = tryFitSummary(file, CHARSETS.values(), attempt);

        return EncodingGuesser.guess(summaryList, attempt.size());
    }

}
