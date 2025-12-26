import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

import controller.BarangController;
import view.BarangFrame;

public class GudangApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
        } catch (Exception ex) {
            System.err.println("Gagal mengatur tema FlatLaf");
            ex.printStackTrace();
        }   

        SwingUtilities.invokeLater(() -> {
            BarangFrame frame = new BarangFrame();
            new BarangController(frame);
            frame.setVisible(true);
        });
    }

}