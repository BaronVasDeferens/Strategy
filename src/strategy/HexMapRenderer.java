package strategy;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author skot
 */
public class HexMapRenderer {

    private HexMap hexMap;
    private GameMaster gm;
    private BufferedImage cachedImage;

    private int hexSize;
    private float strokeThickness;
    private int width, height;
    private int beginDrawingFromX, beginDrawingFromY;

    private boolean antialiasingOn = false;
    private boolean showCoordinates = false;

    private boolean requiresUpdate = true;

    public Color hexOutlineColor = Color.BLACK;
    private ScaleFactor currentScale;

    Random rando;

    public HexMapRenderer(HexMap hexMap, int width, int height, ScaleFactor currentScale) {

        this.hexMap = hexMap;
        this.width = width;
        this.height = height;
        this.currentScale = currentScale;
        this.hexSize = currentScale.getHexSize();
        this.strokeThickness = currentScale.getStrokeThickness();

        beginDrawingFromX = (int)(0.25f * hexSize);
        beginDrawingFromY = (int)(0.25f * hexSize);

        rando = new Random();

        int rows = hexMap.rows;
        int cols = hexMap.cols;
        
        int x = beginDrawingFromX;
        int y = beginDrawingFromY;

        cachedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = getCachedImage().createGraphics();
        g.setStroke(new BasicStroke(strokeThickness));

        if (antialiasingOn)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                if ((j % 2) != 0) {
                    y = beginDrawingFromY + (int) (.8660 * hexSize);
                } else {
                    y = beginDrawingFromY;
                }

                java.awt.Polygon p = new java.awt.Polygon();
                p.reset();

                p.addPoint(x + (hexSize / 2), y);
                p.addPoint(x + (hexSize / 2) + hexSize, y);
                p.addPoint(x + 2 * hexSize, (int) (.8660 * hexSize + y));
                p.addPoint(x + (hexSize / 2) + hexSize, (int) (.8660 * 2 * hexSize + y));
                p.addPoint(x + (hexSize / 2), (int) (.8660 * 2 * hexSize + y));
                p.addPoint(x, y + (int) (.8660 * hexSize));
                
                g.setColor(hexOutlineColor);
                if (antialiasingOn)
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawPolygon(p);

                //associate a hex with this polygon
                associatePolygonWithHex(i, j, p);
                hexMap.polyList.add(p);

                //Coordinates
                if (showCoordinates) {
                    g.setColor(Color.GRAY);
                    g.drawString("[" + hexMap.hexArray[i][j].getCol() + "," + hexMap.hexArray[i][j].getRow() + "]", (x + (int) (hexSize / 2)), y + (int) (hexSize / 2));
                }

                //scoot the pencil over
                x = x + (hexSize / 2) + hexSize;

            }// for j (columns)

            //Reset for the next row
            beginDrawingFromY += (int) 2 * (.8660 * hexSize);
            x = beginDrawingFromX;

            if ((i % 2) != 0) {
                y = beginDrawingFromY + (int) (.8660 * hexSize);
            } else {
                y = beginDrawingFromY;
            }

        }

        g.dispose();
        
    }

    public void requestUpdate() {
        requiresUpdate = true;
    }

    public synchronized void setDrawingDimensions (ScaleFactor factor) {
        this.currentScale = factor;
        this.strokeThickness = factor.getStrokeThickness();
        this.hexSize = factor.getHexSize();
        beginDrawingFromX = (int)(0.25f * hexSize);
        beginDrawingFromY = (int)(0.25f * hexSize);
        requiresUpdate = true;
    }

    public synchronized BufferedImage renderHexmap () {

        if (requiresUpdate == false)
            return cachedImage;

        hexMap.polyList.clear();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setStroke(new BasicStroke(currentScale.getStrokeThickness()));

        beginDrawingFromX = (int)(0.25f * hexSize);
        beginDrawingFromY = (int)(0.25f * hexSize);

        int x = beginDrawingFromX;
        int y = beginDrawingFromY;

        int rows = hexMap.rows;
        int cols = hexMap.cols;

        //Draw hex field
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                if ((j % 2) != 0) {
                    y = beginDrawingFromY + (int) (.8660 * hexSize);
                } else {
                    y = beginDrawingFromY;
                }

                java.awt.Polygon p = new java.awt.Polygon();
                p.reset();

                p.addPoint(x + (hexSize / 2), y);
                p.addPoint(x + (hexSize / 2) + hexSize, y);
                p.addPoint(x + 2 * hexSize, (int) (.8660 * hexSize + y));
                p.addPoint(x + (hexSize / 2) + hexSize, (int) (.8660 * 2 * hexSize + y));
                p.addPoint(x + (hexSize / 2), (int) (.8660 * 2 * hexSize + y));
                p.addPoint(x, y + (int) (.8660 * hexSize));

                //associate a hex with this polygon
                associatePolygonWithHex(i, j, p);
                hexMap.polyList.add(p);

                //Adjacent hex colorization
                if (hexMap.adjacentHexes.contains(hexMap.hexArray[i][j])) {
                    g.setColor(Color.PINK);
                    g.fillPolygon(p);
                    g.setColor(Color.BLACK);
                    g.drawPolygon(p);
                    g.setColor(Color.PINK);
                }

                //Paint RED on selected hexes
                if (hexMap.hexArray[i][j].isSelected()) {
                    g.setColor(Color.RED);
                    g.fillPolygon(p);
                }

                // paint magenta on highlighted hexes
                if (hexMap.highlightedHexes.contains(hexMap.hexArray[i][j])) {
                    g.setColor(Color.MAGENTA);
                    g.fillPolygon(p);
                }

                //Draw Units (if any)
                if (hexMap.hexArray[i][j].isOccupied()) {

                    //Rescale and offset
                    BufferedImage unitImage = null;

                    int Xoffset, Yoffset, imageSize;

                    Xoffset = (int) ((2 * hexSize) / 5.464);
                    Yoffset = (int) (.66 * hexSize - (2 * hexSize) / 5.464);
                    imageSize = (int) (2 * (1.732 * Xoffset));

                    g.drawImage(unitImage, x + Xoffset, y + Yoffset, imageSize, imageSize, null, null);
                }

                //Draw basic polygon 
                g.setColor(hexOutlineColor);
                g.drawPolygon(p);

                //Coordinates
                if (showCoordinates) {
                    g.setColor(Color.GRAY);
                    g.drawString("[" + hexMap.hexArray[i][j].getCol() + "," + hexMap.hexArray[i][j].getRow() + "]", (x + (int) (hexSize / 2)), y + (int) (hexSize / 2));
                }

                //Move the pencil over
                x = x + (hexSize / 2) + hexSize;
            }

            beginDrawingFromY += (int) 2 * (.8660 * hexSize);

            x = beginDrawingFromX;
            y = y + (int) (2 * .8660 * hexSize);

            if ((i % 2) != 0) {
                y = beginDrawingFromY + (int) (.8660 * hexSize);
            } else {
                y = beginDrawingFromY;
            }
        }


        g.dispose();
        cachedImage = newImage;
        requiresUpdate = false;
        return cachedImage;
    }

    //ASSOCIATE POLYGON WITH HEX
    private void associatePolygonWithHex(int rw, int cl, Polygon poly) {
        hexMap.hexArray[rw][cl].setPolygon(poly);
    }

    public Polygon getPolygon(int pointX, int pointY) {
        //bounds checking
        Iterator polys = hexMap.polyList.iterator();
        Polygon currentPoly;

        while (polys.hasNext()) {
            currentPoly = (Polygon) polys.next();

            if (currentPoly.contains(pointX, pointY)) {
                return currentPoly;
            }
        }

        return null;
    }

    public Hex getHexFromPoly(Polygon thisOne) {
        Iterator hexes = hexMap.hexList.iterator();
        Hex current;

        while (hexes.hasNext()) {
            current = (Hex) hexes.next();

            if (current.getPolygon() == thisOne) {
                return (current);
            }
        }

        return null;
    }

    //GET HEX FROM COORDS
    public Hex getHexFromCoords(int rw, int cl) {
        if ((rw <= hexMap.rows) && (rw >= 0) && (cl <= hexMap.cols) && (cl >= 0)) {
            return hexMap.hexArray[rw][cl];
        } else {
            return null;
        }
    }

    //GET IMAGE
    //Returns the latest rendering of the image
    public BufferedImage getCachedImage() {
        return (cachedImage);
    }

}
