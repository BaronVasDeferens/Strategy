package strategy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by skot on 10/8/16.
 */
public class Testo implements Runnable, KeyListener {

    public static void main (String ... args) {

        Testo testicle = new Testo();
        testicle.run();
    }

    public void run() {

        MyImagePanel jp = new MyImagePanel();
            jp.setBackground(Color.RED);
            jp.setSize(500,500);
            jp.setDoubleBuffered(true);
            jp.sprites.add(new Sprite (loadImage("t34-76.png")));
            jp.sprites.add(new Sprite (loadImage("ship01.png")));

        JFrame jf = new JFrame();
            jf.addKeyListener(this);
            jf.setBackground(Color.BLACK);
            jf.setSize(550, 550);
            jf.setPreferredSize(new Dimension(600, 600));
            jf.add(jp);
            jf.setVisible(true);
            jp.run();
    }

    private BufferedImage loadImage(String imageName) {

        BufferedImage loaded = null;
        try (InputStream fin = getClass().getResourceAsStream("images/" + imageName)) {
            loaded = ImageIO.read(fin);
            fin.close();
            System.out.println("loaded " + imageName + ": " + loaded.getWidth() + "x" + loaded.getHeight());
            return loaded;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }


    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            default:
                break;
        }


    }

    public void keyReleased(KeyEvent e) {    }

    public void keyTyped(KeyEvent e) {    }

}

class MyImagePanel extends JPanel implements Runnable {

    public ArrayList<Sprite> sprites = new ArrayList<>();

    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ie) {
                System.out.println(ie.toString());
            }
        }
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for (Sprite sprite: sprites) {
            sprite.update();
            if (sprite.image != null) {

                g.drawImage(
                        sprite.image,
                        sprite.x,
                        0,
                        (int)(sprite.scale * sprite.image.getWidth()),
                        (int)(sprite.scale * sprite.image.getHeight()),
                        this);
            }
        }
    }
}

class Sprite {
    BufferedImage image;
    int x, y;
    float scale = .51f;

    public Sprite(BufferedImage image) {
        this.image = image;
    }

    public Sprite (BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    public void update() {
        x++;
    }
}
