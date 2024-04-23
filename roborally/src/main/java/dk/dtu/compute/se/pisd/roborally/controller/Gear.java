package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * author Julius s235462
 */
public class Gear extends FieldAction {
    public String direction;
    public Gear() {
    }


    @Override
    public boolean doAction(GameController gameController, Space space) {
        System.out.println("yeds22222");
        Player player = space.getPlayer();
        if (player != null) {
            Heading heading = player.getHeading();
            switch (direction) {
                case "Right":
                    player.setHeading(heading.next());
                    break;
                case "Left":
                    player.setHeading(heading.prev());
            }
            return true;
        }
        return false;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public String getDirection() {
        return this.direction;
    }
}