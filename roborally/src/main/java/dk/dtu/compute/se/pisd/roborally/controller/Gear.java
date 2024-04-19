package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * author Julius s235462
 */
public class Gear extends FieldAction {
    public int x;
    public int y;

    public Gear(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            // Call the turnRight method from the GameController
            gameController.turnRight(player);
            return true;
        }
        return false;
    }

}