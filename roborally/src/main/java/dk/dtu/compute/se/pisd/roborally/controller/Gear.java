package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * author Julius s235462
 */
public class Gear extends FieldAction {
    //true clockwise == right
    //false clockwise == left
    // image names.
    public boolean isClockWise;

    public Gear() {
    }


    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            Heading heading = player.getHeading();
            if (isClockWise) {
                player.setHeading(heading.next());
            } else {
                player.setHeading(heading.prev());
            }
            return true;
        }
        return false;
    }

    public void setIsClockWise(Boolean isClockWise) {
        this.isClockWise = isClockWise;
    }
    public boolean getIsClockWise() {
        return isClockWise;
    }
}