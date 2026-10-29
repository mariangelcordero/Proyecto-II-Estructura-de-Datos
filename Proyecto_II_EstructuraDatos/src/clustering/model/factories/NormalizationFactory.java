package clustering.model.factories;

import clustering.model.strategies.NormalizationStrategy;
import clustering.model.strategies.normalization.LogarithmicNormalization;
import clustering.model.strategies.normalization.MinMaxNormalization;
import clustering.model.strategies.normalization.ZScoreNormalization;

public class NormalizationFactory implements StrategyFactory<NormalizationStrategy> {
    @Override public NormalizationStrategy create(String type) {
        switch (type.trim().toLowerCase()) {
            case "min-max": return new MinMaxNormalization();
            case "z-score": return new ZScoreNormalization();
            case "logarithmic": return new LogarithmicNormalization();
            default: throw new IllegalArgumentException("Normalización desconocida: " + type);
        }
    }

    @Override public String[] getAvailableTypes() {
        return new String[] { "Min-Max", "Z-Score", "Logarithmic" };
    }
}
