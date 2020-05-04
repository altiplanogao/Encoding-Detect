package being.gaoyuan.encodingdetect.detectors;

import com.ibm.icu.text.UnicodeSet;

import java.util.Set;

public class UnicodeSetForbids  implements Forbids{
    private final UnicodeSet unicodeSet;

    public UnicodeSetForbids(Set<Integer> values) {
        unicodeSet = new UnicodeSet();
        for (int i : values) {
            unicodeSet.add(i);
        }
        unicodeSet.freeze();
    }

    @Override
    public boolean contains(int value) {
        return unicodeSet.contains(value);
    }
}
