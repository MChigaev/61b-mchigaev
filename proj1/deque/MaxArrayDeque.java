package deque;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private java.util.Comparator<T> comp;
    public MaxArrayDeque(java.util.Comparator<T> c) {
        super();
        this.comp = c;
    }
    public T max() {
        if (this.isEmpty()) {
            return null;
        }
        T max = (T) this.get(0);
        for (int i = 0; i < this.size(); i++) {
            if (this.comp.compare((T) this.get(i), max) < 0) {
                // DO NOTHING
                max = max;
            } else {
                max = (T) this.get(i);
            }
        }
        return max;
    }
    public T max(java.util.Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T max = (T) this.get(0);
        for (int i = 0; i < this.size(); i++) {
            if (c.compare((T) this.get(i), max) < 0) {
                // DO NOTHING
                max = max;
            } else {
                max = (T) this.get(i);
            }
        }
        return max;
    }
}
