package clustering.model.strategies.normalization;

import clustering.model.strategies.NormalizacionStrategy;

public class NormalizacionMm implements NormalizacionStrategy {
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
