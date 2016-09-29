package strategy;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class GameFrame extends JFrame implements KeyListener, MouseListener {

    Launcher launcher;

    public GameFrame(Launcher launcher) {

        this.launcher = launcher;
        //this.setUndecorated(true);        // Windows
        //initComponents();

        addKeyListener(this);
        addMouseListener(this);
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
    public void mouseEntered (MouseEvent e) {

    }
    
    @Override
    public void mouseExited (MouseEvent e) {
        
    }

    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
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

