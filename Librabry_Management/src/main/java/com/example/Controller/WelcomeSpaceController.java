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
import javafx.scene.media.MediaPlayer;

public class WelcomeSpaceController {
    @FXML
    private Button homeButton;

    @FXML
    private Button aboutUsButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button getStartedButton;

    @FXML
    private Button musicToggleButton;

    private Stage loginStage;
    private Stage signUpStage;
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = true;

    public Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    /**
     * handle the switch to about us scene event.
     */
    public void aboutUsButtonHandle() {
        MainStaticObjectControl.openAboutUsStage(getCurrentStage());
    }

    /**
     * handle the switch to login stage event.
     */
    public void loginButtonHandle() {
        openLoginStage();
    }

    /**
     * handle the switch to login stage event by getStarted button.
     */
    public void getStartedButtonHandle() {
        openLoginStage();
    }

    /**
     * open login stage.
     */
    public void openLoginStage() {
        try {
            if (loginStage == null) {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Login.fxml"));

                loginStage = new Stage();
                loginStage.initModality(Modality.WINDOW_MODAL);
                loginStage.initOwner(loginButton.getScene().getWindow());
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

    /**
     * handle the switch to sign up stage event.
     */
    public void signUpButtonHandle() {
        try {
            if (signUpStage == null) {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/SignUp.fxml"));

                loginStage = new Stage();
                loginStage.initModality(Modality.WINDOW_MODAL);
                loginStage.initOwner(loginButton.getScene().getWindow());
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

    /**
     * handle turn on/off music event.
     */
    public void musicToggleButtonHandle() {
        MediaPlayer mediaPlayer = TestApplication.getMediaPlayer();
        if (mediaPlayer != null) {
            if (isMusicPlaying) {
                mediaPlayer.pause();
                musicToggleButton.setText("Music On");
            } else {
                mediaPlayer.play();
                musicToggleButton.setText("Music Off");
            }
            isMusicPlaying = !isMusicPlaying;
        }
    }
}
