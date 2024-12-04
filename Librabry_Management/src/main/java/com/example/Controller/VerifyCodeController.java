package com.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VerifyCodeController {
    @FXML
    private TextField verificationCodeField;

    private String actualCode;
    private String userEmail;

    public void setVerificationCode(String actualCode) {
        this.actualCode = actualCode;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    public void verifyCode() {
        String enteredCode = verificationCodeField.getText();

        if (enteredCode.equals(actualCode)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/ResetPasswordForm.fxml"));
                Parent resetPasswordForm = loader.load();

                ResetPasswordController resetPasswordController = loader.getController();
                resetPasswordController.setUserEmail(userEmail);

                Stage stage = new Stage();
                stage.setTitle("Reset Password");
                stage.setScene(new Scene(resetPasswordForm));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Invalid verification code. Please try again.");
        }
    }

    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
