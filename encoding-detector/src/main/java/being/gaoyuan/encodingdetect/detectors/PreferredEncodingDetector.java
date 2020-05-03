package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Optional;

public class PreferredEncodingDetector extends AbstractEncodingDetector {
    private final Charset[] prefers;

    public PreferredEncodingDetector(Charset... prefers) {
        this.prefers = new Charset[prefers.length];
        System.arraycopy(prefers, 0, this.prefers, 0, prefers.length);
    }

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        for (Charset prefer : prefers) {
            if(attempt.contains(prefer)){
                continue;
            }
            DetectSummary summary = tryFit(file, prefer);
            attempt.add(prefer);
            if (summary.ok) {
                return Optional.of(new FileType(prefer.name(), attempt.size()));
            }
        }
        return Optional.empty();
    }
}
