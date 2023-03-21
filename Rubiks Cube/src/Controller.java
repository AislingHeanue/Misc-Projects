import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;


public class Controller {
    public VBox controlPane;
    public Pane viewPane;
    public Button uButton, lButton, fButton, rButton, dButton, bButton, xButton, yButton, zButton,mButton,eButton,sButton;
    public Button shuffleButton, algButton, resetButton, clearButton,camResetButton;
    public Text shuffleText;
    public TextField algTextBox;
    public ToggleButton acToggle, wideToggle;
    public String currentSuffix;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);



    Cube cube;
    SpinGroup group;
    Scene scene;
    Camera camera;
    Cubie[][][] cubies;
    int n, boxSize;

    boolean wideSelected;

    private Cube.Turn thisTurn;

    Animator a;

    public void setup(int n, int boxSize,Scene s,int subSceneSize) {
        this.n = n;
        this.boxSize = boxSize;
        this.scene = s;
        cube = new ArrayCube(n);
        currentSuffix = "";
        wideSelected = false;
        thisTurn = Cube.Turn.CLOCKWISE;
        setActions();
        cubies = new Cubie[n][n][n];
        group = new SpinGroup();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < n; z++) {
                    cubies[x][y][z] = new Cubie(x, y, z, group, n,cube);
                }
            }
        }
        a = new Animator(this);

        group.resetSpin();
        group.getChildren().add(new AmbientLight());

        camera = new PerspectiveCamera(true);
        camera.translateXProperty().set(0);
        camera.translateYProperty().set(0);
        camera.setTranslateZ(-1200);
        camera.setNearClip(1);
        camera.setFarClip(4000);


        SubScene cubeScene = new SubScene(group, subSceneSize, subSceneSize, true, SceneAntialiasing.BALANCED);
        cubeScene.setCamera(camera);
        group.initMouseControl();
        viewPane.getChildren().add(cubeScene);
        cubeScene.setFill(Color.GREY);

    }

    public void setActions() {

        wideToggle.setOnAction(event -> {
            wideSelected = wideToggle.isSelected();
            updateButtons();
        });

        acToggle.setOnAction(event -> {
            boolean selected = acToggle.isSelected();
            if (selected) {
                thisTurn = Cube.Turn.ANTICLOCKWISE;
                currentSuffix = "'";
            } else {
                thisTurn = Cube.Turn.CLOCKWISE;
                currentSuffix = "";
            }
            updateButtons();
        });

        uButton.setOnMouseClicked(event -> a.Up(thisTurn,wideSelected));

        lButton.setOnMouseClicked(event -> a.Left(thisTurn,wideSelected));

        rButton.setOnMouseClicked(event -> a.Right(thisTurn,wideSelected));

        fButton.setOnMouseClicked(event -> a.Front(thisTurn,wideSelected));

        bButton.setOnMouseClicked(event -> a.Back(thisTurn,wideSelected));

        dButton.setOnMouseClicked(event -> a.Down(thisTurn,wideSelected));

        xButton.setOnMouseClicked(event -> a.x(thisTurn));

        yButton.setOnMouseClicked(event -> a.y(thisTurn));

        zButton.setOnMouseClicked(event -> a.z(thisTurn));

        mButton.setOnMouseClicked(event -> a.M(thisTurn));

        eButton.setOnMouseClicked(event -> a.E(thisTurn));

        sButton.setOnMouseClicked(event -> a.S(thisTurn));

        shuffleButton.setOnMouseClicked(event -> {
            String shuffleString = cube.shuffle(false,20);
            updateFaces();
            shuffleText.setText(shuffleString);
        });

        algButton.setOnMouseClicked(event -> {
            try {
                cube.doAlgorithm(algTextBox.getText());
            } catch (Exception e) {System.out.println("ERROR parsing string: "+algTextBox.getText() + "\n" + e);}
            updateFaces();
        });

        resetButton.setOnMouseClicked(event -> {
            cube.reset();
            shuffleText.setText("");
            updateFaces();
        });

        camResetButton.setOnMouseClicked(event -> {
            group.resetSpin();
            camera.setTranslateZ(-1200);
        });

        clearButton.setOnMouseClicked(event -> algTextBox.setText(""));

        scene.setOnKeyPressed(event -> {
            if (!algTextBox.isFocused()) {
                switch(event.getCode()) {
                    case SHIFT: acToggle.setSelected(true); currentSuffix = "'"; thisTurn = Cube.Turn.ANTICLOCKWISE; updateButtons(); break;
                    case CONTROL: wideToggle.setSelected(true); wideSelected = true; updateButtons();  break;
                    case U: a.Up(thisTurn,wideSelected); break;
                    case R: a.Right(thisTurn,wideSelected); break;
                    case L: a.Left(thisTurn,wideSelected); break;
                    case F: a.Front(thisTurn,wideSelected); break;
                    case B: a.Back(thisTurn,wideSelected); break;
                    case D: a.Down(thisTurn,wideSelected); break;
                    case X: a.x(thisTurn); break;
                    case Y: a.y(thisTurn); break;
                    case Z: a.z(thisTurn); break;
                    case M: a.M(thisTurn); break;
                    case E: a.E(thisTurn); break;
                    case S: a.S(thisTurn); break;
                    case MINUS: moveCamera(-50); break;
                    case EQUALS: moveCamera(50); break;
//                    case Q: animateSpin(cubies[], Cube.Turn.CLOCKWISE,Rotate.Y_AXIS);
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case SHIFT:
                    acToggle.setSelected(false); currentSuffix = ""; thisTurn = Cube.Turn.CLOCKWISE; updateButtons(); break;
                case CONTROL:
                    wideToggle.setSelected(false); wideSelected = false; updateButtons(); break;
            }
        });

    }

    public void updateButtons() {
        uButton.setText((!wideSelected?"U":"u")+currentSuffix);
        fButton.setText((!wideSelected?"F":"f")+currentSuffix);
        lButton.setText((!wideSelected?"L":"l")+currentSuffix);
        rButton.setText((!wideSelected?"R":"r")+currentSuffix);
        bButton.setText((!wideSelected?"B":"b")+currentSuffix);
        xButton.setText("x"+currentSuffix);
        yButton.setText("y"+currentSuffix);
        zButton.setText("z"+currentSuffix);
        dButton.setText((!wideSelected?"D":"d")+currentSuffix);
        mButton.setText("M"+currentSuffix);
        eButton.setText("E"+currentSuffix);
        sButton.setText("S"+currentSuffix);

    }

    public void updateFaces() {
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < 3; z++) {
                    cubies[x][y][z].updateColours();
                }
            }
        }
    }

    public void moveCamera(int amount) {
        camera.setTranslateZ(camera.getTranslateZ()+amount);
    }

    class SpinGroup extends Group {

        void resetSpin() {
            angleX.set(30);
            angleY.set(45);
        }

        void initMouseControl() {
            Rotate xRotate;
            Rotate yRotate;
            getTransforms().addAll(xRotate = new Rotate(0,Rotate.X_AXIS),yRotate = new Rotate(0,Rotate.Y_AXIS));
            xRotate.angleProperty().bind(angleX);
            yRotate.angleProperty().bind(angleY);

            scene.setOnMousePressed(event -> {
                anchorX = event.getSceneX();
                anchorY = event.getSceneY();
                anchorAngleX = angleX.get();
                anchorAngleY = angleY.get();
            });

            scene.setOnMouseDragged(event -> {
                angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
                angleY.set(anchorAngleY + (anchorX - event.getSceneX()));
            });
        }
    }

    void animateSpin(ArrayList<Cubie> movedCubies, Cube.Turn turn, Point3D axis) {
        AnimationTimer timer = new AnimationTimer() {
            int i = 0;
            final int turnMod = ((turn == Cube.Turn.CLOCKWISE)?1:((turn == Cube.Turn.ANTICLOCKWISE)?-1:((turn == Cube.Turn.TWO)?2:0)));
            @Override
            public void handle(long now) {
                if (i==0) {
                    for (Cubie cubie:movedCubies) {
                        cubie.cuboid.setRotationAxis(axis);
                    }
                }
                if (i++ >= 30) {
                    for (Cubie cubie: movedCubies) {
                        cubie.cuboid.setRotate(0);
                    }
                    updateFaces();
                    stop();
                } else {
                    for (Cubie cubie:movedCubies) {
                        cubie.cuboid.setRotate(cubie.cuboid.getRotate()+3*turnMod);
                    }
                }
            }
        };
        timer.start();
    }
}
