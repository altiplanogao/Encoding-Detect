package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.EncodingDetector;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;

public class EncodingDetectorAgent implements EncodingDetector {
    private static class SpecifiedDetector implements EncodingDetector {
        final Predicate<File> preCheck;
        final EncodingDetector inner;

        public SpecifiedDetector(EncodingDetector inner) {
            this(null, inner);
        }

        public SpecifiedDetector(Predicate<File> preCheck, EncodingDetector inner) {
            this.preCheck = preCheck;
            this.inner = inner;
        }

        @Override
        public Optional<FileType> detect(File file, Collection<Charset> attempt) {
            if (preCheck == null || preCheck.test(file)) {
                return inner.detect(file, attempt);
            } else {
                return Optional.empty();
            }
        }
    }

    private final List<SpecifiedDetector> specifiedDetectorList = new ArrayList<>();
    private final GeneralEncodingDetector generalEncodingDetector = new GeneralEncodingDetector();

    public EncodingDetectorAgent() {
    }

    public Optional<FileType> detect(File file) {
        return detect(file, new HashSet<>());
    }

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        for (EncodingDetector detector : specifiedDetectorList) {
            Optional<FileType> stepResult = detector.detect(file, attempt);
            if (stepResult.isPresent()) {
                return stepResult;
            }
        }
        return generalEncodingDetector.detect(file, attempt);
    }

    public EncodingDetectorAgent addDetector(EncodingDetector detector) {
        specifiedDetectorList.add(new SpecifiedDetector(detector));
        return this;
    }

    public EncodingDetectorAgent addDetector(EncodingDetector detector, Predicate<File> preCheck) {
        specifiedDetectorList.add(new SpecifiedDetector(preCheck, detector));
        return this;
    }
}