package clustering.model.strategies.distance;

import clustering.model.iterators.MyIterator;
import clustering.model.strategies.DistanceStrategy;
import clustering.model.structures.MyVector;

/** d(x,y) = 1 - (x·y) / (|x||y|). Si algún vector es nulo se define d = 1. */
public class CosineDistance implements DistanceStrategy {
    @Override public double calculate(MyVector a, MyVector b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        double dot = 0, magA = 0, magB = 0;
        MyIterator<Double> ia = a.getIterator();
        MyIterator<Double> ib = b.getIterator();
        while (ia.hasNext() && ib.hasNext()) {
            double x = ia.next(), y = ib.next();
            dot += x * y;
            magA += x * x;
            magB += y * y;
        }
        magA = Math.sqrt(magA);
        magB = Math.sqrt(magB);
        if (magA < 1e-10 || magB < 1e-10) return 1;
        return 1 - dot / (magA * magB);
    }

    @Override public String getName() { return "Cosine"; }
}
