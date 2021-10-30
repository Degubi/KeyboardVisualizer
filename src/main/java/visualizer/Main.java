package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.swing.*;
import visualizer.gui.*;
import visualizer.model.*;
import visualizer.utils.*;

public final class Main {

    public static void main(String[] args) throws Exception {
        var popupMenu = new PopupMenu();
        var keyboardsMenu = Settings.keyboards.stream()
                                    .reduce(new Menu("Keyboards"), Main::addNewKeyboardMenu, (k, l) -> l);

        popupMenu.add(keyboardsMenu);
        popupMenu.add(newButtonMenuItem("Add Keyboard", e -> handleAddKeyboardButtonClick(keyboardsMenu)));
        popupMenu.add(newButtonMenuItem("Close", e -> System.exit(0)));

        SystemTray.getSystemTray().add(new TrayIcon(GuiUtils.appIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH), "Keyboard Visualizer", popupMenu));
        Runtime.getRuntime().addShutdownHook(new Thread(Settings::save));
        Thread.currentThread().setName("Global Key Listener");
        NativeUtils.keyboardListChangeListener = k -> handleKeyboardListChange(k, keyboardsMenu);
        NativeUtils.initializeNativeUtils();
    }


    private static MenuItem newButtonMenuItem(String text, ActionListener onPressListener) {
        var item = new MenuItem(text);
        item.addActionListener(onPressListener);
        return item;
    }

    private static Menu addNewKeyboardMenu(Menu keyboardsMenu, KeyboardView keyboard) {
        var keyboardMenu = new Menu(keyboard.name);
        var resizableKeyboardHelperFrameCheckbox = newButtonMenuItem("Resizable Keyboard Visualizer", e -> handleKeyboardVisualizerResizeToggle(keyboard));

        keyboardMenu.add(newCheckboxMenuItem("Toggle Keyboard Visualizer", t -> handleKeyboardVisualizerToggle(t, keyboard)));
        keyboardMenu.add(resizableKeyboardHelperFrameCheckbox);
        keyboardMenu.add(newButtonMenuItem("Re-Pick Keyboard", e -> handleKeyboardRepickButtonClick(keyboard, keyboardMenu)));

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
            var keyboardName = JOptionPane.showInputDialog("Enter keyboard name!");

            if(keyboardName != null && !keyboardName.isBlank()) {
                var handle = GuiUtils.showKeyboardSelectionScreen();

                if(handle != GuiUtils.CANCELED_KEYBOARD_HANDLE && Settings.keyboards.stream().noneMatch(k -> k.handle == handle)) {
                    var keyboard = new KeyboardView(handle, keyboardName, 0, 0, 600, 400);

                    Settings.keyboards.add(keyboard);
                    addNewKeyboardMenu(keyboardsMenu, keyboard);
                }
            }
        }).start();
    }

    private static void handleKeyboardRepickButtonClick(KeyboardView keyboard, Menu keyboardMenu) {
        new Thread(() -> {
            var handle = GuiUtils.showKeyboardSelectionScreen();

            if(handle != GuiUtils.CANCELED_KEYBOARD_HANDLE && Settings.keyboards.stream().noneMatch(k -> k.handle == handle)) {
                keyboard.handle = handle;
                keyboardMenu.getItem(0).setEnabled(true);
                keyboardMenu.getItem(1).setEnabled(true);
            }
        }).start();
    }

    private static void handleKeyboardVisualizerResizeToggle(KeyboardView keyboard) {
        if(GuiUtils.hideKeyboardVisualizer(keyboard)) {
            GuiUtils.showKeyboardVisualizer(true, keyboard);
        }
    }

    private static void handleKeyboardVisualizerToggle(boolean isEnabled, KeyboardView keyboard) {
        if(isEnabled) {
            GuiUtils.showKeyboardVisualizer(false, keyboard);

            keyboard.keyDownListener = (k, h) -> handleKeyboardVisualizerKeyStateChanges(k, h, Color.RED, keyboard);
            keyboard.keyUpListener = (k, h) -> handleKeyboardVisualizerKeyStateChanges(k, h, Color.GRAY, keyboard);

            NativeUtils.keyDownListeners.add(keyboard.keyDownListener);
            NativeUtils.keyUpListeners.add(keyboard.keyUpListener);
        }else{
            GuiUtils.hideKeyboardVisualizer(keyboard);

            NativeUtils.keyDownListeners.remove(keyboard.keyDownListener);
            NativeUtils.keyUpListeners.remove(keyboard.keyUpListener);

            keyboard.keyDownListener = null;
            keyboard.keyUpListener = null;
        }
    }


    private static void handleKeyboardVisualizerKeyStateChanges(int keyCode, long keyboardHandle, Color buttonColor, KeyboardView keyboard) {
        if(keyboard.handle == keyboardHandle) {
            var keyIndex = KeyUtils.getKeyColorIndex(keyCode);
            var keyText = KeyUtils.getKeyboardKeyFromCode(keyCode);

            if(keyIndex != -1) {
                keyboard.visualizerScreen.buttonColors[keyIndex] = buttonColor;

                if(buttonColor == Color.RED) {
                    if(!keyboard.heldKeys.contains(keyText)) {
                        keyboard.heldKeys.add(keyText);
                    }
                }else{
                    keyboard.heldKeys.remove(keyText);
                }

                keyboard.visualizerScreen.repaint();
            }
        }
    }

    private static void handleKeyboardListChange(long[] keyboardHandles, Menu keyboardsMenu) {
        IntStream.range(0, keyboardsMenu.getItemCount())
                 .mapToObj(keyboardsMenu::getItem)
                 .forEach(m -> {
                     var keyboardMenu = (Menu) m;
                     var keyboardName = keyboardMenu.getLabel();
                     var keyboardHandle = Settings.keyboards.stream()
                                                  .filter(k -> k.name.equals(keyboardName))
                                                  .mapToLong(k -> k.handle)
                                                  .findFirst()
                                                  .orElseThrow();

                     var isConnected = Arrays.stream(keyboardHandles).anyMatch(h -> h == keyboardHandle);

                     keyboardMenu.getItem(0).setEnabled(isConnected);
                     keyboardMenu.getItem(1).setEnabled(isConnected);
                 });
    }
}