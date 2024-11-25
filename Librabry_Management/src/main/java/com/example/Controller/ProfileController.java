package com.example.Controller;

import com.example.librabry_management.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private Button donateUsButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button booksButton;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private Button myLibraryButton;

    @FXML
    public void myLibraryButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/MyLibrary.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) myLibraryButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void homeButtonHandler() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Dashboard.fxml"));
            Scene homeScene = new Scene(homeRoot);

            Stage stage = (Stage) homeButton.getScene().getWindow();

            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void DonateUsButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/DonateUs.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) homeButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void BooksButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Book.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) booksButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
    }

}
