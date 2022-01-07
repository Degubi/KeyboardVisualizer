package visualizer.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.*;
import javax.swing.*;
import visualizer.utils.*;

public final class KeyboardSelectionScreen extends JPanel {
    private static final Font INFO_FONT = new Font("Arial", Font.BOLD, 32);

    public static final int CANCELED_KEYBOARD_HANDLE = 0;

    KeyboardSelectionScreen() {}


    public static long showAndGet() {
        var frame = new JFrame();
        frame.setContentPane(new KeyboardSelectionScreen());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 128));
        frame.setIconImage(GuiUtils.appIcon);
        frame.setVisible(true);

        var handle = new AtomicLong(-1);

        NativeUtils.keyboardSelectionListener = (keyCode, deviceHandle) -> {
            NativeUtils.keyboardSelectionListener = NativeUtils.INACTIVE_LISTENER;

            frame.dispose();
            handle.set(keyCode == KeyEvent.VK_ESCAPE ? CANCELED_KEYBOARD_HANDLE : deviceHandle);
        };

        while(handle.get() == -1) {
            NativeUtils.sleepMs(100);
        }

        return handle.get();
    }


    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var centerWidth = GuiUtils.screenWidth / 2;
        var centerHeight = GuiUtils.screenHeight / 2;

        graphics.setColor(Color.WHITE);
        graphics.setFont(INFO_FONT);
        graphics.drawString("Press a key on the keyboard!", centerWidth - 256, centerHeight);
    }
}