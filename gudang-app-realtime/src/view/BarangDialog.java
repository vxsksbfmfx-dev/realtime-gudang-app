package view;

import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import model.Barang;
import net.miginfocom.swing.MigLayout;

public class BarangDialog extends JDialog {

    private final JTextField namaBarangField = new JTextField(25);
    private final JTextField kategoriField = new JTextField(25);
    private final JTextField stokField = new JTextField(25);
    private final JTextField tanggalMasukField = new JTextField(25);
    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");

    private Barang barang;

    public BarangDialog(JFrame owner) {
        super(owner, "Add New Barang", true);
        this.barang = new Barang();
        setupComponents();
        tanggalMasukField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    public BarangDialog(JFrame owner, Barang barangToEdit) {
        super(owner, "Edit Barang", true);
        this.barang = barangToEdit;
        setupComponents();
        
        namaBarangField.setText(barangToEdit.getNamaBarang());
        kategoriField.setText(barangToEdit.getKategori());
        stokField.setText(String.valueOf(barangToEdit.getStok()));
        if (barangToEdit.getTanggalMasuk() != null) {
            tanggalMasukField.setText(barangToEdit.getTanggalMasuk().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    private void setupComponents() {
        setLayout(new MigLayout("fill, insets 30", "[right]20[grow]"));
        add(new JLabel("Nama Barang"), "");
        add(namaBarangField, "growx, wrap");
        add(new JLabel("Kategori"), "");
        add(kategoriField, "growx, wrap");
        add(new JLabel("Stok"), "");
        add(stokField, "growx, wrap");
        add(new JLabel("Tanggal Masuk (YYYY-MM-DD)"), "");
        add(tanggalMasukField, "growx, wrap");

        saveButton.setBackground(UIManager.getColor("Button.default.background"));
        saveButton.setForeground(UIManager.getColor("Button.default.foreground"));
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));

        JPanel buttonPanel = new JPanel(new MigLayout("", "[]10[]"));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, "span, right");

        pack();
        setMinimumSize(new Dimension(500, 450));
        setLocationRelativeTo(getOwner());
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public Barang getBarang() {
        barang.setNamaBarang(namaBarangField.getText().trim());
        barang.setKategori(kategoriField.getText().trim());
        try {
            barang.setStok(Integer.parseInt(stokField.getText().trim()));
        } catch (NumberFormatException e) {
            barang.setStok(0);
        }
        try {
            barang.setTanggalMasuk(LocalDate.parse(tanggalMasukField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (Exception e) {
            barang.setTanggalMasuk(LocalDate.now());
        }
        return barang;
    }
}