package com.example.Controller;

import com.example.librabry_management.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class BookDetailController {

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookYear;

    @FXML
    private Label bookPublisher;

    @FXML
    private TextFlow bookDescription;

    @FXML
    private Button bookButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button donateUsButton;

    @FXML
    private Label ratingStarLabel;

    @FXML
    public void bookButtonHandler() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Book.fxml"));
            Scene homeScene = new Scene(homeRoot);
            Stage stage = (Stage) bookButton.getScene().getWindow();
            stage.setScene(homeScene);
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
            Stage stage = (Stage) bookButton.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void profileButtonHandler() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Profile.fxml"));
            Scene homeScene = new Scene(homeRoot);
            Stage stage = (Stage) bookButton.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void donateUsButtonHandler() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/DonateUs.fxml"));
            Scene homeScene = new Scene(homeRoot);
            Stage stage = (Stage) bookButton.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức nhận dữ liệu từ BookController
    public void setBookDetail(Book book) {
        bookImage.setImage(new Image(book.getThumbnailUrl())); // Ảnh bìa
        bookTitle.setText(book.getTitle()); // Tiêu đề
        bookAuthor.setText(book.getAuthor()); // Tác giả
        bookYear.setText(book.getDate()); // Năm sáng tác
        bookPublisher.setText(book.getPublisher() == null ? "Unknown Publisher" : book.getPublisher()); // Mã sách
        ratingStarLabel.setText(book.getRating() == null ? "Unrated" : book.getRating() + "★");

        String Description = "Description: ";
        Text descriptionTextTitle = new Text(Description + "\n");
        descriptionTextTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
        Text descriptionText = new Text("\t" + book.getDescription());
        descriptionText.setStyle("-fx-font-size: 18;"); // Thiết lập font-size
        bookDescription.getChildren().clear(); // Xóa nội dung cũ nếu có
        bookDescription.getChildren().addAll(descriptionTextTitle, descriptionText);
    }
}
