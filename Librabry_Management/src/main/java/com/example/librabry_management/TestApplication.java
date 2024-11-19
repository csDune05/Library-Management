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

            Stage welcomeStage = new Stage();
            Scene scene = new Scene(root);
            welcomeStage.setTitle("Welcome");
            welcomeStage.setScene(scene);

            StageManager.setWelcomeStage(welcomeStage);

            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            welcomeStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && newScene.getStylesheets().isEmpty()) {
                    newScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                }
            });

            welcomeStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
