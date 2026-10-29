package clustering.model.structures;

import clustering.model.iterators.MyIterable;
import clustering.model.iterators.MyIterator;
import clustering.model.iterators.VectorIterator;

/** Vector numérico de tamaño fijo. */
public class MyVector implements MyIterable<Double> {
    private final double[] elements;

    public MyVector(int size) {
        if (size <= 0) throw new IllegalArgumentException("El tamaño debe ser positivo");
        elements = new double[size];
    }

    public MyVector(double[] source) {
        if (source == null || source.length == 0) throw new IllegalArgumentException("El vector no puede estar vacío");
        elements = new double[source.length];
        System.arraycopy(source, 0, elements, 0, source.length);
    }

    public int size() { return elements.length; }

    public double get(int index) {
        if (index < 0 || index >= elements.length) throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango");
        return elements[index];
    }

    public void set(int index, double value) {
        if (index < 0 || index >= elements.length) throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango");
        elements[index] = value;
    }

    @Override public MyIterator<Double> getIterator() { return new VectorIterator(this); }
}
