package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

/**
 * This class represents a checkpoint in a game of RoboRally.
 * Each checkpoint is numbered and has a position on the board represented by x and y coordinates.
 * The `doAction` method checks whether a player has reached the next checkpoint.
 *
 * @author s235459 & s230577
 */
public class CheckPoint extends FieldAction {

    private int number;
    private int x;
    private int y;

    /**
     * Constructor to create a new checkpoint with set x and y coordinates and a number.
     *
     * @param x the x coordinate of the checkpoint.
     * @param y the y coordinate of the checkpoint.
     * @param number the number of the checkpoint.
     */
    public CheckPoint(int x, int y, int number) {
        this.x = x;
        this.y = y;
        this.number = number;
    }

    /**
     * Constructor to create a new checkpoint with a number and an indicator for being the last checkpoint.
     * If `last` is true, the x coordinate is set to 1, otherwise it is set to 0.
     *
     * @param number the number of the checkpoint.
     * @param last whether or not this is the last checkpoint.
     */
    public CheckPoint(int number, Boolean last) {
        this.number = number;
        this.x = last ? 1 : 0;
    }

    /**
     * Returns the number of the checkpoint.
     *
     * @return the number of the checkpoint.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the x coordinate of the checkpoint.
     *
     * @return the x coordinate of the checkpoint.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Sets the x coordinate of the checkpoint.
     *
     * @param x the new x coordinate of the checkpoint.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the y coordinate of the checkpoint.
     *
     * @return the y coordinate of the checkpoint.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Sets the y coordinate of the checkpoint.
     *
     * @param y the new y coordinate of the checkpoint.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Updates a player's current checkpoint if they have reached this checkpoint.
     * The player must reach the checkpoints in order, starting from 1.
     * This method is called as part of the game controller's main loop.
     *
     * @param gameController the game controller, used to manage game flow.
     * @param space the space containing player, used to identify the player.
     * @return true if the player's current checkpoint was updated; false otherwise.
     */
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
