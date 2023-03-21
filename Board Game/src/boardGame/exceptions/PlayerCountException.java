package boardGame.exceptions;

/**
 * Exception for when the player count is lower than 2 or higher than the width of the board.
 */
public class PlayerCountException extends Exception{
    public PlayerCountException() {super();}
    public PlayerCountException(String s) {super(s);}
}
