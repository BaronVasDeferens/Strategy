package strategy;


import javax.swing.*;
import java.awt.event.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;
import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;


public class Strategy implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

    RenderThread renderer;
    JFrame gameFrame;
    DrawPanel drawPanel;

    int fullScreenWidth, fullScreenHeight;


    public static void main (String[] args) {

        Strategy game = new Strategy();
        game.start();
    }

    private void start() {

        gameFrame = new JFrame();

        // Set window size and deploy fullscreen mode
        goFullscreen(gameFrame);

        // Wait for fullscreen to fully expand
        try {
            Thread.sleep(250);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        drawPanel = new DrawPanel();
        drawPanel.setSize(fullScreenWidth, fullScreenHeight);
        drawPanel.setDoubleBuffered(true);

        renderer = new RenderThread(fullScreenWidth, fullScreenHeight);
        renderer.setTargetPanel(drawPanel);
        renderer.start();

        gameFrame.add(drawPanel);
        gameFrame.addKeyListener(this);
        gameFrame.addMouseListener(this);
        gameFrame.addMouseWheelListener(this);
        gameFrame.addMouseMotionListener(this);
        gameFrame.requestFocus();
        gameFrame.setVisible(true);

    }

    public void quit() {
        renderer.quit();
        gameFrame.dispose();
        System.exit(0);
    }

    private void goFullscreen(JFrame frame) {

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
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                renderer.quit();
                gameFrame.dispose();
                break;
            case KeyEvent.VK_SPACE:
                break;
            default:
                break;
            }
    }

    public void keyReleased(KeyEvent e) {
        renderer.keyReleased(e);
    }

    public void keyTyped(KeyEvent e) {
        renderer.keyTyped(e);
    }

    public void mouseClicked(MouseEvent e) {
        //renderer.mouseClicked(e);
    }

    public void mouseMoved (MouseEvent e) {
        renderer.updateMousePosition(e);
    }

    public void mouseDragged (MouseEvent e) { }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        renderer.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {    }

    public void mouseWheelMoved(MouseWheelEvent e) {

        // TODO: think about this:
        // When the mouse wheel moves a lot, we don't want to render ALL levels between the current level
        // and the desired one.

        int scrollAmount = Math.abs(e.getScrollAmount());

        // wheel DOWN : zoom in
        if (e.getWheelRotation() > 0) {
            renderer.zoomIn(1);
        } else {
            // wheel UP : zoom out
            renderer.zoomOut(1);
        }

    }



}

