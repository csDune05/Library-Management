package com.example.Controller;

import com.example.librabry_management.MainStaticObjectControl;
import com.example.librabry_management.TestApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

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

    @FXML
    private Button MusicToggleButton;

    private Stage loginStage;
    private Stage signUpStage;

    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = true;

    public Stage getCurrentStage() {
        return (Stage) HomeButton.getScene().getWindow();
    }

    public void AboutUsButtonHandle() {
        MainStaticObjectControl.openAboutUsStage(getCurrentStage());
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

    public void MusicToggleButtonHandle() {

        MediaPlayer mediaPlayer = TestApplication.getMediaPlayer();

        if (mediaPlayer != null) {
            if (isMusicPlaying) {
                mediaPlayer.pause();
                MusicToggleButton.setText("Music On");
            } else {
                mediaPlayer.play();
                MusicToggleButton.setText("Music Off");
            }
            isMusicPlaying = !isMusicPlaying;
        }
    }
}
