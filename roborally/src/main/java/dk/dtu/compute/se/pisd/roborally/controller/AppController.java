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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.*;
import dk.dtu.compute.se.pisd.roborally.model.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    Board board = LoadBoard.loadBoard("defaultboard");  // loading board from defaultboard.json

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                if (!stopGame()) {
                    return;
                }
            }



            if (board == null) {
                // display an error message or create a default board
                board = BoardFactory.getInstance().createBoard(null);
            }

            gameController = new GameController(board);
            int no = result.get();  // Number of players
            int middleColumn = board.width / 2; // Calculate the middle column
            int startColumn = middleColumn - (no / 2);  // Calculate the starting column for the leftmost player

            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1), 30); // Initialize players
                board.addPlayer(player);
                // Set each player's space to be side by side in the top center row of the board
                player.setSpace(board.getSpace(startColumn + i, 0));
            }




            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);
        }
    }

    /**
     * @author s235459
     * Makes it possible to save the game to the database.
     */
    public void saveGame() {
        // XXX needs to be implemented eventually
        //Connector connector= new Connector();
        Repository repo = new Repository(new Connector());


        if (this.board.getGameId() != null) {
            repo.updateGameInDB(this.gameController.board);
        } else {
            repo.createGameInDB(this.gameController.board);
        }


    }

    /**
     * @author s235459
     * Makes it possible to see the saved games, that are possible to load. The player is able to choose himself what game to load
     *
     */
    public void loadGame() {
        // XXX needs to be implememted eventually
        // for now, we just create a new game

        Repository gameRepo = new Repository(new Connector());
        List< GameInDB> games = gameRepo.getGames();
        //board = BoardFactory.getInstance().createBoard("defaultboard");

        ChoiceDialog<GameInDB> LoadChoice = new ChoiceDialog<>();
        LoadChoice.setTitle("Load Game");
        LoadChoice.setHeaderText("Choose a game to load");
        LoadChoice.getItems().addAll(games);
        LoadChoice.showAndWait();


        if (LoadChoice.getSelectedItem() != null) {
            this.board = gameRepo.loadGameFromDB(LoadChoice.getSelectedItem().id);
            //this.board = BoardFactory.getInstance().createBoard("defaultboard");
            this.gameController = new GameController(this.board);
            //Player player = board.getPlayer(this.board.getPlayersNumber());

            /*
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player =this.board.getPlayer(i);

                CommandCardField cardField = player.getCardField(i);
                if (cardField != null) {
                    //gameController.board.setPhase(Phase.PROGRAMMING);
                    gameController.startProgrammingPhase();
                } else gameController.board.setPhase(Phase.INITIALISATION);
            }*/

            //gameController.startProgrammingPhase();

        }
        roboRally.createBoardView(this.gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
