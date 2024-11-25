package com.example.librabry_management;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class MainStaticObjectControl {
    private static Stage welcomeStage;
    private static Stage loginStage;
    private static Stage dashboardStage;
    private static int currentUserId; // Lưu user_id của người dùng đăng nhập

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static void setWelcomeStage(Stage stage) {
        welcomeStage = stage;
    }

    public static void setLoginStage(Stage stage) {
        loginStage = stage;
    }

    public static void setDashboardStage(Stage stage) {
        dashboardStage = stage;
    }

    public static void closeWelcomeStage() {
        if (welcomeStage != null) {
            welcomeStage.close();
        }
    }

    public static void closeLoginStage() {
        if (loginStage != null) {
            loginStage.close();
        }
    }

    public static void openWelcomeStage() {
        welcomeStage.show();
    }

    public static void configureOptionsComboBox(ComboBox<String> optionsComboBox) {
        optionsComboBox.getItems().addAll("My Profile", "Log out");
        optionsComboBox.setOnAction(event -> {
            String selectedOption = optionsComboBox.getValue();
            if ("Log out".equals(selectedOption)) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmation");
                confirmationAlert.setHeaderText("Are you sure you want to log out?");
                confirmationAlert.setContentText("Press OK to log out, or Cancel to stay.");

                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        optionsComboBox.getScene().getWindow().hide();
                        openWelcomeStage();
                    } else {
                        optionsComboBox.setValue(null);
                    }
                });
            }
            // Xử lý cho "My Profile" hoặc các tùy chọn khác nếu cần
            else if ("My Profile".equals(selectedOption)) {
                // Điều hướng sang giao diện Profile
                try {
                    Stage currentStage = (Stage) optionsComboBox.getScene().getWindow();
                    MainStaticObjectControl.openProfileStage(currentStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void openProfileStage(Stage currentStage) {
        try {
            Parent profileRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Profile.fxml"));
            currentStage.setScene(new Scene(profileRoot));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
