package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;
    private final String BOARD_NAME = "defaultboard";

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT , BOARD_NAME);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i , 30);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment V1 (can be delete later once V1 was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should beSpace (0,4)!");
    }

    
        //The following tests should be used later for assignment V2

   

    @Test
    void testsetCheckpoint(){

        Board board = new Board(8, 8, "defaultboard");
        Space space = board.getSpace(1, 5);
        CheckPoint checkpoint = new CheckPoint(1, 5, 1);
        
        
        assertNull(space.getCheckPoint());

        space.setCheckPoint(checkpoint);

        assertNotNull(space.getCheckPoint());
        assertEquals(checkpoint, space.getCheckPoint());
    }
     



     @Test
     void moveCurrentPlayerToSpace() {
         Board board = gameController.board;
         Player player1 = board.getPlayer(0);
         Player player2 = board.getPlayer(1);
 
         gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));
 
         Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
         Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
         Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
     }
     


     @Test
     void moveForward() {
         Board board = gameController.board;
         Player current = board.getCurrentPlayer();
 
         gameController.moveForward(current);
 
         Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
         Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
         Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
     }
       

     @Test
     void moveLeft(){
         Board board = gameController.board;
         Player player = board.getCurrentPlayer();
         Space spaceCurrentPlayer = player.getSpace();
         
         Heading heading = player.getHeading();
         
         
 
         gameController.turnLeft(player);
 
         Assertions.assertEquals(player, spaceCurrentPlayer.getPlayer(), "Player " + player.getName() + " should beSpace (0,0)!");
         Assertions.assertEquals(Heading.EAST, player.getHeading(), "Player" + player.getName() + "should be heading West!");
 
     }
     

    @Test
     void moveRight(){
         Board board = gameController.board;
         Player player = board.getCurrentPlayer();
         Space spaceCurrentPlayer = player.getSpace();
         
         Heading heading = player.getHeading();
         
         
 
         gameController.turnRight(player);
 
         Assertions.assertEquals(player, spaceCurrentPlayer.getPlayer(), "Player " + player.getName() + " should beSpace (0,0)!");
         Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player" + player.getName() + "should be heading West!");
 
 
     }


    @Test
    void testConveyorBelt(){
        Board board = new Board(8, 8 , "defaultboard");
        Space space = board.getSpace(1, 5);
        ConveyorBelt con = new ConveyorBelt();

        assertNull(con.getHeading());

        con.setHeading(Heading.EAST);
        assertEquals(Heading.EAST, con.getHeading());
    }
    @Test
    void Uturn(){
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space spaceCurrentPlayer = player.getSpace();

        Heading heading = player.getHeading();



        gameController.uTurn(player);

        Assertions.assertEquals(player, spaceCurrentPlayer.getPlayer(), "Player " + player.getName() + " should beSpace (0,0)!");
        Assertions.assertEquals(Heading.NORTH, player.getHeading(), "Player" + player.getName() + "should be heading North!");

    }

    /**
     * @author s235459
     * Checks if the shooting player damages the other players standing in front
     */
    @Test
    void ShootLaser(){
        Board board = gameController.board;
        Player shooter = board.getPlayer(0);
        Player target1 = board.getPlayer(1);
        Player target2 = board.getPlayer(2);

        shooter.setSpace(board.getSpace(0, 0));
        target1.setSpace(board.getSpace(0, 1));
        target2.setSpace(board.getSpace(0, 2));

        gameController.shootLaser(shooter);

        Assertions.assertEquals(20 , target1.getHealth());
        Assertions.assertEquals(25 , target2.getHealth());

    }


}


