package com.example.Controller;

import com.example.librabry_management.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private boolean confirmChangeAvatar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change Avatar");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to change your avatar?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void handleChangeAvatar() {
        if (!confirmChangeAvatar()) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (selectedFile != null) {
            // Kiểm tra định dạng file hợp lệ
            if (!selectedFile.getName().matches(".*\\.(png|jpg|jpeg)$")) {
                showAlert("Invalid File", "Please select a valid image file.");
                return;
            }

            // Hiển thị ảnh đã chọn
            Image newAvatar = new Image(selectedFile.toURI().toString());
            avatarImageView.setImage(newAvatar);

            // Lưu đường dẫn ảnh (nếu cần)
            saveAvatarPath(selectedFile.getAbsolutePath());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void saveAvatarPath(String path) {
        // Thêm logic lưu đường dẫn vào cơ sở dữ liệu hoặc tệp cấu hình
        System.out.println("Avatar path saved: " + path);
    }

    @FXML
    public void myLibraryButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/MyLibrary.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) myLibraryButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void homeButtonHandler() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Dashboard.fxml"));
            Scene homeScene = new Scene(homeRoot);

            Stage stage = (Stage) homeButton.getScene().getWindow();

            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void DonateUsButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/DonateUs.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) homeButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void BooksButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Book.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) booksButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User getCurrentUser() {
        return MainStaticObjectControl.getCurrentUser();
    }

    private void setAvatarRoundedCorners() {
        Rectangle clip = new Rectangle(avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        clip.setArcWidth(20); // Độ bo góc
        clip.setArcHeight(20);
        avatarImageView.setClip(clip);
    }

    @FXML
    private void handleSave() {

        saveUser(currentUser);

        System.out.println("Information saved!");
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
    }


    private void saveUser(User user) {

        // Lấy thông tin từ các TextField
        String name = nameField.getText();
        String birthdate = birthdateField.getText();
        String phone_number = phoneField.getText();
        String email = emailField.getText();
        String location = locationField.getText();

        // Kiểm tra thông tin nhập vào
        if (name.trim().isEmpty() || birthdate.trim().isEmpty() || phone_number.trim().isEmpty() || email.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng điền đầy đủ thông tin!");
            alert.showAndWait();
            return;
        }

        // Cập nhật thông tin trong cơ sở dữ liệu
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
        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        // notification
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);

        // Gắn sự kiện khi nhấn vào ImageView
        avatarImageView.setOnMouseClicked(event -> handleChangeAvatar());

        // Hiệu ứng khi rê chuột qua ImageView
        avatarImageView.setOnMouseEntered(e -> avatarImageView.setStyle("-fx-opacity: 0.8;"));
        avatarImageView.setOnMouseExited(e -> avatarImageView.setStyle("-fx-opacity: 1;"));

        // Hiệu ứng viền khi rê chuột qua
        avatarImageView.setOnMouseEntered(e -> avatarImageView.setStyle("-fx-effect: dropshadow(gaussian, blue, 10, 0.5, 0, 0);"));
        avatarImageView.setOnMouseExited(e -> avatarImageView.setStyle("-fx-effect: null;"));

        setAvatarRoundedCorners();
    }

}
