package being.gaoyuan.encodingdetect.detectors;

public abstract class DetectContext {
    private final Forbids forbids;
    private final int minAllowed;
    private final int maxAllowed;
    private int handled = 0;
    private int max = 0;
    private int lastValue = -1;
    private int brokenValue = 0;
    private boolean broken = false;

    private boolean committed = false;

    public DetectContext(Forbids forbids, int minAllowed, int maxAllowed) {
        this.forbids = forbids;
        this.minAllowed = minAllowed;
        this.maxAllowed = maxAllowed;
    }

    void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isBroken() {
        return broken;
    }

    int getLastValue() {
        return lastValue;
    }

    public int getBrokenValue() {
        return brokenValue;
    }

    public int getHandled() {
        return handled;
    }

    public int getMax() {
        return max;
    }

    protected abstract void preHandle();

    protected abstract boolean doHandle(final int value);

    public void handle(final int value) {
        handled++;
        preHandle();
        if (!doHandle(value)) {
            if (forbids.contains(value) ||
                    value < minAllowed || value > maxAllowed) {
                brokenValue = value;
                broken = true;
                return;
            }
        }
        if (max < value) {
            max = value;
        }
        lastValue = value;
    }

    protected abstract boolean doCommit();

    public void commit() {
        if (committed) {
            throw new IllegalStateException();
        } else {
            committed = doCommit();
        }
    }
}
