package clustering.model.strategies.distance;

import clustering.model.iterators.MyIterator;
import clustering.model.strategies.DistanceStrategy;
import clustering.model.structures.MyVector;

public class EuclideanDistance implements DistanceStrategy {
    @Override public double calculate(MyVector a, MyVector b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        double sum = 0;
        MyIterator<Double> ia = a.getIterator();
        MyIterator<Double> ib = b.getIterator();
        while (ia.hasNext() && ib.hasNext()) {
            double diff = ia.next() - ib.next();
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    @Override public String getName() { return "Euclidean"; }
}
