package clustering.model;

import clustering.model.entities.ColumnSetting;
import clustering.model.entities.DataPoint;
import clustering.model.factories.DistanceFactory;
import clustering.model.factories.NormalizationFactory;
import clustering.model.structures.MyList;
import clustering.model.structures.MyMatrix;
import clustering.model.structures.MyTreeNode;
import clustering.services.DataProcessor;
import clustering.services.DendrogramExporter;
import clustering.services.DendrogramReader;
import clustering.services.HierarchicalClustering;
import java.io.IOException;

public class ClusteringModel {
    private final DataProcessor processor = new DataProcessor();
    private final NormalizationFactory normalizationFactory = new NormalizationFactory();
    private final DistanceFactory distanceFactory = new DistanceFactory();
    private HierarchicalClustering clustering;
    private MyTreeNode displayedRoot;
    private boolean vectorized;
    private boolean nameColumnWasIgnored;
    private String linkageMethod = "Enlace Simple";

    public void loadCsv(String path) throws IOException {
        processor.loadCsv(path);
        Configuration cfg = Configuration.getInstance();
        cfg.reset();
        MyList<ColumnSetting> settings = processor.detectColumnSettings();
        cfg.setColumns(settings);
        int nameColumn = guessNameColumn();
        cfg.setNameColumnIndex(nameColumn);
        nameColumnWasIgnored = settings.get(nameColumn).isIgnored();
        settings.get(nameColumn).setIgnored(true);
        processor.applyNameColumn(nameColumn);
        clustering = null;
        displayedRoot = null;
        vectorized = false;
    }

    private int guessNameColumn() {
        MyList<String> headers = processor.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i).toLowerCase();
            if (h.equals("original_title") || h.equals("title") || h.equals("name") || h.equals("nombre")) return i;
        }
        return 0;
    }


    public void setNameColumn(int column) {
        // Obtener la configuración general del programa
        Configuration cfg = Configuration.getInstance();

        // Obtener la lista de columnas del CSV
        MyList<ColumnSetting> columns = cfg.getColumns();

        // Verificar que la columna seleccionada exista
        if (column < 0 || column >= columns.size()) {
            throw new IllegalArgumentException("La columna seleccionada no existe");
        }

        // Guardar el índice de la columna anterior
        int previous = cfg.getNameColumnIndex();

        // Si seleccionamos la misma columna, no hacemos cambios
        if (column == previous) {
            return;
        }

        // Restaurar el estado de la columna anterior
        if (previous >= 0 && previous < columns.size()) {
            columns.get(previous).setIgnored(nameColumnWasIgnored);
        }

        // Guardar el estado original de la nueva columna
        nameColumnWasIgnored = columns.get(column).isIgnored();

        // Ignorar la columna que contiene los nombres
        columns.get(column).setIgnored(true);

        // Actualizar la configuración
        cfg.setNameColumnIndex(column);

        // Actualizar los nombres de los elementos
        processor.applyNameColumn(column);

        // Invalidar los resultados anteriores
        vectorized = false;
        clustering = null;
        displayedRoot = null;
    }

    public void configureStrategies(String normalizationType,
                                    String distanceType,
                                    String linkageMethod) {

        Configuration cfg = Configuration.getInstance();

        cfg.setNormalizationStrategy(
                normalizationFactory.create(normalizationType)
        );

        cfg.setDistanceStrategy(
                distanceFactory.create(distanceType)
        );

        this.linkageMethod = linkageMethod;
    }

    public void runClustering() {
        if (!isDataLoaded()) throw new IllegalStateException("Primero cargue un archivo CSV");
        Configuration cfg = Configuration.getInstance();
        processor.convertToVectors();
        vectorized = true;
        clustering = new HierarchicalClustering(processor.getDataPoints(), cfg.getDistanceStrategy(), linkageMethod);
        clustering.buildDistanceMatrix();
        displayedRoot = clustering.performClustering();
    }

    public void exportDendrogram(String path) throws IOException {
        if (displayedRoot == null) throw new IllegalStateException("No hay un dendrograma para exportar");
        new DendrogramExporter().export(displayedRoot, path);
    }

    public MyTreeNode loadDendrogram(String path) throws IOException {
        displayedRoot = new DendrogramReader().read(path);
        return displayedRoot;
    }

    public boolean isDataLoaded() { return processor.getDataPoints().size() > 0; }
    public boolean isVectorized() { return vectorized; }
    public int getDimension() { return vectorized ? processor.getDataPoints().get(0).getVectorData().size() : 0; }
    public MyList<String> getHeaders() { return processor.getHeaders(); }
    public MyList<DataPoint> getDataPoints() { return processor.getDataPoints(); }
    public MyMatrix getDistanceMatrix() { return clustering == null ? null : clustering.getDistanceMatrix(); }
    public MyTreeNode getDisplayedRoot() { return displayedRoot; }
    public MyList<String> getDimensionLabels() { return processor.getDimensionLabels(); }
}
