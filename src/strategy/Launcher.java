package strategy;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


public class Launcher {

    static int sleepInterval = 1;
    public boolean isPaused = false;

    ImageRenderer renderer;
    GameFrame gameFrame;
    DrawPanel drawPanel;

    static int fullScreenWidth, fullScreenHeight;

    public Launcher() {

        gameFrame = new GameFrame(this);
        goFullscreen(gameFrame);

        // Wait for fullscreen to fully expand
        try {
            Thread.sleep(250);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        renderer = new ImageRenderer(fullScreenWidth, fullScreenHeight);
        drawPanel = new DrawPanel(renderer);
        drawPanel.setSize(fullScreenWidth, fullScreenHeight);
        drawPanel.setDoubleBuffered(true);

        gameFrame.add(drawPanel);
        gameFrame.requestFocus();
        gameFrame.setVisible(true);

    }


    private static void goFullscreen(javax.swing.JFrame frame) {

        java.awt.GraphicsDevice devices[] = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        for (int i = 0; i < devices.length; i++) {
            System.out.println((devices[i].toString()));
            System.out.println(devices[i].getDisplayMode().getWidth() + "x" + devices[i].getDisplayMode().getHeight());
            System.out.println("bit depth: " + devices[i].getDisplayMode().getBitDepth());
            System.out.println("refresh: " + devices[i].getDisplayMode().getRefreshRate());
            if (devices[i].isFullScreenSupported()) {
                System.out.println("Fullscreen: yes");
                devices[i].setFullScreenWindow(frame);
                fullScreenWidth = devices[i].getDisplayMode().getWidth();
                fullScreenHeight = devices[i].getDisplayMode().getHeight();
                return;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        renderer.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        renderer.keyReleased(e);
    }

    public void keyTyped(KeyEvent e) {
        renderer.keyTyped(e);
    }

    public void mouseClicked (MouseEvent e) {
        //renderer.mouseClicked(e);
    }


    public void mousePressed (MouseEvent e) {
        renderer.mousePressed(e);
    }


    public void mouseReleased (MouseEvent e) {

    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        // wheel DOWN : zoom in
        if (e.getWheelRotation() > 0) {
            renderer.zoomIn();

        }
        else {
            // wheel UP : zoom out
            renderer.zoomOut();
        }

    }

    public void mouseEntered (MouseEvent e) {

    }


    public void mouseExited(MouseEvent e) {

    }



}
