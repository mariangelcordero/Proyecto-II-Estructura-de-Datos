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

/** Fachada del modelo (MVC): orquesta carga, configuración, vectorización, clustering y archivos JSON. */
public class ClusteringModel {
    private final DataProcessor processor = new DataProcessor();
    private final NormalizationFactory normalizationFactory = new NormalizationFactory();
    private final DistanceFactory distanceFactory = new DistanceFactory();
    private HierarchicalClustering clustering;
    private MyTreeNode displayedRoot;
    private boolean vectorized;

    public void loadCsv(String path) throws IOException {
        processor.loadCsv(path);
        Configuration cfg = Configuration.getInstance();
        cfg.reset();
        MyList<ColumnSetting> settings = processor.detectColumnSettings();
        cfg.setColumns(settings);
        int nameColumn = guessNameColumn();
        cfg.setNameColumnIndex(nameColumn);
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
        Configuration cfg = Configuration.getInstance();
        cfg.setNameColumnIndex(column);
        cfg.getColumns().get(column).setIgnored(true);
        processor.applyNameColumn(column);
    }

    public void configureStrategies(String normalizationType, String distanceType) {
        Configuration cfg = Configuration.getInstance();
        cfg.setNormalizationStrategy(normalizationFactory.create(normalizationType));
        cfg.setDistanceStrategy(distanceFactory.create(distanceType));
    }

    /** Ejecuta vectorización + matriz de distancias + clustering. */
    public void runClustering() {
        if (!isDataLoaded()) throw new IllegalStateException("Primero cargue un archivo CSV");
        Configuration cfg = Configuration.getInstance();
        processor.convertToVectors();
        vectorized = true;
        clustering = new HierarchicalClustering(processor.getDataPoints(), cfg.getDistanceStrategy());
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
