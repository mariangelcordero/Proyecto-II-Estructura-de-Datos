package clustering.model.iterators;

import clustering.model.structures.MyList;

public class MyListIterator<T> implements MyIterator<T> {
    private final MyList<T> list;
    private int index = -1;

    public MyListIterator(MyList<T> list) {
        if (list == null) throw new IllegalArgumentException("La lista no puede ser nula");
        this.list = list;
    }

    @Override public boolean hasNext() { return index < list.size() - 1; }

    @Override public T next() {
        if (!hasNext()) throw new IllegalStateException("No hay más elementos");
        index++;
        return list.get(index);
    }

    @Override public void reset() { index = -1; }

    @Override public T current() {
        if (index < 0 || index >= list.size()) throw new IllegalStateException("Llame next() primero");
        return list.get(index);
    }
}
