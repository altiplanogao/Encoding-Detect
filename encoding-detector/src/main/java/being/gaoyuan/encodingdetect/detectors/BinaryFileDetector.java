package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.BinaryType;
import being.gaoyuan.encodingdetect.EncodingDetector;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.util.Optional;

public class BinaryFileDetector implements EncodingDetector {
    @Override
    public Optional<FileType> detect(File file) {
        Optional<BinaryType> fileType = BinaryType.checkType(file);
        return fileType.map(x -> new FileType(x));
    }
}
