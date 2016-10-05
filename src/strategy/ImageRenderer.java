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
    public BufferedImage cachedImage;
    private boolean requiresUpdate = true;

    private int currentMasterPosX = 0, currentMasterPosY = 0;
    private int priorMouseX = 0, priorMouseY = 0;

    public ScaleFactor currentScale = ScaleFactor.MAXIMUM;

    HexMap hexmap;
    HexMapRenderer hexmaprenderer;

    List<Entity> entities;

    BufferedImage backgroundImageFullSize, backgroundImageScaled;


    public ImageRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        backgroundImageFullSize = loadImage("img02.jpg");
        backgroundImageScaled = backgroundImageFullSize;
        cachedImage = new BufferedImage(width, height, BufferedImage.OPAQUE);
        scaleBackgroundImage();

        entities = new ArrayList<Entity>();
        //entities.add(new Entity(loadImage("ship01.png"), image.getWidth() / 2, image.getHeight() / 2));

        hexmap = new HexMap(22,32);
        hexmaprenderer = new HexMapRenderer(
                hexmap,
                backgroundImageFullSize.getWidth(),
                backgroundImageFullSize.getHeight(),
                currentScale);

        PointerInfo pInfo = MouseInfo.getPointerInfo();

        currentMasterPosX = pInfo.getLocation().x;
        currentMasterPosY = pInfo.getLocation().y;
    }

    public BufferedImage update() {

        if (requiresUpdate == false)
            return cachedImage;

        PointerInfo pInfo = MouseInfo.getPointerInfo();

        int x = pInfo.getLocation().x;
        int y = pInfo.getLocation().y;

        // Scroll region : right
        if (x >= cachedImage.getWidth() - 5) {
            currentMasterPosX = currentMasterPosX + Math.abs(priorMouseX - x);

            if (currentMasterPosX >= backgroundImageScaled.getWidth() - cachedImage.getWidth())
                currentMasterPosX = backgroundImageScaled.getWidth() - cachedImage.getWidth();
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
        else if (y >= cachedImage.getHeight() - 5) {
            currentMasterPosY = currentMasterPosY + Math.abs(priorMouseY - y);

            if (currentMasterPosY >= backgroundImageScaled.getHeight() - cachedImage.getHeight())
                currentMasterPosY = backgroundImageScaled.getHeight() - cachedImage.getHeight();
        } else if (y > priorMouseY) {
            //currentMasterPosY = currentMasterPosY + (y - priorMouseY);
            priorMouseY = pInfo.getLocation().y;
        } else if ((y < priorMouseY) || (y <= 0)) {
            //currentMasterPosY = currentMasterPosY - (priorMouseY - y);
            priorMouseY = pInfo.getLocation().y;
        }

        if (currentMasterPosX > backgroundImageScaled.getWidth())
            currentMasterPosX = backgroundImageScaled.getWidth() - cachedImage.getWidth();
        else if (currentMasterPosX <= 0)
            currentMasterPosX = 0;

        if (currentMasterPosY > backgroundImageScaled.getHeight())
            currentMasterPosY = backgroundImageScaled.getHeight() - cachedImage.getHeight();
        else if (currentMasterPosY <= 0)
            currentMasterPosY = 0;

        try {
            cachedImage = backgroundImageScaled.getSubimage(currentMasterPosX, currentMasterPosY, width, height);
            BufferedImage hexOverlay = hexmaprenderer.renderHexmap().getSubimage(currentMasterPosX, currentMasterPosY, width, height);
            cachedImage = composite(cachedImage, hexOverlay, 0.65f);
            cachedImage = composite(cachedImage, entities);

        } catch (RasterFormatException e) {
            System.out.println(e.toString());
            System.out.println("currentMasterPosX : " + currentMasterPosX);
            System.out.println("currentMasterPosY : " + currentMasterPosY);
        }

        return cachedImage;
    }

    public void zoomIn() {
            currentScale = currentScale.increase();
            hexmaprenderer.setDrawingDimensions(currentScale);
            scaleBackgroundImage();
            requiresUpdate = true;
            update();
    }

    public void zoomOut() {
            currentScale = currentScale.decrease();
            hexmaprenderer.setDrawingDimensions(currentScale);
            scaleBackgroundImage();
            requiresUpdate = true;
            update();
    }

    private void scaleBackgroundImage() {

        // Try to scale the background image
        float scaleFactor = (1 + currentScale.sequence) / 6.0f;

        int newWidth = (int)(backgroundImageFullSize.getWidth() * scaleFactor);
        int newHeight = (int)(backgroundImageFullSize.getHeight() * scaleFactor);

        if ( (newWidth >= width) && (newHeight >= height) ) {

            System.out.println("scale: " + scaleFactor + ": " + newWidth + "x" + newHeight);

            BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.OPAQUE);
            Graphics g = scaled.getGraphics();
            //g.drawImage(backgroundImageFullSize.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_FAST), 0, 0, null);
            g.drawImage(backgroundImageFullSize, 0, 0, newWidth,  newHeight, null);

            g.dispose();

            currentMasterPosX = (int)(currentMasterPosX * scaleFactor);
            currentMasterPosY = (int)(currentMasterPosY * scaleFactor);

            backgroundImageScaled = scaled;
            requiresUpdate = true;
        }

//        int suggestedWidth = (int)(width * zoomLevel);
//        int suggestedHeight = (int)(height * zoomLevel);
//
//        if (suggestedWidth < width)
//            suggestedWidth = width;
//        if (suggestedHeight < height)
//            suggestedHeight = height;

//
//        Image temp = backgroundImageFullSize.getScaledInstance(suggestedWidth, suggestedHeight, BufferedImage.SCALE_SMOOTH);
//        backgroundImageScaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = backgroundImageScaled.createGraphics();
//        g2.drawImage(temp,0,0,width, height, null);
//        g2.dispose();
//
//        currentMasterPosX = (int)(currentMasterPosX * zoomLevel);
//        currentMasterPosY = (int)(currentMasterPosY * zoomLevel);
//        priorMouseX = currentMasterPosX;
//        priorMouseY = currentMasterPosY;
//
    }

    private BufferedImage composite(final BufferedImage background, final BufferedImage foreground, float alpha) {
        BufferedImage compositedImage = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        Graphics2D g = compositedImage.createGraphics();
        g.drawImage(background, 0, 0, null);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
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
            System.out.println("loaded " + imageName + ": " + loaded.getWidth() + "x" + loaded.getHeight());
            return loaded;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    public void keyPressed(KeyEvent e) {    }

    public void keyReleased(KeyEvent e) {    }

    public void keyTyped(KeyEvent e) {    }

    public void mouseClicked (MouseEvent e) {   }

    public void mousePressed (MouseEvent e) {

        int x = e.getX() + currentMasterPosX;
        int y = e.getY() + currentMasterPosY;

        Polygon p = hexmaprenderer.getPolygon(x,y);

        if (p != null) {

            Hex h = hexmaprenderer.getHexFromPoly(p);

            if (h == null)
                return;

            if (h.isSelected())
                hexmap.deselect(h);
            else
                hexmap.select(h);

            hexmaprenderer.requestUpdate();
        }
    }

    public void mouseReleased (MouseEvent e) {    }

    public void mouseEntered (MouseEvent e) {    }

    public void mouseExited (MouseEvent e) {    }
}
