package clustering.model.entities;

import clustering.model.structures.MyVector;

/** Un registro del CSV: su etiqueta, sus valores originales y su vector numérico. */
public class DataPoint {
    private String name;
    private final String[] rawValues;
    private MyVector vectorData;

    public DataPoint(String name, String[] rawValues) {
        this.name = name;
        this.rawValues = rawValues;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRawValue(int column) { return rawValues[column]; }
    public MyVector getVectorData() { return vectorData; }
    public void setVectorData(MyVector vectorData) { this.vectorData = vectorData; }
}
