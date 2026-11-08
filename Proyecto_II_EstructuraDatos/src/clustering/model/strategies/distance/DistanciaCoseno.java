package clustering.model.strategies.distance;

import clustering.model.iterators.MyIterator;
import clustering.model.strategies.DistanciaStrategy;
import clustering.model.structures.MyVector;

public class DistanciaCoseno implements DistanciaStrategy {
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

    @Override public String getName() { return "Coseno"; }
}
