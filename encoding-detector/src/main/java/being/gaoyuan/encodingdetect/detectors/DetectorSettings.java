package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.utils.ForbidsSet;
import being.gaoyuan.encodingdetect.utils.IntRange;

import java.nio.charset.Charset;
import java.util.*;

public class DetectorSettings {
    public static final Set<Integer> CONTROL_EXCEPTS;
    private static Collection<Charset> charsets;
    private static Forbids forbids;

    static {
        Set<Integer> controlExcepts = new HashSet<>();
        for (char c : "\t\n\r".toCharArray()) {
            controlExcepts.add((int) c);
        }
        CONTROL_EXCEPTS = Collections.unmodifiableSet(controlExcepts);
        charsets = new ArrayList<>(
                Charset.availableCharsets().values());
        forbids = calcSimpleForbids();
    }

    public static Collection<Charset> getCharsets() {
        return Collections.unmodifiableCollection(charsets);
    }

    public static void setCharsets(Collection<Charset> charsets) {
        DetectorSettings.charsets = charsets;
    }

    public static Forbids calcSimpleForbids() {
        final Set<Integer> textFileForbids = calcSimpleForbidsSet();
        return new ForbidsSet(textFileForbids);
    }

    public static Set<Integer> calcSimpleForbidsSet() {
        final IntRange[] forbidRanges = new IntRange[]{
                new IntRange(0x000000, 0x000020),
                new IntRange(0x000080, 0x0000A0),
                new IntRange(0x00FFFC, 0x010000)};
        final Set<Integer> textFileForbids = new HashSet<>();
        for (IntRange range : forbidRanges) {
            for (int i : range) {
                textFileForbids.add(i);
            }
        }
        textFileForbids.removeAll(CONTROL_EXCEPTS);
        return textFileForbids;
    }

    public static Forbids getForbids() {
        return forbids;
    }

    public static void setForbids(Forbids forbids) {
        DetectorSettings.forbids = forbids;
    }
}