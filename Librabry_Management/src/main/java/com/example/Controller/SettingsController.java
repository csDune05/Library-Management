package com.example.Controller;

import com.example.librabry_management.MainStaticObjectControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private Button onButton;

    @FXML
    private Button offButton;

    @FXML
    private Button cancelButton;

    private MediaPlayer musicPlayer;

    @FXML
    public void initialize() {
        musicPlayer = MainStaticObjectControl.getMusicPlayer();
        if (musicPlayer == null) {
            return;
        }

        try {
            // Xử lý nút on
            onButton.setOnAction(event -> {
                if (musicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                    musicPlayer.play();
                }
            });

            // Xử lý nút off
            offButton.setOnAction(event -> {
                if (musicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    musicPlayer.stop();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CancelActionHandle() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}