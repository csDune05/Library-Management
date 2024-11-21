package com.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.example.librabry_management.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

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

    private Preferences prefs;

    private Stage signUpStage;
    private Stage DashboardStage;

    @FXML
    public void initialize() {
        // Tải Preferences
        prefs = Preferences.userNodeForPackage(LoginController.class);

        // Kiểm tra trạng thái Remember Me
        String savedUsername = prefs.get("username", "");
        String savedPassword = prefs.get("password", "");
        boolean rememberMe = prefs.getBoolean("rememberMe", false);

        if (rememberMe) {
            emailField.setText(savedUsername);
            passwordField.setText(savedPassword);
            staysignedin.setSelected(true);
        }
    }

    @FXML
    public void SignInButtonHandle() {
        String username = emailField.getText();
        String password = passwordField.getText();

        // Kiểm tra trạng thái checkbox
        if (staysignedin.isSelected()) {
            prefs.put("username", username);
            prefs.put("password", password);
            prefs.putBoolean("rememberMe", true);
        } else {
            prefs.remove("username");
            prefs.remove("password");
            prefs.putBoolean("rememberMe", false);
        }

        if (isLoginValid(username, password)) {
            statusLabel.setText("Login Successful!");
            openDashboard();
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
                Parent signupRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/SignUp.fxml"));

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

    private void openDashboard() {
        try {
            if (DashboardStage == null) {
                Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Dashboard.fxml"));
                Scene dashboardScene = new Scene(dashboardRoot);

                Stage currentStage = (Stage) signinButton.getScene().getWindow();
                currentStage.close();

                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Dashboard");
                dashboardStage.setScene(dashboardScene);
                StageManager.setDashboardStage(dashboardStage);
                StageManager.closeWelcomeStage();
                StageManager.closeLoginStage();
                dashboardStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
