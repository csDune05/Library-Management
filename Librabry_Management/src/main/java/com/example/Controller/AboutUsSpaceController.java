package com.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.example.librabry_management.*;

public class AboutUsSpaceController {
    @FXML
    private Button homeButton;

    public Stage getCurrenStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    /**
     * Handle the return home event.
     */
    public void homeButtonHandle() {
        MainStaticObjectControl.openWelcomeStage(getCurrenStage());
    }
}
