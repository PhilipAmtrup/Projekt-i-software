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
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.BoardFactory;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;

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
        }
        // else if ...
        // XXX if new field actions are added, the corresponding templates
        //     need to be added to the model subpackage of fileaccess and
        //     the else statement must be extended for converting the
        //     action template to the corresponding field action.

        return null;
    }

    // The following method is not needed for RoboRally; but it would
    // allow to programmatically generate a board and save it to a
    // JSON file, if need should be. This might make it easier to
    // create a first version fof some JSON file of a board.

    public static void saveBoard(Board board, String name) {
        BoardTemplate template = convertToTemplate(board);
        template.width = board.width;
        template.height = board.height;

        ClassLoader classLoader = AppController.class.getClassLoader();
        // FIXME: this is not very defensive and will result in a NullPointerException
        //         when the folder BOARDSFOLDER does not exist! But, the file does not
        //         need to exist at this point!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(ActionTemplate.class, new Adapter<ActionTemplate>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

    private static BoardTemplate convertToTemplate(Board board) {
        BoardTemplate result = new BoardTemplate();
        result.width = board.width;
        result.height = board.height;

        for (int x = 0; x < board.width; x++) {
            for (int y= 0; y < board.height; y++) {
                Space space = board.getSpace(x,y);
                if (space != null && ( !space.getActions().isEmpty() /*!space.getWalls().isEmpty()*/) ) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = x;
                    spaceTemplate.y = y;
                    spaceTemplate.actions = convertToTemplate(space.getActions());
                    //spaceTemplate.walls = new ArrayList<>(space.getWalls());
                }
            }
        }
        return result;
    }

    private static List<ActionTemplate> convertToTemplate(List<FieldAction> actions) {
        List<ActionTemplate> result = new ArrayList<>();

        for (FieldAction action: actions) {
            ActionTemplate template = convertToTemplate(action);
            if (template != null) {
                result.add(template);
            }
        }

        return result;
    }

    private static ActionTemplate convertToTemplate(FieldAction action) {
        if (action instanceof ConveyorBelt) {
            ConveyorBelt conveyorBelt = (ConveyorBelt) action;
            ConveyorBeltTemplate conveyorBeltTemplate = new ConveyorBeltTemplate();
            conveyorBeltTemplate.heading = conveyorBelt.getHeading();
            return conveyorBeltTemplate;
        } // else if ...
        // XXX if new field actions are added, the corresponding templates
        //     need to be added to the model subpackage of fileaccess and
        //     the else statement must be extended for converting the
        //    field action to the corresponding action template.

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
