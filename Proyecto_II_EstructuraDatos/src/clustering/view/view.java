package clustering.view;

import javax.swing.*;

import clustering.*;
import clustering.controller.ClusteringController;
import clustering.model.ClusteringModel;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

import java.awt.Cursor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import clustering.model.entities.ColumnSetting;
import clustering.model.entities.ColumnType;
import clustering.model.entities.DataPoint;
import clustering.model.structures.MyList;
import clustering.model.structures.MyMatrix;
import clustering.model.structures.MyTreeNode;
import clustering.services.TextUtils;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class view extends JFrame {
    private JButton cargarCSVButton;
    private JButton ejecutarClusteringButton;
    private JButton exportarJSONButton;
    private JButton cargarJSONButton;
    private JButton zoomButton;
    private JButton zoomButton1;
    private JComboBox normalizacionCB;
    private JComboBox distanciaCB;
    private JComboBox metodoCB;
    private JComboBox columnaCB;
    private JTable informacionTable;
    private JTabbedPane tabbedPane1;
    private JTextArea matrizArea;
    private JTextArea resumenArea;
    private JPanel mainPanel;
    private JPanel dendrogramaPanel;

    private final ColumnTableModel columnModel = new ColumnTableModel();
    private final DendrogramPanel dendrogramPanel = new DendrogramPanel();

    private boolean updatingNameCombo = false;

    private static final int MAX_MATRIX_SIZE = 40;


    public view(ClusteringController controller) {

        setTitle("Clasificación jerárquica - Dendrograma");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);

        // Configuración de la tabla
        informacionTable.setModel(columnModel);
        informacionTable.setRowHeight(24);

        // Configuración de las áreas de texto
        matrizArea.setEditable(false);
        resumenArea.setEditable(false);

        matrizArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resumenArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        dendrogramaPanel.setLayout(new BorderLayout());
        dendrogramaPanel.add(dendrogramPanel, BorderLayout.CENTER);

        normalizacionCB.setModel(new DefaultComboBoxModel<>(new String[]{"Minimo - Maximo", "Z-Score", "Logaritmica"}));

        distanciaCB.setModel(new DefaultComboBoxModel<>(new String[]{"Euclidiana", "Manhattan", "Coseno", "Hamming"}));

        metodoCB.setModel(new DefaultComboBoxModel<>(new String[]{"Enlace Simple", "Enlace Promedio"}));

        columnaCB.removeAllItems();
        columnaCB.addItem("Seleccione una columna");

        informacionTable.setModel(columnModel);
        informacionTable.setRowHeight(24);

        informacionTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(ColumnType.values())));

        columnaCB.addActionListener(e -> {
            if (!updatingNameCombo) {
                controller.onNameColumnChanged(columnaCB.getSelectedIndex());
            }
        });

        cargarCSVButton.addActionListener(e -> {
            controller.onLoadCsv();
        });

        ejecutarClusteringButton.addActionListener(e -> {
            controller.onRun();
        });

        exportarJSONButton.addActionListener(e -> {
            controller.onExport();
        });

        cargarJSONButton.addActionListener(e -> {
            controller.onLoadJson();
        });

        zoomButton.addActionListener(e -> {
            dendrogramPanel.zoom(100);
        });

        zoomButton1.addActionListener(e -> {
            dendrogramPanel.zoom(-100);
        });
    }


    // Mostrar las columnas del archivo CSV
    public void showColumns(
            MyList<ColumnSetting> settings,
            MyList<String> headers,
            int nameColumn) {

        columnModel.setSettings(settings);

        updatingNameCombo = true;

        columnaCB.removeAllItems();

        for (int i = 0; i < headers.size(); i++) {
            columnaCB.addItem(headers.get(i));
        }

        columnaCB.setSelectedIndex(nameColumn);

        updatingNameCombo = false;
    }

    // Actualizar la tabla
    public void refreshColumns() {
        columnModel.fireTableDataChanged();
    }

    // Guardar los cambios realizados en la tabla
    public void commitEdits() {
        if (informacionTable.isEditing()) {
            informacionTable.getCellEditor().stopCellEditing();
        }
    }

    // Obtener la normalización seleccionada
    public String getSelectedNormalization() {
        return (String) normalizacionCB.getSelectedItem();
    }

    // Obtener la distancia seleccionada
    public String getSelectedDistance() {
        return (String) distanciaCB.getSelectedItem();
    }

    public String getSelectedLinkageMethod() {
        return (String) metodoCB.getSelectedItem();
    }

    // Mostrar el dendrograma
    public void setDendrogram(MyTreeNode root) {
        dendrogramPanel.setRoot(root);
    }

    // Mostrar el resumen
    public void setSummary(String text) {
        resumenArea.setText(text);
        resumenArea.setCaretPosition(0);
    }

    // Mostrar u ocultar el cursor de espera
    public void setWaiting(boolean waiting) {
        setCursor(Cursor.getPredefinedCursor(
                waiting ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR
        ));
    }

    // Mostrar errores
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Seleccionar un archivo
    public String askOpenFile(String description, String extension) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileFilter(
                new FileNameExtensionFilter(description, extension)
        );

        return chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
                ? chooser.getSelectedFile().getAbsolutePath()
                : null;
    }

    // Seleccionar dónde guardar el JSON
    public String askSaveFile(String defaultName) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setSelectedFile(new File(defaultName));

        chooser.setFileFilter(
                new FileNameExtensionFilter("Archivos JSON", "json")
        );

        return chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION
                ? chooser.getSelectedFile().getAbsolutePath()
                : null;
    }

    // Mostrar la matriz de distancias
    public void setDistanceMatrix(
            MyMatrix matrix,
            MyList<DataPoint> points) {

        if (matrix == null) {
            matrizArea.setText("");
            return;
        }

        int maxMatrixSize = 40;
        int shown = Math.min(matrix.rows(), maxMatrixSize);

        StringBuilder sb = new StringBuilder();

        sb.append(pad("", 6));

        for (int j = 0; j < shown; j++) {
            sb.append(pad("#" + (j + 1), 9));
        }

        sb.append('\n');

        for (int i = 0; i < shown; i++) {
            sb.append(pad("#" + (i + 1), 6));

            for (int j = 0; j < shown; j++) {
                sb.append(pad(
                        Double.toString(TextUtils.round2(matrix.get(i, j))),
                        9
                ));
            }

            sb.append("  ")
                    .append(points.get(i).getName())
                    .append('\n');
        }

        if (shown < matrix.rows()) {
            sb.append("\n(Se muestran ")
                    .append(shown)
                    .append(" de ")
                    .append(matrix.rows())
                    .append(" filas)\n");
        }

        matrizArea.setText(sb.toString());
        matrizArea.setCaretPosition(0);
    }

    // Alinear los datos de la matriz
    private String pad(String text, int width) {
        StringBuilder sb = new StringBuilder(text);

        while (sb.length() < width) {
            sb.append(' ');
        }

        return sb.toString();
    }



}
