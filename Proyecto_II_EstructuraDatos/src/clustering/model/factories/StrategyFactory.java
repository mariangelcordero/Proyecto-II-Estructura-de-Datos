package clustering.model.factories;


public interface StrategyFactory<T> {
    T create(String type);
    String[] getAvailableTypes();
}
