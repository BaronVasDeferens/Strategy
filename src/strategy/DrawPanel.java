package strategy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawPanel extends JPanel {

    BufferedImage image;

    public DrawPanel() {
        super();
    }

    @Override
    public void paintComponent(Graphics g) {
        //g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }

        g.dispose();
    }

}
