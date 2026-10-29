package clustering.model.iterators;

/** Patrón Iterator: recorrido secuencial de una estructura sin exponer su representación. */
public interface MyIterator<T> {
    boolean hasNext();
    T next();
    void reset();
    T current();
}
