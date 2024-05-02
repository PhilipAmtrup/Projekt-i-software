package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class CheckPointTemplate extends ActionTemplate {
    public int x;
    public int y;
    public int number;
    public Boolean last;

    public CheckPointTemplate(int x, int y, int number, Boolean last) {
        this.x = x;
        this.y = y;
        this.number = number;
        this.last = last;


    }

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
}
