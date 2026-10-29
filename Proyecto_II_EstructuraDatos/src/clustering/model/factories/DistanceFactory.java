package clustering.model.factories;

import clustering.model.strategies.DistanceStrategy;
import clustering.model.strategies.distance.CosineDistance;
import clustering.model.strategies.distance.EuclideanDistance;
import clustering.model.strategies.distance.HammingDistance;
import clustering.model.strategies.distance.ManhattanDistance;

public class DistanceFactory implements StrategyFactory<DistanceStrategy> {
    @Override public DistanceStrategy create(String type) {
        switch (type.trim().toLowerCase()) {
            case "euclidean": return new EuclideanDistance();
            case "manhattan": return new ManhattanDistance();
            case "cosine": return new CosineDistance();
            case "hamming": return new HammingDistance();
            default: throw new IllegalArgumentException("Distancia desconocida: " + type);
        }
    }

    @Override public String[] getAvailableTypes() {
        return new String[] { "Euclidean", "Manhattan", "Cosine", "Hamming" };
    }
}
