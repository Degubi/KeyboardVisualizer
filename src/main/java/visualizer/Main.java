package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.util.function.*;
import visualizer.gui.*;

public final class Main {

    public static void main(String[] args) throws Exception {
        var resizableKeyboardHelperFrameCheckbox = newCheckboxMenuItem("Resizable Keyboard Helper", Main::handleKeyboardHelperResizeToggle);
        var popupMenu = new PopupMenu();

        popupMenu.add(newCheckboxMenuItem("Toggle Keyboard Helper", t -> handleKeyboardHelperToggle(t, resizableKeyboardHelperFrameCheckbox)));
        popupMenu.add(resizableKeyboardHelperFrameCheckbox);
        popupMenu.add(newButtonMenuItem("Close", e -> System.exit(0)));

        SystemTray.getSystemTray().add(new TrayIcon(GuiUtils.appIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH), "Keyboard Visualizer", popupMenu));
        Runtime.getRuntime().addShutdownHook(new Thread(Settings::save));
        Thread.currentThread().setName("Global Key Listener");
        NativeUtils.initializeNativeUtils();
    }


    private static MenuItem newButtonMenuItem(String text, ActionListener onPressListener) {
        var item = new MenuItem(text);
        item.addActionListener(onPressListener);
        return item;
    }

    @SuppressWarnings("boxing")
    private static CheckboxMenuItem newCheckboxMenuItem(String text, Consumer<Boolean> onToggleHandler) {
        var item = new CheckboxMenuItem(text);
        item.addItemListener(e -> onToggleHandler.accept(item.getState()));
        return item;
    }


    private static void handleKeyboardHelperResizeToggle(boolean isResizable) {
        if(GuiUtils.hideKeyboardHelper()) {
            GuiUtils.showKeyboardHelper(isResizable);
        }
    }

    private static void handleKeyboardHelperToggle(boolean isEnabled, CheckboxMenuItem resizableKeyboardHelperFrameCheckbox) {
        if(isEnabled) {
            GuiUtils.showKeyboardHelper(resizableKeyboardHelperFrameCheckbox.getState());

            NativeUtils.keyDownListener = (k, h) -> handleKeyboardHelperKeyStateChanges(k, Color.RED);
            NativeUtils.keyUpListener = (k, h) -> handleKeyboardHelperKeyStateChanges(k, Color.GRAY);
        }else{
            GuiUtils.hideKeyboardHelper();

            NativeUtils.keyDownListener = NativeUtils.INACTIVE_LISTENER;
            NativeUtils.keyUpListener = NativeUtils.INACTIVE_LISTENER;
        }
    }

    private static void handleKeyboardHelperKeyStateChanges(int keyCode, Color buttonColor) {
        var keyIndex = KeyboardHelperScreen.getKeyIndex(keyCode);

        if(keyIndex != -1) {
            GuiUtils.keyboardHelperScreen.buttonColors[keyIndex] = buttonColor;
            GuiUtils.keyboardHelperScreen.repaint();
        }
    }
}