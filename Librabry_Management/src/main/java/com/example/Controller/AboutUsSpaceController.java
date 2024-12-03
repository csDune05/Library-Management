package com.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.librabry_management.*;
import com.example.Feature.*;
import java.io.IOException;

public class AboutUsSpaceController {
    @FXML
    private Button HomeButton;

    private Stage getCurrenStage() {
        return (Stage) HomeButton.getScene().getWindow();
    }

    public void HomeButtonHandle() {
        MainStaticObjectControl.openWelcomeStage(getCurrenStage());
    }
}
