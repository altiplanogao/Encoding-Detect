package being.gaoyuan.encodingdetect;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface EncodingDetector {
    default Optional<FileType> detect(Path file, Collection<Charset> attempt) {
        return detect(file.toFile(), attempt);
    }

    Optional<FileType> detect(File file, Collection<Charset> attempt);
}
