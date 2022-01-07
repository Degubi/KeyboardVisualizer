package visualizer.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import visualizer.settings.*;
import visualizer.utils.*;

public final class KeyboardVisualizerScreen extends JPanel {
    private static final Font labelFont = new Font("Arial", Font.BOLD, 14);
    private static final String[] buttonLabels = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Y", "X", "C", "V", "B", "N", "M"
    };
    private static final int secondRowBeginIndex = indexOf("Q", buttonLabels);
    private static final int thirdRowBeginIndex = indexOf("A", buttonLabels);
    private static final int fourthRowBeginIndex = indexOf("Y", buttonLabels);

    public final Color[] buttonColors = new Color[buttonLabels.length];

    private final KeyboardView keyboard;

    KeyboardVisualizerScreen(KeyboardView keyboard) {
        super(null);

        this.keyboard = keyboard;

        Arrays.fill(buttonColors, Color.GRAY);
        setOpaque(false);
    }


    public static void show(CheckboxMenuItem resizeEnableDisableToggleItem, KeyboardView keyboard) {
        var frame = new JFrame();
        var keyboardHelperPanel = new KeyboardVisualizerScreen(keyboard);
        var isResizable = resizeEnableDisableToggleItem.getState();

        if(isResizable) {
            frame.addWindowListener(GuiUtils.newWindowClosedListener(() -> {
                resizeEnableDisableToggleItem.setState(false);

                hide(keyboard);
                show(resizeEnableDisableToggleItem, keyboard);
            }));
        }

        frame.setAlwaysOnTop(true);
        frame.setUndecorated(!isResizable);
        frame.setBackground(isResizable ? new Color(128, 128, 128) : new Color(0, 0, 0, 0));
        frame.setContentPane(keyboardHelperPanel);
        frame.setBounds(keyboard.visualizerFrameXPosition, keyboard.visualizerFrameYPosition, keyboard.visualizerFrameWidth, keyboard.visualizerFrameHeight);
        frame.setType(JFrame.Type.UTILITY);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        if(!isResizable) {
            NativeUtils.makeJFrameBehindClickable(frame);
        }

        keyboard.visualizerFrame = frame;
        keyboard.visualizerScreen = keyboardHelperPanel;
    }

    public static boolean hide(KeyboardView keyboard) {
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


    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var screenWidth = getWidth();
        var buttonSize = screenWidth / 15;

        drawButtons(0, secondRowBeginIndex, 20, 0, graphics, buttonSize);
        drawButtons(secondRowBeginIndex, thirdRowBeginIndex, 50, 1, graphics, buttonSize);
        drawButtons(thirdRowBeginIndex, fourthRowBeginIndex, 60, 2, graphics, buttonSize);
        drawButtons(fourthRowBeginIndex, buttonLabels.length, 50, 3, graphics, buttonSize);

        //graphics.drawString(String.join(" + ", keyboard.heldKeys), 30, 200);
    }


    private void drawButtons(int beginIndex, int endIndex, int startingXPosOffset, int row, Graphics graphics, int outerSize) {
        var outerSizeWithPadding = outerSize + 12;
        var innerFillSize = outerSize - 1;
        var buttonYPos = 20 + (outerSizeWithPadding * row);
        var buttonBaseXPos = -(beginIndex * outerSizeWithPadding) + startingXPosOffset;
        var textOffset = outerSize / 2;

        for(var i = beginIndex; i < endIndex; ++i) {
            var buttonXPos = buttonBaseXPos + (i * outerSizeWithPadding);

            graphics.setColor(buttonColors[i]);
            graphics.fillRect(buttonXPos + 1, buttonYPos + 1, innerFillSize, innerFillSize);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(buttonXPos, buttonYPos, outerSize, outerSize);

            graphics.setFont(labelFont);
            graphics.setColor(Color.BLACK);
            graphics.drawString(buttonLabels[i], buttonXPos + textOffset, buttonYPos + textOffset);
        }
    }

    private static int indexOf(String value, String[] values) {
        for(var i = 0; i < values.length; ++i) {
            if(values[i].equals(value)) {
                return i;
            }
        }

        return -1;
    }
}