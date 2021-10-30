package visualizer.utils;

import java.util.*;
import java.util.function.*;
import javax.swing.*;

public final class NativeUtils {
    static {
        try {
            System.loadLibrary("app/NativeUtils");
        }catch (UnsatisfiedLinkError e) {
            JOptionPane.showMessageDialog(null, "Unable to load NativeUtils.dll!");
            System.exit(0);
        }
    }

    private static final boolean ENABLE_INPUT_LOGGING = false;

    public static final GlobalKeyboardInputListener INACTIVE_LISTENER = (a, b) -> {};

    public static final ArrayList<GlobalKeyboardInputListener> keyDownListeners = new ArrayList<>();
    public static final ArrayList<GlobalKeyboardInputListener> keyUpListeners = new ArrayList<>();
    public static GlobalKeyboardInputListener keyboardSelectionListener = INACTIVE_LISTENER;
    public static Consumer<long[]> keyboardListChangeListener = k -> {};

    public static native void initializeNativeUtils();  // Steals caller thread, called only once in main
    public static native void makeJFrameBehindClickable(JFrame frame);
    public static native long[] listAllKeyboardHandles();

    public static void sleepMs(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Called by NativeUtils.c on 'WM_INPUT_DEVICE_CHANGE'
    private static void onKeyboardListChange() {
        var keyboardList = listAllKeyboardHandles();

        if(ENABLE_INPUT_LOGGING) {
            System.out.println("Keyboard list change: " + Arrays.toString(keyboardList));
        }

        keyboardListChangeListener.accept(keyboardList);
    }

    // Called by NativeUtils.c on 'WM_INPUT' event on type 'RIM_TYPEKEYBOARD' with 'WM_KEYUP' and 'WM_SYSKEYUP' events
    private static void onKeyboardKeyUp(int virtualKeyCode, long deviceHandle) {
        if(ENABLE_INPUT_LOGGING) {
            System.out.println("Keyboard button up interaction - device: " + deviceHandle + " - key: " + virtualKeyCode);
        }

        for(var keyUpListener : keyUpListeners) {
            keyUpListener.keyStateChanged(virtualKeyCode, deviceHandle);
        }

        keyboardSelectionListener.keyStateChanged(virtualKeyCode, deviceHandle);
    }

    // Called by NativeUtils.c on 'WM_INPUT' event on type 'RIM_TYPEKEYBOARD' with 'WM_KEYDOWN' and 'WM_SYSKEYDOWN' events
    private static void onKeyboardKeyDown(int virtualKeyCode, long deviceHandle) {
        if(ENABLE_INPUT_LOGGING) {
            System.out.println("Keyboard button down interaction - device: " + deviceHandle + " - key: " + virtualKeyCode);
        }

        for(var keyDownListener : keyDownListeners) {
            keyDownListener.keyStateChanged(virtualKeyCode, deviceHandle);
        }
    }
}