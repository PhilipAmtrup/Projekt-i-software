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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;
    private int health;

    /* 
     * counter for the number of moves (but only for assigment v1)
     * Mangler en get og set!!! (er blevet løst kig lige nede under)
     */
    

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;

        // Adding checkpoints and walls during board initialization
        addCheckPoints();

    }
    /**
 * @author s230577, s235462
 * Walls being added during board initialization with their specific coordinates
 * Can also add more if needed
 */

private void addCheckPoints() {
    // Specifying checkpoints with x and y coordinates

        CheckPoint checkpoint1 = new CheckPoint(0, 5, 1);
        getSpace(0, 7).setCheckPoint(checkpoint1);
        getSpace(0, 7).setCheckpointNumber(checkpoint1.getNumber());

        CheckPoint checkpoint2 = new CheckPoint(6, 2, 2);
        getSpace(7, 0).setCheckPoint(checkpoint2);
        getSpace(7, 0).setCheckpointNumber(checkpoint2.getNumber());

        CheckPoint checkpoint3 = new CheckPoint(6, 2, 3);
        getSpace(7, 7).setCheckPoint(checkpoint3);
        getSpace(7, 7).setCheckpointNumber(checkpoint3.getNumber());
        // Add additional checkpoints as needed


    // Add additional checkpoints as needed
}

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Metode til at hente et space på spillebrættet. Det kan være en spillers current space man vil hente.
     * @param x
     * @param y
     * @return Space
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * retunere til current player of the game.
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * ændrer til den spiller du vil have.
     * @param player
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Metode til at hente hvilken fase vi er i.
     * @return phase
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Metode til at sætte fasen til hvad man ønsker.
     * @param phase
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Metode til at hente hvilket step man er på.
     * "Step" er rækkefølgen for de udførte kommandoer man kan udfører i sit program.
     * @return step
     */
    public int getStep() {
        return step;
    }

    /**
     * Metode til at vælge hvilket step man skal være på.
     * @param step
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Går ud fra det er hvilket step man er på i forhold til fasen. (Prgramming eller activation phase). Er dog ikke sikker.
     * @return StepMode
     */
    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Henter den spiller afhængig af spillernes nummer
     * @param player
     * @return spiller nummer
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V1 add the move count to the status message
        // XXX: V2 changed the status so that it shows the phase, the current player and the number of steps
        return "Player = " + getCurrentPlayer().getName() + "Player's health: " + getCurrentPlayer().getHealth()+ ", number of moves " + getCounter() + ", current phase: " + getPhase();
    }


    private int counter;

    public int getCounter() {
        return counter;
    }
    
    public void setCounter(int counter) {
        if (counter != this.counter){
            this.counter = counter;
            notifyChange();
        }
        
    }

    
    

}
