package clustering.model.structures;

import clustering.model.iterators.MyIterable;
import clustering.model.iterators.MyIterator;
import clustering.model.iterators.MyListIterator;

/** Lista dinámica sobre arreglo (capacidad duplicada al llenarse). add: O(1) amortizado, get/set: O(1). */
public class MyList<T> implements MyIterable<T> {
    private Object[] items = new Object[10];
    private int size;

    public void add(T item) {
        if (size == items.length) {
            Object[] bigger = new Object[items.length * 2];
            System.arraycopy(items, 0, bigger, 0, size);
            items = bigger;
        }
        items[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) items[index];
    }

    public void set(int index, T value) {
        checkIndex(index);
        items[index] = value;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void clear() {
        items = new Object[10];
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango");
    }

    @Override public MyIterator<T> getIterator() { return new MyListIterator<>(this); }
}
