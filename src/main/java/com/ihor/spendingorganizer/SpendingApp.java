package com.ihor.spendingorganizer;

import com.ihor.spendingorganizer.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

// Class that creates GUI window and launches the application
public class SpendingApp extends Application {

    // EFFECTS: creates primary stage,
    //          and gets an instance of Controller to handle closing window event
    // Implementation is based on https://stackoverflow.com/a/13247005
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css"))
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
        launch();
    }
}
