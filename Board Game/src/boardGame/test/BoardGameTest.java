package boardGame.test;

import boardGame.Controller;
import boardGame.Launcher;
import boardGame.exceptions.PlayerCountException;
import boardGame.objects.Board;
import boardGame.objects.Piece;
import boardGame.objects.obstacles.Obstacle;
import boardGame.objects.obstacles.Teleporter;
import boardGame.objects.score.ScoreEntry;
import boardGame.objects.score.Scoreboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit tests for this project.
 */
public class BoardGameTest {

    /**
     * The bord which all tests will be run on. It is reset between each test.
     */
    Board testBoard;

    /**
     * A 2x2 sized obstacle.
     */
    Obstacle bigTree;

    /**
     * A 1x1 sized obstacle.
     */
    Obstacle pole;

    /**
     * The first player, with specified name "Aisling", and random colour.
     */
    Piece player;

    /**
     * The first teleporter to be placed.
     */
    Teleporter tele1;
    /**
     * The second teleporter to be placed.
     */
    Teleporter tele2;


    /**
     * Run before the first test. Sets up a new instance of BoardGameTest.
     */
    @BeforeAll
    static void setupObstaclesPlayers() {
        new BoardGameTest();
    }

    /**
     * Sets up the obstacles {@link #bigTree}, {@link #pole} and the {@link #tele1 two} {@link #tele2 teleporters}. Also sets up the first {@link #player}, and adds it to the board.
     */
    public BoardGameTest() {
        bigTree = new Obstacle(2, 2, "b");
        player = new Piece(1, "Aisling","");
        pole = new Obstacle(1,1,"p");
        tele1 = new Teleporter(1);
        tele2 = new Teleporter(2);
    }

    /**
     * This is run before each test. It resets the {@link #testBoard} to be a blank board of width 4 and height 6.
     */
    @BeforeEach
    void setupBoard() {
        testBoard = new Board(null,4,6);
        try {
            testBoard.addPiece(player);
        } catch (PlayerCountException ignored) {}
    }

    /**
     * Tries to place an obstacle of zero width. Should throw an exception.
     */
    @Test
    void zeroObstacle() {
        try {
            testBoard.placeObstacle(new Obstacle(0, 1, "x"), 3, 3);
            fail("Should have raised exception.");
        } catch (Exception ignored){}
    }

    /**
     * Checks the that {@link #testBoard} is not null.
     */
    @Test
    void boardExists() {
        assertNotNull(testBoard);
    }

    /**
     * Checks the that {@link #bigTree} is not null.
     */
    @Test
    void obstacleExists(){
        assertNotNull(bigTree);
    }

    /**
     * Checks the that {@link #player} is not null.
     */
    @Test
    void pieceExists(){
        assertNotNull(player);
    }

    /**
     * Checks that the player has been placed on the starting line of the board.
     */
    @Test
    void testPlayerStart() {
        assertEquals(player.y,testBoard.height-1);
    }

    /**
     * Moves a player to the tile (2,2) and checks if it is actually at the tile (2,2) according to the board (playerMatrix[2][2]) and the player (player.x,player.y).
     */
    @Test
    void testMovement() {
        testBoard.moveTo(player,2,2,false);
        assertEquals(testBoard.playerMatrix[2][2],String.valueOf(player.playerNumber));
        assertEquals(player.x,2);
        assertEquals(player.y,2);
    }

    /**
     * Places and object and checks that its representation at that tile of the board is correct.
     */
    @Test
    void testPlaceObject(){
        testBoard.placeObstacle(bigTree, 1, 1);
        assertEquals(testBoard.getTile(2,2), "b,1,1");
    }

    /**
     * Adds one too many players to the board and fails if no exception is thrown.
     */
    @Test
    void testTooManyPlayers() {
        try {
            testBoard.addPiece(player);
            testBoard.addPiece(player);
            testBoard.addPiece(player);
            testBoard.addPiece(player);
            fail("Too many players added.");
        } catch (PlayerCountException ignored) {}
    }

    /**
     * Places an obstacle out of bounds and fails if no exception is thrown.
     */
    @Test
    void outOfBoundsObstacle(){
        try{
            testBoard.placeObstacle(bigTree,3,3);
            fail("Should have raised an exception.");
        } catch (Exception ignored){}
    }

    /**
     * Places an obstacle where another obstacle is and fails if no exception is thrown.
     */
    @Test
    void overlappingObstacles() {
        try {
            testBoard.placeObstacle(bigTree, 2, 2);
            testBoard.placeObstacle(bigTree, 1, 1);
            fail("Should have raised an exception.");
        } catch (Exception ignored) {}
    }

    /**
     * Moves a player to the finish line and checks if the game has ended according to the board (by checking if testBoard.gameOver is true).
     */
    @Test
    void testGameEnd() {
        testBoard.moveTo(player,0,0,false);
        assertTrue(testBoard.gameOver);
    }

    /**
     * Moves a player to a tile which is not the finish line and checks that the game has not ended according to the board (by checking if testBoard.gameOver is false).
     */
    @Test
    void testNotGameEnd() {
        testBoard.moveTo(player,1,0,false);
        assertFalse(testBoard.gameOver);
    }

    /**
     * Moves the player onto a teleporter and checks if their position is changed to that of the other teleporter.
     */
    @Test
    void testTeleporter() {
        testBoard.placeObstacle(tele1,2,0);
        testBoard.placeObstacle(tele2,2,1);
        testBoard.moveTo(player,2,0,false);
        assertEquals(player.x,1);
    }

    /**
     * Moves a player onto a teleporter with the argument {@code hasJustTeleported = true}, and makes sure they have not teleported. This is done to prevent an infinite teleportation loop.
     */
    @Test
    void testTeleporterLoop() {
        Teleporter tele1 = new Teleporter(1);
        Teleporter tele2 = new Teleporter(2);
        testBoard.placeObstacle(tele1,1,0);
        testBoard.placeObstacle(tele2,1,1);
        testBoard.moveTo(player,1,0,true);
        assertEquals(player.x,0);
    }

    /**
     * Moves a player onto a teleporter while another player is occupying the second teleporter, and checks to make sure they have not teleported.
     */
    @Test
    void testOccupiedTeleporter() {
        Piece player2 = new Piece(2,"Baisling","");
        player2.x = 1;
        testBoard.placeObstacle(tele1,1,0);
        testBoard.placeObstacle(tele2,1,1);
        testBoard.moveTo(player2,1,0,false);
        testBoard.moveTo(player,1,0,false);
        assertEquals(player.x,0);
    }
    /**
     * Tries to move a player to where another player is standing. The first player should be prevented from moving and stay where they are.
     *
     */
    @Test
    void movePlayerOnPlayer() {
        Piece player2 = new Piece(2,"Baisling","");
        try {
            testBoard.addPiece(player2);
        } catch (PlayerCountException e) {System.out.println("ERROR.");}
        testBoard.moveTo(player2,2,2,false);
        testBoard.show();
        int firstY = player.y;
        testBoard.moveTo(player, player2.y, player2.x, false);
        assertEquals(firstY,player.y); //making sure player has not moved
    }
    /**
     * Tries to move a player on top of an obstacle. They should be prevented from moving and stay where they are.
     */
    @Test
    void movePlayerOnObstacle() {
        testBoard.placeObstacle(bigTree,1,1);
        testBoard.moveTo(player,4,3,false);
        int currentY = player.y;
        testBoard.moveTo(player,1,1,false);
        assertEquals(currentY,player.y);
    }

    /**
     * Tries to create a board with width less than 3, and then one with height less than 6. An exception should be thrown both times.
     */
    @Test
    void boardTooSmall(){
        try {
            new Board(null,1,5);
            fail("Thin board was allowed");
        } catch (IllegalArgumentException ignored) {}
        try {
            new Board(null,15,3);
            fail("Short board was allowed");
        } catch (IllegalArgumentException ignored) {}
    }

    /**
     * Tries to move a player onto a teleporter when the second teleporter does not exist. The teleporter should throw an exception.
     */
    @Test
    void testLonelyTelepoter(){
        try {
            testBoard.placeObstacle(tele1, 1, 1);
            testBoard.moveTo(player, 1, 1, false);
            fail("Teleporter was supposed to complain.");
        } catch (Exception ignored) {}
    }

    /**
     * Tries to place obstacles in a
    *  <p>
    *  {@code 010}
    *  <p>
    *  {@code 101}
    *  <p>
     * shape, where 1's are obstacles and 0's are blank tiles. The rightmost tile is place last.
     * This should throw an exception since it would create a difficult to win scenario for some players.
     */
    @Test
    void testIllegalShape1(){
        try {
            testBoard.placeObstacle(pole, 3, 0);
            testBoard.show();
            testBoard.placeObstacle(pole, 2, 1);
            testBoard.placeObstacle(pole, 3, 2);
            fail("Impossible to win state created.");
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Tries to place obstacles in a
     *  <p>
     *  {@code 0100}
     *  <p>
     *  {@code 1001}
     *  <p>
     * shape in the centre of the board. No exception should be thrown.
     */
    @Test
    void testLegalShape(){
        try {
            testBoard.placeObstacle(pole, 3, 0);
            testBoard.show();
            testBoard.placeObstacle(pole, 2, 1);
            testBoard.show();
            testBoard.placeObstacle(pole, 3, 3);
        } catch (IllegalStateException ignored) {
            fail("Exception should not have been thrown.");
        }
    }

    /**
     * Tries to place obstacles in a
     *  <p>
     *  {@code 10}
     *  <p>
     *  {@code 01}
     *  <p>
     * shape in the centre of the board. No exception should be thrown.
     */
    @Test
    void testLegalShape2(){
        try {
            testBoard.placeObstacle(pole, 1, 2);
            testBoard.show();
            testBoard.placeObstacle(pole, 2, 1);
        } catch (IllegalStateException ignored) {
            fail("Exception should not have been thrown.");
        }
    }

    /**
     * Tries to place obstacles in a
     *  <p>
     *  {@code 010}
     *  <p>
     *  {@code 101}
     *  <p>
     * shape, where 1's are obstacles and 0's are blank tiles. The middle tile is placed last.
     * This should throw an exception since it would create a difficult to win scenario for some players.
     */
    @Test
    void testIllegalShape2(){
        try {
            testBoard.placeObstacle(pole, 2, 1);
            testBoard.show();
            testBoard.placeObstacle(pole, 2, 3);
            testBoard.placeObstacle(pole, 1, 2);
            fail("Impossible to win state created.");
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Tries to place obstacles in a straight horizontal line, blocking all players from the finish line. This should throw an exception.
     */
    @Test
    void testIllegalShape3(){
        try {
            testBoard.placeObstacle(pole, 3, 0);
            testBoard.show();
            testBoard.placeObstacle(pole, 3, 1);
            testBoard.show();
            testBoard.placeObstacle(pole, 3, 3);
            testBoard.placeObstacle(pole, 3, 2);
            fail("Impossible to win state created.");
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Tries to place obstacles in a
     *  <p>
     *  {@code 10}
     *  <p>
     *  {@code 01}
     *  <p>
     * shape pressed up against the left wall, where 1's are obstacles and 0's are blank tiles. This should throw an exception since it would create a difficult to win scenario for some players.
     */
    @Test
    void testIllegalShape4(){
        try {
            testBoard.placeObstacle(pole, 2, 0);
            testBoard.placeObstacle(pole, 3, 1);
            fail("Impossible to win state created.");
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Tries to place obstacles in a
     *  <p>
     *  {@code 01}
     *  <p>
     *  {@code 10}
     *  <p>
     * shape pressed up against the right wall, where 1's are obstacles and 0's are blank tiles. This should throw an exception since it would create a difficult to win scenario for some players.
     */
    @Test
    void testIllegalShape5(){
        try {
            testBoard.placeObstacle(pole, 2, 3);
            testBoard.placeObstacle(pole, 3, 2);
            fail("Impossible to win state created.");
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Tries to place obstacles in a
     *  <p>
     *  {@code 011}
     *  <p>
     *  {@code 100}
     *  <p>
     * shape pressed up against the right wall, where 1's are obstacles and 0's are blank tiles. This should throw an exception since it would create a difficult to win scenario for some players.
     */
    @Test
    void testIllegalShape6(){
        try {
            testBoard.placeObstacle(pole, 2, 2);
            testBoard.show();
            testBoard.placeObstacle(pole, 3, 1);
            testBoard.placeObstacle(pole, 2, 3);
            fail("Impossible to win state created.");
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Creates a new scoreboard and checks it's not null.
     */
    @Test
    void testScoreboardExists() {
        Scoreboard s = new Scoreboard();
        assertNotNull(s.sortedScores);
    }

    /**
     * Adds a score entry to the scoreboard and check that it is present in the scoreboard with the correct score.
     */
    @Test
    void testAddToScoreboard() {
        Scoreboard s = new Scoreboard();
        s.addEntry("banana",15);
        assertEquals(15,s.sortedScores.stream().filter(scoreEntry -> scoreEntry.name.equals("banana")).findFirst().orElse(new ScoreEntry("broken score",0)).score);
    }

    /**
     * Adds three scores to the scoreboard and checks to make sure the top score is the one with the highest value (and not just the most recent entry).
     */
    @Test
    void testScoreboardSorting() {
        Scoreboard s = new Scoreboard();
        s.addEntry("cheese",12);
        s.addEntry("fork",47);
        s.addEntry("banana",15);
        assertEquals("fork",s.sortedScores.get(0).name);
        assertEquals(47,s.sortedScores.get(0).score);
    }

    /**
     * Checks that the tile height is at its default value (50) when the board is sufficiently small.
     */
    @Test
    void testTileHeightNormal() {
        Controller c = new Controller();
        c.setGridPane(4,6);
        assertEquals(50,c.tileHeight);
    }

    /**
     * Checks that the tile height is at its default value (50) when scrolling is enabled in the vertical direction but not horizontal.
     */
    @Test
    void testTileHeightVerticalScroll() {
        Controller c = new Controller();
        c.setGridPane(4,60);
        assertEquals(50,c.tileHeight);
    }


    /**
     * Checks that the tile height is at its default value (50) when scrolling is enabled in the horizontal direction but not vertical.
     */
    @Test
    void testTileHeightHorizontalScroll() {
        Controller c = new Controller();
        c.setGridPane(60,6);
        assertEquals(50,c.tileHeight);
    }

    /**
     * Checks that the tile height has been reduced once the board is large enough that scrolling in both directions would need to be enabled otherwise.
     */
    @Test
    void testTileHeightTooLarge() {
        Controller c = new Controller();
        c.setGridPane(60,30);
        assertEquals(29,c.tileHeight);
    }

    /**
     * Checks that the direction dices correctly assigns a roll of 1 to missing a turn, a roll of 2 to moving backwards, and all other rolls with moving forward.
     */
    @Test
    void testDirectionDice() {
        assertEquals(Piece.parseDirection(1), Piece.direction.NONE);
        assertEquals(Piece.parseDirection(2), Piece.direction.BACKWARDS);
        assertEquals(Piece.parseDirection(3), Piece.direction.UP);
        assertEquals(Piece.parseDirection(4), Piece.direction.UP);
    }

    /**
     * Checks that rolling a D4 only returns values between 1 and 4.
     */
    @Test
    void testRollD4() {
        for (int i = 0;i < 50;i++) {
            int rolled = player.rollD4();
            if (rolled > 4 || rolled < 1) {
                fail("Illegal value of dice rolled:" + rolled);
            }
        }
    }


    /**
     * A range of tests to make sure the validateBoard function is behaving as expected. It checks that a valid input for width, height and playerCount (4,6 and 4) returns true,
     * and also checks false is returned if the board is too thin, if the board is too short, or if there are too few or too many players.
     */
    @Test
    void testValidateBoardSize() {
        assertTrue(new Launcher().validateBoardSize(4,6,4,null));
        assertFalse(new Launcher().validateBoardSize(2,6,2,null));
        assertFalse(new Launcher().validateBoardSize(4,5,4,null));
        assertFalse(new Launcher().validateBoardSize(4,6,1,null));
        assertFalse(new Launcher().validateBoardSize(4,6,5,null));
    }

    /**
     * Shows the state of the board after the test has completed.
     */
    @AfterEach
    void showBoard() {
        testBoard.show();
    }
}