package strategy;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


public class Hex implements Serializable
{

    private int row, col;
    private Polygon myPoly;
    private ArrayList<Entity> occupyingEntitys;
    private boolean [] openFaces;
    private boolean isSelected;


    public Hex(int rws, int cls)
    {
        row = rws;
        col = cls;

        occupyingEntitys = new ArrayList<Entity>();
        myPoly = null;
        openFaces = new boolean[6];
        for (int i = 0; i < 6; i++) {
            openFaces[i] = false;
        }
        isSelected = false;

    }


    public Hex (int rows, int cols, boolean [] faces) {
        this(rows, cols);
        try {
            for (int i = 0; i < 6; i++) {
                openFaces[i] = faces[i];
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ARRAY INDEX OUT OF BOUNDS EXCEPTION on " + rows + "," + cols);
            openFaces = new boolean [6];
        }

    }


    public void rotateClockWise () {

        boolean newOrientation[] = new boolean[6];

        newOrientation[0] = openFaces[5];
        newOrientation[1] = openFaces[0];
        newOrientation[2] = openFaces[1];
        newOrientation[3] = openFaces[2];
        newOrientation[4] = openFaces[3];
        newOrientation[5] = openFaces[4];

        openFaces = newOrientation;

    }

    public void rotateCounterCW () {

        boolean newOrientation[] = new boolean[6];

        newOrientation[0] = openFaces[1];
        newOrientation[1] = openFaces[2];
        newOrientation[2] = openFaces[3];
        newOrientation[3] = openFaces[4];
        newOrientation[4] = openFaces[5];
        newOrientation[5] = openFaces[0];

        openFaces = newOrientation;
    }


    public void setPolygon(Polygon poly) { myPoly = poly; }

    public Polygon getPolygon() { return myPoly; }

    public void addOccupyingEntity (Entity unit) {
        occupyingEntitys.add(unit);
    }

    public void removeOccupyingEntity (Entity unit) {
        occupyingEntitys.remove(unit);
    }

    public boolean isOccupied() {
        if ((occupyingEntitys == null) || (occupyingEntitys.isEmpty()))
            return (false);
        else
            return (true);
    }

    public ArrayList<Entity> getOccupyingEntities() {
        return occupyingEntitys;
    }


    public void select() {
        isSelected = true;
    }

    public void deselect() {
        isSelected = false;
    }

    public boolean isSelected() {
        return (isSelected);
    }

    public int getRow() {
        return (row);
    }

    public int getCol() {
        return (col);
    }
}

    /*
    public void addRidge(Hex h1, int face1, Hex h2, int face2)
    {
        if (ridges == null)
        {
            ridges = new LinkedList();
            ridges.clear();
        }

        ridges.add(new Ridge(h1,face1,h2,face2));
    }

    public LinkedList<Ridge>  getRidges()
    {
        return ridges;
    }


    public boolean sharesRidgeWithThisHex(Hex thisHex)
    {
        if (ridges == null)
            return false;

        else
        {
            java.util.Iterator iter = ridges.iterator();
            Ridge thisRidge;

            while (iter.hasNext())
            {
                thisRidge = (Ridge)iter.next();

                if (thisRidge.hexB.equals(thisHex))
                {
                    return true;
                }
            }

        }

        return (false);
    }
}

class Ridge implements Serializable
{
    public Hex hexA, hexB;
    int faceA, faceB;

    Ridge(Hex hexOne, int faceOne, Hex hexTwo, int faceTwo)
    {
        hexA = hexOne;
        faceA = faceOne;
        hexB = hexTwo;
        faceB = faceTwo;
    }
}
*/
