package clustering.view;

import clustering.model.entities.ColumnSetting;
import clustering.model.entities.ColumnType;
import clustering.model.structures.MyList;
import javax.swing.table.AbstractTableModel;

/** Tabla editable: tipo, ignorar y peso de cada variable. Edita directamente los ColumnSetting del modelo. */
public class ColumnTableModel extends AbstractTableModel {
    private static final String[] NAMES = { "Variable", "Tipo", "Ignorar", "Peso" };
    private MyList<ColumnSetting> settings = new MyList<>();

    public void setSettings(MyList<ColumnSetting> settings) {
        this.settings = settings;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return settings.size(); }
    @Override public int getColumnCount() { return NAMES.length; }
    @Override public String getColumnName(int c) { return NAMES[c]; }

    @Override public Class<?> getColumnClass(int c) {
        switch (c) {
            case 1: return ColumnType.class;
            case 2: return Boolean.class;
            case 3: return Double.class;
            default: return String.class;
        }
    }

    @Override public boolean isCellEditable(int row, int col) { return col > 0; }

    @Override public Object getValueAt(int row, int col) {
        ColumnSetting s = settings.get(row);
        switch (col) {
            case 0: return s.getName();
            case 1: return s.getType();
            case 2: return s.isIgnored();
            default: return s.getWeight();
        }
    }

    @Override public void setValueAt(Object value, int row, int col) {
        ColumnSetting s = settings.get(row);
        try {
            if (col == 1) s.setType((ColumnType) value);
            else if (col == 2) s.setIgnored((Boolean) value);
            else if (col == 3) s.setWeight((Double) value);
        } catch (IllegalArgumentException ignoredInvalidValue) {
            // se conserva el valor anterior si el peso es inválido
        }
        fireTableRowsUpdated(row, row);
    }
}
