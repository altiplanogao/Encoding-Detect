package being.gaoyuan.encodingdetect.detectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface Forbids {
    boolean contains(int value);
}

class CollectionForbids implements Forbids {
    private final Set<Integer> values;

    public CollectionForbids(Collection<Integer> values) {
        this.values = new HashSet<>(values);
    }

    @Override
    public boolean contains(int value) {
        return values.contains(value);
    }
}
