package clustering.model.iterators;

public interface MyIterator<T> {
    boolean hasNext();
    T next();
    void reset();
    T current();
}
