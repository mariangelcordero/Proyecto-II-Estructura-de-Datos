package clustering.model.entities;

public enum ColumnType {
    NUMERIC("Numérico"),
    CATEGORICAL("Categórico (one-hot)"),
    MULTI_VALUE("Multivalor (palabras)");

    private final String label;

    ColumnType(String label) { this.label = label; }

    @Override public String toString() { return label; }
}
