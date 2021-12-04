package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import visualizer.gui.*;
import visualizer.model.*;
import visualizer.utils.*;

public final class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
        var enableDisableToggleItem = new CheckboxMenuItem("Toggle Visibility");
        var resizeEnableDisableToggleItem = new CheckboxMenuItem("Toggle Resizability");

        ItemListener listener = e -> handleKeyboardVisualizerToggle(enableDisableToggleItem.getState(), resizeEnableDisableToggleItem, keyboard);

        enableDisableToggleItem.addItemListener(listener);
        resizeEnableDisableToggleItem.addItemListener(listener);

        keyboardMenu.add(enableDisableToggleItem);
        keyboardMenu.add(resizeEnableDisableToggleItem);
        keyboardMenu.add(newButtonMenuItem("Re-Pick Keyboard", e -> handleKeyboardRepickButtonClick(keyboard, keyboardMenu)));

        keyboardsMenu.add(keyboardMenu);
        return keyboardsMenu;
    }


    private static void handleAddKeyboardButtonClick(Menu keyboardsMenu) {
        new Thread(() -> {
            var keyboardName = JOptionPane.showInputDialog("Enter keyboard name!");

            if(keyboardName != null && !keyboardName.isBlank()) {
                var handle = GuiUtils.showKeyboardSelectionScreen();

                if(handle != GuiUtils.CANCELED_KEYBOARD_HANDLE) {
                    var keyboard = new KeyboardView(NativeUtils.getKeyboardIdentifierFromHandle(handle), keyboardName, 0, 0, 600, 400);

                    Settings.keyboards.add(keyboard);
                    addNewKeyboardMenu(keyboardsMenu, keyboard);
                }
            }
        }).start();
    }

    private static void handleKeyboardRepickButtonClick(KeyboardView keyboard, Menu keyboardMenu) {
        new Thread(() -> {
            var handle = GuiUtils.showKeyboardSelectionScreen();

            if(handle != GuiUtils.CANCELED_KEYBOARD_HANDLE) {
                keyboard.keyboardIdentifier = NativeUtils.getKeyboardIdentifierFromHandle(handle);
                keyboardMenu.getItem(0).setEnabled(true);
                keyboardMenu.getItem(1).setEnabled(true);
            }
        }).start();
    }

    private static void handleKeyboardVisualizerToggle(boolean isEnabled, CheckboxMenuItem resizeEnableDisableToggleItem, KeyboardView keyboard) {
        GuiUtils.hideKeyboardVisualizer(keyboard);

        NativeUtils.keyDownListeners.remove(keyboard.keyDownListener);
        NativeUtils.keyUpListeners.remove(keyboard.keyUpListener);
        keyboard.keyDownListener = null;
        keyboard.keyUpListener = null;

        if(isEnabled) {
            GuiUtils.showKeyboardVisualizer(resizeEnableDisableToggleItem, keyboard);

            keyboard.keyDownListener = (k, h) -> handleKeyboardVisualizerKeyStateChanges(k, h, Color.RED, keyboard);
            keyboard.keyUpListener = (k, h) -> handleKeyboardVisualizerKeyStateChanges(k, h, Color.GRAY, keyboard);

            NativeUtils.keyDownListeners.add(keyboard.keyDownListener);
            NativeUtils.keyUpListeners.add(keyboard.keyUpListener);
        }
    }


    private static void handleKeyboardVisualizerKeyStateChanges(int keyCode, long keyboardHandle, Color buttonColor, KeyboardView keyboard) {
        if(NativeUtils.getKeyboardHandleFromIdentifier(keyboard.keyboardIdentifier) == keyboardHandle) {
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

    private static void handleKeyboardListChange(String[] keyboardIdentifiers, Menu keyboardsMenu) {
        IntStream.range(0, keyboardsMenu.getItemCount())
                 .mapToObj(keyboardsMenu::getItem)
                 .forEach(m -> {
                     var keyboardMenu = (Menu) m;
                     var keyboardName = keyboardMenu.getLabel();
                     var keyboardIdentifier = Settings.keyboards.stream()
                                                      .filter(k -> k.name.equals(keyboardName))
                                                      .map(k -> k.keyboardIdentifier)
                                                      .findFirst()
                                                      .orElseThrow();

                     var isConnected = Arrays.stream(keyboardIdentifiers).anyMatch(h -> h.equals(keyboardIdentifier));

                     keyboardMenu.getItem(0).setEnabled(isConnected);
                     keyboardMenu.getItem(1).setEnabled(isConnected);
                 });
    }
}