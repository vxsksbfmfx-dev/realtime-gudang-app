package view.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Barang;

public class BarangTableModel extends AbstractTableModel {
    private List<Barang> barangList = new ArrayList<>();
    private final String[] columnNames = { "ID", "Nama Barang", "Kategori", "Stok", "Tanggal Masuk" };

    public void setBarangList(List<Barang> barangList) {
        this.barangList = barangList;
        fireTableDataChanged();
    }

    public Barang getBarangAt(int rowIndex) {
        return barangList.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return barangList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Barang barang = barangList.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> barang.getIdBarang();
            case 1 -> barang.getNamaBarang();
            case 2 -> barang.getKategori();
            case 3 -> barang.getStok();
            case 4 -> barang.getTanggalMasuk();
            default -> null;
        };
    }
}