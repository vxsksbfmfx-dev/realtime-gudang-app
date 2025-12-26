package worker.barang;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BarangApiClient;
import model.Barang;
import view.BarangFrame;

public class SaveBarangWorker extends SwingWorker<Void, Void> {
    private final BarangFrame frame;
    private final BarangApiClient barangApiClient;
    private final Barang barang;

    public SaveBarangWorker(BarangFrame frame, BarangApiClient barangApiClient, Barang barang) {
        this.frame = frame;
        this.barangApiClient = barangApiClient;
        this.barang = barang;
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Saving new barang...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        barangApiClient.create(barang);
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            get(); // To catch any exception
            frame.getProgressBar().setString("Barang saved successfully");
            JOptionPane.showMessageDialog(frame,
                    "New barang record has been saved.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to save barang");
            JOptionPane.showMessageDialog(frame,
                    "Error saving data: \n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}