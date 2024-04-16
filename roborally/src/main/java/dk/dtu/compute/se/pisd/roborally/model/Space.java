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
package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    private List<Heading> walls = new ArrayList<>();
    private Player player;
    private CheckPoint checkPoint;
    private List<FieldAction> actions = new ArrayList<>();

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
       
    }

    /**
     * Denne metoden henter player.
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Checks whether there is a wall in the specified direction.
     *
     * @param direction The Heading (direction) to check for a wall.
     * @return True if there is a wall in the specified direction, false otherwise.
     */
    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }
    /**
     * Returns the walls (actually their direction) on this space.
     * Note that clients may change this list; this should, however,
     * be done only during the setup of the game (not while the game
     * is running).
     *
     * @return the list of walls on this space
     */
    // XXX A3
    public List<Heading> getWalls() {
        return walls;
    }

    private int checkpointNumber = -1; // Initialize with -1 indicating no checkpoint

    /**
     * Retrieves the list of checkpoint numbers. If there is a valid checkpoint number, it adds it to the list.
     *
     * @return A list of integers representing the checkpoint numbers
     */
    public List<Integer> getCheckpointNumbers() {
        List<Integer> checkpointNumbers = new ArrayList<>();
        if (checkpointNumber != -1) {
            checkpointNumbers.add(checkpointNumber);
        }
        return checkpointNumbers;
    }

    /**
     * Sets the checkpoint number and notifies observers of the change.
     *
     * @param checkpointNumber The updated checkpoint number
     */
    public void setCheckpointNumber(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
        notifyChange();
    }
    /**
     * Retrieves the CheckPoint object.
     *
     * @return The CheckPoint object
     */
    public CheckPoint getCheckPoint(){
        return checkPoint;
    }

    /**
     * Sets the CheckPoint object and notifies observers of the change.
     *
     * @param checkPoint The updated CheckPoint object
     */
    public void setCheckPoint(CheckPoint checkPoint){
        this.checkPoint = checkPoint;
        notifyChange();
    }


    
        /**
     * Returns the list of field actions on this space.
     * Note that clients may change this list; this should, however,
     * be done only during the setup of the game (not while the game
     * is running).
     *
     * @return the list of field actions on this space
     */
    public List<FieldAction> getActions(){
        return actions;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }


}
