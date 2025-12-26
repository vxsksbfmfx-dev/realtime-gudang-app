package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import api.BarangApiClient;
import api.WebSocketClientHandler;
import model.Barang;
import view.BarangDialog;
import view.BarangFrame;
import worker.barang.DeleteBarangWorker;
import worker.barang.LoadBarangWorker;
import worker.barang.SaveBarangWorker;
import worker.barang.UpdateBarangWorker;

public class BarangController {
    private final BarangFrame frame;
    private final BarangApiClient barangApiClient = new BarangApiClient();

    private List<Barang> allBarang = new ArrayList<>();
    private List<Barang> displayedBarang = new ArrayList<>();

    private WebSocketClientHandler wsClient;

    public BarangController(BarangFrame frame) {
        this.frame = frame;
        setupEventListeners();
        setupWebSocket();
        loadAllBarang();
    }

    private void setupWebSocket() {
        try {
            URI uri = new URI("ws://localhost:3000");
            wsClient = new WebSocketClientHandler(uri, new Consumer<String>() {
                @Override
                public void accept(String message) {
                    System.out.println("Received real-time update: " + message);
                    handleWebSocketMessage(message);
                }
            });
            wsClient.connect();
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(frame, "Failed to connect to real-time server: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWebSocketMessage(String message) {
        // Parse JSON jika dibutuhkan, untuk kasus ini setiap message yang diterima akan men-trigger loadAllBarang
        
        // Run refresh di Swing thread (EDT)
        SwingUtilities.invokeLater(() -> loadAllBarang());
    }

    private void setupEventListeners() {
        frame.getAddButton().addActionListener(e -> openBarangDialog(null));
        frame.getRefreshButton().addActionListener(e -> loadAllBarang());
        frame.getDeleteButton().addActionListener(e -> deleteSelectedBarang());
        frame.getBarangTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = frame.getBarangTable().getSelectedRow();
                    if (selectedRow >= 0) {
                        openBarangDialog(displayedBarang.get(selectedRow));
                    }
                }
            }
        });
        frame.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            private void applySearchFilter() {
                String keyword = frame.getSearchField().getText().toLowerCase().trim();
                displayedBarang = new ArrayList<>();
                for (Barang barang : allBarang) {
                    if (barang.getNamaBarang().toLowerCase().contains(keyword) ||
                            (barang.getKategori() != null && barang.getKategori().toLowerCase().contains(keyword))) {
                        displayedBarang.add(barang);
                    }
                }
                frame.getBarangTableModel().setBarangList(displayedBarang);
                updateTotalRecordsLabel();
            }
        });
    }

    private void openBarangDialog(Barang barangToEdit) {
        BarangDialog dialog;
        if (barangToEdit == null) {
            dialog = new BarangDialog(frame);
        } else {
            dialog = new BarangDialog(frame, barangToEdit);
        }
        dialog.getSaveButton().addActionListener(e -> {
            Barang barang = dialog.getBarang();
            SwingWorker<Void, Void> worker;
            if (barangToEdit == null) {
                worker = new SaveBarangWorker(frame, barangApiClient, barang);
            } else {
                worker = new UpdateBarangWorker(frame, barangApiClient, barang);
            }
            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    dialog.dispose();
                    loadAllBarang();
                }
            });
            worker.execute();
        });
        dialog.setVisible(true);
    }

    private void deleteSelectedBarang() {
        int selectedRow = frame.getBarangTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(frame, "Please select a record to delete.");
            return;
        }
        Barang barang = displayedBarang.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Delete barang: " + barang.getNamaBarang() + " (Stok: " + barang.getStok() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DeleteBarangWorker worker = new DeleteBarangWorker(frame, barangApiClient, barang);
            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    loadAllBarang();
                }
            });
            worker.execute();
        }
    }

    private void loadAllBarang() {
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Loading data...");
        LoadBarangWorker worker = new LoadBarangWorker(frame, barangApiClient);
        worker.addPropertyChangeListener(evt -> {
            if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                try {
                    allBarang = worker.get();
                    displayedBarang = new ArrayList<>(allBarang);
                    frame.getBarangTableModel().setBarangList(displayedBarang);
                    updateTotalRecordsLabel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to load data.");
                } finally {
                    frame.getProgressBar().setIndeterminate(false);
                    frame.getProgressBar().setString("Ready");
                }
            }
        });
        worker.execute();
    }

    private void updateTotalRecordsLabel() {
        frame.getTotalRecordsLabel().setText(displayedBarang.size() + " Records");
    }
}