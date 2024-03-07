package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
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

    /*
        The following tests should be used later for assignment V2

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

     */

     /**
      * Testing whether it is possible to move the current player to another space
      * @author s235459
      */

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
     

     /**
      * @author s235459
      * Test to check whether it is possible to move the current player forward
      */
     @Test
     void moveForward() {
         Board board = gameController.board;
         Player current = board.getCurrentPlayer();
 
         gameController.moveForward(current);
 
         Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
         Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
         Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
     }
       
     /**
      * Test to check whether it is possible to move the heading of the current player to the left
      * @author s235459
      */
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
     
     /**
      * Test to check whether it is possible to move the current player to the right
      * @author s235459
      */

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
     void optionButton(){
         Board board = gameController.board;
         Player player = board.getCurrentPlayer();
         Space spaceCurrentPlayer = player.getSpace();
         
         Heading heading = player.getHeading();
 
         
         gameController.executeNextStep();
 
 
 
         Assertions.assertEquals(player , spaceCurrentPlayer.getPlayer(), "Player" + player.getName() + "should be Space (0,0)!" );
         Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player" + player.getName() + "should be heading West!");
         Assertions.assertEquals(Heading.EAST, player.getHeading(), "Player" + player.getName() + "should be heading East!" );
     }



}