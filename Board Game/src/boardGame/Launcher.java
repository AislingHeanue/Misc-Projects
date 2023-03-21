package boardGame;

import boardGame.exceptions.BoardSizeException;
import boardGame.exceptions.PlayerCountException;
import boardGame.objects.Board;
import boardGame.objects.Piece;
import boardGame.objects.obstacles.Obstacle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;


/**
 * The main class of the project. Sets up and displays the two setup windows (to specify the board dimensions, difficulty and player count and names),
 * and loads the main scene from the FXML file. Once this has been set up, control of the game and the output is mainly handled by the {@link Controller} class.
 */
public class Launcher extends Application {
    /**
     * The stage used for each of the setup screens and the game screen.
     */
    private Stage primaryStage;
    /**
     * The Controller instance specified by the FXML document.
     */
    private Controller controller;
    /**
     * Decimal chance of an obstacle generation attempt for each square of the board. (Easy = 0.2, Medium = 0.3, Hard = 0.5)
     */
    private double obstacleChance;

    /**
     * Called when the application is started, and sets up and displays the first screen
     * for user input. It asks for the board size, the number of players and the difficulty, and
     * calls {@link #goButton} when the button or Enter is pressed.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);
        root.setPadding(new Insets(15));
        root.add(new Text("Enter width: "),0,0);
        root.add(new Text("Enter height: "),0,1);
        root.add(new Text("Enter number of players: "),0,2);
        root.add(new Text("Choose difficulty:"),0,3);
        TextField w = new TextField();
        TextField h = new TextField();
        TextField playerNo = new TextField();
        ChoiceBox<String> difficultyChoiceBox = new ChoiceBox<>();
        difficultyChoiceBox.getItems().addAll("Easy","Medium","Hard");
        difficultyChoiceBox.setValue("Easy");

        Text errorBox = new Text("");
        Button firstButton = new Button("Go");
        root.add(w,1,0);
        root.add(h,1,1);
        root.add(playerNo,1,2);
        root.add(difficultyChoiceBox,1,3);
        root.add(errorBox,0,4);
        root.add(firstButton,1,4);
        root.setAlignment(Pos.CENTER);
        ColumnConstraints columnCon = new ColumnConstraints();
        columnCon.setMinWidth(280);
        root.getColumnConstraints().add(columnCon);
        Scene scene = new Scene(root);

        firstButton.setOnAction(event -> goButton(w,h,playerNo,errorBox,difficultyChoiceBox));

        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                goButton(w,h,playerNo,errorBox,difficultyChoiceBox);
            }
        });

        primaryStage.setTitle("Game Setup");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Takes the input from the width, height and playerCount text fields and converts them to integers. Assuming there was
     * no error, {@link #initialiseBoard} is run.
     *
     * @param w The width text field.
     * @param h The height text field.
     * @param playerNo The player number text field.
     * @param errorBox The error text which is update if an error is detected.
     * @param difficultyChoiceBox The choice box containing the difficulty selected by the user.
     */
    private void goButton(TextField w, TextField h, TextField playerNo, Text errorBox,ChoiceBox<String> difficultyChoiceBox) {
        try {
            int width = Integer.parseInt(w.getText());
            int height = Integer.parseInt(h.getText());
            int playerCount = Integer.parseInt(playerNo.getText());
            if (validateBoardSize(width,height,playerCount,errorBox)) {
                initialiseBoard(width,height,playerCount,difficultyChoiceBox);
            }
        } catch (IllegalArgumentException e) {
            errorBox.setText("Non-int or empty field detected in input.");
        }
    }

    /**
     * An error is displayed and the user is prevented from progressing if there is
     * an error, such as the board being too small, or there being too few or too many players.
     *
     * @param width The width specified by the user.
     * @param height The height specified by the user.
     * @param playerCount The number of players specified by the user.
     * @param errorBox The Text where an error is printed if one is found. Nothing is printed if there is no errorBox is given (this is used for unit testing).
     * @return true if there are no errors with the currently inputted values.
     */
    public boolean validateBoardSize(int width, int height, int playerCount, Text errorBox) {
        try {
            if (width<3 || height<6) throw new BoardSizeException("Board is too small. (Must have width >= 3 and height >= 6).");
            if (playerCount<2) throw new PlayerCountException("At least 2 players are required.");
            if (playerCount > width) throw new PlayerCountException("Too many players for this board size.");
        } catch (PlayerCountException e) {
            if (errorBox != null) errorBox.setText("Players must be (>= 2) and (< width).");
            return false;
        } catch (BoardSizeException e) {
            if (errorBox != null) errorBox.setText("Board size must be (>= (3*6)).");
            return false;
        } catch (Exception e) {
            if (errorBox != null) errorBox.setText("Unknown error.");
            return false;
        }
        return true;
    }

    /**
     * Creates the obstacle list and uses it and the specified parameters to create the board and store it as a static variable of {@link Controller}.
     * Once the board has been created, {@link #enterPlayerNames} is called.
     *
     * @param width The specified board height in tiles.
     * @param height The specified board width in tiles.
     * @param playerCount The specified cumber of players.
     * @param difficultyChoiceBox The choice box containing the difficulty selected by the user.
     */
    public void initialiseBoard(int width,int height,int playerCount,ChoiceBox<String> difficultyChoiceBox) {
        switch (difficultyChoiceBox.getValue()) {
            case "Easy": obstacleChance = 0.2; break;
            case "Medium": obstacleChance = 0.3; break;
            case "Hard": obstacleChance = 0.5;break;
        }
        ArrayList<Obstacle> obstacleList = new ArrayList<>();
        Image fireImage = null;
        Image holeImage = null;
        Image logImage = null;
        try {
            fireImage = new Image(Files.newInputStream(Paths.get(Objects.requireNonNull(getClass().getResource("images/fire.png")).getPath())));
            holeImage = new Image(Files.newInputStream(Paths.get(Objects.requireNonNull(getClass().getResource("images/hole.png")).getPath())));
            logImage = new Image(Files.newInputStream(Paths.get(Objects.requireNonNull(getClass().getResource("images/log.png")).getPath())));
        } catch ( IOException e) {
            e.printStackTrace();
        }
        Obstacle fire = new Obstacle(1, 2, "F",fireImage);
        Obstacle hole = new Obstacle(1, 1, "h",holeImage);
        Obstacle log = new Obstacle(2, 1, "l",logImage);
        obstacleList.add(fire);
        obstacleList.add(hole);
        obstacleList.add(log);

        Controller.board = new Board(obstacleList,width,height);
        enterPlayerNames(playerCount);
    }

    /**
     * Closes the first window and opens a second setup window to ask for the player names and colours. If none are given,
     * the players are named "Player N" where N is the player's number, and their colour is selected at random. When the button
     * or Enter is pressed, {@link #populateBoard} is called.
     *
     * @param playerCount The number of players specified by the user.
     */
    private void enterPlayerNames(int playerCount) {
        ArrayList<TextField> nameTextFields = new ArrayList<>();
        ArrayList<ChoiceBox<String>> colourChoiceBoxes = new ArrayList<>();
        GridPane root2 = new GridPane();
        root2.setVgap(5);
        root2.setHgap(5);
        root2.setPadding(new Insets(15));
        for (int i = 0; i<playerCount;i++){
            root2.add(new Text("Player "+(i+1) +"'s name:") ,0,i);
            nameTextFields.add(new TextField());
            ChoiceBox<String> colourChoiceBox = new ChoiceBox<>();
            colourChoiceBox.getItems().addAll("Select Colour","Red","Green","Blue","Purple","Grey","Random");
            colourChoiceBox.setValue("Select Colour");
            colourChoiceBoxes.add(colourChoiceBox);
            root2.add(nameTextFields.get(i),1,i);
            root2.add(colourChoiceBoxes.get(i),2,i);
        }
        Button nameButton = new Button("Go");
        root2.add(nameButton,1,playerCount);
        root2.setAlignment(Pos.CENTER);

        nameButton.setOnAction(event -> {
            try {
                populateBoard(nameTextFields,colourChoiceBoxes,playerCount);
            } catch (PlayerCountException e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(root2);
        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    populateBoard(nameTextFields,colourChoiceBoxes,playerCount);
                } catch (PlayerCountException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        primaryStage.setTitle("Enter Player Names");

        primaryStage.setScene(scene);
    }

    /**
     * Reads the contents of the first two setup screens and adds the players and obstacles specified to the board.
     * This instance of board was created in the {@link #initialiseBoard} method. The FXML file Scene.fxml is then loaded
     * and used to create the main screen where the game is played.
     *
     * @param nameTextFields The ArrayList of text fields from the player name entry screen.
     * @param colourChoiceBoxes The ArrayList of choice boxes from the player name entry screen.
     * @param playerCount The number of players specified by the user.
     * @throws PlayerCountException Thrown if there are too many players for a given board width.
     */
    private void populateBoard(ArrayList<TextField> nameTextFields,ArrayList<ChoiceBox<String>> colourChoiceBoxes,int playerCount) throws PlayerCountException {
        for (int i = 0; i<playerCount;i++) {
            Controller.playerList.add((nameTextFields.get(i).getText() == null)
                    ? new Piece(i + 1,colourChoiceBoxes.get(i).getValue())
                    : new Piece(i + 1, nameTextFields.get(i).getText(),colourChoiceBoxes.get(i).getValue()));
        }

        for (Piece p:Controller.playerList) Controller.board.addPiece(p);
        Controller.board.placeMultipleObstacles(true,obstacleChance);
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
//            root = fxmlLoader.load(getClass().getResource("Scene.fxml").openStream());
            root = fxmlLoader.load(Objects.requireNonNull(getClass().getResource("Scene.fxml")).openStream());
            controller = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(root);
        scene.setOnKeyPressed((KeyEvent event) -> controller.arrowPressed(event));

        primaryStage.setTitle("Simon's Race");
        primaryStage.setScene(scene);
    }

    /**
     * Launches the application.
     * @param args args are passed to the launcher.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
