package clustering.services;

import clustering.model.strategies.NormalizationStrategy;
import clustering.model.structures.MyMatrix;

/** Aplica una estrategia de normalización columna por columna y luego los pesos (v_i = w * f(x_i)). */
final class NormalizationApplier {
    private NormalizationApplier() { }

    static void apply(NormalizationStrategy strategy, MyMatrix matrix, double[] weights) {
        int rows = matrix.rows();
        for (int c = 0; c < matrix.columns(); c++) {
            double[] column = new double[rows];
            for (int r = 0; r < rows; r++) column[r] = matrix.get(r, c);
            double[] normalized = strategy.normalizeColumn(column);
            double weight = weights == null ? 1.0 : weights[c];
            for (int r = 0; r < rows; r++) matrix.set(r, c, normalized[r] * weight);
        }
    }
}
