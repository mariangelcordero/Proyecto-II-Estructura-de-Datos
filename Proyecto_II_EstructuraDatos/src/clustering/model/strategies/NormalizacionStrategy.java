package clustering.model.strategies;

public interface NormalizacionStrategy {
    double[] normalizeColumn(double[] values);
    String getName();
}
