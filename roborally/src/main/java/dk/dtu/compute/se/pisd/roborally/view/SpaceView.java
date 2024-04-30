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
package dk.dtu.compute.se.pisd.roborally.view;
import java.util.List;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;



import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 45; // 60; // 75;
    final public static int SPACE_WIDTH = 45;  // 60; // 75;

    public final Space space;



    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        Pane wallsPane = new Pane(); // This pane will contain the walls for the space
        wallsPane.setPrefSize(SPACE_WIDTH, SPACE_HEIGHT);

        Rectangle rectangle = new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        wallsPane.getChildren().add(rectangle);

         // Add the wallsPane on top of everything else, but underneath the player
         this.getChildren().add(0, wallsPane); // Add at the beginning to ensure it's under the player

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);

    }
    private void drawConveyorBelt() {
        List<FieldAction> actions = space.getActions();
        for(FieldAction action : actions) {
            if(action instanceof ConveyorBelt) {
                ConveyorBelt belt = (ConveyorBelt) action;

                // Define an arrow
                Polygon arrow = new Polygon(0.0, 0.0, 15.0, 30.0, 30.0, 0.0);                arrow.setFill(Color.GRAY); // set color of arrow

                // Set rotation of the arrow based on belt's heading
                switch(belt.getHeading()) {
                    case SOUTH:
                        arrow.setRotate(0);
                        break;
                    case NORTH:
                        arrow.setRotate(180);
                        break;
                    case WEST:
                        arrow.setRotate(90);
                        break;
                    case EAST:
                        arrow.setRotate(270);
                        break;
                }

                // Position the arrow at the center of the space
                arrow.relocate((SPACE_WIDTH - 30)/2, (SPACE_HEIGHT - 30)/2);

                // add arrow to children
                this.getChildren().add(arrow);
            }
        }
    }

/**
 * @author s230577, s235462
 * Visuals of the walls and their position on a space
 */
private void drawWalls(Pane pane, List<Heading > walls) {
    for (Heading wall : walls) {
        Line line = new Line();
        switch (wall) {
            case NORTH:
                line.setStartX(2);
                line.setEndX(SPACE_WIDTH - 2);
                line.setStartY(2);
                line.setEndY(2);
                break;
            case SOUTH:
                line.setStartX(2);
                line.setEndX(SPACE_WIDTH - 2);
                line.setStartY(SPACE_HEIGHT - 2);
                line.setEndY(SPACE_HEIGHT - 2);
                break;
            case EAST:
                line.setStartX(SPACE_WIDTH - 2);
                line.setEndX(SPACE_WIDTH - 2);
                line.setStartY(2);
                line.setEndY(SPACE_HEIGHT - 2);
                break;
            case WEST:
                line.setStartX(2);
                line.setEndX(2);
                line.setStartY(2);
                line.setEndY(SPACE_HEIGHT - 2);
                break;
        }
        line.setStroke(Color.RED);
        line.setStrokeWidth(5);
        pane.getChildren().add(line);
    }
}

    private void updatePlayer() {
        // Remove only player visuals if they exist
        this.getChildren().removeIf(node -> node instanceof Polygon && "player".equals(node.getUserData()));

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0, 10.0, 20.0, 20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            // Tag this node as "player"
            arrow.setUserData("player");

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    private void drawCheckpoint() {
        List<FieldAction> actions = space.getActions();
        for (FieldAction action : actions) {
            if (action instanceof CheckPoint) {
                // Remove only checkpoint visuals if they exist
                getChildren().removeIf(node -> node instanceof Circle && "checkpoint".equals(node.getUserData()));

                // Define the center points for drawing the checkpoint
                double centerX = getWidth() / 2.0;
                double centerY = getHeight() / 2.0;

                // Create a visual representation for the checkpoint
                Circle checkpointVisual = new Circle(centerX, centerY, 15);
                checkpointVisual.setFill(Color.TURQUOISE);
                checkpointVisual.setUserData("checkpoint");  // Tag this node as "checkpoint"

                int number = ((CheckPoint) action).getNumber();
                Text numberText = new Text(Integer.toString(number));
                numberText.setFill(Color.BLACK); // Set text color
                numberText.setFont(Font.font("Arial", FontWeight.BOLD, 15)); // Set font and size

                // Position the text at the center of the circle
                numberText.setX(centerX - numberText.getLayoutBounds().getWidth() / 2);
                numberText.setY(centerY + numberText.getLayoutBounds().getHeight() / 4);

                // Add the checkpoint to the pane
                getChildren().addAll(checkpointVisual, numberText);  // Add at the beginning to ensure it's below other elements
            }
        }
    }



    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear(); // Clear the current drawing

            drawCheckpoint();
            drawConveyorBelt();
            updatePlayer();

            // Draw walls last
            Pane wallsPane = new Pane();
            List<Heading> walls = space.getWalls();
            drawWalls(wallsPane, walls);
            this.getChildren().add(wallsPane);
        }
    }
}
