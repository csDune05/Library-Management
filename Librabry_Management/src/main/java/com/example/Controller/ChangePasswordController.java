package com.example.Controller;
import com.almasb.fxgl.net.Connection;
import com.example.librabry_management.DatabaseHelper;
import com.example.librabry_management.MainStaticObjectControl;
import com.example.librabry_management.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    private Button donateUsButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button booksButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button clearNotificationsButton;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private ImageView notificationImageView;

    @FXML
    private Button notificationButton;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private ScrollPane notificationScrollPane;

    @FXML
    private VBox notificationList;

    @FXML
    private TextArea notificationText;

    @FXML
    private Button myLibraryButton;


    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }


    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String userEmail = MainStaticObjectControl.getCurrentUser().getEmail();

    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Lỗi", "Tất cả các trường mật khẩu đều phải được điền đầy đủ!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
            return;
        }

        if (changePassword(oldPassword, newPassword)) {
            showAlert(AlertType.INFORMATION, "Thành công", "Mật khẩu đã được thay đổi!");
        } else {
            showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu cũ không đúng!");
        }
    }

    private boolean changePassword(String oldPassword, String newPassword) {

        String sqlSelect = "SELECT password FROM users WHERE email = ?";
        try (PreparedStatement stmt = DatabaseHelper.connect().prepareStatement(sqlSelect)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(oldPassword)) {

                    String sqlUpdate = "UPDATE users SET password = ? WHERE email = ?";
                    try (PreparedStatement updateStmt = DatabaseHelper.connect().prepareStatement(sqlUpdate)) {
                        updateStmt.setString(1, newPassword);
                        updateStmt.setString(2, userEmail);
                        int rowsUpdated = updateStmt.executeUpdate();

                        if (rowsUpdated > 0) {
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void editProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    @FXML
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }


    @FXML
    public void initialize() {
        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        // notification
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }
}
