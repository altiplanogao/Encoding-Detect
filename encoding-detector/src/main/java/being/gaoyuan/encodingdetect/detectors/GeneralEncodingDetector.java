package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.EncodingGuesser;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

public class GeneralEncodingDetector extends AbstractEncodingDetector {
    private static final SortedMap<String, Charset> CHARSETS = Charset.availableCharsets();

    @Override
    public Optional<FileType> detect(File file) {
        List<DetectSummary> summaryList = new ArrayList<>();

//        ByteOrderMarks.resolve(file).ifPresent(
//                bom -> summaryList.add(
//                        tryFit(file, bom.charset, bom.markLen())));

        for (Charset charset : CHARSETS.values()) {
            summaryList.add(tryFit(file, charset));
        }

        return EncodingGuesser.guess(summaryList);
    }

}
