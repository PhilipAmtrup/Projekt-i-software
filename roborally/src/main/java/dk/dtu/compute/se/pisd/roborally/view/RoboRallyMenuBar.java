package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Fil til Menubaren, hvor der er blevet tilføjet de 4 muligheder i menubjælken. 
 */





/**
 * @author s235459
 */
public class RoboRallyMenuBar extends MenuBar {
    private AppController appController;

    public RoboRallyMenuBar (AppController appController){
        this.appController = appController;

        Menu controlMenu = new Menu("File");
        this.getMenus().add(controlMenu);

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(e -> {this.appController.newGame();});
        controlMenu.getItems().add(newGame);

        MenuItem saveGame = new MenuItem("Save Game");
        saveGame.setOnAction(e -> {this.appController.saveGame();});
        controlMenu.getItems().add(saveGame);

        MenuItem stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction(e -> {this.appController.stopGame();});
        controlMenu.getItems().add(stopGame); 

        MenuItem exitApp = new MenuItem("Exit ");
        exitApp.setOnAction(e -> {this.appController.exit();});
        controlMenu.getItems().add(exitApp); 

    }
    
}
