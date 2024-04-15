package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;



/**
 * @author s235459
 * Her bliver typen CheckPoint defineret, som herefter bliver brugt og kaldt til checkpoint funktioner
 */


 public class CheckPoint extends FieldAction {

    private int number;
    private int x;
    private int y;

    public CheckPoint(int x, int y, int number) {
        this.x = x;
        this.y = y;
        this.number = number;
    }

    public CheckPoint(int number, Boolean last) {
        this.number = number;
        this.x = last ? 1 : 0;
    }

    public int getNumber() {
        return number;
    }


    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null && this.getNumber() == player.getCurrentCheckpoint() + 1) {
            player.setCurrentCheckpoint(this.getNumber());
            return true;
        }
        return false;
    }
}
