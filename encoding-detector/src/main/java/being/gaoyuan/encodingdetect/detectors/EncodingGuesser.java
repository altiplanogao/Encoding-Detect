package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.BinaryType;
import being.gaoyuan.encodingdetect.FileType;

import java.util.*;
import java.util.stream.Collectors;

public class EncodingGuesser {
    private static final List<String> THE_PREFERRED = new ArrayList<String>() {
        {
            add("UTF-8");
            add("ISO-8859-1");
            add("GBK");
            add("GB18030");
            add("Big5");
            add("UTF-16");
            add("UTF-16LE");
            add("UTF-32");
        }
    };
    private static final List<String> BLACK_LIST = new ArrayList<String>() {
        {
//            add("x-COMPOUND_TEXT");
        }
    };

    private static String theBest(List<String> charsets) {
        for (String soFarBest : THE_PREFERRED) {
            if (charsets.contains(soFarBest)) {
                return soFarBest;
            }
        }
        return charsets.get(0);
    }

    public static Optional<FileType> guess(List<DetectSummary> summaries, Collection<?> attempt) {
        return guess(summaries, attempt.size());
    }

    public static Optional<FileType> guess(List<DetectSummary> summaries, int decodeAttempt) {
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

        Map<Summary, List<String>> hash2SummaryMap = groupBySummary(filter, new TreeMap<>());

        List<String> potentialEncodings = filter.stream()
                .map(summary -> summary.charset.name()).collect(Collectors.toList());
        potentialEncodings.removeAll(BLACK_LIST);
        switch (potentialEncodings.size()) {
            case 0:
                return Optional.of(new FileType(BinaryType.UNKNOWN_BINARY, decodeAttempt));
            case 1:
                return Optional.of(new FileType(potentialEncodings.get(0), decodeAttempt));
            default:
                return Optional.of(new FileType(theBest(potentialEncodings), potentialEncodings, decodeAttempt));
        }
    }

    private static Map<Summary, List<String>> groupBySummary(List<DetectSummary> summaryList,
                                                             Map<Summary, List<String>> summaryToEncodings) {
        if (summaryToEncodings == null) {
            summaryToEncodings = new HashMap<>();
        }
        for (DetectSummary summary : summaryList) {
            Summary brief = new Summary(summary);
            summaryToEncodings
                    .computeIfAbsent(brief, x -> new ArrayList<>())
                    .add(summary.charset.name());
        }
        return summaryToEncodings;
    }
}
