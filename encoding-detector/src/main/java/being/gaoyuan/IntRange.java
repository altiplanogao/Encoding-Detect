package being.gaoyuan;

public class IntRange{
    public final int first;
    public final int last;

    public IntRange(int first, int last) {
        this.first = first;
        this.last = last;
    }

    public boolean contains(int value){
        return first <= value && value <= last;
    }

}
