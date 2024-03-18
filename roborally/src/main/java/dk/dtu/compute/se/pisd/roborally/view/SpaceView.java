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

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);

        addWalls();
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    private void addWalls() {
        this.getChildren().removeIf(node -> node instanceof Line);
    
        for (Heading heading : Heading.values()) {
            if (space.hasWall(heading)) {
                Line wall = null;
                switch (heading) {
                    case NORTH:
                        wall = new Line(0, 0, SPACE_WIDTH, 0);
                        break;
                    case SOUTH:
                        wall = new Line(0, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT);
                        break;
                    case EAST:
                        wall = new Line(SPACE_WIDTH, 0, SPACE_WIDTH, SPACE_HEIGHT);
                        break;
                    case WEST:
                        wall = new Line(0, 0, 0, SPACE_HEIGHT);
                        break;
                }
                if (wall != null) {
    wall.setStroke(Color.RED);
    wall.setStrokeWidth(5);
    wall.setStrokeLineCap(StrokeLineCap.ROUND);
    StackPane.setAlignment(wall, Pos.TOP_LEFT); // Positioning the wall
    this.getChildren().add(wall);
}
            }
        }
    }
    

    private void drawCheckpoint() {
        if (space.getCheckPoint() != null) {
            // Remove only checkpoint visuals if they exist
            getChildren().removeIf(node -> node instanceof Circle && "checkpoint".equals(node.getUserData()));
    
            // Define the center points for drawing the checkpoint
            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;
    
            // Create a visual representation for the checkpoint
            Circle checkpointVisual = new Circle(centerX, centerY, 10);
            checkpointVisual.setFill(Color.TURQUOISE);
            checkpointVisual.setUserData("checkpoint");  // Tag this node as "checkpoint"
    
            // Add the checkpoint to the pane
            getChildren().add(0, checkpointVisual);  // Add at the beginning to ensure it's below other elements
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            
            updatePlayer();
            addWalls();
            
            drawCheckpoint();
            
        }
    }



}
