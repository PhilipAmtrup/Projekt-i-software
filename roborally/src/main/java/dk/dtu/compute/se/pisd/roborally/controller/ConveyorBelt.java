/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space in a RoboRally game.
 * The conveyor belt moves player's robots in a certain direction (heading) when they end a turn on it.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class ConveyorBelt extends FieldAction {

    private Heading heading;

    /**
     * Returns the heading of the conveyor belt, which is the direction in which it moves player's robots.
     * @return the heading of the conveyor belt.
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sets the heading of the conveyor belt.
     *
     * @param heading the new heading of the conveyor belt.
     */
    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Defines the action of the conveyor belt, moving the player's robot to the next space in the heading direction.
     * @author s230577
     * @param gameController the game controller, used to manage game flow.
     * @param space the current space of the player's robot.
     * @return true if the player's robot was moved; false otherwise.
     */
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        Board board = gameController.board;
        if (player != null) {
            Heading heading = getHeading();
            Space spaceNew = board.getNeighbour(space, heading);
            if (spaceNew != null) {
                try {
                    gameController.moveToSpace(player, spaceNew, heading);
                    player.setHeading(heading);
                    return true;
                } catch (ImpossibleMoveException ex) {
                    // Handle the exception here, you could perhaps log it or communicate this issue to the user
                    return false;
                }
            }
        }
        return false;
    }
}
