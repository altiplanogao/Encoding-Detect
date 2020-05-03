package being.gaoyuan.encodingdetect.utils;

import being.gaoyuan.encodingdetect.detectors.Forbids;

import java.util.Set;

public class ForbidsSet implements Forbids {
    private final Set<Integer> values;

    public ForbidsSet(Set<Integer> values) {
        this.values = values;
    }

    @Override
    public boolean contains(int value) {
        return values.contains(value);
    }
}
