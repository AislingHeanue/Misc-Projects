package boardGame.objects.obstacles;

import javafx.scene.image.Image;

/**
 * An object which can block a player on the board.
 */
public class Obstacle {

    /**
     * Height of the obstacle in tiles.
     */
    public int height;

    /**
     * Width of the obstacle in tiles.
     */
    public int width;
    /**
     * Letter which represents the obstacle in the command-line form of this game.
     */
    public String letter;

    /**
     * The image which should be displayed for this obstacle.
     */
    public Image image;

    /**
     * Constructor for the obstacle with image file unspecified.
     *
     * @param w Obstacle width in tiles.
     * @param h Obstacle height in tiles.
     * @param s Obstacle letter representation when displayed in the command-line form.
     * @throws IllegalArgumentException when an obstacle with less than 1 width or height is specified.
     */
    public Obstacle(int w, int h, String s) throws IllegalArgumentException {
        if (w<1||h<1){
            throw new IllegalArgumentException("Attempted to create zero size object.");
        }
        width = w;
        height = h;
        letter = s;
        image = null;
    }

    /**
     * Constructor for the obstacle with image file specified.
     *
     * @param w Obstacle width
     * @param h Obstacle height
     * @param s Obstacle letter
     * @param i Obstacle image
     */
    public Obstacle(int w,int h, String s, Image i) {
        this(w,h,s);
        image = i;
    }
}
