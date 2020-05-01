package being.gaoyuan;

import java.util.*;
import java.util.stream.Collectors;

public class FileType {
    public boolean binary;
    public List<String> charsets;

    public FileType(List<String> charsets) {
        this.binary = charsets.isEmpty();
        this.charsets = new ArrayList<>(charsets);
    }

    public static Optional<FileType> bestFit(List<DetectSummary> summaries) {
        List<DetectSummary> supported = new ArrayList<>();
        boolean anyLineEnd = false;
        for (DetectSummary summary : summaries) {
            if (summary.ok) {
                supported.add(summary);
                anyLineEnd |= summary.lines > 0;
            }
        }

        if (anyLineEnd) {
            supported = supported.stream().filter(summary -> summary.lines > 0).collect(Collectors.toList());
        }

        Map<String, List<DetectSummary>> hash2SummaryMap = new HashMap<>();
        for (DetectSummary summary : supported) {
            hash2SummaryMap
                    .computeIfAbsent(summary.contentHash, x -> new ArrayList<>())
                    .add(summary);
        }

        return Optional.of(new FileType(supported.stream()
                .map(summary -> summary.charset.name()).collect(Collectors.toList())));
    }

    public String brief() {
        return binary ? "<binary>" : "<text>" + "." + charsets.size() + ".encoding";
    }
}
