package strategy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawPanel extends JPanel implements Runnable {

    Thread t;

    Renderer renderer;
    BufferedImage image;

    int sleepInterval = 1;
    boolean isAlive = true;
    boolean isPaused = false;


    public DrawPanel(Renderer renderer) {
        super();
        this.renderer = renderer;
        t = new Thread(this);
        t.start();
    }

    public void setSleepInterval(int sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }

        g.dispose();
    }

    @Override
    public void run() {

        while (isAlive) {

            if (isPaused == false) {
                image = renderer.update();
                repaint();
            }

            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        System.out.println("drawPanel quit normally");
    }

    public synchronized void quit() {
        isAlive = false;
    }

}
