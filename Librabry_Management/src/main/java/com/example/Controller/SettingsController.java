package com.example.Controller;

import com.example.librabry_management.*;
import com.example.Feature.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private Button onButton;

    @FXML
    private Button offButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ToggleButton darkModeToggle; // Thêm ToggleButton cho Dark Mode

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

            // Liên kết nút ToggleButton với trạng thái Dark Mode
            ThemeManager themeManager = ThemeManager.getInstance();
            darkModeToggle.selectedProperty().bindBidirectional(themeManager.darkModeEnabledProperty());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CancelActionHandle() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        MainStaticObjectControl.resetComboBoxOptions();
    }
}
