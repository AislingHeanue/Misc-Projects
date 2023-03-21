package boardGame.objects;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * The class representing each player of the game and their piece on the board.
 */
public class Piece {

    /**
     * The direction dice can result in a piece having to move up, backwards, or not at all. If a piece hits a wall it may choose its direction.
     */
    public enum direction {UP,BACKWARDS,NONE,CHOOSE}

    /**
     * The number of the player, which counts up from one and is unique to each player.
     */
    public int playerNumber;

    /**
     * The colour of the player's icon.
     */
    public Color colour;

    /**
     * The name of the player, if specified.
     */
    private final String name;
    /**
     * True if the player opted not to choose a name. Their name will be displayed as "Player N".
     */
    public boolean noName = false;
    /**
     * Random number generator for the player.
     */
    private final Random r;

    /**
     * The player's current x position.
     */
    public int x;
    /**
     * The player's current y position.
     */
    public int y;

    /**
     * Constructor for when the player has chosen a name.
     * @param number The player's number.
     * @param n The name of the player.
     * @param selectedColour The player's chosen colour from the colour ChoiceBox.
     */
    public Piece (int number, String n,String selectedColour) {
        r = new Random();
        name = n;
        playerNumber = number;
//        rollHistory = new ArrayList<>();
        if (name.replaceAll(" ","").length()==0) {
            noName = true;
        }
        pickColour(selectedColour);
    }

    /**
     * @return Returns the player's name if the player has chosen a name, otherwise "Player N".
     */
    public String getName() {
        if (noName) {
            return "Player " + playerNumber;
        } else {
            return name;
        }
    }

    /**
     * Constructor for when the player has not chosen a name.
     * @param number The player's number.
     * @param selectedColour The player's chosen colour from the colour ChoiceBox
     */
    public Piece (int number,String selectedColour) {
        this(number,"",selectedColour);
        noName = true;
    }

    /**
     * Converts the chosen colour which is represented by text into an actual colour, and set the player's colour accordingly.
     *
     * @param selectedColour The player's chosen colour from the colour ChoiceBox
     */
    public void pickColour(String selectedColour) {
        switch (selectedColour) {
            case "Red": colour = Color.RED; break;
            case "Blue": colour = Color.BLUE; break;
            case "Green": colour = Color.GREEN; break;
            case "Purple": colour = Color.PURPLE; break;
            case "Grey": colour = Color.GREY; break;
            default: colour = new Color(r.nextDouble(),r.nextDouble(),r.nextDouble(),1);
        }
    }

    /**
     * Rolls a 4 sided die.
     * @return A random number between 1 and 4.
     */
    public int rollD4() {
        return r.nextInt(4) + 1;
    }

    /**
     * Converts a number rolled (1,2,3,4) into a direction (FORWARDS,BACKWARDS,NONE)
     * @param dirRoll The number rolled on the direction dice.
     * @return The direction the player should move.
     */
    public static direction parseDirection(int dirRoll) {
        if (dirRoll > 2) {
            return direction.UP;
        } else if (dirRoll == 1) {
            return direction.NONE;
        } else {
            return direction.BACKWARDS;
        }
    }
}
