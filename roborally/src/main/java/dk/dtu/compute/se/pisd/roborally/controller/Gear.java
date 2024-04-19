package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * author Julius s235462
 */
public class Gear extends FieldAction {

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
