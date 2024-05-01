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

    /**
     * Denne method starter et nyt spil i starten når man trykker "new game".
     * Man har derefter mulighed for at vælge hvilket board man vil spille på gennem choicedialog og metoden "getAvailableBoards();"
     * Efter boarded er valgt, vælger man antal spillere, gennem choicedialog også.
     * Når man har gået disse trin igennem, starter spillet med metoden setupPlayersAndGame();
     * @author s226870
     * @return Start af nyt spil
     */
    public void newGame() {
        // Get available boards
        List<String> availableBoards = LoadBoard.getAvailableBoards();
        if (availableBoards.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No board files found!");
            alert.showAndWait();
            return;
        }

        // Create a dialog to choose the board
        ChoiceDialog<String> boardDialog = new ChoiceDialog<>(availableBoards.get(0), availableBoards);
        boardDialog.setTitle("Select Board");
        boardDialog.setHeaderText("Choose a board to play on:");
        Optional<String> boardChoice = boardDialog.showAndWait();

        if (!boardChoice.isPresent()) {
            return; // Exit if no selection is made
        }

        // Load the selected board
        try {
            this.board = LoadBoard.loadBoard(boardChoice.get());// Do not append .json; getAvailableBoards() already strips it
            this.board.setBoardName(boardChoice.get());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading board: " + e.getMessage());
            alert.showAndWait();
            return; // Important to return here if the board could not be loaded
        }

        // Continue with player number selection
        ChoiceDialog<Integer> playerNumberDialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        playerNumberDialog.setTitle("Player Number");
        playerNumberDialog.setHeaderText("Select number of players:");
        Optional<Integer> playerNumber = playerNumberDialog.showAndWait();

        if (!playerNumber.isPresent()) {
            return; // Exit if no selection is made
        }

        setupPlayersAndGame(playerNumber.get()); // Setup the game with the number of players
    }

    private void setupPlayersAndGame(int no) {
        if (gameController != null) {
            if (!stopGame()) {
                return; // Ensure current game is stopped before setting up a new one
            }
        }



        gameController = new GameController(board);
        int middleColumn = board.width / 2; // Calculate the middle column
        int startColumn = middleColumn - (no / 2);  // Calculate the starting column for the leftmost player

        for (int i = 0; i < no; i++) {
            String color = PLAYER_COLORS.get(i % PLAYER_COLORS.size()); // Ensure cycling through colors
            Player player = new Player(board, color, "Player " + (i + 1), 30);
            board.addPlayer(player);
            player.setSpace(board.getSpace(startColumn + i, 0)); // Assumes 0 is a valid row
        }

        gameController.startProgrammingPhase();
        roboRally.createBoardView(gameController); // Assuming this sets up the view
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
            roboRally.createBoardView(this.gameController);

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
