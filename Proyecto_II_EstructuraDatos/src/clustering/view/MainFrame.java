package clustering.view;

import clustering.controller.ClusteringController;
import clustering.model.entities.ColumnSetting;
import clustering.model.entities.ColumnType;
import clustering.model.entities.DataPoint;
import clustering.model.structures.MyList;
import clustering.model.structures.MyMatrix;
import clustering.model.structures.MyTreeNode;
import clustering.services.TextUtils;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Vista (MVC): ventana de escritorio. No contiene lógica de negocio; delega todo en el controlador. */
public class MainFrame extends JFrame {
    private static final int MAX_MATRIX_SIZE = 40;

    private final JComboBox<String> normalizationCombo;
    private final JComboBox<String> distanceCombo;
    private final JComboBox<String> nameColumnCombo = new JComboBox<>();
    private final ColumnTableModel columnModel = new ColumnTableModel();
    private final JTable columnTable = new JTable(columnModel);
    private final DendrogramPanel dendrogramPanel = new DendrogramPanel();
    private final JTextArea matrixArea = new JTextArea();
    private final JTextArea summaryArea = new JTextArea();
    private final JLabel statusLabel = new JLabel(" Cargue un archivo CSV para comenzar.");
    private boolean updatingNameCombo;

    public MainFrame(ClusteringController controller, String[] normalizations, String[] distances) {
        super("Clasificación jerárquica - Dendrograma");
        normalizationCombo = new JComboBox<>(normalizations);
        distanceCombo = new JComboBox<>(distances);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel();
        toolbar.add(button("Cargar CSV", () -> controller.onLoadCsv()));
        toolbar.add(button("Ejecutar clustering", () -> controller.onRun()));
        toolbar.add(button("Exportar JSON", () -> controller.onExport()));
        toolbar.add(button("Cargar JSON (visualizador)", () -> controller.onLoadJson()));
        toolbar.add(button("Zoom +", () -> dendrogramPanel.zoom(100)));
        toolbar.add(button("Zoom −", () -> dendrogramPanel.zoom(-100)));
        add(toolbar, BorderLayout.NORTH);

        nameColumnCombo.addActionListener(e -> {
            if (!updatingNameCombo) controller.onNameColumnChanged(nameColumnCombo.getSelectedIndex());
        });
        columnTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(ColumnType.values())));
        columnTable.setRowHeight(24);
        add(buildConfigPanel(), BorderLayout.WEST);

        matrixArea.setEditable(false);
        matrixArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dendrograma", new JScrollPane(dendrogramPanel));
        tabs.addTab("Matriz de distancias", new JScrollPane(matrixArea));
        tabs.addTab("Resumen", new JScrollPane(summaryArea));
        add(tabs, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel buildConfigPanel() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Normalización:"));
        form.add(normalizationCombo);
        form.add(new JLabel("Distancia:"));
        form.add(distanceCombo);
        form.add(new JLabel("Columna de etiqueta:"));
        form.add(nameColumnCombo);

        JLabel hint = new JLabel("<html><i>Hamming: úsela solo con variables categóricas (binarias).<br>"
                + "Tipo, Ignorar y Peso se editan en la tabla.</i></html>");
        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.add(form, BorderLayout.NORTH);
        top.add(hint, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Configuración"));
        panel.setPreferredSize(new Dimension(430, 0));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(columnTable), BorderLayout.CENTER);
        return panel;
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        return b;
    }

    // ----- API usada por el controlador -----

    public void showColumns(MyList<ColumnSetting> settings, MyList<String> headers, int nameColumn) {
        columnModel.setSettings(settings);
        updatingNameCombo = true;
        nameColumnCombo.removeAllItems();
        for (int i = 0; i < headers.size(); i++) nameColumnCombo.addItem(headers.get(i));
        nameColumnCombo.setSelectedIndex(nameColumn);
        updatingNameCombo = false;
    }

    public void refreshColumns() { columnModel.fireTableDataChanged(); }

    public void commitEdits() {
        if (columnTable.isEditing()) columnTable.getCellEditor().stopCellEditing();
    }

    public String getSelectedNormalization() { return (String) normalizationCombo.getSelectedItem(); }

    public String getSelectedDistance() { return (String) distanceCombo.getSelectedItem(); }

    public void setDendrogram(MyTreeNode root) { dendrogramPanel.setRoot(root); }

    public void setSummary(String text) { summaryArea.setText(text); summaryArea.setCaretPosition(0); }

    public void setStatus(String text) { statusLabel.setText(" " + text); }

    public void setWaiting(boolean waiting) { setCursor(Cursor.getPredefinedCursor(waiting ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR)); }

    public void showError(String message) { JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); }

    public String askOpenFile(String description, String extension) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        return chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().getAbsolutePath() : null;
    }

    public String askSaveFile(String defaultName) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setSelectedFile(new java.io.File(defaultName));
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON", "json"));
        return chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().getAbsolutePath() : null;
    }

    public void setDistanceMatrix(MyMatrix matrix, MyList<DataPoint> points) {
        if (matrix == null) { matrixArea.setText(""); return; }
        int shown = Math.min(matrix.rows(), MAX_MATRIX_SIZE);
        StringBuilder sb = new StringBuilder();
        sb.append(pad("", 6));
        for (int j = 0; j < shown; j++) sb.append(pad("#" + (j + 1), 9));
        sb.append('\n');
        for (int i = 0; i < shown; i++) {
            sb.append(pad("#" + (i + 1), 6));
            for (int j = 0; j < shown; j++) sb.append(pad(Double.toString(TextUtils.round2(matrix.get(i, j))), 9));
            sb.append("  ").append(points.get(i).getName()).append('\n');
        }
        if (shown < matrix.rows()) sb.append("\n(Se muestran ").append(shown).append(" de ").append(matrix.rows()).append(" filas)\n");
        matrixArea.setText(sb.toString());
        matrixArea.setCaretPosition(0);
    }

    private String pad(String text, int width) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}
