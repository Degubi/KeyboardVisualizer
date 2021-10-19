package visualizer.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public final class KeyboardHelperScreen extends JPanel {
    private static final Font labelFont = new Font("Arial", Font.BOLD, 14);
    private static final String[] buttonLabels = {
            "Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Y", "X", "C", "V", "B", "N", "M"
    };

    public final Color[] buttonColors = new Color[buttonLabels.length];

    KeyboardHelperScreen() {
        super(null);

        Arrays.fill(buttonColors, Color.GRAY);
        setOpaque(false);
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        drawButtons(0, 10, 20, 20, graphics);
        drawButtons(10, 19, 30, 70, graphics);
        drawButtons(19, 26, 40, 120, graphics);
    }


    private void drawButtons(int beginIndex, int endIndex, int startingXPosOffset, int y, Graphics graphics) {
        for(var i = beginIndex; i < endIndex; ++i) {
            var buttonPos = -(beginIndex * 54) + startingXPosOffset + (i * 54);

            graphics.setColor(buttonColors[i]);
            graphics.fillRect(buttonPos + 1, y + 1, 39, 39);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(buttonPos, y, 40, 40);

            graphics.setFont(labelFont);
            graphics.setColor(Color.BLACK);
            graphics.drawString(buttonLabels[i], buttonPos + 16, y + 22);
        }
    }


    public static int getKeyIndex(int keyCode) {
        return switch(keyCode) {
            case 81 -> 0;  case 87 -> 1;  case 69 -> 2;  case 82 -> 3;  case 84 -> 4;  case 90 -> 5;  case 85 -> 6;  case 73 -> 7;  case 79 -> 8;  case 80 -> 9;
            case 65 -> 10; case 83 -> 11; case 68 -> 12; case 70 -> 13; case 71 -> 14; case 72 -> 15; case 74 -> 16; case 75 -> 17; case 76 -> 18;
            case 89 -> 19; case 88 -> 20; case 67 -> 21; case 86 -> 22; case 66 -> 23; case 78 -> 24; case 77 -> 25;
            default -> -1;
        };
    }
}