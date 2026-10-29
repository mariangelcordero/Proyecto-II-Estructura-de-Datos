package clustering.model.strategies.normalization;

import clustering.model.strategies.NormalizationStrategy;

/** f(x) = (x - μ) / σ con desviación estándar muestral. Columna constante -> 0. */
public class ZScoreNormalization implements NormalizationStrategy {
    @Override public double[] normalizeColumn(double[] values) {
        int n = values.length;
        double[] result = new double[n];
        if (n < 2) return result;
        double mean = 0;
        for (double v : values) mean += v;
        mean /= n;
        double squares = 0;
        for (double v : values) squares += (v - mean) * (v - mean);
        double std = Math.sqrt(squares / (n - 1));
        if (std < 1e-10) return result;
        for (int i = 0; i < n; i++) result[i] = (values[i] - mean) / std;
        return result;
    }

    @Override public String getName() { return "Z-Score"; }
}
