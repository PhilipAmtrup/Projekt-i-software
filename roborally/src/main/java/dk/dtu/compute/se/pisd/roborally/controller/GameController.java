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
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
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
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
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

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep(null);
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }


    // XXX: V2

    /* 
     * phase.activation = phase er om spillet er startet eller om spillet er igang
     * 
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
                        if(option == null){
                            board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                        } else {
                            executeCommand(currentPlayer, option);
                        }
                        
                    } 
                    else {
                        executeCommand(currentPlayer, command); // left or right
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



    public void executeOptionsAndContinue(@NotNull Command option){
        assert board.getPhase() == Phase.PLAYER_INTERACTION;
        board.setPhase(Phase.ACTIVATION);
        executeNextStep(option);
        

        while(board.getPhase() == Phase.ACTIVATION && !board.isStepMode()){
            executeNextStep(null);
        }
        
    }

    // XXX: V2
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

                default:
                    // DO NOTHING (for now)
            }
        }
    }


    /**
     * Moves the current player one space forward
     * @param player moves forward
     */

    // TODO Assignment V2
    public void moveForward(@NotNull Player player) {
        Space space = player.getSpace();
        if (space != null){
            Heading heading = player.getHeading();
            Space newSpace = board.getNeighbour(space, heading);
            if (newSpace != null && newSpace.getPlayer() == null){
                newSpace.setPlayer(player);
            }
        }
    }


    /**
     * Moves the current player two spaces foward
     * @param player moves the player
     */
    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        Space space = player.getSpace();
        
        if (space != null){
        Heading heading = player.getHeading();
        Space newSpace = board.getNeighbour(space, heading);
        newSpace = board.getNeighbour(newSpace, heading);
            if (newSpace != null && newSpace.getPlayer() == null){
                newSpace.setPlayer(player);
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


    public void OptionLeftRight(@NotNull Player player) {
        
    }
// Skal være en left or right method her (måske) som gør at når man trykker på left og right så bliver det udført. Ligesom ovenover




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

   

}
