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
import dk.dtu.compute.se.pisd.roborally.controller.ImpossibleMoveException;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;


    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;
    private int currentCheckPoint = 0;
    private int health;

    public Player(@NotNull Board board, String color, @NotNull String name, @NotNull int health ) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        this.health = health;



        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

   /**
     * Get the name of the player
     * @return the player name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the player
     * @param name gets matched to a player object
     */       
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Get the color of the player
     * @return Player color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gives a player their specific color
     * @param color gets matched to a player
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method reduce the health of a player.
     * When you reach zero (0) health you are being moved to the start position
     * The player also lose 1 checkpoint and have to reach the last one again
     * @author s226870
     * @param amount
     */
    public void reduceHealth(int amount, GameController gameController)  {
        this.health -= amount;

        if(this.health <= 0){
            String message = "You have lost all your health " + getName() + "\nYou have been moved back to the start position." +
                    "\nAll your checkpoints have been lost.\nSo make sure to get them again!";
            showAlert(Alert.AlertType.INFORMATION, "Death", message);
            this.setHeading(SOUTH);

            try {

                gameController.moveToSpace(this, board.getSpace(4, 0), SOUTH);
            } catch (ImpossibleMoveException e) {
                throw new RuntimeException(e);
            }
            this.setHealth(50);
            this.setCurrentCheckpoint(0);
        }
    }
    public void addHealth(int amount) {
        this.health += amount;
    }

    /**
     * @author s230577
     * This method returns the current checkpoint number the player is at.
     *
     * @return The number of the current checkpoint.
     */
    public int getCurrentCheckpoint() {
        return currentCheckPoint;
    }

    /**
     * @author s230577
     * This method sets the current checkpoint number.
     *
     * @param checkpoint The number to be set as the current checkpoint.
     */
    public void setCurrentCheckpoint(int checkpoint) {
        this.currentCheckPoint = checkpoint;
    }

    /**
     * Checks the availability of the space 
     * @return current space 
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Rykker spiller til en plads.
     * @param space
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }
    /**
     * Define the heading of the player
     * @return The heading of the player
     */
    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }


    public CommandCardField getProgramField(int i) {
        return program[i];
    }



    public CommandCardField getCardField(int i) {
        return cards[i];
    }
    
    /**
     * @author s235459
     * Funktion der giver spillerens liv/Damage token
     * @return Returnere spillerens health/liv
     */
    
    public int getHealth(){ 
        return health;
    }
    
    /**
     * Sætter/definere spillerens liv/health, samt opdatere spillerens health hvis det er blevet ændret.
     * @param health  Parameter som værende spillerens liv i mængde
     * @author s235459
     */
    
    public void setHealth(@NotNull int health){
        //Hvordan sætter jeg health til fx at være 30, og at health kan variere og ændres
        if (health != this.health){
            this.health = health;
            notifyChange();
        }
    }



}
