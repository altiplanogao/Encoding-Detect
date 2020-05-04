package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GeneralEncodingDetector extends AbstractEncodingDetector {

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        List<DetectSummary> summaryList = tryFitSummary(file, DetectorSettings.getCharsets(), attempt);
        return EncodingGuesser.guess(summaryList, attempt.size());
    }
}