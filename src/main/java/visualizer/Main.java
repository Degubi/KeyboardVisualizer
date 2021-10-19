package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.util.function.*;
import visualizer.gui.*;
import visualizer.model.*;

public final class Main {

    public static void main(String[] args) throws Exception {
        var popupMenu = new PopupMenu();
        var keyboardsMenu = Settings.keyboards.stream()
                                    .reduce(new Menu("Keyboards"), Main::newKeyboardMenu, (k, l) -> l);

        popupMenu.add(keyboardsMenu);
        popupMenu.add(newButtonMenuItem("Add Keyboard", e -> handleAddKeyboardButtonClick(keyboardsMenu)));
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

    private static Menu newKeyboardMenu(Menu keyboardsMenu, KeyboardView keyboard) {
        var keyboardMenu = new Menu(String.valueOf(keyboard.handle));
        var resizableKeyboardHelperFrameCheckbox = newCheckboxMenuItem("Resizable Keyboard Helper", t -> handleKeyboardHelperResizeToggle(t, keyboard));

        keyboardMenu.add(newCheckboxMenuItem("Toggle Keyboard Helper", t -> handleKeyboardHelperToggle(t, resizableKeyboardHelperFrameCheckbox, keyboard)));
        keyboardMenu.add(resizableKeyboardHelperFrameCheckbox);

        keyboardsMenu.add(keyboardMenu);
        return keyboardsMenu;
    }

    @SuppressWarnings("boxing")
    private static CheckboxMenuItem newCheckboxMenuItem(String text, Consumer<Boolean> onToggleHandler) {
        var item = new CheckboxMenuItem(text);
        item.addItemListener(e -> onToggleHandler.accept(item.getState()));
        return item;
    }


    private static void handleAddKeyboardButtonClick(Menu keyboardsMenu) {
        new Thread(() -> {
            var handle = GuiUtils.showKeyboardSelectionScreen();

            if(handle != GuiUtils.CANCELED_KEYBOARD_HANDLE) {
                var keyboard = new KeyboardView(handle, 0, 0, 600, 400);

                Settings.keyboards.add(keyboard);
                newKeyboardMenu(keyboardsMenu, keyboard);
            }
        }).start();
    }

    private static void handleKeyboardHelperResizeToggle(boolean isResizable, KeyboardView keyboard) {
        if(GuiUtils.hideKeyboardHelper(keyboard)) {
            GuiUtils.showKeyboardHelper(isResizable, keyboard);
        }
    }

    private static void handleKeyboardHelperToggle(boolean isEnabled, CheckboxMenuItem resizableKeyboardHelperFrameCheckbox, KeyboardView keyboard) {
        if(isEnabled) {
            GuiUtils.showKeyboardHelper(resizableKeyboardHelperFrameCheckbox.getState(), keyboard);

            keyboard.keyDownListener = (k, h) -> handleKeyboardHelperKeyStateChanges(k, h, Color.RED, keyboard);
            keyboard.keyUpListener = (k, h) -> handleKeyboardHelperKeyStateChanges(k, h, Color.GRAY, keyboard);

            NativeUtils.keyDownListeners.add(keyboard.keyDownListener);
            NativeUtils.keyUpListeners.add(keyboard.keyUpListener);
        }else{
            GuiUtils.hideKeyboardHelper(keyboard);

            NativeUtils.keyDownListeners.remove(keyboard.keyDownListener);
            NativeUtils.keyUpListeners.remove(keyboard.keyUpListener);

            keyboard.keyDownListener = null;
            keyboard.keyUpListener = null;
        }
    }

    private static void handleKeyboardHelperKeyStateChanges(int keyCode, long keyboardHandle, Color buttonColor, KeyboardView keyboard) {
        if(keyboard.handle == keyboardHandle) {
            var keyIndex = KeyboardHelperScreen.getKeyIndex(keyCode);

            if(keyIndex != -1) {
                keyboard.helperScreen.buttonColors[keyIndex] = buttonColor;
                keyboard.helperScreen.repaint();
            }
        }
    }
}