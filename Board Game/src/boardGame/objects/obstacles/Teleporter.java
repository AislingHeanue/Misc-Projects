package boardGame.objects.obstacles;

import boardGame.Launcher;
import boardGame.objects.Board;
import boardGame.objects.Piece;
import javafx.scene.image.Image;

/**
 * A special obstacle which causes the player to teleport to the other instance of this obstacle if they land on it.
 */
public class Teleporter extends Obstacle {

    /**
     * The images representing the portals.
     */
    private static final Image orangeImage,blueImage;

    static {
        try {
            orangeImage = new Image(Launcher.class.getResourceAsStream("images/orangeportal.png"));
            blueImage = new Image(Launcher.class.getResourceAsStream("images/blueportal.png"));
        } catch (Exception
                e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Uses the default constructor for obstacles (with size 1x1 and letter "t" or "T"), then updates the image and letter depending on if it is the first or second teleporter.
     *
     * @param teleId The number corresponding to whether it is the first or second teleporter on a given board. This is to prevent a teleporter teleporting a player to itself.
     */
    public Teleporter(int teleId) {
            super(1,1, "t");
            switch (teleId) {
                case 1: image = blueImage; break;
                case 2: image = orangeImage; letter = "T"; break;
                default: image = null;
        }


    }

    /**
     * Scans the board for a teleporter which isn't the one currently being stood on by the player, and if one is found, the player is moved to that teleporter.
     *
     * @param board The current board.
     * @param piece The player being teleported.
     * @param x The player's x position.
     * @param y The player's y position.
     */
    public static void teleport(Board board, Piece piece, int x, int y) {
        String currentString = board.getTile(y,x);
        for (int i = 0; i<board.width;i++) {
            for (int j = 0; j < board.height-1;j++){
                if (!board.getTile(j,i).equals(currentString) & (board.getTile(j,i).startsWith("t") || board.getTile(j,i).startsWith("T"))) {
                    board.moveTo(piece,j,i,true);
                    return;
                }
            }
        }
        throw new IllegalStateException("Could only find one teleporter on the map.");
    }
}
