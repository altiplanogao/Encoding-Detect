package being.gaoyuan.encodingdetect.detectors;

class BoundedCharBuffer {
    private final int limit;
    private final char[] contents;
    private int size = 0;

    public BoundedCharBuffer(int limit) {
        this.limit = limit;
        this.contents = new char[this.limit];
    }

    public BoundedCharBuffer append(char ch) {
        final int offset = size++;
        if (offset < limit) {
            contents[offset] = ch;
        }
        return this;
    }

    @Override
    public String toString() {
        if (size <= limit) {
            return new String(contents, 0, size);
        } else {
            return new String(contents) + "...[" + (size - limit) + " extra]";
        }
    }
}
