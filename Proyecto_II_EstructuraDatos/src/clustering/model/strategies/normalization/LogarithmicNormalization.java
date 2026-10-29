package clustering.model.strategies.normalization;

import clustering.model.strategies.NormalizationStrategy;

/** f(x) = ln(x + 1). Valores menores o iguales a -1 se acotan para evitar un logaritmo indefinido. */
public class LogarithmicNormalization implements NormalizationStrategy {
    @Override public double[] normalizeColumn(double[] values) {
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) result[i] = Math.log(Math.max(values[i], -1 + 1e-12) + 1);
        return result;
    }

    @Override public String getName() { return "Logarithmic"; }
}
