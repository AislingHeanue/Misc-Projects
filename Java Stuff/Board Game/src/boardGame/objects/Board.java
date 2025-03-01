package boardGame.objects;

import boardGame.exceptions.PlayerCountException;
import boardGame.objects.obstacles.Obstacle;
import boardGame.objects.obstacles.Teleporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The board which contains the obstacles and pieces along with their locations.
 */
public class Board {

    /**
     * Keeps a record of all tiles and obstacles, as well as the start and finish line.
     * The start and finish line are stored as "s" and "f".
     * Empty tiles are stored as ".".
     * Obstacles are stored as a string containing information about which obstacle it is (encoded as one character), followed by
     * the specific tile of the obstacle to be drawn in the form "y,x".
     * Only the first character is displayed when viewing the board in the command line (ie when using {@link #show}).
     */
    private final String[][] matrix;
    /**
     * Keeps a record of where each player is in the grid. It is the same size as {@link #matrix}.
     */
    public final String[][] playerMatrix;

    /**
     * Keeps a record of where obstacles have been placed, ignoring obstacle type. It is 1 for each tile containing an obstacle,
     * and 0 otherwise. It is the same size as {@link #matrix}
     */
    private final int[][] placedObstacleMatrix;
    /**
     * The width of the matrix in tiles.
     */
    public final int width;
    /**
     * The height of the matrix in tiles.
     */
    public final int height;
    /**
     * Set to true once one of the players has landed on the finish line. Once this happens, the game will end and the winning player will be announced.
     */
    public boolean gameOver;
    /**
     * The number of players.
     */
    private int numPieces;
    /**
     * Random number generation for placing obstacles and players.
     */
    private final Random r;
    /**
     * The list of obstacles to be used. Obstacles are chosen at random from this list.
     */
    private final ArrayList<Obstacle> obstacles;
    /**
     * A map from the obstacle's letter as stored in {@link #matrix} to the obstacle it represents.
     */
    public Map<String, Obstacle> obstacleMap;

    /**
     * The constructor for board. Throws an exception if the board is too small (less than 3 wide and 6 tall).
     *
     * @param obstacleList A list of obstacles to be used by the board.
     * @param w The width of the board in tiles.
     * @param h The height of the board in tiles.
     */
    public Board(ArrayList<Obstacle> obstacleList, int w, int h) {
        obstacles = obstacleList;
        width = w;
        height = h;
        gameOver = false;
        obstacleMap = new HashMap<>();
        numPieces = 0;
        if (obstacles != null) {
            for (Obstacle o:obstacles) {
                obstacleMap.put(o.letter,o);
            }
        }

        r = new Random();
        if ((width < 3) || (height < 6)) {
            throw new IllegalArgumentException("Board size too small.");
        }
        matrix = new String[height][width];
        playerMatrix = new String[height][width];
        placedObstacleMatrix = new int[height][width];
        //define start and end strip
        for (int i = 0; i < width; i++) {
            for (int j = 1; j < height - 1; j++) {
                matrix[j][i] = ".";
            }
            for (int k = 0; k < height; k++) {
                playerMatrix[k][i] = ".";
                placedObstacleMatrix[k][i] = 0;
            }
            matrix[height - 1][i] = "s"; //the start is at height-1
            matrix[0][i] = "f"; //the end is at zero
        }
    }

    /**
     * Shows the board in the command line interface. No longer used while playing, but it is still useful for testing.
     */
    public void show() {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (!playerMatrix[j][i].equals(".")) {
                    System.out.print(playerMatrix[j][i].charAt(0));
                } else {
                    System.out.print(matrix[j][i].charAt(0));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Adds the obstacle and information about the location of each of its tiles to the {@link #matrix}.
     *
     * @param o The obstacle to place.
     * @param y y coordinate of the top left corner of the obstacle.
     * @param x x coordinate of the top left corner of the obstacle.
     */
    public void placeObstacle(Obstacle o, int y, int x) {
        checkCanPlace(o,y,x);
        for (int i = x; i < x + o.width; i++) {
            for (int j = y; j < y + o.height; j++) {
                    matrix[j][i] = o.letter +","+ (j-y) +","+ (i-x);
            }
        }
    }

    /**
     * Checks if it is legal to place an obstacle at a given location. First, we ensure the obstacle is entirely contained in the play area of the board.
     * Then, we check the position against {@link #placedObstacleMatrix} to ensure the obstacle doesn't overlap with another obstacle, or an area which has been
     * marked as illegal. Finally, {@link #winPossible()} is called to make suer placing this obstacle wouldn't create an impossible (or very difficult) to
     * win scenario.
     *
     * @param o The obstacle to place.
     * @param y y coordinate of the top left corner of the obstacle.
     * @param x x coordinate of the top left corner of the obstacle.
     */
    private void checkCanPlace(Obstacle o, int y, int x) {
        //x and y specify the top left corner of the obstacle, need to ensure obstacle is entirely contained in the play area of the board.
        //usable region = (1 -> height - 2) x (0 -> width - 1)
        if (!(x >= 0 && x + o.width <= width && y >= 1 && y + o.height <= height - 2)) {
            throw new IndexOutOfBoundsException("Attempted to place object out of bounds.");
        }
        //scan the area where the object is being placed for other objects.
        for (int i = x; i < x + o.width; i++) {
            for (int j = y; j < y + o.height; j++) {
                if (placedObstacleMatrix[j][i] != 0) {
                    throw new IllegalStateException("Tried to place object in a banned area, or on another obstacle.");
                }
            }
        }
        for (int i = x; i < x + o.width; i++) {
            for (int j = y; j < y + o.height; j++) {
                placedObstacleMatrix[j][i] = 1;
            }
        }
        if (!winPossible()) {
            //undo change to placeObstacleMatrix
            for (int i = x; i < x + o.width; i++) {
                for (int j = y; j < y + o.height; j++) {
                    placedObstacleMatrix[j][i] = 0;
                }
            }
            throw new IllegalStateException("Tried to create an impossible to win scenario.");
        }
    }

    /**
     * Scans the {@link #placedObstacleMatrix} line by line to ensure there are no occurrences of the following shape.
     * These shapes would result in an impossible trap for a player, or at least one they wouldn't be able to escape
     * without some very creative backwards movement, which is a hassle and goes against the spirit of the game.
     * This check also has the effect of not allowing a horizontal line occupy a whole row and block the finish line entirely.
     * Some examples of disallowed shapes are
     * <p>
     * {@code .1.   .11.  .111.}
     * <p>
     * {@code 101   1001  10001}
     * <p>
     * or any width greater than this, or any variation of these combined with the left and right border of the screen. '1' represents an obstacle, '0' represents no obstacle and '.' means it may or may not be an obstacle.
     * To detect the edge of this shape, we look at whether {@link #placedObstacleMatrix}[y+1][x] is 1.
     * To detect the middle of this shape, we look at {@link #placedObstacleMatrix}[y][x]. condition1 tells us if we are in the middle of one of these shapes,
     * and condition 2 tells us if this shape has more than zero width.
     *
     * @return true if there is no instance of shapes which would make winning impossible or very difficult.
     */
    private boolean winPossible() {
        boolean condition1;
        boolean condition2;
        for (int pointerY = 0; pointerY < height-2;pointerY++) {
            condition1 = true;
            condition2 = false;
            for(int pointerX = 0; pointerX < width; pointerX++) {
                if (placedObstacleMatrix[pointerY+1][pointerX] == 1) {
                    if (condition1&condition2) return false;
                    condition1 = true;
                    condition2 = false;
                } else if (placedObstacleMatrix[pointerY][pointerX] == 1) {
                    condition2 = true;
                } else {
                    condition1 = false;
                    condition2 = false;
                }
                if (pointerX == width-1 & condition1 & condition2) return false;
            }
        }
        return true;
    }

    /**
     * Returns the value of the tile at the given coordinates. Throws an exception if the coordinates are out of bounds.
     *
     * @param y y coordinate of the tile.
     * @param x x coordinate of the tile.
     * @return The entry of {@link #matrix} present at these coordinates.
     */
    public String getTile(int y, int x) {
        if (!(x >= 0 && x < width && y >= 0 && y < height)) {
            throw new IndexOutOfBoundsException("Trying to get an out of bounds character");
        }
        return matrix[y][x];
    }


    /**
     * Adds a player to a random position on the start line which isn't already occupied by another player.
     *
     * @param piece The player being added to the board.
     * @throws PlayerCountException Thrown when there are more players than can fit on the board.
     */
    public void addPiece(Piece piece) throws PlayerCountException {
        numPieces++;
        if (numPieces > width) throw new PlayerCountException("Board has too many players for the specified width.");
        while (piece.y == 0) {
            int x = r.nextInt(width);
            if (playerMatrix[height - 1][x].equals(".")) {
                playerMatrix[height - 1][x] = String.valueOf(numPieces);
                piece.x = x;
                piece.y = height - 1;
                piece.playerNumber = numPieces;
                return;
            }
        }
    }

    /**
     * Checks if there are no obstacles or other players that would block a piece from moving there. Teleporters are exceptions in that they are obstacles which
     * allow players to move onto them.
     *
     * @param y The y coordinate the piece wants to move to.
     * @param x The x coordinate the piece wants to move to.
     * @return True if it is possible for the piece to move to the specified position.
     */
    private boolean canMoveTo(int y, int x) {
        try {
            getTile(y, x);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return (matrix[y][x].equals(".") || matrix[y][x].equals("s") || matrix[y][x].equals("f") || matrix[y][x].startsWith("t") || matrix[y][x].startsWith("T")) & (playerMatrix[y][x].equals("."));
    }

    /**
     * First checks if a player can move to a tile, and if they can, they are moved. If they land on a teleporter they are teleported to the other teleporter.
     *
     * @param piece The player to move.
     * @param yNew The y coordinate to move to.
     * @param xNew The x coordinate to move to.
     * @param hasJustTeleported A special condition which is only true if the player is in the process of being teleported, to prevent an infinite teleporting loop.
     * @return true if the player successfully moved.
     */
    public boolean moveTo(Piece piece, int yNew, int xNew, boolean hasJustTeleported) {
        if (canMoveTo(yNew, xNew)) {
            if (matrix[yNew][xNew].equals("f")) {
                gameOver = true;
            }
            playerMatrix[piece.y][piece.x] = ".";
            piece.x = xNew;
            piece.y = yNew;
            playerMatrix[piece.y][piece.x] = String.valueOf(piece.playerNumber);
            if ((matrix[yNew][xNew].startsWith("t") || matrix[yNew][xNew].startsWith("T")) & !hasJustTeleported) Teleporter.teleport(this, piece, xNew, yNew);
            return true;
        } else {
            return false;
        }
    }

    /**
     * If we wish to add teleporters, two teleporters are placed at random valid positions on the board. Then, the board attempts to place obstacles
     * along each row of the board, starting from the top (one space before the finish line). Obstacles are chosen at random from {@link #obstacles}
     * and their validity is checked with {@link #checkCanPlace}.
     *
     * @param addTelepoter This is set to true if we want to add two teleporters to the board, and false if we want to add none.
     * @param obstacleChance The decimal chance of an obstacle placing attempt at any given tile on the board. (Easy = 0.3, Medium = 0.4, Hard = 0.5)
     */
    public void placeMultipleObstacles(boolean addTelepoter,double obstacleChance) {
        int teleportersPlaced = 0;
        if (addTelepoter) {
            int failedTeleporterCount = 0;
            while(teleportersPlaced<2 & failedTeleporterCount < 50) {
                try{
                    Teleporter tele = new Teleporter(teleportersPlaced+1);
                    placeObstacle(tele,r.nextInt(height-3)+1,r.nextInt(width));
                    obstacleMap.put(tele.letter,tele);
                    teleportersPlaced++;
                } catch (Exception e) {failedTeleporterCount+=1;} //this counter prevents an infinite loop if there are no spots for a teleporter.
            }
        }
        Obstacle obstacle;
        for (int j = 1; j < height - 2; j++) {
            int offset = r.nextInt(width);
            for (int i = 0; i < width; i++) {
                int i2 = (i + offset) % width;
                if (Math.random() < obstacleChance) {
                    try {
                        obstacle = obstacles.get(r.nextInt(obstacles.size()));
                        placeObstacle(obstacle, j, i2);
                    } catch (Exception ignored) {} //don't place the obstacle
                }
            }
        }
    }

}



