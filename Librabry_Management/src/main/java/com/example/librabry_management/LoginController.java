package com.example.librabry_management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signinButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button signupButton;

    @FXML
    private CheckBox staysignedin;

    private Stage signUpStage;

    @FXML
    public void SignInButtonHandle() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (isLoginValid(username, password)) {
            statusLabel.setText("Login Successful!");
        } else {
            statusLabel.setText("Email or Password is incorrect!");
        }
    }

    private boolean isLoginValid(String email, String password) {
        File accountsList = new File("accounts.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsList))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] accountDetails = line.split(",");
                if (accountDetails.length >= 2) {
                    String storedEmail = accountDetails[0];
                    String storedPassword = accountDetails[1];

                    if (storedEmail.equals(email) && storedPassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void SignUpButtonHandle() {
        try {
            if (signUpStage == null) {
                Parent signupRoot = FXMLLoader.load(getClass().getResource("SignUp.fxml"));

                signUpStage = new Stage();
                signUpStage.initModality(Modality.WINDOW_MODAL);
                signUpStage.initOwner(signinButton.getScene().getWindow());
                signUpStage.initStyle(StageStyle.UTILITY);
                signUpStage.setTitle("Sign Up");

                Scene loginScene = new Scene(signupRoot);
                signUpStage.setScene(loginScene);

                signUpStage.setOnHidden(event -> signUpStage = null);
            }

            signUpStage.centerOnScreen();
            signUpStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancelAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
