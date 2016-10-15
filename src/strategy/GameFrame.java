package strategy;

import javax.swing.*;
import java.awt.event.*;


public class GameFrame extends JFrame implements KeyListener, MouseListener, MouseWheelListener {

    Launcher launcher;

    public GameFrame(Launcher launcher) {

        this.launcher = launcher;
        //this.setUndecorated(true);        // Windows
        //initComponents();

        addKeyListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        requestFocus();

    }

    @Override
    public void mouseClicked (MouseEvent e) {
        launcher.mouseClicked(e);
    }

    @Override
    public void mousePressed (MouseEvent e) {
        launcher.mousePressed(e);
    }

    @Override
    public void mouseReleased (MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        launcher.mouseWheelMoved(e);
    }

    @Override
    public void mouseEntered (MouseEvent e) {

    }
    
    @Override
    public void mouseExited (MouseEvent e) {
        
    }

    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                launcher.quit();
                break;
            case KeyEvent.VK_SPACE:
                launcher.isPaused = !launcher.isPaused;
                break;
            default:
                launcher.keyPressed(e);
                break;

        }
    }

    public void keyTyped(KeyEvent e) {
        launcher.keyTyped(e);
    }

    public void keyReleased(KeyEvent e) {
        launcher.keyReleased(e);
    }
}

