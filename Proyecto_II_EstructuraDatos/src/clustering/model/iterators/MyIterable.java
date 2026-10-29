package clustering.model.iterators;

/** Toda estructura recorrible entrega su propio iterador. */
public interface MyIterable<T> {
    MyIterator<T> getIterator();
}
