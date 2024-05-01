package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Laser extends FieldAction{

    private int x;
    private int y;
    private int reduceHealth;

    public Laser(int x, int y, int reduceHealth) {
        this.x = x;
        this.y = y;
        this.reduceHealth = 10;

    }
    public int getX() { return this.x;}

    public void setX(int x) {this.x = x;}

    public void setY(int y) {this.y = y;}

    public int getY() { return this.y;}

    public void setReduceHealth(int reduceHealth) {this.reduceHealth = reduceHealth;}

    public int getReduceHealth() { return reduceHealth;}

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player =  space.getPlayer();
        if (player != null){
            player.reduceHealth(reduceHealth);
            return true;
        }
        return false;
    }
}
