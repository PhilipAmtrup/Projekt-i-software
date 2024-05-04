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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.BoardFactory;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.Gear;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String JSON_EXT = "json";

    /**
     * Indlæser et spillebræt fra en JSON-fil baseret på et angivet brætnavn. Denne metode validerer først, at brætnavnet
     * ikke er tomt. Derefter forsøger den at hente den tilsvarende JSON-fil fra ressourcerne. Hvis filen findes,
     * deserialiseres den til en BoardTemplate-instans ved hjælp af Gson med en tilpasset adapter til ActionTemplate.
     * Til sidst konverteres BoardTemplate-objektet til en Board-instans.
     * @author s226870
     * @param boardname
     * @return boards
     */
    public static Board loadBoard(String boardname) {
        if (boardname == null || boardname.trim().isEmpty()) {
            throw new IllegalArgumentException("Board name must not be empty.");
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("boards/" + boardname + ".json");
        if (inputStream == null) {
            throw new IllegalArgumentException("Board not found: " + boardname);
        }

        GsonBuilder simpleBuilder = new GsonBuilder()
                .registerTypeAdapter(ActionTemplate.class, new Adapter<ActionTemplate>());
        Gson gson = simpleBuilder.create();

        try (JsonReader reader = gson.newJsonReader(new InputStreamReader(inputStream))) {
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);
            return convert(template, boardname);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load or parse board: " + boardname, e);
        }
    }

    private static Board convert(BoardTemplate template, String boardname) {
        Board result = new Board(template.width, template.height, boardname);
        for (SpaceTemplate spaceTemplate: template.spaces) {
            Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
            if (space != null) {
                space.getActions().addAll(convert(spaceTemplate.actions));
                space.getWalls().addAll(spaceTemplate.walls);
            }
        }
        return result;
    }

    private static List<FieldAction> convert(List<ActionTemplate> actionTemplates) {
        List<FieldAction> result = new ArrayList<>();

        for (ActionTemplate template: actionTemplates) {
            FieldAction fieldAction = convert(template);
            if (fieldAction != null) {
                result.add(fieldAction);
            }
        }

        return result;
    }
    /**
     * @author 230577
     * Converts an ActionTemplate object into the corresponding FieldAction object.
     * If the ActionTemplate is an instance of ConveyorBeltTemplate, converts it to a ConveyorBelt.
     * If the ActionTemplate is an instance of CheckPointTemplate, converts it to a CheckPoint.
     *
     * @param actionTemplate The ActionTemplate object to be converted
     * @return The converted FieldAction object (either ConveyorBelt or CheckPoint)
     */
    private static FieldAction convert(ActionTemplate actionTemplate) {
        if (actionTemplate instanceof ConveyorBeltTemplate) {
            ConveyorBeltTemplate template = (ConveyorBeltTemplate) actionTemplate;
            ConveyorBelt conveyorBelt = new ConveyorBelt();
            conveyorBelt.setHeading(template.heading);
            return conveyorBelt;
        } else if(actionTemplate instanceof CheckPointTemplate){
            CheckPointTemplate template = (CheckPointTemplate) actionTemplate;
            CheckPoint checkPoint = new CheckPoint(template.number, template.last);
            return checkPoint;
        } else if(actionTemplate instanceof GearTemplate) {
            GearTemplate template = (GearTemplate) actionTemplate;
            Gear gear = new Gear();
            gear.setIsClockWise(template.isClockWise);
            return gear;
        } else if (actionTemplate instanceof LaserTemplate){
            LaserTemplate template = (LaserTemplate) actionTemplate;
            Laser laser = new Laser(template.x , template.y, template.reduceHealth);
            return laser;
        }
        // else if ...
        // XXX if new field actions are added, the corresponding templates
        //     need to be added to the model subpackage of fileaccess and
        //     the else statement must be extended for converting the
        //     action template to the corresponding field action.

        return null;
    }

    public static List<String> getAvailableBoards() {
        try {
            Path boardsFolderPath = Paths.get(LoadBoard.class.getResource("/" + BOARDSFOLDER).toURI());
            return Files.walk(boardsFolderPath, 1)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("." + JSON_EXT))
                    .map(path -> path.getFileName().toString().replace("." + JSON_EXT, ""))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return an empty list in case of an error
        }
    }

}
