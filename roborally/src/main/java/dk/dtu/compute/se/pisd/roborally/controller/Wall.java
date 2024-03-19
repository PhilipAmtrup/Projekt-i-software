package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

/**
 * Wall klasse bruges til at lave walls på spille brætet.
 * @author Julius s235462
 */
public class Wall {

    private int x;
    private int y;
    private Heading heading;

    public Wall(int x, int y, Heading heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    // Getters og setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /** Denne metode retunere wall Heading
     * @author Julius s235462
     * @return Heading wall 
     */
    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }
}