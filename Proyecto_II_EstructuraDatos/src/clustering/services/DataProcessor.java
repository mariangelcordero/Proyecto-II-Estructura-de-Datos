package clustering.services;

import clustering.model.Configuration;
import clustering.model.entities.ColumnSetting;
import clustering.model.entities.ColumnType;
import clustering.model.entities.DataPoint;
import clustering.model.iterators.MyIterable;
import clustering.model.iterators.MyIterator;
import clustering.model.iterators.MyListIterator;
import clustering.model.structures.MyHashMap;
import clustering.model.structures.MyList;
import clustering.model.structures.MyMatrix;
import clustering.model.structures.MyVector;
import java.io.IOException;

/** Carga el CSV, detecta tipos y convierte los registros en vectores normalizados y ponderados. */
public class DataProcessor implements MyIterable<DataPoint> {
    private final MyList<DataPoint> dataPoints = new MyList<>();
    private final MyList<String> headers = new MyList<>();
    private MyList<String> dimensionLabels = new MyList<>();

    /** Plan de vectorización de una variable activa. */
    private static class ColumnPlan {
        int columnIndex;
        ColumnType type;
        double weight;
        int offset;
        int dims;
        MyHashMap<Integer> indexByValue;
    }

    /** Lee el CSV. Se descartan columnas sin encabezado. O(filas*columnas). */
    public void loadCsv(String path) throws IOException {
        MyList<String[]> rows = new CsvParser().parse(path);
        if (rows.size() < 2) throw new IllegalArgumentException("El CSV debe tener encabezado y al menos un registro");
        dataPoints.clear();
        headers.clear();
        dimensionLabels = new MyList<>();
        String[] rawHeaders = rows.get(0);
        MyList<Integer> validIndices = new MyList<>();
        for (int j = 0; j < rawHeaders.length; j++) {
            if (!rawHeaders[j].trim().isEmpty()) {
                headers.add(rawHeaders[j].trim());
                validIndices.add(j);
            }
        }
        if (headers.isEmpty()) throw new IllegalArgumentException("El CSV no tiene encabezados válidos");
        for (int i = 1; i < rows.size(); i++) {
            String[] cells = rows.get(i);
            String[] values = new String[headers.size()];
            for (int j = 0; j < values.length; j++) {
                int source = validIndices.get(j);
                values[j] = source < cells.length ? cells[source].trim() : "";
            }
            dataPoints.add(new DataPoint("Fila " + i, values));
        }
    }

    /** Propone el tipo de cada columna: numérica, multivalor (palabras repetidas entre filas) o categórica. */
    public MyList<ColumnSetting> detectColumnSettings() {
        MyList<ColumnSetting> settings = new MyList<>();
        for (int j = 0; j < headers.size(); j++) {
            boolean allNumeric = true, anyValue = false, anySpace = false;
            MyHashMap<Boolean> distinctValues = new MyHashMap<>();
            MyHashMap<Boolean> distinctTokens = new MyHashMap<>();
            for (int i = 0; i < dataPoints.size(); i++) {
                String v = dataPoints.get(i).getRawValue(j);
                if (v.isEmpty()) continue;
                anyValue = true;
                if (!TextUtils.isNumber(v)) allNumeric = false;
                distinctValues.put(v, true);
                MyList<String> tokens = TextUtils.splitWhitespace(v);
                if (tokens.size() > 1) anySpace = true;
                for (int t = 0; t < tokens.size(); t++) distinctTokens.put(tokens.get(t), true);
            }
            ColumnType type;
            if (anyValue && allNumeric) type = ColumnType.NUMERIC;
            else if (anySpace && distinctTokens.size() < distinctValues.size()) type = ColumnType.MULTI_VALUE;
            else type = ColumnType.CATEGORICAL;
            settings.add(new ColumnSetting(headers.get(j), type));
        }
        return settings;
    }

    /** Usa la columna indicada como etiqueta de cada registro. */
    public void applyNameColumn(int column) {
        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint dp = dataPoints.get(i);
            String value = dp.getRawValue(column);
            dp.setName(value.isEmpty() ? "Fila " + (i + 1) : value);
        }
    }

    /**
     * Vectoriza: numéricos directos (faltantes = media), categóricos one-hot, multivalor one-hot por palabra;
     * luego normaliza cada dimensión y multiplica por el peso de su variable.
     * O(n*D) para llenar + O(n*D) para normalizar, con D = dimensiones totales.
     */
    public void convertToVectors() {
        Configuration cfg = Configuration.getInstance();
        if (cfg.getNormalizationStrategy() == null) throw new IllegalStateException("Seleccione una normalización");
        int n = dataPoints.size();
        if (n == 0) throw new IllegalStateException("No hay datos cargados");

        MyList<ColumnPlan> plans = new MyList<>();
        dimensionLabels = new MyList<>();
        int totalDims = 0;
        for (int j = 0; j < headers.size(); j++) {
            ColumnSetting setting = cfg.getColumns().get(j);
            if (setting.isIgnored()) continue;
            ColumnPlan plan = new ColumnPlan();
            plan.columnIndex = j;
            plan.type = setting.getType();
            plan.weight = setting.getWeight();
            plan.offset = totalDims;
            if (plan.type == ColumnType.NUMERIC) {
                plan.dims = 1;
                dimensionLabels.add(setting.getName());
            } else {
                MyList<String> categories = collectCategories(j, plan.type == ColumnType.MULTI_VALUE);
                plan.indexByValue = new MyHashMap<>();
                for (int c = 0; c < categories.size(); c++) {
                    plan.indexByValue.put(categories.get(c), c);
                    dimensionLabels.add(setting.getName() + "=" + categories.get(c));
                }
                plan.dims = categories.size();
            }
            totalDims += plan.dims;
            plans.add(plan);
        }
        if (totalDims == 0) throw new IllegalStateException("No hay variables activas para vectorizar");

        MyMatrix raw = new MyMatrix(n, totalDims);
        for (int p = 0; p < plans.size(); p++) fillColumns(plans.get(p), raw);

        NormalizationApplier.apply(cfg.getNormalizationStrategy(), raw, plans.size() == 0 ? null : weightsPerDimension(plans, totalDims));
        for (int i = 0; i < n; i++) dataPoints.get(i).setVectorData(new MyVector(raw.getRow(i)));
    }

    private double[] weightsPerDimension(MyList<ColumnPlan> plans, int totalDims) {
        double[] weights = new double[totalDims];
        for (int p = 0; p < plans.size(); p++) {
            ColumnPlan plan = plans.get(p);
            for (int d = 0; d < plan.dims; d++) weights[plan.offset + d] = plan.weight;
        }
        return weights;
    }

    private MyList<String> collectCategories(int column, boolean multiValue) {
        MyHashMap<Boolean> seen = new MyHashMap<>();
        MyList<String> categories = new MyList<>();
        for (int i = 0; i < dataPoints.size(); i++) {
            String v = dataPoints.get(i).getRawValue(column);
            if (v.isEmpty()) continue;
            if (multiValue) {
                MyList<String> tokens = TextUtils.splitWhitespace(v);
                for (int t = 0; t < tokens.size(); t++) addIfNew(seen, categories, tokens.get(t));
            } else {
                addIfNew(seen, categories, v);
            }
        }
        TextUtils.sortStrings(categories);
        return categories;
    }

    private void addIfNew(MyHashMap<Boolean> seen, MyList<String> list, String value) {
        if (!seen.containsKey(value)) { seen.put(value, true); list.add(value); }
    }

    private void fillColumns(ColumnPlan plan, MyMatrix raw) {
        int n = dataPoints.size();
        if (plan.type == ColumnType.NUMERIC) {
            double sum = 0;
            int count = 0;
            boolean[] missing = new boolean[n];
            for (int i = 0; i < n; i++) {
                String v = dataPoints.get(i).getRawValue(plan.columnIndex);
                if (TextUtils.isNumber(v)) {
                    double value = TextUtils.parseNumber(v);
                    raw.set(i, plan.offset, value);
                    sum += value;
                    count++;
                } else {
                    missing[i] = true;
                }
            }
            double mean = count == 0 ? 0 : sum / count;
            for (int i = 0; i < n; i++) if (missing[i]) raw.set(i, plan.offset, mean);
            return;
        }
        for (int i = 0; i < n; i++) {
            String v = dataPoints.get(i).getRawValue(plan.columnIndex);
            if (v.isEmpty()) continue;
            if (plan.type == ColumnType.MULTI_VALUE) {
                MyList<String> tokens = TextUtils.splitWhitespace(v);
                for (int t = 0; t < tokens.size(); t++) raw.set(i, plan.offset + plan.indexByValue.get(tokens.get(t)), 1);
            } else {
                raw.set(i, plan.offset + plan.indexByValue.get(v), 1);
            }
        }
    }

    public MyList<DataPoint> getDataPoints() { return dataPoints; }
    public MyList<String> getHeaders() { return headers; }
    public MyList<String> getDimensionLabels() { return dimensionLabels; }
    @Override public MyIterator<DataPoint> getIterator() { return new MyListIterator<>(dataPoints); }
}
