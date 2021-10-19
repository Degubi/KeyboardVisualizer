package visualizer;

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

    public static final GlobalKeyboardInputListener INACTIVE_LISTENER = (a, b) -> {};

    public static GlobalKeyboardInputListener keyDownListener = INACTIVE_LISTENER;
    public static GlobalKeyboardInputListener keyUpListener = INACTIVE_LISTENER;
    public static GlobalKeyboardInputListener keyboardSelectionListener = INACTIVE_LISTENER;

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


    // Called by NativeUtils.c on 'WM_INPUT' event on type 'RIM_TYPEKEYBOARD' with 'WM_KEYUP' and 'WM_SYSKEYUP' events
    private static void onKeyboardKeyUp(int virtualKeyCode, long deviceHandle) {
        System.out.println("Keyboard button up interaction - device: " + deviceHandle + " - key: " + virtualKeyCode);

        keyUpListener.keyStateChanged(virtualKeyCode, deviceHandle);
    }

    // Called by NativeUtils.c on 'WM_INPUT' event on type 'RIM_TYPEKEYBOARD' with 'WM_KEYDOWN' and 'WM_SYSKEYDOWN' events
    private static void onKeyboardKeyDown(int virtualKeyCode, long deviceHandle) {
        System.out.println("Keyboard button down interaction - device: " + deviceHandle + " - key: " + virtualKeyCode);

        keyDownListener.keyStateChanged(virtualKeyCode, deviceHandle);
    }
}