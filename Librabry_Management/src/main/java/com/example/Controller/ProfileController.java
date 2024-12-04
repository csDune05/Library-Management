package com.example.Controller;

import com.example.librabry_management.*;
import com.example.Feature.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {

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

    @FXML
    private ImageView avatarImageView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField birthdateField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField locationField;

    @FXML
    private PasswordField passwordField;

    private User currentUser;

    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    /**
     * Handle confirm chang avatar event.
     */
    public boolean confirmChangeAvatar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change Avatar");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to change your avatar?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Handle change avatar.
     */
    public void handleChangeAvatar() {
        if (!confirmChangeAvatar()) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (selectedFile != null) {
            if (!selectedFile.getName().matches(".*\\.(png|jpg|jpeg)$")) {
                showAlert("Invalid File", "Please select a valid image file.");
                return;
            }

            Image newAvatar = new Image(selectedFile.toURI().toString());
            avatarImageView.setImage(newAvatar);

            saveAvatarPath(selectedFile.getAbsolutePath());
        }
    }

    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void saveAvatarPath(String path) {
        System.out.println("Avatar path saved: " + path);
    }

    /**
     * Show forum.
     */
    @FXML
    public void forumProfileButtonHandler() {MainStaticObjectControl.openProfileForumStage(getCurrentStage());}

    /**
     * Show note.
     */
    @FXML
    public void notesProfileButtonHandler() {MainStaticObjectControl.openProfileNotesStage(getCurrentStage());}

    /**
     * Handle switch to password and security scene.
     */
    @FXML
    public void passwordAndSecurityButtonHandler() {MainStaticObjectControl.openProfilePasswordAndSecurityStage(getCurrentStage());}

    /**
     * Handle switch to my library scene.
     */
    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    /**
     * Handle switch to home scene.
     */
    @FXML
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    /**
     * Handle switch to donate us scene.
     */
    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    /**
     * Handle switch to books scene.
     */
    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    /**
     * Handle exit event.
     */
    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    /**
     * Handle clear all notification.
     */
    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    public User getCurrentUser() {
        return MainStaticObjectControl.getCurrentUser();
    }

    public void setAvatarRoundedCorners() {
        Rectangle clip = new Rectangle(avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        clip.setArcWidth(20); // Độ bo góc
        clip.setArcHeight(20);
        avatarImageView.setClip(clip);
    }

    /**
     * Handle save change profile make new notification event.
     */
    @FXML
    public void handleSave() {
        saveUser(currentUser);
        String notification = "You have changed your profile.";
        MainStaticObjectControl.addNotificationToFile(notification);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
    }

    /**
     * Handle show notification event.
     */
    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * save user.
     */
    public void saveUser(User user) {
        String name = nameField.getText();
        String birthdate = birthdateField.getText();
        String phone_number = phoneField.getText();
        String email = emailField.getText();
        String location = locationField.getText();

        if (name.trim().isEmpty() || birthdate.trim().isEmpty() || phone_number.trim().isEmpty() || email.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng điền đầy đủ thông tin!");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET name = ?, birthdate = ?, phone_number = ?, email = ?, location = ? WHERE name = ? AND email = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, birthdate);
            pstmt.setString(3, phone_number);
            pstmt.setString(4, email);
            pstmt.setString(5, location);
            pstmt.setString(6, currentUser.getName());
            pstmt.setString(7, currentUser.getEmail());
            pstmt.executeUpdate();

            currentUser.setName(nameField.getText());
            currentUser.setBirthDate(birthdateField.getText());
            currentUser.setPhone_number(phoneField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setLocation(locationField.getText());
            currentUser.setPassword(passwordField.getText());

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cập nhật thông tin thành công!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi cập nhật thông tin!");
            alert.showAndWait();
        }
    }

    /**
     * Initialize profile scene.
     */
    @FXML
    public void initialize() {
        currentUser = getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            birthdateField.setText(currentUser.getBirthDate());
            phoneField.setText(currentUser.getPhone_number());
            emailField.setText(currentUser.getEmail());
            locationField.setText(currentUser.getLocation());
            passwordField.setText(currentUser.getPassword());
        }
        Image placeholderImage = new Image(getClass().getResource("/com/example/librabry_management/Images/avatar.png").toExternalForm());
        avatarImageView.setImage(placeholderImage);

        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);

        avatarImageView.setOnMouseClicked(event -> handleChangeAvatar());
        avatarImageView.setOnMouseEntered(e -> avatarImageView.setStyle("-fx-opacity: 0.8;"));
        avatarImageView.setOnMouseExited(e -> avatarImageView.setStyle("-fx-opacity: 1;"));

        avatarImageView.setOnMouseEntered(e -> avatarImageView.setStyle("-fx-effect: dropshadow(gaussian, blue, 10, 0.5, 0, 0);"));
        avatarImageView.setOnMouseExited(e -> avatarImageView.setStyle("-fx-effect: null;"));

        setAvatarRoundedCorners();
    }

}
