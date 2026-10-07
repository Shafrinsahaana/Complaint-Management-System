package ui;

import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatLightLaf;

/** Starts the citizen registration screen. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        Theme.setupGlobal();
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setIconImage(Theme.getAppIcon());
            frame.setVisible(true);
        });
    }
}
