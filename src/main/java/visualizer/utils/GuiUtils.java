package visualizer.utils;

import java.awt.*;
import java.awt.event.*;
import visualizer.*;

public final class GuiUtils {
    public static final Image appIcon;
    public static final int screenWidth;
    public static final int screenHeight;

    static {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        appIcon = Toolkit.getDefaultToolkit().createImage(Main.class.getResource("/assets/icon.png"));
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
    }

    public static WindowListener newWindowClosedListener(Runnable onClose) {
        return new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) { onClose.run(); }
            @Override public void windowActivated(WindowEvent e) {}
        };
    }
}