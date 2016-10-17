package strategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;


public class RenderThread extends Thread {

    protected int screenWidth, screenHeight;
    private int currentMasterPosX = 0, currentMasterPosY = 0;
    private int currentMouseX = 0, currentMouseY = 0;
    private int priorMouseX = 0, priorMouseY = 0;

    public BufferedImage backgroundImageFullSize;
    public BufferedImage backgroundImageScaled;
    public BufferedImage backgroundHexMapOverlay;
    public BufferedImage masterComposite;
    public BufferedImage cachedImage;

    DrawPanel drawOnMe;
    ScaleFactor currentScale;
    HexMap hexmap;
    HexMapRenderer hexmaprenderer;
    List<Entity> entities;

    private boolean ready = false;
    private boolean isAlive = true;
    private boolean requiresUpdate = true;
    private boolean requiresRender = true;


    public RenderThread (int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        backgroundImageFullSize = loadImage("img02.jpg");
        backgroundImageScaled = backgroundImageFullSize;
        cachedImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.OPAQUE);

        entities = new ArrayList<Entity>();
        //entities.add(new Entity(loadImage("ship01.png"));

        hexmap = new HexMap(14,20);

        currentScale = new ScaleFactor(hexmap, screenWidth, screenHeight, backgroundImageFullSize.getWidth(), backgroundImageFullSize.getHeight());

        hexmaprenderer = new HexMapRenderer(
                hexmap,
                backgroundImageFullSize.getWidth(),
                backgroundImageFullSize.getHeight(),
                currentScale);

        PointerInfo pInfo = MouseInfo.getPointerInfo();
        currentMasterPosX = pInfo.getLocation().x;
        currentMasterPosY = pInfo.getLocation().y;

        ready = true;
    }

    public void setTargetPanel(DrawPanel drawOnMe) {
        this.drawOnMe = drawOnMe;
    }

    @Override
    public void run() {

        while (isAlive) {

            cachedImage = update();
            drawOnMe.image = cachedImage;
            drawOnMe.repaint();

            try {
                Thread.sleep(15);
            }
            catch (InterruptedException ie) {
                System.out.println(ie.toString());
            }
        }
    }

    public synchronized void quit() {
        isAlive = false;
    }

    public synchronized void updateMousePosition (MouseEvent e) {
        currentMouseX = e.getX();
        currentMouseY = e.getY();
    }

    public synchronized BufferedImage update() {

        // Scroll region: RIGHT
        // When the mouse enters the rightmost 5 pixels, begin scrolling
        if (currentMouseX >= cachedImage.getWidth() - 5) {
            currentMasterPosX = currentMasterPosX + Math.abs(priorMouseX - currentMouseX);
            requiresUpdate = true;

            // Be sure to not to go so far right as to run out of background
            if (currentMasterPosX >= backgroundImageScaled.getWidth() - cachedImage.getWidth())
                currentMasterPosX = backgroundImageScaled.getWidth() - cachedImage.getWidth();
        }

        // scroll region : LEFT
        else if (currentMouseX <= 5) {
            currentMasterPosX = currentMasterPosX - Math.abs(priorMouseX - currentMouseX);
            requiresUpdate = true;

            // Be sure not to go so far left as to run out of background
            if (currentMasterPosX <= 0)
                currentMasterPosX = 0;
        }

        // Normal motion: capture coordinates but do not update
        else if (currentMouseX > priorMouseX) {
            priorMouseX = currentMouseX;
        } else if ((currentMouseX < priorMouseX) || (currentMouseX <= 0)) {
            //currentMasterPosX = currentMasterPosX - (priorMouseX - x);
            priorMouseX = currentMouseX;
        }


        // Scroll region : UP
        if (currentMouseY <= 5) {
            currentMasterPosY = currentMasterPosY - Math.abs(priorMouseY - currentMouseY);
            requiresUpdate = true;

            if (currentMasterPosY <= 0)
                currentMasterPosY = 0;
        }
        // Scroll region: down
        else if (currentMouseY >= cachedImage.getHeight() - 5) {
            currentMasterPosY = currentMasterPosY + Math.abs(priorMouseY - currentMouseY);
            requiresUpdate = true;

            if (currentMasterPosY >= backgroundImageScaled.getHeight() - cachedImage.getHeight())
                currentMasterPosY = backgroundImageScaled.getHeight() - cachedImage.getHeight();
        }

        // Normal motion: capture coordinates but do not update
        else if (currentMouseY > priorMouseY) {
            priorMouseY = currentMouseY;
        } else if ((currentMouseY < priorMouseY) || (currentMouseY <= 0)) {
            priorMouseY = currentMouseY;
        }

        // Sanity check: adjust view window
        if (currentMasterPosX > backgroundImageScaled.getWidth())
            currentMasterPosX = backgroundImageScaled.getWidth() - cachedImage.getWidth();
        else if (currentMasterPosX <= 0)
            currentMasterPosX = 0;

        if (currentMasterPosY > backgroundImageScaled.getHeight())
            currentMasterPosY = backgroundImageScaled.getHeight() - cachedImage.getHeight();
        else if (currentMasterPosY <= 0)
            currentMasterPosY = 0;


        if ((requiresUpdate == false) && (requiresRender == false))
            return cachedImage;

        if (requiresRender) {

            try {

                backgroundImageScaled = scaleBackgroundImage();
                hexmaprenderer.setDrawingDimensions(currentScale);
                hexmaprenderer.requestUpdate();
                backgroundHexMapOverlay = hexmaprenderer.renderHexmap();

                masterComposite = composite(backgroundImageScaled, backgroundHexMapOverlay, 0.65f);

                BufferedImage currentWindow = masterComposite.getSubimage(currentMasterPosX, currentMasterPosY, screenWidth, screenHeight);
//                currentWindow = composite(currentWindow, entities);
                cachedImage = currentWindow;
                requiresRender = false;
                //requiresUpdate = false;

            } catch (RasterFormatException e) {
                System.out.println("_______________________");
                System.out.println("screenWidth: " + screenWidth);
                System.out.println("screenHeight: " + screenHeight);
                System.out.println("bkrndImageScaled = " + backgroundImageScaled.getWidth() + "x" + backgroundImageScaled.getHeight());
                System.out.println("currentMasterPosX : " + currentMasterPosX);
                System.out.println("currentMasterPosY : " + currentMasterPosY);
                e.printStackTrace();
                System.out.println(e.toString());
            }
        }

        if (requiresUpdate) {
            requiresUpdate = false;
            cachedImage = masterComposite.getSubimage(currentMasterPosX, currentMasterPosY, screenWidth, screenHeight);
        }

        return cachedImage;
    }

    public void zoomIn(int levels) {
        if (currentScale.increase(levels) ) {
            hexmaprenderer.setDrawingDimensions(currentScale);
            requiresUpdate = true;
            requiresRender = true;
        }
    }

    public void zoomOut(int levels) {
        if (currentScale.decrease(levels)) {
            hexmaprenderer.setDrawingDimensions(currentScale);
            requiresUpdate = true;
            requiresRender = true;
        }
    }

    private BufferedImage scaleBackgroundImage() {

        System.out.println("scaleBackgroundImage: " + currentScale.getMapWidth() + "x" + currentScale.getMapHeight());

        BufferedImage scaled = new BufferedImage(currentScale.getMapWidth(), currentScale.getMapHeight(), BufferedImage.OPAQUE);
        Graphics g = scaled.getGraphics();
        g.drawImage(backgroundImageFullSize, 0, 0, currentScale.getMapWidth(),  currentScale.getMapHeight(), null);

        g.dispose();

        currentMasterPosX = (int)(currentMasterPosX * currentScale.getScaleFactor());
        currentMasterPosY = (int)(currentMasterPosY * currentScale.getScaleFactor());

        // Prevent raster exception
        if (currentMasterPosX + screenWidth > scaled.getWidth())
            currentMasterPosX = 0;
        if (currentMasterPosY + screenHeight > scaled.getHeight())
            currentMasterPosY = 0;

        requiresRender = true;
        requiresUpdate = true;

        return scaled;

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
