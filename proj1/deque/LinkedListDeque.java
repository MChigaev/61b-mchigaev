package deque;

//@Source Implementations of SLList helped to implement this implementation.
public class LinkedListDeque<T> implements Deque<T>, java.lang.Iterable<T> {
    private class IntNode {
        T first;
        IntNode rest;
        IntNode prev;

        IntNode(T f, IntNode r) {
            first = f;
            rest = r;
            prev = r;
        }
    }
    private int size;
    private IntNode sentinel;
    public LinkedListDeque() {
        sentinel = new IntNode(null, null);
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        if (size == 0) {
            sentinel.rest = new IntNode(item, null);
            sentinel.rest.rest = sentinel.rest;
            sentinel.rest.prev = sentinel.rest;
            sentinel.prev = sentinel.rest;
            size++;
            return;
        }
        sentinel.rest = new IntNode(item, sentinel.rest);
        sentinel.rest.rest.prev = sentinel.rest;
        sentinel.rest.prev = sentinel.prev;
        size++;
    }
    @Override
    public void addLast(T item) {
        if (size == 0) {
            sentinel.rest = new IntNode(item, null);
            sentinel.rest.rest = sentinel.rest;
            sentinel.rest.prev = sentinel.rest;
            sentinel.prev = sentinel.rest;
            size++;
            return;
        }
        IntNode temp = new IntNode(item, sentinel.rest);
        temp.prev = sentinel.prev;
        sentinel.prev = temp;
        sentinel.rest.prev = sentinel.prev;
        sentinel.prev.prev.rest = sentinel.prev;
        size++;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        IntNode temp = sentinel.rest;
        System.out.print(temp.first + " ");
        temp = temp.rest;
        while (temp != sentinel.rest) {
            System.out.print(temp.first + " ");
            temp = temp.rest;
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T temp = sentinel.rest.first;
        sentinel.rest = sentinel.rest.rest;
        sentinel.rest.prev = sentinel.prev;
        sentinel.prev.rest = sentinel.rest;
        size--;
        return temp;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T temp = sentinel.prev.first;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.rest = sentinel.rest;
        sentinel.rest.prev = sentinel.prev;
        size--;
        return temp;
    }
    @Override
    public T get(int index) {
        IntNode temp = sentinel.rest;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                return temp.first;
            }
            temp = temp.rest;
        }
        return null;
    }
    public java.util.Iterator<T> iterator() {
        return new Iterator();
    }
    private class Iterator implements java.util.Iterator<T> {
        private IntNode pos;
        boolean hit = false;
        Iterator() {
            pos = sentinel.rest;
        }
        public boolean hasNext() {
            if (isEmpty()) {
                return false;
            }
            if (pos == sentinel.rest && hit) {
                return false;
            }
            return true;
        }
        public T next() {
            hit = true;
            T ret = pos.first;
            pos = pos.rest;
            return ret;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        ArrayDeque<T> comp = new ArrayDeque<T>();
        if (o.getClass() != this.getClass() && o.getClass() != comp.getClass()) {
            return false;
        }
        Deque<T> t;
        if (o.getClass() == this.getClass()) {
            t = (LinkedListDeque<T>) o;
        } else {
            t = (ArrayDeque<T>) o;
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
    public T getRecursive(int index) {
        if (index > size) {
            return null;
        }
        IntNode temp = sentinel.rest;
        return getrecur(0, index, temp);
    }
    private T getrecur(int i, int index, IntNode node) {
        if (i == index) {
            return node.first;
        }
        return getrecur(i + 1, index, node.rest);
    }
}
