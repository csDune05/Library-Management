package com.example.librabry_management;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutUsSpaceController {
    @FXML
    private Button HomeButton;

    public void HomeButtonHandle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Wellcome.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) HomeButton.getScene().getWindow();

            Scene welcomeScene = new Scene(root);

            stage.setScene(welcomeScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
