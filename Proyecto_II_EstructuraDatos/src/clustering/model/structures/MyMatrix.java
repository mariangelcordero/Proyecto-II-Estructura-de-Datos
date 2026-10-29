package clustering.model.structures;

import clustering.model.iterators.MatrixRowIterator;
import clustering.model.iterators.MyIterable;
import clustering.model.iterators.MyIterator;

/** Matriz densa de doubles (filas x columnas). Memoria O(filas*columnas). */
public class MyMatrix implements MyIterable<double[]> {
    private final double[][] data;
    private final int rows;
    private final int columns;

    public MyMatrix(int rows, int columns) {
        if (rows <= 0 || columns <= 0) throw new IllegalArgumentException("Las dimensiones deben ser positivas");
        this.rows = rows;
        this.columns = columns;
        this.data = new double[rows][columns];
    }

    public int rows() { return rows; }

    public int columns() { return columns; }

    public double get(int row, int col) {
        check(row, col);
        return data[row][col];
    }

    public void set(int row, int col, double value) {
        check(row, col);
        data[row][col] = value;
    }

    public double[] getRow(int row) {
        check(row, 0);
        double[] copy = new double[columns];
        System.arraycopy(data[row], 0, copy, 0, columns);
        return copy;
    }

    private void check(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= columns) throw new IndexOutOfBoundsException("Posición fuera de rango");
    }

    @Override public MyIterator<double[]> getIterator() { return new MatrixRowIterator(this); }
}
