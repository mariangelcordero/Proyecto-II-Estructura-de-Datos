package clustering.model.strategies.normalization;

import clustering.model.strategies.NormalizationStrategy;

/** f(x) = (x - min) / (max - min). Rango [0,1]. Columna constante -> 0. */
public class MinMaxNormalization implements NormalizationStrategy {
    @Override public double[] normalizeColumn(double[] values) {
        double min = values[0], max = values[0];
        for (double v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        double[] result = new double[values.length];
        double range = max - min;
        if (Math.abs(range) < 1e-10) return result;
        for (int i = 0; i < values.length; i++) result[i] = (values[i] - min) / range;
        return result;
    }

    @Override public String getName() { return "Min-Max"; }
}
