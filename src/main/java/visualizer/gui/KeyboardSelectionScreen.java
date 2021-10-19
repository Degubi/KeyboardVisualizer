package visualizer.gui;

import java.awt.*;
import javax.swing.*;

public final class KeyboardSelectionScreen extends JPanel {
    private static final Font INFO_FONT = new Font("Arial", Font.BOLD, 32);

    KeyboardSelectionScreen() {}

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var centerWidth = GuiUtils.screenWidth / 2;
        var centerHeight = GuiUtils.screenHeight / 2;

        graphics.setColor(Color.WHITE);
        graphics.setFont(INFO_FONT);
        graphics.drawString("Press a key on the keyboard!", centerWidth - 256, centerHeight);
    }
}