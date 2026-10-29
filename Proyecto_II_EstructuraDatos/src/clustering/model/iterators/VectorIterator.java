package clustering.model.iterators;

import clustering.model.structures.MyVector;

public class VectorIterator implements MyIterator<Double> {
    private final MyVector vector;
    private int index = -1;

    public VectorIterator(MyVector vector) {
        if (vector == null) throw new IllegalArgumentException("El vector no puede ser nulo");
        this.vector = vector;
    }

    @Override public boolean hasNext() { return index < vector.size() - 1; }

    @Override public Double next() {
        if (!hasNext()) throw new IllegalStateException("No hay más componentes");
        index++;
        return vector.get(index);
    }

    @Override public void reset() { index = -1; }

    @Override public Double current() {
        if (index < 0 || index >= vector.size()) throw new IllegalStateException("Llame next() primero");
        return vector.get(index);
    }
}
