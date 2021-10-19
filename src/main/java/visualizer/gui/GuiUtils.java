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

    public static void showKeyboardHelper(boolean isResizable, KeyboardView keyboard) {
        var frame = new JFrame();
        var keyboardHelperPanel = new KeyboardHelperScreen();

        frame.setAlwaysOnTop(true);
        frame.setUndecorated(!isResizable);
        frame.setResizable(false);
        frame.setBackground(isResizable ? new Color(128, 128, 128) : new Color(0, 0, 0, 0));
        frame.setContentPane(keyboardHelperPanel);
        frame.setBounds(keyboard.visualizerFrameXPosition, keyboard.visualizerFrameYPosition, keyboard.visualizerFrameWidth, keyboard.visualizerFrameHeight);
        frame.setType(JFrame.Type.UTILITY);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        if(!isResizable) {
            NativeUtils.makeJFrameBehindClickable(frame);
        }

        keyboard.helperFrame = frame;
        keyboard.helperScreen = keyboardHelperPanel;
    }

    public static boolean hideKeyboardHelper(KeyboardView keyboard) {
        if(keyboard.helperFrame != null) {
            var helperFrameBounds = keyboard.helperFrame.getBounds();

            keyboard.visualizerFrameXPosition = helperFrameBounds.x;
            keyboard.visualizerFrameYPosition = helperFrameBounds.y;
            keyboard.visualizerFrameWidth = helperFrameBounds.width;
            keyboard.visualizerFrameHeight = helperFrameBounds.height;

            keyboard.helperFrame.dispose();
            keyboard.helperFrame = null;
            keyboard.helperScreen = null;

            return true;
        }

        return false;
    }
}