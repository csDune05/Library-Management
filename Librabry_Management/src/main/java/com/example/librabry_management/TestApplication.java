package com.example.librabry_management;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class TestApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Wellcome.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Welcome");
            primaryStage.setScene(scene);

            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && newScene.getStylesheets().isEmpty()) {
                    newScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                }
            });

            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
