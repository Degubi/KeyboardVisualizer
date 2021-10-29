package visualizer.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.*;
import javax.swing.*;
import visualizer.*;
import visualizer.model.*;

public final class GuiUtils {
    public static final Image appIcon;
    public static final int screenWidth;
    public static final int screenHeight;
    public static final int CANCELED_KEYBOARD_HANDLE = 0;

    static {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        appIcon = Toolkit.getDefaultToolkit().createImage(Main.class.getResource("/assets/icon.png"));
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
    }

    public static long showKeyboardSelectionScreen() {
        var frame = new JFrame();
        frame.setContentPane(new KeyboardSelectionScreen());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 128));
        frame.setIconImage(appIcon);
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

    public static void showKeyboardVisualizer(boolean isResizable, KeyboardView keyboard) {
        var frame = new JFrame();
        var keyboardHelperPanel = new KeyboardVisualizerScreen(keyboard);

        frame.setAlwaysOnTop(true);
        frame.setUndecorated(!isResizable);
        frame.setBackground(isResizable ? new Color(128, 128, 128) : new Color(0, 0, 0, 0));
        frame.setContentPane(keyboardHelperPanel);
        frame.setBounds(keyboard.visualizerFrameXPosition, keyboard.visualizerFrameYPosition, keyboard.visualizerFrameWidth, keyboard.visualizerFrameHeight);
        frame.setType(JFrame.Type.UTILITY);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        if(!isResizable) {
            NativeUtils.makeJFrameBehindClickable(frame);
        }

        keyboard.visualizerFrame = frame;
        keyboard.visualizerScreen = keyboardHelperPanel;
    }

    public static boolean hideKeyboardVisualizer(KeyboardView keyboard) {
        if(keyboard.visualizerFrame != null) {
            var helperFrameBounds = keyboard.visualizerFrame.getBounds();

            keyboard.visualizerFrameXPosition = helperFrameBounds.x;
            keyboard.visualizerFrameYPosition = helperFrameBounds.y;
            keyboard.visualizerFrameWidth = helperFrameBounds.width;
            keyboard.visualizerFrameHeight = helperFrameBounds.height;

            keyboard.visualizerFrame.dispose();
            keyboard.visualizerFrame = null;
            keyboard.visualizerScreen = null;

            return true;
        }

        return false;
    }
}