package being.gaoyuan.encodingdetect;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface EncodingDetector {
    default Optional<FileType> detect(Path file) {
        return detect(file.toFile());
    }

    Optional<FileType> detect(File file);
}
