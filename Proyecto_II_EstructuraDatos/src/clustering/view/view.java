package clustering.view;

import javax.swing.*;

import clustering.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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


    public view() {

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

        // Agregar el dendrograma al panel visual
        dendrogramaPanel.setLayout(new BorderLayout());
        dendrogramaPanel.add(dendrogramPanel, BorderLayout.CENTER);

        normalizacionCB.setModel(new DefaultComboBoxModel<>(new String[]{
                "Mínimo-Máximo",
                "Puntuación Z (Z-Score)",
                "Logarítmica"
        }));

        distanciaCB.setModel(new DefaultComboBoxModel<>(new String[]{
                "Euclidiana",
                "Manhattan",
                "Coseno",
                "Hamming"
        }));

        metodoCB.setModel(new DefaultComboBoxModel<>(new String[]{
                "Enlace simple",
                "Enlace promedio"
        }));

        columnaCB.removeAllItems();
        columnaCB.addItem("Seleccione una columna");
    }


}
