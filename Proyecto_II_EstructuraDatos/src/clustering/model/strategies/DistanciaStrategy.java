package clustering.model.strategies;

import clustering.model.structures.MyVector;

public interface DistanciaStrategy {
    double calculate(MyVector a, MyVector b);
    String getName();
}
