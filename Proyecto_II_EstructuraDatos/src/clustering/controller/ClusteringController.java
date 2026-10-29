package clustering.controller;

import clustering.model.ClusteringModel;
import clustering.model.Configuration;
import clustering.model.factories.DistanceFactory;
import clustering.model.factories.NormalizationFactory;
import clustering.model.structures.MyTreeNode;
import clustering.view.MainFrame;
import javax.swing.SwingUtilities;

/** Controlador (MVC): traduce los eventos de la ventana en operaciones del modelo y actualiza la vista. */
public class ClusteringController {
    private final ClusteringModel model;
    private MainFrame view;

    public ClusteringController(ClusteringModel model) { this.model = model; }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            view = new MainFrame(this,
                    new NormalizationFactory().getAvailableTypes(),
                    new DistanceFactory().getAvailableTypes());
            view.setVisible(true);
        });
    }

    public void onLoadCsv() {
        String path = view.askOpenFile("Archivos CSV", "csv");
        if (path == null) return;
        try {
            model.loadCsv(path);
            view.showColumns(Configuration.getInstance().getColumns(), model.getHeaders(),
                    Configuration.getInstance().getNameColumnIndex());
            view.setDendrogram(null);
            view.setDistanceMatrix(null, null);
            view.setSummary("Archivo: " + path + "\nRegistros: " + model.getDataPoints().size()
                    + "\nVariables: " + model.getHeaders().size()
                    + "\n\nAjuste tipos, pesos y variables ignoradas, y presione «Ejecutar clustering».");
            view.setStatus("CSV cargado: " + model.getDataPoints().size() + " registros");
        } catch (Exception ex) {
            view.showError("No se pudo cargar el CSV: " + ex.getMessage());
        }
    }

    public void onNameColumnChanged(int column) {
        if (!model.isDataLoaded() || column < 0) return;
        model.setNameColumn(column);
        view.refreshColumns();
    }

    public void onRun() {
        if (!model.isDataLoaded()) { view.showError("Primero cargue un archivo CSV."); return; }
        try {
            view.commitEdits();
            model.configureStrategies(view.getSelectedNormalization(), view.getSelectedDistance());
            view.setWaiting(true);
            model.runClustering();
            MyTreeNode root = model.getDisplayedRoot();
            view.setDendrogram(root);
            view.setDistanceMatrix(model.getDistanceMatrix(), model.getDataPoints());
            Configuration cfg = Configuration.getInstance();
            view.setSummary("Registros: " + model.getDataPoints().size()
                    + "\nDimensión del vector: " + model.getDimension()
                    + "\nNormalización: " + cfg.getNormalizationStrategy().getName()
                    + "\nDistancia: " + cfg.getDistanceStrategy().getName()
                    + "\nNodos del árbol: " + (2 * model.getDataPoints().size() - 1)
                    + "\nDistancia de la última fusión: " + root.getDistance()
                    + "\n\nDimensiones:\n" + dimensionList());
            view.setStatus("Clustering completado");
        } catch (Exception ex) {
            view.showError("Error al ejecutar el clustering: " + ex.getMessage());
        } finally {
            view.setWaiting(false);
        }
    }

    public void onExport() {
        if (model.getDisplayedRoot() == null) { view.showError("Ejecute el clustering o cargue un JSON primero."); return; }
        String path = view.askSaveFile("dendrogram.json");
        if (path == null) return;
        try {
            model.exportDendrogram(path);
            view.setStatus("Exportado a " + path);
        } catch (Exception ex) {
            view.showError("No se pudo exportar: " + ex.getMessage());
        }
    }

    public void onLoadJson() {
        String path = view.askOpenFile("Archivos JSON", "json");
        if (path == null) return;
        try {
            view.setDendrogram(model.loadDendrogram(path));
            view.setStatus("Dendrograma cargado desde " + path);
        } catch (Exception ex) {
            view.showError("No se pudo cargar el JSON: " + ex.getMessage());
        }
    }

    private String dimensionList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < model.getDimensionLabels().size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(model.getDimensionLabels().get(i)).append('\n');
        }
        return sb.toString();
    }
}
