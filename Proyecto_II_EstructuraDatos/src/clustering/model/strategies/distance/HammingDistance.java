package clustering.model.strategies.distance;

import clustering.model.iterators.MyIterator;
import clustering.model.strategies.DistanceStrategy;
import clustering.model.structures.MyVector;

/** Cuenta las posiciones donde los vectores difieren. Pensada para vectores binarios/discretos. */
public class HammingDistance implements DistanceStrategy {
    @Override public double calculate(MyVector a, MyVector b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        int count = 0;
        MyIterator<Double> ia = a.getIterator();
        MyIterator<Double> ib = b.getIterator();
        while (ia.hasNext() && ib.hasNext()) {
            if (Math.abs(ia.next() - ib.next()) > 1e-10) count++;
        }
        return count;
    }

    @Override public String getName() { return "Hamming"; }
}
