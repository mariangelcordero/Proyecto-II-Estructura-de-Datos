package clustering.model.factories;

import clustering.model.strategies.NormalizacionStrategy;
import clustering.model.strategies.normalization.NormalizacionLogaritmica;
import clustering.model.strategies.normalization.NormalizacionMm;
import clustering.model.strategies.normalization.NormalizacionZScore;

public class NormalizationFactory implements StrategyFactory<NormalizacionStrategy> {
    @Override public NormalizacionStrategy create(String type) {
        switch (type.trim().toLowerCase()) {
            case "min-max": return new NormalizacionMm();
            case "z-score": return new NormalizacionZScore();
            case "logarithmic": return new NormalizacionLogaritmica();
            default: throw new IllegalArgumentException("Normalización desconocida: " + type);
        }
    }

    @Override public String[] getAvailableTypes() {
        return new String[] { "Min-Max", "Z-Score", "Logarithmic" };
    }
}
