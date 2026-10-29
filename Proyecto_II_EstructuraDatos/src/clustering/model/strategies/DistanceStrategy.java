package clustering.model.strategies;

import clustering.model.structures.MyVector;

/** Patrón Strategy: familia de funciones de distancia intercambiables. */
public interface DistanceStrategy {
    double calculate(MyVector a, MyVector b);
    String getName();
}
