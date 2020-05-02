package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.ByteOrderMarks;
import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.util.Optional;

public class WithBomEncodingDetector extends AbstractEncodingDetector {
    @Override
    public Optional<FileType> detect(File file) {
        Optional<ByteOrderMarks> optionalBom = ByteOrderMarks.resolve(file);
        if (optionalBom.isPresent()) {
            ByteOrderMarks bom = optionalBom.get();
            DetectSummary fit = tryFit(file, bom.charset, bom.markLen());
            return fit.ok ? Optional.of(new FileType(bom.charset)) : Optional.empty();
        } else {
            return Optional.empty();
        }
    }
}
