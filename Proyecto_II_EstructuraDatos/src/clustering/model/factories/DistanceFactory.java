package clustering.model.factories;

import clustering.model.strategies.DistanciaStrategy;
import clustering.model.strategies.distance.DistanciaCoseno;
import clustering.model.strategies.distance.DistanciaEuclidiana;
import clustering.model.strategies.distance.DistanciaHamming;
import clustering.model.strategies.distance.DistanciaManhattan;

public class DistanceFactory implements StrategyFactory<DistanciaStrategy> {
    @Override public DistanciaStrategy create(String type) {
        switch (type.trim().toLowerCase()) {
            case "euclidean": return new DistanciaEuclidiana();
            case "manhattan": return new DistanciaManhattan();
            case "cosine": return new DistanciaCoseno();
            case "hamming": return new DistanciaHamming();
            default: throw new IllegalArgumentException("Distancia desconocida: " + type);
        }
    }

    @Override public String[] getAvailableTypes() {
        return new String[] { "Euclidean", "Manhattan", "Cosine", "Hamming" };
    }
}
