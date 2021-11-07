package ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // EFFECTS: creates primary stage
    // Implementation is based on https://www.youtube.com/watch?v=9XJicRt_FaI [12:40]
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/scene.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css"))
                .toExternalForm());

        this.primaryStage.setTitle("Spending Organizer");
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public static void main(String[] args) {
//        new SpendingApp();
        launch();
    }
}
