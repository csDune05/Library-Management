package com.example.librabry_management;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.Objects;

public class LibrarySearchApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải giao diện từ FXML
        Parent root = FXMLLoader.load(getClass().getResource("Book.fxml"));

        // Tạo Scene và hiển thị Stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Google Books Search");
        primaryStage.show();
    }
}