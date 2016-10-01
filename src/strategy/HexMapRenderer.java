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

    HexMap hexMap;
    GameMaster gm;
    BufferedImage cachedImage;

    int hexagonSize;
    int width, height;

    int beginDrawingFromX, beginDrawingFromY;

    public boolean showCoordinates = true;
    boolean requiresUpdate = true;
    public Color hexOutlineColor = Color.WHITE;
    Random rando;

    public HexMapRenderer(HexMap hxMap, int width, int height, int hexagonSize) {

        hexMap = hxMap;

        this.width = width;
        this.height = height;
        this.hexagonSize = hexagonSize;

        beginDrawingFromX = (int)(0.25f * hexagonSize);
        beginDrawingFromY = (int)(0.25f * hexagonSize);

        rando = new Random();

        int rows = hexMap.rows;
        int cols = hexMap.cols;
        
        int x = beginDrawingFromX;
        int y = beginDrawingFromY;

        cachedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = getCachedImage().createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(6.0f));

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                if ((j % 2) != 0) {
                    y = beginDrawingFromY + (int) (.8660 * hexagonSize);
                } else {
                    y = beginDrawingFromY;
                }

                java.awt.Polygon p = new java.awt.Polygon();
                p.reset();

                p.addPoint(x + (hexagonSize / 2), y);
                p.addPoint(x + (hexagonSize / 2) + hexagonSize, y);
                p.addPoint(x + 2 * hexagonSize, (int) (.8660 * hexagonSize + y));
                p.addPoint(x + (hexagonSize / 2) + hexagonSize, (int) (.8660 * 2 * hexagonSize + y));
                p.addPoint(x + (hexagonSize / 2), (int) (.8660 * 2 * hexagonSize + y));
                p.addPoint(x, y + (int) (.8660 * hexagonSize));
                
                g.setColor(hexOutlineColor);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g.drawPolygon(p);

                //associate a hex with this polygon
                associatePolygonWithHex(i, j, p);
                hexMap.polyList.add(p);

                //Coordinates
                if (showCoordinates) {
                    g.setColor(Color.GRAY);
                    g.drawString("[" + hexMap.hexArray[i][j].getCol() + "," + hexMap.hexArray[i][j].getRow() + "]", (x + (int) (hexagonSize / 2)), y + (int) (hexagonSize / 2));
                }

                //scoot the pencil over
                x = x + (hexagonSize / 2) + hexagonSize;

            }// for j (columns)

            //Reset for the next row
            beginDrawingFromY += (int) 2 * (.8660 * hexagonSize);
            x = beginDrawingFromX;

            if ((i % 2) != 0) {
                y = beginDrawingFromY + (int) (.8660 * hexagonSize);
            } else {
                y = beginDrawingFromY;
            }

        }

        g.dispose();
        
    }

    public void setDimensions() {
        
        beginDrawingFromX = (int)(0.25f * hexagonSize);
        beginDrawingFromY = (int)(0.25f * hexagonSize);

        //The formula for the image size is:
        //(hexSize * 1.5 * cols + ( 4 * hexSize)) by (hexSize * 1.5 * rows + ( 4 * hexSize))
        //int sizeX = (int) ((hexagonSize * 2 * cols) + (4 * hexagonSize));
        //int sizeY = (int) ((hexagonSize * 2 * rows) + (4 * hexagonSize));
        
    }
    
    

    //SET HEXAGON SIZE
    public synchronized void setHexSize(int newSize) {
        hexagonSize = newSize;
        requiresUpdate = true;
    }

    public int getHexSize() {
        return (hexagonSize);
    }

    
    public BufferedImage renderHexmap () {

        if (requiresUpdate == false)
            return cachedImage;

        //System.out.println("RE-RENDERING...");

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(6.0f));

        beginDrawingFromX = (int)(0.25f * hexagonSize);
        beginDrawingFromY = (int)(0.25f * hexagonSize);

        int x = beginDrawingFromX;
        int y = beginDrawingFromY;

        int rows = hexMap.rows;
        int cols = hexMap.cols;

        //Draw hex field
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                if ((j % 2) != 0) {
                    y = beginDrawingFromY + (int) (.8660 * hexagonSize);
                } else {
                    y = beginDrawingFromY;
                }

                java.awt.Polygon p = new java.awt.Polygon();
                p.reset();

                p.addPoint(x + (hexagonSize / 2), y);
                p.addPoint(x + (hexagonSize / 2) + hexagonSize, y);
                p.addPoint(x + 2 * hexagonSize, (int) (.8660 * hexagonSize + y));
                p.addPoint(x + (hexagonSize / 2) + hexagonSize, (int) (.8660 * 2 * hexagonSize + y));
                p.addPoint(x + (hexagonSize / 2), (int) (.8660 * 2 * hexagonSize + y));
                p.addPoint(x, y + (int) (.8660 * hexagonSize));

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
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g.fillPolygon(p);
                }

                // paint magenta on highlighted hexes
                if (hexMap.highlightedHexes.contains(hexMap.hexArray[i][j])) {
                    g.setColor(Color.MAGENTA);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g.fillPolygon(p);
                }

                //Draw Units (if any)
                if (hexMap.hexArray[i][j].isOccupied()) {

                    //Rescale and offset
                    BufferedImage unitImage = null;

                    int Xoffset, Yoffset, imageSize;

                    Xoffset = (int) ((2 * hexagonSize) / 5.464);
                    Yoffset = (int) (.66 * hexagonSize - (2 * hexagonSize) / 5.464);
                    imageSize = (int) (2 * (1.732 * Xoffset));

                    g.drawImage(unitImage, x + Xoffset, y + Yoffset, imageSize, imageSize, null, null);
                }

                //Draw basic polygon 
                g.setColor(hexOutlineColor);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g.drawPolygon(p);

                //Coordinates
                if (showCoordinates) {
                    g.setColor(Color.GRAY);
                    g.drawString("[" + hexMap.hexArray[i][j].getCol() + "," + hexMap.hexArray[i][j].getRow() + "]", (x + (int) (hexagonSize / 2)), y + (int) (hexagonSize / 2));
                }

                //Move the pencil over
                x = x + (hexagonSize / 2) + hexagonSize;
            }

            beginDrawingFromY += (int) 2 * (.8660 * hexagonSize);

            x = beginDrawingFromX;
            y = y + (int) (2 * .8660 * hexagonSize);

            if ((i % 2) != 0) {
                y = beginDrawingFromY + (int) (.8660 * hexagonSize);
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