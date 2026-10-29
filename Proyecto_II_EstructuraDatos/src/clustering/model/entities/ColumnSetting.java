package clustering.model.entities;

/** Configuración editable de una variable (columna del CSV). */
public class ColumnSetting {
    private final String name;
    private ColumnType type;
    private boolean ignored;
    private double weight = 1.0;

    public ColumnSetting(String name, ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public ColumnType getType() { return type; }
    public void setType(ColumnType type) { this.type = type; }
    public boolean isIgnored() { return ignored; }
    public void setIgnored(boolean ignored) { this.ignored = ignored; }
    public double getWeight() { return weight; }

    public void setWeight(double weight) {
        if (weight < 0 || Double.isNaN(weight) || Double.isInfinite(weight))
            throw new IllegalArgumentException("El peso debe ser un número mayor o igual a 0");
        this.weight = weight;
    }
}
