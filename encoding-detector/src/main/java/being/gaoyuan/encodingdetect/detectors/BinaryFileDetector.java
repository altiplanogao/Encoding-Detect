package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.BinaryType;
import being.gaoyuan.encodingdetect.BinaryTypeCollection;
import being.gaoyuan.encodingdetect.EncodingDetector;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.util.Optional;

public class BinaryFileDetector implements EncodingDetector {
    private final BinaryTypeCollection binaryTypeCollection;

    public BinaryFileDetector(BinaryTypeCollection binaryTypeCollection) {
        this.binaryTypeCollection = binaryTypeCollection;
    }

    @Override
    public Optional<FileType> detect(File file) {
        Optional<BinaryType> fileType = binaryTypeCollection.checkType(file);
        return fileType.map(x -> new FileType(x, 0));
    }
}
