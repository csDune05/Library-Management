package com.example.Controller;

import com.example.librabry_management.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResetPasswordController {
    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void resetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền vào tất cả các trường.");
            return;
        }

        if (newPassword.equals(confirmPassword)) {

            try (Connection connection = DatabaseHelper.connect()) {
                String updatePasswordQuery = "UPDATE users SET password = ? WHERE email = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updatePasswordQuery);
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, userEmail);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Thành công", "Mật khẩu đã được đặt lại thành công!");
                    // Đóng form reset mật khẩu, form verify code và form forgot password


                } else {
                    showAlert("Lỗi", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Có lỗi xảy ra khi cập nhật mật khẩu.");
            }
        } else {
            showAlert("Lỗi", "Mật khẩu không khớp. Vui lòng thử lại.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
