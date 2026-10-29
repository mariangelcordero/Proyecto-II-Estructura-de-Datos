package clustering.model.strategies;

/** Patrón Strategy: normaliza una columna completa (los estadísticos se calculan una sola vez, O(n)). */
public interface NormalizationStrategy {
    double[] normalizeColumn(double[] values);
    String getName();
}
