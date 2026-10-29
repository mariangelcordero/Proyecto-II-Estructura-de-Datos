package clustering.model.iterators;

import clustering.model.structures.MyMatrix;

public class MatrixRowIterator implements MyIterator<double[]> {
    private final MyMatrix matrix;
    private int row = -1;

    public MatrixRowIterator(MyMatrix matrix) {
        if (matrix == null) throw new IllegalArgumentException("La matriz no puede ser nula");
        this.matrix = matrix;
    }

    @Override public boolean hasNext() { return row < matrix.rows() - 1; }

    @Override public double[] next() {
        if (!hasNext()) throw new IllegalStateException("No hay más filas");
        row++;
        return matrix.getRow(row);
    }

    @Override public void reset() { row = -1; }

    @Override public double[] current() {
        if (row < 0 || row >= matrix.rows()) throw new IllegalStateException("Posición inválida");
        return matrix.getRow(row);
    }
}
