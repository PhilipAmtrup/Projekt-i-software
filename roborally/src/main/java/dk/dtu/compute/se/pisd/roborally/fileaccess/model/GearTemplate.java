package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

/**
 * author Julius s235462
 */

public class GearTemplate extends ActionTemplate {
    private int x;
    private int y;


        // Constructor
        public GearTemplate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Getter for x
        public int getX() {
            return x;
        }

        // Setter for x
        public void setX(int x) {
            this.x = x;
        }

        // Getter for y
        public int getY() {
            return y;
        }

        // Setter for y
        public void setY(int y) {
            this.y = y;
        }
    }