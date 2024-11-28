package com.example.librabry_management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainStaticObjectControl {
    private static Stage welcomeStage;
    private static Stage loginStage;
    private static Stage dashboardStage;
    private static User currentUser;
    private static Stage settingsStage;
    private static Stage notificationStage;

    private static MediaPlayer musicPlayer;
    private static ComboBox<String> activeComboBox;

    private static int numOfNotifications = 1;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
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

    // cai dat combobox options.
    public static void configureOptionsComboBox(ComboBox<String> optionsComboBox) {
        optionsComboBox.getItems().addAll("My Profile", "Log out", "Settings");
        activeComboBox = optionsComboBox;
        optionsComboBox.setOnAction(event -> {
            String selectedOption = optionsComboBox.getValue();
            // Xử lý log out
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
            // Xử lý cho "My Profile"
            else if ("My Profile".equals(selectedOption)) {
                // Điều hướng sang giao diện Profile
                openProfileStage((Stage) optionsComboBox.getScene().getWindow());
            }
            // Xử lý cho "Settings"
            else if ("Settings".equals(selectedOption)) {
                openSettingsStage();
            }
        });
    }

    public static void resetComboBoxOptions() {
        if (activeComboBox != null) {
            activeComboBox.setValue(null);
            activeComboBox.setPromptText("Options");
        }
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

    public static void openSettingsStage() {
        try {
            if (settingsStage == null) {
                settingsStage = new Stage();
                Parent settingsRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Settings.fxml"));
                settingsStage.setScene(new Scene(settingsRoot));
                settingsStage.setTitle("Settings");
                settingsStage.setResizable(false);
                settingsStage.initModality(Modality.APPLICATION_MODAL);
                settingsStage.setOnCloseRequest(event -> resetComboBoxOptions());
            }
            settingsStage.centerOnScreen();
            settingsStage.showAndWait();

            // Đặt lại ComboBox về null khi cửa sổ đóng
            resetComboBoxOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // cai dat thong bao.
    public static void configureNotificationButton(ImageView notificationImageView, javafx.scene.control.Button notificationButton) {
        updateNotificationIcon(notificationImageView);

        notificationButton.setOnAction(event -> openNotificationStage());
    }

    public static void updateNotificationIcon(ImageView notificationImageView) {
        if (numOfNotifications == 0) {
            notificationImageView.setImage(new Image(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Images/bell1.png").toExternalForm()));
        } else {
            notificationImageView.setImage(new Image(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Images/bell2.png").toExternalForm()));
        }
    }

    public static void openNotificationStage() {
        try {
            if (notificationStage == null) {
                notificationStage = new Stage();
                Parent notificationRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Notification.fxml"));
                notificationStage.setScene(new Scene(notificationRoot));
                notificationStage.setTitle("Notifications");
                notificationStage.setResizable(false);
                notificationStage.initModality(Modality.APPLICATION_MODAL);
                notificationStage.setOnCloseRequest(event -> notificationStage = null);
            }
            notificationStage.centerOnScreen();
            notificationStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MediaPlayer getMusicPlayer() {
        return TestApplication.getMediaPlayer();
    }

    public static void setMusicPlayer(MediaPlayer player) {
        musicPlayer = player;
    }

    public static void setNumOfNotifications(int count) {
        numOfNotifications = Math.max(0, count);
    }

    public static int getNumOfNotifications() {
        return numOfNotifications;
    }
}
