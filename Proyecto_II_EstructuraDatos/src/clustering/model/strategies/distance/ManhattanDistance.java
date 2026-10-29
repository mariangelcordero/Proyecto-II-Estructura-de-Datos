package clustering.model.strategies.distance;

import clustering.model.iterators.MyIterator;
import clustering.model.strategies.DistanceStrategy;
import clustering.model.structures.MyVector;

public class ManhattanDistance implements DistanceStrategy {
    @Override public double calculate(MyVector a, MyVector b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        double sum = 0;
        MyIterator<Double> ia = a.getIterator();
        MyIterator<Double> ib = b.getIterator();
        while (ia.hasNext() && ib.hasNext()) sum += Math.abs(ia.next() - ib.next());
        return sum;
    }

    @Override public String getName() { return "Manhattan"; }
}
