package deque;

//@Source Implementations of AList helped to implement this implementation.
public class ArrayDeque<T> implements Deque<T>, java.lang.Iterable<T> {
    private T[] items;
    private int size;
    private int front = 3;
    private int back = 3;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        front--;
        if (front == -1) {
            front = items.length - 1;
        }
        if (size > 1 && front == back) {
            front++;
            resize(items.length * 4);
            front = items.length - 1;
        }
        items[front] = item;
        size++;
        if (size == 1) {
            back = front;
        }
    }
    @Override
    public void addLast(T item) {
        back++;
        if (back == items.length) {
            back = 0;
        }
        if (size > 1 && front == back) {
            back--;
            resize(items.length * 4);
            back++;
        }
        items[back] = item;
        size++;
        if (size == 1) {
            front = back;
        }
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        if (front < back) {
            for (int i = front; i <= back; i++) {
                System.out.print(items[i] + " ");
            }
            System.out.println();
        } else {
            for (int i = front; i < items.length; i++) {
                System.out.print(items[i] + " ");
            }
            for (int i = 0; i <= back; i++) {
                System.out.print(items[i] + " ");
            }
            System.out.println();
        }
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T ret = items[front];
        items[front] = null;
        front++;
        if (front == items.length) {
            front = 0;
        }
        size--;
        if (size < items.length / 4 && size > 1) {
            resize(items.length / 4);
        }
        return ret;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T ret = items[back];
        items[back] = null;
        back--;
        if (back == -1) {
            back = items.length - 1;
        }
        size--;
        if (size < items.length / 4 && size > 1) {
            resize(items.length / 4);
        }
        return ret;
    }
    @Override
    public T get(int index) {
        int temp = index + front;
        if (temp > items.length - 1) {
            temp = temp - items.length;
        }
        if (temp > items.length - 1) {
            return null;
        }
        return items[temp];
    }
    public java.util.Iterator<T> iterator() {
        return new ArrayDeque.Iterator();
    }
    private class Iterator implements java.util.Iterator<T> {
        int pos;
        boolean loop = false;
        Iterator() {
            pos = front;
            if (back < front) {
                loop = true;
            }
        }
        public boolean hasNext() {
            if (isEmpty()) {
                return false;
            }
            if (pos > back) {
                return false;
            }
            return true;
        }
        public T next() {
            if (loop && hasNext()) {
                T ret = items[pos];
                pos++;
                if (pos == items.length) {
                    pos = 0;
                }
                return ret;
            } else if (hasNext()) {
                T ret = items[pos];
                pos++;
                return ret;
            }
            return null;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        LinkedListDeque<T> comp = new LinkedListDeque<T>();
        if (o.getClass() != this.getClass() && o.getClass() != comp.getClass()) {
            return false;
        }
        Deque<T> t;
        if (o.getClass() == this.getClass()) {
            t = (ArrayDeque<T>) o;
        } else {
            t = (LinkedListDeque<T>) o;
        }
        if (size() != t.size()) {
            return false;
        }
        int[] alreadycontained = new int[size];
        for (int i = 0; i < alreadycontained.length; i++) {
            alreadycontained[i] = 0;
        }
        for (int x = 0; x < size; x++) {
            boolean contains = false;
            for (int y = 0; y < size; y++) {
                if (t.get(x).equals(get(y)) && alreadycontained[y] != 1) {
                    contains = true;
                    alreadycontained[y] = 1;
                    break;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;

    }
    private void resize(int capacity) {
        T[] newarr = (T[]) new Object[capacity];
        if (front < back) {
            System.arraycopy(items, front, newarr, 0, back - front + 1);
            front = 0;
            back = size - 1;
        } else {
            T[] curritems = (T[]) new Object[size];
            int c = 0;
            for (int i = front; i < items.length; i++) {
                curritems[c] = items[i];
                c++;
            }
            for (int i = 0; i <= back; i++) {
                curritems[c] = items[i];
                c++;
            }
            System.arraycopy(curritems, 0, newarr, 0, size);
            front = 0;
            back = size - 1;
        }
        items = newarr;
    }
}
