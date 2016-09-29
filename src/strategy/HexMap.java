package strategy;

/*
HEXMAP
Keeps track of a map of hexagons. Contains and manages several sizes/aspects of the same
map (large, medium, small, for example).

A LITTLE REMINDER ABOUT MULTI-DIMENSIONAL ARRAYS FOR THE AUTHOR
Multi-dimensional arrays are a little tricky. Just keep in mind a few things:
--  When we make an multi-dimensional array, we're making a multiple LAYERS of sequences.
--  So, if you want to end up with a grid that is, say, 7 units wide by 3 units deep, the declaration would be:
--  array[3][7] , or three layers, each seven units long
--  Now, we're sort of slaved to the (x,y) notion of naming a particular position within the grid.
--  For instance, if you want the spot three units over and two down, you have to flip it around to array[2][3]

            TL;DR: think array[ROW][COLUMN] when CREATING
                   but think array[COLUMN][ROW] when ACCESSING WITH AN (X,Y) MENTALITY

                   or [LAYER][COLUMN]

 */

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

//HEXMAP
public class HexMap implements Serializable
{
    int rows, cols;

    Hex hexArray[][];

    LinkedList<Hex> hexList;
    LinkedList<Polygon> polyList;
    LinkedList<Hex> selectedHexes;
    LinkedList<Hex> adjacentHexes;
    LinkedList<Hex> highlightedHexes;

    int beginDrawingFromX, beginDrawingFromY;


    //Constructor
    public HexMap(int rws, int cls)
    {
        rows = rws;
        cols = cls;

        hexList = new <Hex>LinkedList();
        hexList.clear();

        polyList = new <Polygon>LinkedList();
        polyList.clear();

        selectedHexes = new <Hex>LinkedList();
        selectedHexes.clear();

        adjacentHexes = new <Hex>LinkedList();
        adjacentHexes.clear();

        highlightedHexes = new <Hex>LinkedList();
        highlightedHexes.clear();

        hexArray = new Hex[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                hexArray[i][j] = new Hex(i,j);
                hexList.add(hexArray[i][j]);
            }
        }



    }

    public Hex getHexAtCoords (int row, int col) {

        Hex target = null;

        try {
            target  = hexArray[row][col];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.toString());
        }
        finally {
            return target;
        }

    }


    //*** UNIT MANAGMENT ***
    //Adds a unit to a hex.
    public void addEntityToHex(Hex targetHex, Entity unit)
    {
        targetHex.addOccupyingEntity(unit);
    }



    public void select(Hex thisOne)
    {
        selectedHexes.add(thisOne);
        thisOne.select();
    }

    // Select Unoccupied Hex
    // For temporarily highlighting a hex
    public void highlightHex(Hex thisOne) {
        if (thisOne != null) {
            highlightedHexes.add(thisOne);
        }
    }

    public void removeHighlights() {
        highlightedHexes.clear();
    }

    public void deselect (Hex thisOne) {
        selectedHexes.remove(thisOne);
        thisOne.deselect();
    }


    public LinkedList<Hex> getAdjacentHexes (Hex thisHex) {
        return (getAdjacentHexes(thisHex.getRow(),thisHex.getCol()));
    }


    public LinkedList<Hex> getAdjacentHexes (Polygon thisOne) {

        Iterator hexes = hexList.iterator();
        Hex thisHex;

        while (hexes.hasNext())
        {
            thisHex = (Hex)hexes.next();

            if (thisHex.getPolygon() == thisOne)
            {
                return (getAdjacentHexes(thisHex.getRow(),thisHex.getCol()));
            }
        }

        return (null);

    }

    // ** GET ADJACENT HEXES
    //Returns a list of up to six adjacent hexes to the one specified
    //Based on the weird rules of hexes in 2D arrays
    //NOTE: this function was returning 8 hexes instead of 6 until Sets were introduced.
    public LinkedList<Hex> getAdjacentHexes(int row, int col)
    {
        java.util.Set<Hex> adjHexes = new java.util.HashSet();
        adjHexes.clear();

        //All hexes are adjacent to thsoe ABOVE AND BELOW (the ones in the same column, +- 1);

        //ABOVE
        if ((row-1) >= 0)
            adjHexes.add(hexArray[row-1][col]);
        //BELOW
        if ((row+1) <= (rows-1))
            adjHexes.add(hexArray[row+1][col]);
        //RIGHT
        if ((col+1) <= (cols-1))
            adjHexes.add(hexArray[row][col+1]);
        //LEFT
        if((col-1) >= 0)
            adjHexes.add(hexArray[row][col-1]);

        //EVEN ROW RULES
        if ((col%2) == 0)
        {
            if (((row-1) >= 0) && ((col+1) <= (cols-1)))
                adjHexes.add(hexArray[row-1][col+1]);
            if (((row-1) >= 0) && ((col-1) >= 0))
                adjHexes.add(hexArray[row-1][col-1]);
            if((col-1) >= 0)
                adjHexes.add(hexArray[row][col-1]);
            if((col+1) <= (cols-1))
                adjHexes.add(hexArray[row][col+1]);
        }

        //ODD ROWS
        else if (col%2 != 0)
        {
            if (((row+1) <= (rows-1)) && ((col+1) <= (cols-1)))
                adjHexes.add(hexArray[row+1][col+1]);
            if (((row+1) <= (rows-1)) && ((col-1) >= 0))
                adjHexes.add(hexArray[row+1][col-1]);
            if((col+1) <= (cols-1))
                adjHexes.add(hexArray[row][col+1]);
            if ((col-1) >= 0)
                adjHexes.add(hexArray[row][col-1]);
        }

        return (convertToHexLinkedList(adjHexes));
    }


    //GET HEXES WITHIN RANGE
    //Returns a list of hexes in a "shell" radius within range.
    //If the ignoreCrater flag is set to false, then craters are considered obstructions
    //Likewise, if ignoreRidges is set to false, hexes which share a ridge will be obstructions
    //Relies upon the recursive version of the function of the same name (below)
    public LinkedList<Hex> getHexesWithinRange(Hex fromHere, int distance, boolean ignoreCrater, boolean ignoreRidges, LinkedList<Hex> obstructedHexes)
    {
        //NOTE: the use of sets here was CRUCIAL to solving this problem.
        //A set, as you know, may not contain duplicates. Duplicate entries caused all manner of grief
        //in debugging. Lordy. I figured it out in the shower, addding weight to the good ol' "Shower Principle."
        //

        Set<Hex> tempAdjacents = new HashSet();
        Set<Hex> returnList = new HashSet();
        //LinkedList<Hex> returnList = new LinkedList();
        Set<Hex> doneThese = new HashSet(); //prevents the same hex being called multiple THOUSANDS of times.
        Set<Hex> ignoreThese = new HashSet();

        tempAdjacents.clear();
        returnList.clear();
        doneThese.clear();
        ignoreThese.clear();

        Iterator iter;
        Hex centerHex, thisHex, tempHex;

        //SCENARIO 1: BOTH CRATERS AND RIDGES ARE IGNORED (SHOOTING)
        //If there's no need to process the validity of each hex (crater, ridges), simply
        //add all the concentric rings
        if ((ignoreCrater == true) && (ignoreRidges == true))
        {
            tempAdjacents.addAll(getAdjacentHexes(fromHere));
            returnList.addAll(tempAdjacents);
            doneThese.add(fromHere);

            for (int i = 0; i < distance-1; i++)
            {
                iter = tempAdjacents.iterator();

                while (iter.hasNext())
                {
                    thisHex = (Hex)iter.next();

                    if (!doneThese.contains(thisHex))
                    {
                        returnList.addAll(getAdjacentHexes(thisHex));
                        doneThese.add(thisHex);
                    }
                }

                tempAdjacents.addAll(returnList);
            }

        }

        //SCENARIO 2:
        //RIDGES and CRATERS should be processed/weeded out
        //for MOVEMENT. We should also consider hexes occupied by enemy units to be blocked
        else
        {

            //NOTE: strangely, casting the Sets to LinkedLists does not affect their "one instance per list" behavior.
            //(Hex center, LinkedList<Hex> doneThese, LinkedList<Hex> ignoreThese, LinkedList<Hex> returnList)
            returnList.addAll(getHexesWithinRange(fromHere, convertToHexLinkedList(doneThese), convertToHexLinkedList(ignoreThese), ignoreRidges, obstructedHexes));
            ignoreThese.clear();

            //NOTE: the ignoreList is cleared at the end of each "ring's" examination;
            //a hex which is not accessible from one hex (shares a ridge) might be accessible from another.
            //If those "ignored" hexes are allowed to persists from one concentric exam to another,
            //they will appaear as totally inaccessible-- not good.

            for (int i = 0; i < distance-1; i++)
            {
                iter = returnList.iterator();

                while (iter.hasNext())
                {
                    thisHex = (Hex)iter.next();
                    tempAdjacents.addAll(getHexesWithinRange(thisHex, convertToHexLinkedList(doneThese), convertToHexLinkedList(ignoreThese), ignoreRidges, obstructedHexes));
                }

                if (obstructedHexes != null)
                    tempAdjacents.removeAll(obstructedHexes);

                returnList.addAll(tempAdjacents);
                tempAdjacents.clear();
                ignoreThese.clear();
            }

        }


        // Finally, weed out the enemy-occupied hexes
        if (obstructedHexes != null) {
            returnList.removeAll(obstructedHexes);
        }

        return (convertToHexLinkedList(returnList));
    }

    //GET HEXES WITHIN RANGE
    //My thinking was this:
    //A "center" hex and its 6 neighbors are examined. If there is a ridge between them (and ridges are not ignored),
    //then that hex is inaccessibe from the center hex and is "skipped" for a round of examinations.
    //That hex may be accessible from another neighbor hex, however, and if it can be found in the list of accessible
    //neighbors then it is removed from the skip list at the end.
    public LinkedList<Hex> getHexesWithinRange(Hex center, LinkedList<Hex> doneThese, LinkedList<Hex> ignoreThese, boolean ignoreRidges, LinkedList<Hex> obstructedHexes)
    {

        LinkedList<Hex> returnList = new LinkedList();
        returnList.clear();

        if (center != null)
        {
            //Get the surrounding six hexes around the center
            Iterator iter = getAdjacentHexes(center).iterator();
            Hex thisHex;

            while (iter.hasNext())
            {
                thisHex = (Hex)iter.next();

                //If it is in neither the "done" nor "ignore" pile...
                if ((!doneThese.contains(thisHex) && (!ignoreThese.contains(thisHex))))
                {

                    //Shares a ridge
                    //if ((ignoreRidges == false) && ((center.sharesRidgeWithThisHex(thisHex)) && (!doneThese.contains(thisHex))))
                    //  ignoreThese.add(thisHex);
                    //Contains an enemy Entity
                    //TODO: add this here. What do we need? An OgreEntity will be able to RAM...
                    if (obstructedHexes.contains(thisHex))
                        ignoreThese.add(thisHex);

                    if (!ignoreThese.contains(thisHex))
                    {
                        returnList.add(thisHex);
                    }
                }

            }

            doneThese.add(center);


            //Reintorduce hexes which were "skipped" by putting them into the ignore pile if they were in fact
            //accessible from another hex in this "ring." The ignoreList is cleared when all hexes wihin the ring
            //have been examined.
            iter = ignoreThese.iterator();
            while (iter.hasNext())
            {
                thisHex = (Hex)iter.next();

                if (returnList.contains(thisHex))
                {
                    ignoreThese.remove(thisHex);
                }
            }

        }

        return returnList;
    }

    //DESELECT ALL SELECTED HEXES
    //Cycles through the selectedHexes list and deselects them all, clears the list
    public void deselectAllSelectedHexes()
    {
        Iterator iter = selectedHexes.iterator();
        Hex current;

        while (iter.hasNext())
        {
            current = (Hex)iter.next();
            current.deselect();
        }

        selectedHexes.clear();
    }

    //COMPUTE OVERLAPPING HEXES
    //Gets the overlapping "hexes in common" with  multi-firing wepaon solution...or something.
    //The "comon zone of fire" hexes will be listed in the adjacentHexes list
    /*
    public void computeOverlappingHexes(Player currentPlayer, OgreGame gameMaster)
    {
       adjacentHexes.clear();

       Iterator iter = selectedHexes.iterator();
       Hex thisHex;

       if (iter.hasNext())
       {
           thisHex = (Hex)iter.next();
           //Obtain a friendly unit from the current selections; cycle through til one is found
           while ((!currentPlayer.units.contains(thisHex.occupyingEntity)) && iter.hasNext())
           {
               thisHex = (Hex)iter.next();
           }

           //Get a single zone of fire from the friendly unit...
           if ((currentPlayer.units.contains(thisHex.occupyingEntity)) && (thisHex.occupyingEntity.unitWeapon != null))
           {
                adjacentHexes.addAll(getHexesWithinRange(thisHex, thisHex.occupyingEntity.unitWeapon.range,true,true, null));
           }
       }

       while (iter.hasNext())
       {
           thisHex =(Hex)iter.next();
           //...and get the "intersection" between the first unit's zone and the rest of the frinedly units' zones
           if ((currentPlayer.units.contains(thisHex.occupyingEntity)) && (thisHex.occupyingEntity.unitWeapon != null))
           {
                adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisHex.occupyingEntity.unitWeapon.range, true, true, null));
           }
       }

       //Add in any selectedOgreWeapons


       iter = selectedHexes.iterator();


        if (gameMaster.selectedOgreWeapons != null)
        {
            Iterator weaponIter = gameMaster.selectedOgreWeapons.iterator();
            Weapon thisWeapon;

            while (iter.hasNext())
            {
                thisHex = (Hex)iter.next();

                if (thisHex.occupyingEntity.unitType == EntityType.Ogre)
                {
                    OgreEntity thisOgre = (OgreEntity)thisHex.occupyingEntity;

                    if ((weaponIter.hasNext()) && (adjacentHexes.size() <= 1))
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        adjacentHexes.addAll(getHexesWithinRange(thisHex, thisWeapon.range, true,true, null));
                    }

                    while (weaponIter.hasNext())
                    {
                        thisWeapon = (Weapon)weaponIter.next();

                        if (thisOgre.getWeapons().contains(thisWeapon))
                        {
                            adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisWeapon.range, true,true, null));
                        }
                    }
                }
            }
       }

            //updateMapImage();
    }

    */
    //CONVERT TO HEX LINKED LIST
    //TODO: consider just overhauling any function arguments which require lists to instead
    //require Sets.
    public LinkedList<Hex> convertToHexLinkedList(Set convertMe)
    {
        LinkedList<Hex> returnList = new LinkedList();
        returnList.clear();
        Iterator iter = convertMe.iterator();
        Hex thisHex;

        while (iter.hasNext())
        {
            thisHex = (Hex)iter.next();
            returnList.add(thisHex);
        }

        return (returnList);
    }

    /*
    public LinkedList<Hex> getOccupiedHexes(Player player) {
        Iterator iter = player.units.iterator();
        Entity currentEntity;
        LinkedList returnList = new <Hex>LinkedList();
        returnList.clear();

        while (iter.hasNext()){
            currentEntity = (Entity)iter.next();
            returnList.add(hexArray[currentEntity.yLocation][currentEntity.xLocation]);
        }

        return returnList;
    }

    */

}

