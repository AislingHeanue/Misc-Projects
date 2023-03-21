package boardGame;

import boardGame.objects.Board;
import boardGame.objects.Piece;
import boardGame.objects.obstacles.Obstacle;
import boardGame.objects.score.ScoreEntry;
import boardGame.objects.score.Scoreboard;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * The controller as specified by the FXML file. Controls what each button does and when things get output to the screen, as well as some game logic.
 */
public class Controller {
    /**
     * Controls whether the "Roll Dice" button is allowed to be clicked at any given moment (e.g. when waiting for a player make their to move).
     */
    private boolean diceLocked;

    /**
     * Controls whether the 'WASD' keys are active. This allows the player to specify which direction they wish to move if they are blocked (e.g. by a wall or another player).
     */
    private boolean movementLocked;
    /**
     * The list of players that is iterated through each turn.
     */
    protected static ArrayList<Piece> playerList = new ArrayList<>();
    /**
     * The index of {@link #playerList playerList} which corresponds to the current player.
     */
    private int currentPlayerIndex;
    /**
     * The instance of board is created before the initialisation of the Controller instance by Scene.fxml, but needs to be referenced by the Controller multiple times.
     * It is stored as a static variable and is set by the {@link Launcher Launcher} class before it loads Scene.fxml.
     */
    protected static Board board;
    /**
     * Text specified by Scene.fxml.
     */
    @FXML
    private Text movesRollText;
    /**
     * Text specified by Scene.fxml.
     */
    @FXML
    private Text dirRollText;
    /**
     * Text specified by Scene.fxml.
     */
    @FXML
    private Text moveStatus;
    /**
     * GridPane specified by Scene.fxml, starts off as an empty GridPane before additional rows and columns are added by {@link  #setGridPane(int, int) setGripane}.
     */
    @FXML
    public GridPane boardGrid;
    /**
     * Text specified by Scene.fxml.
     */
    @FXML
    private Text gameStatus;
    /**
     * Text specified by Scene.fxml.
     */
    @FXML
    private Text currentPlayerTextBox;
    /**
     * ScrollPane specified by Scene.fxml.
     */
    @FXML
    public ScrollPane scrollPane;
    /**
     * Text specified by Scene.fxml.
     */
    @FXML
    private Text turnText;
    /**
     * GridPane specified by Scene.fxml. Rows are added for each entry in the Scoreboard.ser file in {@link #showScoreboard() showScoreboard}.
     */
    @FXML
    private GridPane scoresGridPane;

    /**
     * The rectangle used to dim the screen when a player wins the game.
     */
    @FXML
    private Rectangle winnerRectangle;

    /**
     * The text which appears in the centre of the screen to announce a player has won.
     */
    @FXML
    private Text winnerText;

    /**
     * The piece currently being controlled. 'Piece' and 'player' are used interchangeably.
     */
    private Piece currentPlayer;
    /**
     * True when the player has been sent backwards by the direction dice.
     */
    private boolean movingBack;
    /**
     * A 2-Dimensional Array of StackPanes which will be put into {@link #boardGrid} while the scene is being loaded.
     */
    private StackPane[][] stackMatrix;

    /**
     * A list of where all the players icons are drawn. Used to delete all player icons when the new player icons need to be drawn with {@link #updateScreen()}.
     */
    private ArrayList<int[]> playerPositions;

    /**
     * Functions as the recording of what number of moves was rolled, as well as the amount of tiles remaining that can be moved by a player before their turn ends.
     */
    private int rolledMove;
    /**
     * The current turn number.
     */
    private int turnCount;

    /**
     * The height of each tile in the {@link #boardGrid} as specified in {@link #setGridPane(int, int)}.
     */
    public int tileHeight;

    /**
     * The instance of scoreboard loaded from and saved to Scoreboard.ser.
     */
    private Scoreboard scoreboard;

    /**
     * Default constructor.
     */
    public Controller(){}

    /**
     * The first method run after Scene.fxml is loaded. It calls {@link #setGridPane(int, int)}, {@link #loadScoreboard()} and
     * {@link #nextPlayer()} to ensure the screen and all required fields are set up before the game starts.
     */
    @FXML
    public void initialize() {
        setGridPane(board.width,board.height);
        loadScoreboard();
        playerPositions = new ArrayList<>();
        currentPlayerIndex = playerList.size()-1;
        turnCount = 0;
        nextPlayer();
    }


    /**
     * Updates the {@link #currentPlayerTextBox} with the current player. Updates the {@link #boardGrid} GridPane with each player's current position.
     * Also draws the player icon with the players' corresponding colours and numbers at their current position.
     */
    public void updateScreen() {
        Piece currentPlayer = playerList.get(currentPlayerIndex);
        if (!currentPlayer.noName) {
            print(playerList.get(currentPlayerIndex).getName() + " (Player "+ currentPlayer.playerNumber +")",currentPlayerTextBox);
        } else {
            print(playerList.get(currentPlayerIndex).getName(),currentPlayerTextBox);
        }

        for (int[] position : playerPositions) {
            stackMatrix[position[0]][position[1]].getChildren().remove(stackMatrix[position[0]][position[1]].getChildren().size()-1);
        }
        playerPositions = new ArrayList<>();

        for (Piece p : playerList) {
            Text playerHeadNumber = new Text(String.valueOf(p.playerNumber));
            playerHeadNumber.setTextAlignment(TextAlignment.CENTER);
            Circle playerHeadCircle = new Circle((float)tileHeight/4);
            playerHeadCircle.setFill(p.colour);
            Polygon playerBody = new Polygon((float)tileHeight/4,0,0,(float)tileHeight/2,(float)tileHeight/2,(float)tileHeight/2);
            playerBody.setFill(p.colour);
            VBox playerIcon = new VBox(new StackPane(playerHeadCircle,playerHeadNumber),playerBody);
            playerIcon.setAlignment(Pos.CENTER);
            stackMatrix[p.y][p.x].getChildren().add(playerIcon);
            int[] position = {p.y,p.x};
            playerPositions.add(position);
        }
    }


    /**
     * There are several ways the GridPane can be laid out, depending on the number of tiles the board has. By default, the tileWidth and tileHeight (which correspond to the
     * size of each tile in pixels) are set to 100 and 50 respectively. If neither of them cause the {@link #boardGrid} to be too wide or too tall (over 1000 pixels wide or 900 pixels tall),
     * then the GridPane is set with tiles of this size, and there is no need to enable scrolling. If one of them is too large, scrolling is enabled in the corresponding direction to
     * ensure the stage is not bigger than the maximum size. If the board is too large in both dimensions, then tileWidth and tileHeight are scaled down so the entire board fits on the screen in
     * at least one direction. To see these 3 scenarios, try specifying a board width and height of (4,6), then (4,30), then (30,30).
     * <p>
     * Once the tile sizes have been specified, the {@link #boardGrid} is set up with the corresponding row and column constraints, and each tile is given its own StackPane which
     * contain all the objects draw on that tile (e.g. players and obstacles). The checkerboard tile pattern is also added here. Obstacles which take up multiple tiles are drawn
     * by cropping the obstacle image to the fraction of the image which would be visible in just that tile. Once the obstacles have been set up at this step, they do not need to
     * be redrawn again.
     *
     * @param width The width of the board in number of tiles.
     * @param height The height of the board in number of tiles.
     */
    public void setGridPane(int width,int height) {
        tileHeight = 50;
        int tileWidth = 2 * tileHeight;
        int maxWidth = 1000;
        int maxHeight = 900;
        //scrolling in one direction is allowed, this code ensures scrolling in both directions at the same time never happens
        if (50*height > maxHeight & 100*width > maxWidth) {
            if (height >= 2*width) {
                tileWidth = maxWidth/width-1;
                tileHeight = tileWidth/2;
            } else {
                tileHeight = maxHeight/height-1;
                tileWidth = tileHeight*2;
            }
        }
        //for unit testing, we are never given a scrollPane (or any other part of the scene), so the method exits here.
        if (scrollPane == null) return;
        if (tileWidth * width <= maxWidth & tileHeight * height <= maxHeight) {
            scrollPane.setPrefWidth(tileWidth * width+2);
            scrollPane.setPrefHeight(tileHeight * height+2);
        }
        else if (tileWidth * width <= maxWidth) {
            scrollPane.setPrefViewportWidth(tileWidth * width+2);
            scrollPane.setPrefHeight(maxHeight);
        }
        else if (tileHeight * height <= maxHeight) {
            scrollPane.setPrefWidth(maxWidth);
            scrollPane.setPrefViewportHeight(tileHeight * height+2);
        } else {
            throw new RuntimeException("Invalid size defined for scrollPane.");
        }

        stackMatrix = new StackPane[height][width];
        Text[][] textMatrix = new Text[height][width];

        //adding constraints to gridPane
        boardGrid.setVgap(0);
        boardGrid.setHgap(0);
        for (int j = 0; j < height; j++) {
            RowConstraints rowCon = new RowConstraints();
            rowCon.setMinHeight(tileHeight);
            boardGrid.getRowConstraints().add(rowCon);
        }
        for (int i = 0; i < width; i++) {
            ColumnConstraints colCon = new ColumnConstraints();
            colCon.setMinWidth(tileWidth);
            boardGrid.getColumnConstraints().add(colCon);
        }

        //putting things in the gridPane
        Color lightBlue = new Color(0.65, 0.8, 1,0.5);
        Color otherBlue = new Color(0.6,1,1,0.5);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                stackMatrix[j][i] = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(
                        (i+j)%2 == 0 ? lightBlue : otherBlue
                );
                if (j == 0) rectangle.setFill(Color.PINK);
                if (j == height-1) rectangle.setFill(Color.LIGHTGREEN);
                rectangle.setHeight(tileHeight);
                rectangle.setWidth(tileWidth);
                stackMatrix[j][i].getChildren().add(rectangle);


                textMatrix[j][i] = new Text((board.playerMatrix[j][i].equals("."))?String.valueOf(board.getTile(j,i)):String.valueOf(board.playerMatrix[j][i]));
                textMatrix[j][i].setTextAlignment(TextAlignment.LEFT);
                String firstLetter = textMatrix[j][i].getText().split(",")[0];
                if (board.obstacleMap.containsKey(firstLetter)) {
                    Obstacle o = board.obstacleMap.get(firstLetter);
                    ImageView imageView = new ImageView();
                    imageView.setImage(board.obstacleMap.get(firstLetter).image);
                    if (o.width != 1 || o.height != 1) {
                        int yNum = Integer.parseInt(textMatrix[j][i].getText().split(",")[1]);
                        int xNum = Integer.parseInt(textMatrix[j][i].getText().split(",")[2]);
                        Image image = board.obstacleMap.get(firstLetter).image;
                        Rectangle2D rect = new Rectangle2D(xNum/(double)o.width*image.getWidth(),yNum/(double)o.height*image.getHeight(),image.getWidth()/o.width,image.getHeight()/o.height);
                        imageView.setViewport(rect);
                    }
                    imageView.setFitWidth(tileWidth);
                    imageView.setFitHeight(tileHeight);
                    imageView.setSmooth(true);


                    stackMatrix[j][i].getChildren().add(imageView);
                }

                boardGrid.add(stackMatrix[j][i], i, j);
            }
        }
    }

    /**
     * Loads the scoreboard from a file, if it exists. Otherwise, it creates the file and saves a blank Scoreboard object to it.
     */
    public void loadScoreboard() {
        File scoresFile = new File("Scoreboard.ser");
        try {
            if (scoresFile.createNewFile()) {
                scoreboard = new Scoreboard();
                saveScoreboard();
                return;
            }
            ObjectInputStream oi = new ObjectInputStream(Files.newInputStream(scoresFile.toPath()));
            scoreboard = (Scoreboard) oi.readObject();
            oi.close();
            showScoreboard();
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Clears the current entries in the {@link #scoresGridPane} if there are any, and adds score entries to it in descending order of score.
     */
    public void showScoreboard(){
        RowConstraints rowCon = new RowConstraints(20);
        for (int i = 0; i < 10;i++){
            int rowIndex = i;
            scoresGridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == rowIndex);
            String beforeSpace = "  ";
            if (rowIndex == 9) beforeSpace = "";
            if (rowIndex < scoreboard.sortedScores.size()) {
                ScoreEntry entry = scoreboard.sortedScores.get(rowIndex);
                scoresGridPane.add(new Text(beforeSpace + (rowIndex+1) + ". " + entry.name),0,rowIndex);
                scoresGridPane.add(new Text(String.valueOf(entry.score)),1,rowIndex);
            } else {
                scoresGridPane.add(new Text(beforeSpace + (rowIndex+1) + ". "),0,rowIndex);
            }
            scoresGridPane.getRowConstraints().add(rowCon);
        }
    }


    /**
     * Saves the current Scoreboard to a file named Scoreboard.ser.
     */
    public void saveScoreboard() {
        File scoresFile = new File("Scoreboard.ser");
        try {
            scoresFile.delete();
            scoresFile.createNewFile();
            ObjectOutputStream o = new ObjectOutputStream(Files.newOutputStream(scoresFile.toPath()));
            o.writeObject(scoreboard);
            o.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Method generated by Scene.fxml, corresponding to clicking the RollDice button. If {@link #diceLocked} is true, nothing happens. Otherwise, two D4's are
     * rolled and how far the player moves and in what direction are both calculated based on the result of the dice. Both dice rolls are printed to the screen in
     * their respective Text fields.
     */
    @FXML
    public void rollDice() {
        if (diceLocked) return;
        diceLocked = true;
        rolledMove = currentPlayer.rollD4();
        print(String.valueOf(rolledMove),movesRollText);
        int rolledDirectionNum = currentPlayer.rollD4();
        Piece.direction direction = Piece.parseDirection(rolledDirectionNum);
        String plural = (rolledMove != 1)?"s":"";
        switch (direction) {
            case UP:
                print(currentPlayer.getName() + " moves up " + rolledMove + " space" + plural + ".", moveStatus); print("Forwards",dirRollText); break;
            case BACKWARDS:
                print(currentPlayer.getName() + " moves back " + rolledMove + " space" + plural + ".", moveStatus); print("Backwards",dirRollText); break;
            case NONE:
                print(currentPlayer.getName() + " misses a turn.", moveStatus); print("Miss a turn",dirRollText); break;
            default:
                print("ERROR",moveStatus);


        }
        movePiece(currentPlayer,direction);
    }

    /**
     *  Tells the board to move the player one tile in the direction chosen by the direction dice.
     *  This method calls itself until the piece has moved the number of tiles shown on the moves dice,
     *  or has passed its turn.
     *
     * @param piece The current player.
     * @param direction The direction the player will move as chosen by the direction dice.
     */
    public void movePiece(Piece piece, Piece.direction direction) {
        switch (direction) {
            case UP:
                while (rolledMove > 0) {
                    if (!board.gameOver & !board.moveTo(piece, piece.y - 1, piece.x, false)) {
                        movingBack = false;
                        movePiece(piece, Piece.direction.CHOOSE);
                        return;
                    }
                    rolledMove--;
                }
                nextPlayer();
                return;
            case BACKWARDS:
                while (rolledMove > 0) {
                    if (!board.gameOver & !board.moveTo(piece, piece.y + 1, piece.x, false)) {
                        movingBack = true;
                        movePiece(piece, Piece.direction.CHOOSE);
                        return;
                    }
                    rolledMove--;
                }
                nextPlayer();
                return;
            case NONE:
                rolledMove = 0;
                nextPlayer();
                return;
            case CHOOSE:
                updateScreen();
                print("Please enter a direction (WASD) to move, or press 'p' to pass.",gameStatus);
                movementLocked = false;
        }
    }


    /**
     * The general event handler for any time a key is pressed. If the dice button is currently active ({@link #diceLocked} is false) then pressing SPACE will roll the dice.
     * If the player is given a choice of where to move (if {@link #movementLocked} is false), then the piece will be moved in the direction corresponding to the key pressed (W,A,S or D),
     * and if the move is successful, one point will be taken from the number of moves remaining for the piece, and the piece will attempt to continue moving forwards or backwards.
     * If P is pressed then the player's turn ends.
     *
     * @param event The event which caused this method to run. It can be one of W,A,S,D,P or SPACE. Any other key will result in no change.
     */
    public void arrowPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) rollDice();
        if (movementLocked) return;
        switch (event.getCode()) {
            case UP:
            case W:
                //moveTo returns true if and only if the piece has actually moved
                if (board.moveTo(currentPlayer, currentPlayer.y - 1, currentPlayer.x, false)) {
                    rolledMove -= 1;
                    movementLocked = true;
                }
                break;
            case DOWN:
            case S:
                if (board.moveTo(currentPlayer, currentPlayer.y + 1, currentPlayer.x, false)) {
                    updateScreen();
                    rolledMove -= 1;
                    movementLocked = true;
                }
                break;
            case LEFT:
            case A:
                if (board.moveTo(currentPlayer, currentPlayer.y, currentPlayer.x - 1, false)) {
                    updateScreen();
                    rolledMove -= 1;
                    movementLocked = true;
                }
                break;
            case RIGHT:
            case D:
                if (board.moveTo(currentPlayer, currentPlayer.y, currentPlayer.x + 1, false)) {
                    rolledMove -= 1;
                    movementLocked = true;
                }
                break;
            case P:
                rolledMove = 0;
                break;
        }
        updateScreen();
        if (rolledMove == 0||board.gameOver) {
            nextPlayer();
        } else {
            if (movingBack) {
                movePiece(currentPlayer, Piece.direction.BACKWARDS);
            } else {
                movePiece(currentPlayer, Piece.direction.UP);
            }
        }
    }

    /**
     * Chooses the current player to be the next player in {@link #playerList}. Increments the turn counter by one if the new current player is the first player. If the board has
     * entered a state where the game has ended, the game is halted and the winner is announced.
     */
    public void nextPlayer() {
        if (board.gameOver) {
            updateScreen();
            announceWin();
            return;
        }
        if (currentPlayerIndex == playerList.size()-1) {
            currentPlayerIndex = 0;
            turnCount++;
            turnText.setText("Turn " + turnCount);
        } else currentPlayerIndex++;
        currentPlayer = playerList.get(currentPlayerIndex);

        //these 2 booleans control what phase of the turn we're in, they should never be the same while the game is being played.
        diceLocked = false;
        movementLocked = true;
        print(currentPlayer.getName() +", your turn to roll.",gameStatus);
        updateScreen();
    }

    /**
     * The winning player is announced by printing their name and a winning message to the {@link #gameStatus} Text, and adds their entry to the scoreboard.
     * It also dims the screen and displays text announcing the winner.
     * The entries in the scoreboard are re-sorted and re-drawn, and the current scoreboard is saved to a file.
     */
    public void announceWin(){
        print(playerList.get(currentPlayerIndex).getName()+" wins and gets infinite cool points, and one point on the leaderboard.",gameStatus);
        winnerRectangle.setHeight(scrollPane.getHeight());
        winnerRectangle.setWidth(scrollPane.getWidth());
        winnerRectangle.setFill(new Color(0,0,0,0.4));
        print(playerList.get(currentPlayerIndex).getName() + " wins! \\(ˆoˆ)/",winnerText);
        if (playerList.get(currentPlayerIndex).noName) {
            scoreboard.addEntry("Anonymous");
        } else {
            scoreboard.addEntry(playerList.get(currentPlayerIndex).getName());
        }
        saveScoreboard();
        showScoreboard();
    }

    /**
     * Convenience method to set the text of a given text field.
     *
     * @param s The message.
     * @param text The Text field to show this message.
     */
    public static void print(String s, Text text) {
        text.setText(s);
    }
}
