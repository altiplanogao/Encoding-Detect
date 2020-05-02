package being.gaoyuan;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FileEncoding {
    private static final List<String> THE_BESTES = new ArrayList<String>() {
        {
//            add("US-ASCII");
//            add("ISO-8859-1");
            add("UTF-8");
            add("GBK");
            add("GB18030");
            add("Big5");
            add("UTF-16");
            add("UTF-16LE");
        }
    };
    private static final List<String> BLACK_LIST = new ArrayList<String>() {
        {
            add("x-COMPOUND_TEXT");
        }
    };

    public String bestGuess;
    public List<String> charsets;

    public FileEncoding(List<String> charsets) {
        this.charsets = new ArrayList<>(charsets);
        this.charsets.removeAll(BLACK_LIST);
        switch (this.charsets.size()){
            case 0:
                bestGuess = "";
                break;
            case 1:
                bestGuess = charsets.get(0);
                break;
            default:
                bestGuess = theBest(this.charsets);
        }
    }

    private String theBest(List<String> charsets) {
        for (String soFarBest : THE_BESTES) {
            if (charsets.contains(soFarBest)) {
                return soFarBest;
            }
        }
        return charsets.get(0);
    }

    public String brief() {
        return StringUtils.isEmpty(bestGuess) ? "<binary>" : "<text>" + "." + bestGuess + ".in." + charsets.size();
    }

    public boolean isText() {
        return !StringUtils.isEmpty(bestGuess);
    }

    public static Optional<FileEncoding> bestFit(List<DetectSummary> summaries) {
        List<DetectSummary> supported = new ArrayList<>();
        boolean anyLineEnd = false;
        for (DetectSummary summary : summaries) {
            if (summary.ok) {
                supported.add(summary);
                anyLineEnd |= summary.lines > 0;
            }
        }

        List<DetectSummary> filter = supported;
        if (anyLineEnd) {
            filter = supported.stream().filter(summary -> summary.lines > 0).collect(Collectors.toList());
        }

        Map<String, List<DetectSummary>> hash2SummaryMap = new HashMap<>();
        for (DetectSummary summary : filter) {
            hash2SummaryMap
                    .computeIfAbsent(summary.contentHash, x -> new ArrayList<>())
                    .add(summary);
        }

        return Optional.of(new FileEncoding(filter.stream()
                .map(summary -> summary.charset.name()).collect(Collectors.toList())));
    }
}
