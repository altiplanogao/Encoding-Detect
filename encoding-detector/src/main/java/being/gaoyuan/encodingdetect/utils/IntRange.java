package being.gaoyuan.encodingdetect.utils;

import java.util.Iterator;
import java.util.Objects;

public final class IntRange implements Iterable<Integer> {
    //[low, high)
    public final int low;
    //[low, high)
    public final int high;

    public IntRange(int low, int high) {
        this.low = low;
        this.high = high;
        if(high<=low){
            throw new IllegalArgumentException();
        }
    }

    public boolean contains(final int value) {
        return low <= value && value < high;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new RangeIterator(this);
    }

    private static class RangeIterator implements Iterator<Integer> {
        private final int min;
        private final int max;
        private int current;

        public RangeIterator(IntRange range) {
            this.min = range.low;
            this.max = range.high - 1;
            this.current = min;
        }

        @Override
        public boolean hasNext() {
            return current <= max;
        }

        @Override
        public Integer next() {
            return current++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntRange integers = (IntRange) o;
        return low == integers.low &&
                high == integers.high;
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high);
    }

    @Override
    public String toString() {
        return "[" + low + ", " + high + ')';
    }
}
