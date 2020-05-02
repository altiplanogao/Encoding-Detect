package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.EncodingDetector;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EncodingDetectorAgent implements EncodingDetector {
    private static class SpecifiedDetector implements EncodingDetector {
        final Predicate<File> precheck;
        final EncodingDetector inner;

        public SpecifiedDetector(EncodingDetector inner) {
            this(null, inner);
        }

        public SpecifiedDetector(Predicate<File> precheck, EncodingDetector inner) {
            this.precheck = precheck;
            this.inner = inner;
        }

        @Override
        public Optional<FileType> detect(File file) {
            if (precheck == null || precheck.test(file)) {
                return inner.detect(file);
            } else {
                return Optional.empty();
            }
        }
    }

    private final List<SpecifiedDetector> specifiedDetectorList = new ArrayList<>();
    private final GeneralEncodingDetector generalEncodingDetector = new GeneralEncodingDetector();

    public EncodingDetectorAgent() {
    }

    @Override
    public Optional<FileType> detect(File file) {
        for (EncodingDetector detector : specifiedDetectorList) {
            Optional<FileType> stepResult = detector.detect(file);
            if (stepResult.isPresent()) {
                return stepResult;
            }
        }
        return generalEncodingDetector.detect(file);
    }

    public EncodingDetectorAgent addDetector(EncodingDetector detector) {
        specifiedDetectorList.add(new SpecifiedDetector(detector));
        return this;
    }

    public EncodingDetectorAgent addDetector(EncodingDetector detector, Predicate<File> precheck) {
        specifiedDetectorList.add(new SpecifiedDetector(precheck, detector));
        return this;
    }
}