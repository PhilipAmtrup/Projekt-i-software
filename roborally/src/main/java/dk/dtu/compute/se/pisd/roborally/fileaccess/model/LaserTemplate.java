package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class LaserTemplate extends ActionTemplate{
    public int x;
    public int y;
    public int reduceHealth;

    public LaserTemplate(int x, int y, int reduceHealth) {
        this.x = x;
        this.y = y;
        this.reduceHealth = 10;
    }

    public int getX() { return x;}

    public void setX(int x) {this.x = x;}

    public void setY(int y) {this.y = y;}

    public int getY() { return y;}

    public void setReduceHealth(int reduceHealth) {this.reduceHealth = reduceHealth;}
    public int getReduceHealth() { return reduceHealth;}
}
