package worker.barang;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BarangApiClient;
import model.Barang;
import view.BarangFrame;

public class DeleteBarangWorker extends SwingWorker<Void, Void> {
    private final BarangFrame frame;
    private final BarangApiClient barangApiClient;
    private final Barang barang;

    public DeleteBarangWorker(BarangFrame frame, BarangApiClient barangApiClient, Barang barang) {
        this.frame = frame;
        this.barangApiClient = barangApiClient;
        this.barang = barang;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Deleting barang record...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        barangApiClient.delete(barang.getIdBarang());
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            get();
            frame.getProgressBar().setString("Barang deleted successfully");
            JOptionPane.showMessageDialog(frame,
                    "Barang record has been deleted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to delete barang");
            JOptionPane.showMessageDialog(frame,
                    "Error deleting data: \n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}