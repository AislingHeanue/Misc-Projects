package boardGame.exceptions;

/**
 * Exception for when the board size specified is too small.
 */
public class BoardSizeException extends Exception {

    public BoardSizeException() {super();}
    public BoardSizeException(String s) {super(s);}
}
