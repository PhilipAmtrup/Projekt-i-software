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

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place)


        // tekst skrevet i undervisningen

        Player current = board.getCurrentPlayer();
        if (current != null && space.getPlayer() == null) {
            current.setSpace(space);
            int number = board.getPlayerNumber(current);
            board.setCurrentPlayer(board.getPlayer((number + 1) % board.getPlayersNumber()));

            board.setCounter(board.getCounter() + 1);
        }


        // if (current != null && space.getPlayer() == null) {
        //     current.setSpace(space);
        //     int currentNumber = board.getPlayerNumber(current);
        //     int nextPlayerNumber = (currentNumber + 1) % board.getPlayerNumber(current);
        //     Player next = board.getPlayer(nextPlayerNumber);
        //     board.setCurrentPlayer(next);

        //     // fortæller hvor mange gange man har rykket
        //     
        // }
    }


    // XXX: V2


    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2 

    /**
     * Her generer spillet de forskellige kommando kort som er tilgængelig i programming phase.
     *
     * @return CommandCards
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2 a


    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2 
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2 
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2 
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2 java docs!!
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2 java docs!!
    private void continuePrograms() {
        do {
            executeNextStep(null);
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }


    // XXX: V2

    /**
     * Makes the different buttons work, where it makes the buttons execute the cards.
     *
     * @param option the different options of actions, type of cards (option card), defined in section with optionbutton in Playerview
     * @author s235459
     */
    private void executeNextStep(Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        if (option == null) {
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;
                        } else {
                            executeCommand(currentPlayer, option);
                        }
                    } else {
                        executeCommand(currentPlayer, command); // left or right
                    }
                }

                // After executing the command, iterate over all field actions on the player's new space.
                for (FieldAction actionOnSpace : board.getCurrentPlayer().getSpace().getActions()) {
                    // If the current field action is a ConveyorBelt, call doAction method on it.
                    if (actionOnSpace instanceof ConveyorBelt) {
                        actionOnSpace.doAction(this, board.getCurrentPlayer().getSpace());
                    }
                    // If the current field action is a CheckPoint, call doAction method on it.
                    else if (actionOnSpace instanceof CheckPoint) {
                        actionOnSpace.doAction(this, board.getCurrentPlayer().getSpace());
                    }
                }

                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }


    /**
     * Dette er i forhold til vores option "left or right".
     * Når man har valgt "left or right" i programming phase så skal fasen sættes til "PLATER_INTERACTION" for at spilleren aktivt kan vælge hvilken option han ønsker.
     * Når han vælger sin option går den tilbage til activation phase og udfører den valgte option.
     *
     * @param option
     * @author s226870
     */
    public void executeOptionsAndContinue(@NotNull Command option) {
        assert board.getPhase() == Phase.PLAYER_INTERACTION;
        board.setPhase(Phase.ACTIVATION);
        executeNextStep(option);


        while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode()) {
            executeNextStep(null);
        }

    }

    // XXX: V2

    /**
     * Executer den kommando som bliver kaldt (option - fw, left, right, fastfw)
     *
     * @param player
     * @param command
     * @author s226870
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case MOVE_BACK:
                    this.moveBack(player);
                    break;
                case FRONT_RIGHT:
                    this.moveDiagonalRight(player);
                    break;
                case FRONT_LEFT:
                    this.moveDiagonalLeft(player);
                    break;

                default:
                    // DO NOTHING (for now)
            }
            checkForWinCondition(player);
        }
    }

    /**
     * Moves the player diagonally front right based on its heading.
     * checks for any walls around the new space and pushes any players on the space
     * a space front. // can still jump over walls
     * @author Julius s235462
     * @param player
     */
    public void moveDiagonalRight(Player player) {
        // Get the current space of the player
        Space currentSpace = player.getSpace();
        // Determine the heading of the player
        Heading heading = player.getHeading();

        // Calculate the new coordinates based on the current space and heading
        int newX = currentSpace.x;
        int newY = currentSpace.y;
        switch (heading) {
            case NORTH:
                newX++; // Move right
                newY--; // Move up
                break;
            case EAST:
                newX++; // Move right
                newY++; // Move down
                break;
            case SOUTH:
                newX--; // Move left
                newY++; // Move down
                break;
            case WEST:
                newX--; // Move left
                newY--; // Move up
                break;
            default:
                // Handle other cases if needed
                break;
        }

        // Get the new space based on the calculated coordinates
        Space newSpace = board.getSpace(newX, newY);
        if (newSpace != null) {
            // Check for wall conditions before attempting to move the player
            if (newSpace.hasWall(heading.next().next()) && currentSpace.hasWall(heading) || // Wall in front of the player both on current space and in new space
                    currentSpace.hasWall(heading.next()) && currentSpace.hasWall(heading) || // Wall to the right of the player and in front on current space
                    newSpace.hasWall(heading.next().next()) && newSpace.hasWall(heading.prev()) || // Wall in front of the player both on new space and in the opposite direction
                    newSpace.hasWall(heading.prev()) && currentSpace.hasWall(heading.next())) { // Wall to the left of the player and in front on new space
                // Exit the method without executing the move
                return;
            }

            // If the conditions are met, attempt to move the player
            if (newSpace.getPlayer() == null) {
                player.setSpace(newSpace); // Move the player to the next space
                currentSpace.setPlayer(null); // Clear the player from the current space
                newSpace.setPlayer(player);
            } else {
                // If the next space is occupied, attempt to push the player
                Space spaceAfterPush = board.getNeighbour(newSpace, heading);
                if (spaceAfterPush != null && spaceAfterPush.getPlayer() == null && !spaceAfterPush.hasWall(heading)) {
                    try {
                        moveToSpace(newSpace.getPlayer(), spaceAfterPush, heading);
                        player.setSpace(newSpace); // Move the player to the next space
                        currentSpace.setPlayer(null); // Clear the player from the current space
                        newSpace.setPlayer(player); // Update the player's position in the new space
                    } catch (ImpossibleMoveException e) {
                        // Handle the exception if needed
                    }
                }
            }
        }
    }
//test to see if this coomits

    /**
     * exactly the same as moving diagonally right except that it looks for new space
     * dependend on the heading diffrently. and checks for walls with previus instead of next.
     * @author Julius s235462
     * @param player
     */
    public void moveDiagonalLeft(Player player) {
        // Get the current space of the player
        Space currentSpace = player.getSpace();
        // Determine the heading of the player
        Heading heading = player.getHeading();

        // Calculate the new coordinates based on the current space and heading
        int newX = currentSpace.x;
        int newY = currentSpace.y;
        switch (heading) {
            case NORTH:
                newX--; // Move left
                newY--; // Move up
                break;
            case EAST:
                newX--; // Move left
                newY++; // Move down
                break;
            case SOUTH:
                newX++; // Move right
                newY++; // Move down
                break;
            case WEST:
                newX++; // Move right
                newY--; // Move up
                break;
            default:
                // Handle other cases if needed
                break;
        }
        Space newSpace = board.getSpace(newX, newY);
        if (newSpace != null) {
            if (!newSpace.hasWall(heading.next().next()) || !newSpace.hasWall(heading.next())) {
                if (newSpace.getPlayer() == null) {
                    player.setSpace(newSpace); // Move the player to the next space
                    currentSpace.setPlayer(null); // Clear the player from the current space
                    newSpace.setPlayer(player);
                } else {
                    // If the next space is occupied, attempt to push the player
                    Space spaceAfterPush = board.getNeighbour(newSpace, heading);
                    if (spaceAfterPush != null && spaceAfterPush.getPlayer() == null && !spaceAfterPush.hasWall(heading)) {
                        try {
                            moveToSpace(newSpace.getPlayer(), spaceAfterPush, heading);
                            player.setSpace(newSpace); // Move the player to the next space
                            currentSpace.setPlayer(null); // Clear the player from the current space
                            newSpace.setPlayer(player); // Update the player's position in the new space
                        } catch (ImpossibleMoveException e) {
                            // Handle the exception if needed
                        }
                    }
                }
            }
        }
    }



    /**
     * Moves the current player one space forward
     *
     * @param player moves forward
     */

    // TODO Assignment V2
    public void moveBack(@NotNull Player player) {
        Space space = player.getSpace();
        if (space != null) {
            Heading heading = player.getHeading();
            Heading oppositeHeading = heading.next().next();
            Space newSpace = board.getNeighbour(space, oppositeHeading);
            if (newSpace != null && newSpace.getPlayer() == null) {
                newSpace.setPlayer(player);
            }

        }
    }


    /**
     * @author s230577
     * Moves the current player two spaces foward
     * This method moves the provided Player two spaces forward in the direction they are currently heading.
     * The fast-forward movement stops at first successful move if the second movement is not possible.
     * The sequence of operations is: determine heading -> move -> try to move again.
     * Exceptions are caught and handled within the method, which makes this a safe operation regarding the game rules.
     * @param player The Player who is to be moved two spaces forward.
     */
    public void fastForward(Player player) {
        // Define the starting point
        Space currentSpace = player.getSpace();
        // Determine the direction we're moving in
        if (currentSpace != null) {
            Heading heading = player.getHeading();

            // Try to move the player one space ahead
            try {
                Space nextSpace = board.getNeighbour(currentSpace, heading);
                if (nextSpace != null) {
                    moveToSpace(player, nextSpace, heading);
                    currentSpace = nextSpace;
                }
            } catch (ImpossibleMoveException exception) {
                // If can't move further, we stop here
                return;
            }

            // Try to move the player a second space ahead
            try {
                Space nextSpace = board.getNeighbour(currentSpace, heading);
                if (nextSpace != null) {
                    moveToSpace(player, nextSpace, heading);
                }
            } catch (ImpossibleMoveException exception) {
                // If can't move further, we stop here
                return;
            }
        }
    }

    /**
     * It makes the current player's heading turns right
     * @param player turns right
     */

    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading nextHeading = heading.next();
        player.setHeading(nextHeading);
    }

    /**
     * It makes the current player's heading turns left
     * @param player turns left
     */
    
    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading prevHeading = heading.prev();
        player.setHeading(prevHeading);
    }






    /**
     * In the programming phase you can use the command cards to program your robot to do a command
     * @param source Where you have the collection of your command cards given
     * @param target Which commands your robot should do
     * @return When finish programming your robot executes the commands given.
     */
    
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    
    public void reduceHealth(@NotNull Health health , @NotNull Player player){
        //Hvordan sætter jeg funktionen til at reducere health
        int playerHealth = player.getHealth();
        Space space = player.getSpace();
        Heading heading = player.getHeading();

        if (board.getNeighbour(space, heading) != null){
        
            playerHealth -= 10;
            player.setHealth(playerHealth);

        }

    }
    /**
 * @author s230577
 * Now checks if there is any walls blocking the player
     *  Wall checks are enforced at spaces, the move is not executed if a wall blocks the way in current or next space.
     * @param player The Player who is to be moved forward.
 */
  // ... (other parts of the GameController class)

    public void moveForward(Player player) {
        Space currentSpace = player.getSpace();
        if (currentSpace != null) {
            Heading heading = player.getHeading();
            Space nextSpace = board.getNeighbour(currentSpace, heading);

            // Check for walls on the current space and the next space
            if (nextSpace != null && !currentSpace.hasWall(heading) && !nextSpace.hasWall(heading.opposite())) {
                Player otherPlayer = nextSpace.getPlayer();

                // If the next space is empty, move the player into it
                if (otherPlayer == null) {
                    currentSpace.setPlayer(null);
                    nextSpace.setPlayer(player);
                } else {
                    // If the next space is occupied, attempt to push the other player if possible
                    Space spaceAfterNext = board.getNeighbour(nextSpace, heading);
                    if (spaceAfterNext != null && !nextSpace.hasWall(heading) && !spaceAfterNext.hasWall(heading.opposite()) && spaceAfterNext.getPlayer() == null) {
                        // Free the previous spaces, push the other player, and move the current player
                        nextSpace.setPlayer(null);
                        spaceAfterNext.setPlayer(otherPlayer);
                        currentSpace.setPlayer(null);
                        nextSpace.setPlayer(player);
                    }
                }
            }
        }
    }



    boolean moveToSpace(Player player, Space targetSpace, Heading heading) throws ImpossibleMoveException {
        Space currentSpace = player.getSpace();

        if (board.getNeighbour(player.getSpace(), heading) == targetSpace) {
            Player other = targetSpace.getPlayer();
            if (other != null) {
                Space target = board.getNeighbour(targetSpace, heading);
                if (target != null) {
                    moveToSpace(other, target, heading);
                    assert target.getPlayer() == null : target;
                } else {
                    throw new ImpossibleMoveException(player, targetSpace, heading);
                }
            }

            // Check if there are walls in the current space and the target space
            if (!currentSpace.hasWall(heading) && !targetSpace.hasWall(heading.opposite())) {
                player.setSpace(targetSpace);
            } else {
                throw new ImpossibleMoveException(player, targetSpace, heading);
            }
        } else {
            throw new ImpossibleMoveException(player, targetSpace, heading);
        }
        return false;
    }
    public void checkForWinCondition(Player player) {
        int totalCheckpoints = board.totalCheckpoints();
        if (player.getCurrentCheckpoint() == totalCheckpoints) {
            System.out.println("Player " + player.getName() + " has won the game!");
            // Set the game phase to GAME_OVER
            board.setPhase(Phase.GAME_OVER);
        }
    }

        /**
         * Det her burde gøre at man ikke kan gå igennem walls, men vi havde lidt problemer med at få både det og skubbe metoden sammen
         */

        /**
        // Make sure that the target space is the correct neighbour.
        if (board.getNeighbour(currentSpace, heading) != targetSpace) {
            throw new ImpossibleMoveException(player, targetSpace, heading);
        }

        // Check for walls in both current and target spaces.
        if (currentSpace.hasWall(heading) || targetSpace.hasWall(heading.opposite())) {
            throw new ImpossibleMoveException(player, targetSpace, heading);
        }

        // Check if the target space is occupied by another player.
        if (targetSpace.getPlayer() != null) {
            throw new ImpossibleMoveException(player, targetSpace, heading);
        }

        // All checks passed, move the player.
        player.setSpace(targetSpace);
        player.setHeading(heading);
*/

    }







    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }



