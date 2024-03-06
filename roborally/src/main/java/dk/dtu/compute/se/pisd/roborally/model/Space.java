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

import java.util.EnumMap;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

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

    private Player player;
    private EnumMap<WallPosition, Boolean> walls;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;

        //initialisere wall positions
        walls = new EnumMap<>(WallPosition.class);
        for (WallPosition position : WallPosition.values()) {
            walls.put(position, false);
        }
    }
    

    /**
     * Denne metoden henter player.
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

/**
 * enum hjælper os her med at se de mulige lokationer af en wall på et space 
 * siden at walls kan anses for at måtte være på en top, bottom, left, right position
 * @author Julius s235462
 */
    public enum WallPosition {
        TOP, BOTTOM, LEFT, RIGHT
    }

    // skal måske laves en public player?
   

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
     * checke om en væg er på en specifik position
     * @param position den position på væggen der skal checkes 
     * @return true hvis der er en væg ellers falsk
     * @author Julius S235462
     */
    public boolean hasWall(WallPosition position) {
        return walls.get(position);
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    /**
     * sæt tilstedeværelse af en væg på specificeret position 
     * @param position væg position der skal sættes
     * @param hasWall true for at tilføje væg false for at fjerne
     * @author Julius s235462
     */
    public void setWall(WallPosition position, boolean hasWall) {
        walls.put(position, hasWall);
        notifyChange(); // Notify change efter updatering af væg position
    }


    

}
