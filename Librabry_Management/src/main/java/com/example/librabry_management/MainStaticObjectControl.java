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
import com.example.Controller.*;
import com.example.Feature.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainStaticObjectControl {
    private static Stage welcomeStage;
    private static Stage loginStage;
    private static Stage dashboardStage;
    private static User currentUser;
    private static Stage settingsStage;

    private static MediaPlayer musicPlayer;
    private static ComboBox<String> activeComboBox;
    private static Book lastReturnBook;

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

    public static void setLastReturnBook(Book book) {
        lastReturnBook = book;
    }

    public static Book getLastReturnBook() {
        return lastReturnBook;
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

    @FXML
    public static void openLibraryStage(Stage currentStage) {
        try {
            Parent booksRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/MyLibrary.fxml"));
            Scene booksScene = new Scene(booksRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(booksScene);

            currentStage.setScene(booksScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openWelcomeStage(Stage currentStage) {
        try {
            Parent homeRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Wellcome.fxml"));
            Scene homeScene = new Scene(homeRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(homeScene);

            currentStage.setScene(homeScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openAboutUsStage(Stage currentStage) {
        try {
            Parent homeRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/AboutUs.fxml"));
            Scene homeScene = new Scene(homeRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(homeScene);

            currentStage.setScene(homeScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openProfileStage(Stage currentStage) {
        try {
            Parent profileRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Profile.fxml"));
            Scene profileScene = new Scene(profileRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(profileScene);

            currentStage.setScene(profileScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openDashboardStage(Stage currentStage) {
        try {
            Parent homeRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Dashboard.fxml"));
            Scene homeScene = new Scene(homeRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(homeScene);

            currentStage.setScene(homeScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openBookStage(Stage currentStage) {
        try {
            Parent homeRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Book.fxml"));
            Scene homeScene = new Scene(homeRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(homeScene);

            currentStage.setScene(homeScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openDonateStage(Stage currentStage) {
        try {
            Parent homeRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/DonateUs.fxml"));
            Scene homeScene = new Scene(homeRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(homeScene);

            currentStage.setScene(homeScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logOut(Stage currentStage) {
        currentStage.close();
        openWelcomeStage();
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

    public static void openProfilePasswordAndSecurityStage(Stage currentStage) {
        try {
            Parent profileRoot = FXMLLoader.load(MainStaticObjectControl.class.getResource("/com/example/librabry_management/Password&SecurityProfile.fxml"));
            Scene profileScene = new Scene(profileRoot);

            // Áp dụng theme từ SceneHelper
            SceneHelper.applyTheme(profileScene);

            currentStage.setScene(profileScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetComboBoxOptions() {
        if (activeComboBox != null) {
            activeComboBox.setValue(null);
            activeComboBox.setPromptText("Options");
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
            markAllNotificationsAsRead();
            updateNotificationIcon((ImageView) notificationButton.getGraphic());
        } else {
            notification.setVisible(true);
            notificationButton.getStyleClass().add("active");
            markAllNotificationsAsRead();
        }
    }

    public static void updateNotifications(ScrollPane notificationScrollPane, VBox notificationList) {
        List<String> notifications = readNotificationsForCurrentUser();
        notificationScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        notificationScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        notificationList.getChildren().clear();

        notificationList.setSpacing(20);
        Collections.reverse(notifications);

        for (String notification : notifications) {
            String[] parts = notification.split("\n", 2);
            if (parts.length < 2) continue;

            VBox notificationItem = new VBox();
            notificationItem.setStyle("-fx-padding: 10; -fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-width: 1;");
            double initialHeight = notificationItem.getHeight();
            double initialWidth = notificationItem.getWidth();

            // Đổi màu nền khi di chuột vào
            notificationItem.setOnMouseEntered(event -> {
                notificationItem.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-width: 1;");
            });

            // Trả lại màu nền khi chuột rời đi
            notificationItem.setOnMouseExited(event -> {
                notificationItem.setStyle("-fx-padding: 10; -fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-width: 1;");
            });

            Text timestampText  = new Text(parts[0]);;
            timestampText.setStyle("-fx-font-size: 14; -fx-fill: gray; -fx-font-style: italic;");

            Text messageText = new Text(parts[1]);
            if (parts[1].startsWith("[NEW]")) {
                messageText.setStyle("-fx-font-size: 14; -fx-fill: black;");
            } else {
                messageText.setStyle("-fx-font-size: 14; -fx-fill: gray;");
            }
            messageText.setWrappingWidth(notificationScrollPane.getWidth() - 40);

            notificationItem.getChildren().addAll(timestampText, messageText);
            notificationList.getChildren().add(notificationItem);

            notificationItem.setOnMouseClicked(event -> {
                Boolean isExpanded = (Boolean) notificationItem.getUserData();
                if (isExpanded == null) {
                    isExpanded = false;
                }

                if (!isExpanded) {
                    if (parts[1].startsWith("[NEW]")) {
                        parts[1] = parts[1].replace("[NEW]", "[READ]").trim();
                        messageText.setText(parts[1]);
                        messageText.setStyle("-fx-font-size: 14; -fx-fill: gray;");
                    }

                    notificationItem.setPrefHeight(120);
                    messageText.setWrappingWidth(notificationScrollPane.getWidth() - 40); // chiều rộng tự động xuống dòng
                } else {
                    notificationItem.setPrefHeight(initialHeight);
                    notificationItem.setPrefWidth(initialWidth);
                }

                notificationItem.setUserData(!isExpanded);
            });
        }

        notificationScrollPane.setContent(notificationList);
    }

    // Đọc thông báo từ file notifications.json cho user cụ thể
    private static List<String> readNotificationsForCurrentUser() {
        List<String> notifications = new ArrayList<>();
        String username = currentUser.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader("notifications.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject root = new JSONObject(jsonContent.toString());
            JSONArray userNotifications = root.optJSONArray(username);

            if (userNotifications != null) {
                for (int i = 0; i < userNotifications.length(); i++) {
                    JSONObject notification = userNotifications.getJSONObject(i);
                    String timestamp = notification.getString("timestamp");
                    String message = notification.getString("message");
                    boolean isRead = notification.getBoolean("read");

                    String formattedMessage = timestamp + "\n" +
                            (isRead ? "[READ] " : "[NEW] ") + message;
                    notifications.add(formattedMessage);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public static void addNotificationToFile(String notification) {
        String username = currentUser.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader("notifications.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Tạo mới một đối tượng JSONObject nếu chưa có nội dung
            JSONObject root = jsonContent.length() > 0 ? new JSONObject(jsonContent.toString()) : new JSONObject();

            // Tạo mục thông báo cho user nếu chưa tồn tại
            if (!root.has(username)) {
                JSONArray newUserNotifications = new JSONArray();
                root.put(username, newUserNotifications);
            }

            // Lấy mục thông báo của user
            JSONArray userNotifications = root.optJSONArray(username);

            // Tạo thông báo mới
            JSONObject newNotification = new JSONObject();
            newNotification.put("message", notification);
            newNotification.put("timestamp", getCurrentTimestamp());
            newNotification.put("read", false);

            // thông báo đẩy (mới nhất lên trước)
            userNotifications.put(newNotification);
            root.put(username, userNotifications);

            // Ghi vào file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.json"))) {
                writer.write(root.toString(4));
                numOfNotifications++;
            }
        } catch (IOException | JSONException e) {
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

    private static void markAllNotificationsAsRead() {
        String username = currentUser.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader("notifications.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject root = new JSONObject(jsonContent.toString());
            JSONArray userNotifications = root.optJSONArray(username);

            if (userNotifications != null) {
                for (int i = 0; i < userNotifications.length(); i++) {
                    JSONObject notification = userNotifications.getJSONObject(i);
                    boolean isRead = notification.getBoolean("read");
                    if (!isRead) {
                        // Đánh dấu là đã đọc
                        notification.put("read", true);
                        notification.put("message", notification.getString("message").replace("[NEW]", "[READ]"));
                    }
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.json"))) {
                    writer.write(root.toString(4));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void clearAllNotificationsForUser() {
        String username = currentUser.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader("notifications.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject root = jsonContent.length() > 0 ? new JSONObject(jsonContent.toString()) : new JSONObject();

            if (root.has(username)) {
                root.put(username, new JSONArray()); // Xóa toàn bộ thông báo của user
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.json"))) {
                writer.write(root.toString(4));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public static MediaPlayer getMusicPlayer() {
        return TestApplication.getMediaPlayer();
    }

    public static void setMusicPlayer(MediaPlayer player) {
        musicPlayer = player;
    }
}
