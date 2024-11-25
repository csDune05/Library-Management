package com.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.example.librabry_management.*;

public class WellcomeSpaceController {
    @FXML
    private Button HomeButton;

    @FXML
    private Button AboutUsButton;

    @FXML
    private Button SignUpButton;

    @FXML
    private Button LoginButton;

    @FXML
    private Button GetStartedButton;

    private Stage loginStage;
    private Stage signUpStage;

    public void AboutUsButtonHandle() {
        try {
            Parent AboutUsRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/AboutUs.fxml"));
            Scene aboutUsScene = new Scene(AboutUsRoot);

            Stage stage = (Stage) HomeButton.getScene().getWindow();

            stage.setScene(aboutUsScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openLoginStage() {
        try {
            if (loginStage == null) {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Login.fxml"));

                loginStage = new Stage();
                loginStage.initModality(Modality.WINDOW_MODAL);
                loginStage.initOwner(LoginButton.getScene().getWindow());
                loginStage.initStyle(StageStyle.UTILITY);
                loginStage.setTitle("Login");

                Scene loginScene = new Scene(loginRoot);
                loginStage.setScene(loginScene);

                loginStage.setOnHidden(event -> loginStage = null);
            }

            loginStage.centerOnScreen();
            MainStaticObjectControl.setLoginStage(loginStage);
            loginStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoginButtonHandle() {
        openLoginStage();
    }

    public void GetStartedButtonHandle() {
        openLoginStage();
    }

    public void SignUpButtonHandle() {
        try {
            if (signUpStage == null) {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/SignUp.fxml"));

                loginStage = new Stage();
                loginStage.initModality(Modality.WINDOW_MODAL);
                loginStage.initOwner(LoginButton.getScene().getWindow());
                loginStage.initStyle(StageStyle.UTILITY);
                loginStage.setTitle("Sign Up");

                Scene loginScene = new Scene(loginRoot);
                loginStage.setScene(loginScene);

                loginStage.setOnHidden(event -> loginStage = null);
            }

            loginStage.centerOnScreen();
            loginStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
