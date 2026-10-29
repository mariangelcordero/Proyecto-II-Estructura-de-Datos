package clustering.model;

import clustering.model.entities.ColumnSetting;
import clustering.model.strategies.DistanceStrategy;
import clustering.model.strategies.NormalizationStrategy;
import clustering.model.structures.MyList;

/** Patrón Singleton: configuración global de la corrida (estrategias, variables, pesos). */
public final class Configuration {
    private static final class Holder {
        private static final Configuration INSTANCE = new Configuration();
    }

    private NormalizationStrategy normalizationStrategy;
    private DistanceStrategy distanceStrategy;
    private MyList<ColumnSetting> columns = new MyList<>();
    private int nameColumnIndex;

    private Configuration() { }

    public static Configuration getInstance() { return Holder.INSTANCE; }

    public NormalizationStrategy getNormalizationStrategy() { return normalizationStrategy; }
    public void setNormalizationStrategy(NormalizationStrategy s) { this.normalizationStrategy = s; }
    public DistanceStrategy getDistanceStrategy() { return distanceStrategy; }
    public void setDistanceStrategy(DistanceStrategy s) { this.distanceStrategy = s; }
    public MyList<ColumnSetting> getColumns() { return columns; }
    public void setColumns(MyList<ColumnSetting> columns) { this.columns = columns; }
    public int getNameColumnIndex() { return nameColumnIndex; }
    public void setNameColumnIndex(int index) { this.nameColumnIndex = index; }

    public void reset() {
        normalizationStrategy = null;
        distanceStrategy = null;
        columns = new MyList<>();
        nameColumnIndex = 0;
    }
}
