package com.example.librabry_management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminEditorInterface extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseHelper.createUsersTable();
        DatabaseHelper.createTable();
        DatabaseHelper.createUserBooksTable();
        Parent root = FXMLLoader.load(getClass().getResource("AdminInterface.fxml"));
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}