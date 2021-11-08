package ui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.controllers.Controller;

import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // EFFECTS: creates primary stage and gets an instance of Controller
    // Implementation is based on https://stackoverflow.com/a/13247005
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/scene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css"))
                .toExternalForm());
        Controller controller = loader.getController();

        primaryStage.setTitle("Spending Organizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            controller.closeMenuItemClicked();
        });
    }

    public static void main(String[] args) {
//        new SpendingApp();
        launch();
    }
}
