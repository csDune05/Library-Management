package com.example.Controller;

import com.example.librabry_management.SendEmailUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Random;

public class ForgotPasswordController {
    @FXML
    private TextField forgotEmailField;  // Xóa static

    private String verificationCode;
    private String userEmail;

    @FXML
    private void sendVerificationCode() {
        String email = forgotEmailField.getText();
        if (email.isEmpty()) {
            showAlert("Error", "Please enter your email.");
            return;
        }

        // Tạo mã xác thực
        verificationCode = String.valueOf(new Random().nextInt(999999));
        userEmail = email;

        // Gửi mã xác thực qua email sử dụng phương thức sendEmail
        String subject = "Verification Code";
        String messageText = "Your verification code is: " + verificationCode;

        try {
            SendEmailUtil.sendEmail(email, subject, messageText);
            showAlert("Verification Code Sent", "A verification code has been sent to your email: " + email);

            // Mở form nhập mã xác thực và truyền mã xác thực sang VerifyCodeController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/VerifyCodeForm.fxml"));
            Parent verifyCodeForm = loader.load();

            VerifyCodeController verifyCodeController = loader.getController();
            verifyCodeController.setVerificationCode(verificationCode); // Truyền mã xác thực
            verifyCodeController.setUserEmail(userEmail); // Truyền email của người dùng

            Stage stage = new Stage();
            stage.setTitle("Verify Code");
            stage.setScene(new Scene(verifyCodeForm));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send email. Please try again.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
