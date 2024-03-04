package dk.dtu.compute.se.pisd.roborally.model;

/**
 * Dette repræsenter walls på game boarded. 
 * walls er positioneret på kordinater med rows og collums 
 * @author julius s235462
 */

public class Wall {
    
private int row;

private int col;

    public Wall(int row, int col) {

        this.row = row;
        this.col = col;
    }

    /**
     * gets the row cordinates of the wall.
     * @return The row cordinates of wall.
     */
    public int getRow(){
        return row;
    }
/**
 * gets the collum cordinates of the wall.
 * @return The collum cordinates of the wall.
 */

    public int getCol() {
        return col;
    }

}

