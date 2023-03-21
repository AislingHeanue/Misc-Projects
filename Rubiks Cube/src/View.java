import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class View extends Application {

    int n;
    Controller controller;
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        n = 3;
        int boxWidth = 100;
        setUserAgentStylesheet(STYLESHEET_MODENA);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(Files.newInputStream(Paths.get(new URI(Objects.requireNonNull(getClass().getResource("Scene.fxml")).getPath()).getPath())));
        controller = fxmlLoader.getController();
        Scene scene = new Scene(root,811,600,true);
        controller.setup(n,boxWidth,scene,600);

        stage.setTitle("Rubik's Cube Simulator");
        stage.setScene(scene);

        stage.show();
    }
}
