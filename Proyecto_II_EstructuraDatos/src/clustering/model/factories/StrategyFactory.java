package clustering.model.factories;

/** Patrón Factory Method: crea estrategias a partir de su nombre. */
public interface StrategyFactory<T> {
    T create(String type);
    String[] getAvailableTypes();
}
