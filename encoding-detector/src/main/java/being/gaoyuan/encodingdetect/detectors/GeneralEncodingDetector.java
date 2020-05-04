package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.EncodingGuesser;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public class GeneralEncodingDetector extends AbstractEncodingDetector {

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        List<DetectSummary> summaryList = tryFitSummary(file, DetectorSettings.getCharsets(), attempt);
        return EncodingGuesser.guess(summaryList, attempt.size());
    }
}