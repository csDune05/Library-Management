package com.example.Controller;

import com.example.librabry_management.*;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
    private Button myLibraryButton;

    @FXML
    private ImageView avatarImageView;

    private void handleChangeAvatar() {
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

    @FXML
    public void initialize() {
        Image placeholderImage = new Image(getClass().getResource("/com/example/librabry_management/Images/placeholder.jpg").toExternalForm());
        avatarImageView.setImage(placeholderImage);
        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);

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

    private void setAvatarRoundedCorners() {
        Rectangle clip = new Rectangle(avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        clip.setArcWidth(20); // Độ bo góc
        clip.setArcHeight(20);
        avatarImageView.setClip(clip);
    }
}
