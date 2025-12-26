package worker.barang;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BarangApiClient;
import model.Barang;
import view.BarangFrame;

public class LoadBarangWorker extends SwingWorker<List<Barang>, Void> {
    private final BarangFrame frame;
    private final BarangApiClient barangApiClient;

    public LoadBarangWorker(BarangFrame frame, BarangApiClient barangApiClient) {
        this.frame = frame;
        this.barangApiClient = barangApiClient;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Loading barang data...");
    }

    @Override
    protected List<Barang> doInBackground() throws Exception {
        return barangApiClient.findAll();
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            List<Barang> result = get();
            frame.getProgressBar().setString(result.size() + " records loaded");
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to load data");
            JOptionPane.showMessageDialog(frame,
                    "Error loading data: \n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}