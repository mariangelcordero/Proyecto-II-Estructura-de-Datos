package clustering.controller;

import clustering.model.ClusteringModel;
import clustering.model.Configuration;
import clustering.model.structures.MyTreeNode;
import clustering.view.view;
import javax.swing.SwingUtilities;

public class ClusteringController {
    private final ClusteringModel model;
    private view view;


    public ClusteringController(ClusteringModel model) { this.model = model; }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            view = new view(this);
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
            model.configureStrategies(view.getSelectedNormalization(), view.getSelectedDistance(), view.getSelectedLinkageMethod());
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
                    + "\nMetodo de enlace: " + view.getSelectedLinkageMethod()
                    + "\nNodos del árbol: " + (2 * model.getDataPoints().size() - 1)
                    + "\nDistancia de la última fusión: " + root.getDistance()
                    + "\n\nDimensiones:\n" + dimensionList());
        } catch (Exception ex) {
            view.showError("Error al ejecutar el clustering: " + ex.getMessage());
        } finally {
            view.setWaiting(false);
        }
    }

    public void onExport() {
        if (model.getDisplayedRoot() == null) { view.showError("Ejecute el clustering o cargue un JSON primero."); return; }
        String path = view.askSaveFile("dendrograma.json");
        if (path == null) return;
        try {
            model.exportDendrogram(path);
        } catch (Exception ex) {
            view.showError("No se pudo exportar: " + ex.getMessage());
        }
    }

    public void onLoadJson() {
        String path = view.askOpenFile("Archivos JSON", "json");
        if (path == null) return;
        try {
            view.setDendrogram(model.loadDendrogram(path));
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
