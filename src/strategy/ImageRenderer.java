package strategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ImageRenderer {

    protected int width, height;
    public BufferedImage image;

    private int currentMasterPosX = 0, currentMasterPosY = 0;
    private int priorMouseX = 0, priorMouseY = 0;

    BufferedImage backgroundImage;
    List<Entity> entities;
    HexMap hexmap;
    HexMapRenderer hexmaprenderer;


    public ImageRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        backgroundImage = loadImage("img02.jpg");
        image = new BufferedImage(width, height, BufferedImage.OPAQUE);

        entities = new ArrayList<Entity>();
        //entities.add(new Entity(loadImage("ship01.png"), image.getWidth() / 2, image.getHeight() / 2));

        hexmap = new HexMap(22,32);
        hexmaprenderer = new HexMapRenderer(
                hexmap,
                backgroundImage.getWidth(),
                backgroundImage.getHeight(),
                100);

        PointerInfo pInfo = MouseInfo.getPointerInfo();

        currentMasterPosX = pInfo.getLocation().x;
        currentMasterPosY = pInfo.getLocation().y;
    }

    public BufferedImage update() {

        PointerInfo pInfo = MouseInfo.getPointerInfo();

        int x = pInfo.getLocation().x;
        int y = pInfo.getLocation().y;

        // Scroll region : right
        if (x >= image.getWidth() - 5) {
            currentMasterPosX = currentMasterPosX + Math.abs(priorMouseX - x);

            if (currentMasterPosX >= backgroundImage.getWidth() - image.getWidth())
                currentMasterPosX = backgroundImage.getWidth() - image.getWidth();

        }

        // scroll region : left
        else if (x <= 5) {
            currentMasterPosX = currentMasterPosX - Math.abs(priorMouseX - x);

            if (currentMasterPosX <= 0)
                currentMasterPosX = 0;
        }

        // normal
        else if (x > priorMouseX) {
            //currentMasterPosX = currentMasterPosX + (x - priorMouseX);
            priorMouseX = pInfo.getLocation().x;
        } else if ((x < priorMouseX) || (x <= 0)) {
            //currentMasterPosX = currentMasterPosX - (priorMouseX - x);
            priorMouseX = pInfo.getLocation().x;
        }

        // UP - AND - DOWN
        // Scroll region : UP
        if (y <= 5) {
            currentMasterPosY = currentMasterPosY - Math.abs(priorMouseY - y);

            if (currentMasterPosY <= 0)
                currentMasterPosY = 0;

        }
        // Scroll region: down
        else if (y >= image.getHeight() - 5) {
            currentMasterPosY = currentMasterPosY + Math.abs(priorMouseY - y);

            if (currentMasterPosY >= backgroundImage.getHeight() - image.getHeight())
                currentMasterPosY = backgroundImage.getHeight() - image.getHeight();
        } else if (y > priorMouseY) {
            //currentMasterPosY = currentMasterPosY + (y - priorMouseY);
            priorMouseY = pInfo.getLocation().y;
        } else if ((y < priorMouseY) || (y <= 0)) {
            //currentMasterPosY = currentMasterPosY - (priorMouseY - y);
            priorMouseY = pInfo.getLocation().y;
        }


        if (currentMasterPosX > backgroundImage.getWidth())
            currentMasterPosX = backgroundImage.getWidth() - image.getWidth();
        else if (currentMasterPosX <= 0)
            currentMasterPosX = 0;

        if (currentMasterPosY > backgroundImage.getHeight())
            currentMasterPosY = backgroundImage.getHeight() - image.getHeight();
        else if (currentMasterPosY <= 0)
            currentMasterPosY = 0;


        try {
            image = backgroundImage.getSubimage(currentMasterPosX, currentMasterPosY, image.getWidth(), image.getHeight());
            BufferedImage hexOverlay = hexmaprenderer.renderHexmap().getSubimage(currentMasterPosX, currentMasterPosY, image.getWidth(), image.getHeight());
            image = composite(image, hexOverlay);
            image = composite(image, entities);

        } catch (RasterFormatException e) {
            System.out.println(e.toString());
            System.out.println("currentMasterPosX : " + currentMasterPosX);
            System.out.println("currentMasterPosY : " + currentMasterPosY);
        }

        return image;
    }


    private BufferedImage composite(final BufferedImage background, final BufferedImage foreground) {
        BufferedImage compositedImage = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        Graphics2D g = compositedImage.createGraphics();
        g.drawImage(background, 0, 0, null);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(foreground,0,0,background.getWidth(), background.getHeight(),null);
        g.dispose();
        return compositedImage;
    }

    private BufferedImage composite(final BufferedImage background, final List<Entity> entities) {

        BufferedImage compositedImage = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        Graphics2D g = compositedImage.createGraphics();

        g.drawImage(background, 0, 0, null);

        for (Entity entity : entities) {
            if (entity.image != null) {
                g.drawImage(entity.image, entity.x, entity.y, null);
            }
        }

        g.dispose();
        return compositedImage;
    }

    private BufferedImage loadImage(String imageName) {

        BufferedImage loaded = null;
        try (InputStream fin = getClass().getResourceAsStream("images/" + imageName)) {
            loaded = ImageIO.read(fin);
            fin.close();
            return loaded;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mouseClicked (MouseEvent e) {
        System.out.println("click");
    }

    public void mousePressed (MouseEvent e) {

        int x = e.getX() + currentMasterPosX;
        int y = e.getY() + currentMasterPosY;

        Polygon p = hexmaprenderer.getPolygon(x,y);

        if (p != null) {

            Hex h = hexmaprenderer.getHexFromPoly(p);

            if (h.isSelected())
                hexmap.deselect(h);
            else
                hexmap.select(h);

            hexmaprenderer.requiresUpdate = true;
        }
    }

    public void mouseReleased (MouseEvent e) {

    }

    public void mouseEntered (MouseEvent e) {

    }

    public void mouseExited (MouseEvent e) {

    }
}
