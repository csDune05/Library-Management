package com.example.librabry_management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainStaticObjectControl {
    private static Stage welcomeStage;
    private static Stage loginStage;
    private static Stage dashboardStage;
    private static User currentUser;
    private static Stage settingsStage;

    private static MediaPlayer musicPlayer;
    private static ComboBox<String> activeComboBox;
    private static AnchorPane notificationPane;
    private static ScrollPane notificationScrollPane;
    private static VBox notificationList;

    private static int numOfNotifications = 0;

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
    }

    public static void showAnchorPane(AnchorPane notification, javafx.scene.control.Button notificationButton) {
        if (notification.isVisible()) {
            notification.setVisible(false);
            notificationButton.getStyleClass().remove("active");
            numOfNotifications = 0;
        } else {
            notification.setVisible(true);
            notificationButton.getStyleClass().add("active");
        }
    }

    public static void updateNotifications(ScrollPane notificationScrollPane, VBox notificationList) {
        List<String> notifications = readNotificationsFromFile();

        notificationList.getChildren().clear();

        // Thêm thông báo vào VBox
        for (int i = 0; i < notifications.size(); i++) {
            String line = notifications.get(i);

            Text notificationText = new Text(line);

            // Thiết lập định dạng cho dòng nội dung in đậm
            if (line.startsWith("**")) {
                notificationText.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-fill: black;");
            }

            // Thiết lập định dạng cho dòng thời gian in nghiêng
            if (line.startsWith("_") && !line.startsWith("**")) {
                notificationText.setStyle("-fx-font-size: 14; -fx-font-style: italic; -fx-fill: gray;");
            }

            notificationList.getChildren().add(notificationText);
        }

        notificationScrollPane.setContent(notificationList);
    }

    // Đọc thông báo từ file notificationsContent.txt sự kiện gần nhất in ra trước.
    private static List<String> readNotificationsFromFile() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("notificationsContent.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(lines);
        return lines;
    }

    public static void addNotificationToFile(String notification) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notificationsContent.txt", true))) {
            writer.write("**" + notification + "**");
            writer.newLine();

            String timestamp = getCurrentTimestamp();
            writer.write("_" + timestamp + "_");
            writer.newLine();

            writer.newLine();

            numOfNotifications++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH'h'mm dd/MM/yyyy");
        return now.format(formatter);
    }

    public static void updateNotificationIcon(ImageView notificationImageView) {
        if (numOfNotifications == 0) {
            notificationImageView.setImage(new Image(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Images/bell1.png").toExternalForm()));
        } else {
            notificationImageView.setImage(new Image(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Images/bell2.png").toExternalForm()));
        }
    }

    public static MediaPlayer getMusicPlayer() {
        return TestApplication.getMediaPlayer();
    }

    public static void setMusicPlayer(MediaPlayer player) {
        musicPlayer = player;
    }
}
