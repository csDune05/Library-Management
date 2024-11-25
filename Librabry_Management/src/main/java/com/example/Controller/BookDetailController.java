package com.example.Controller;

import com.example.librabry_management.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BookDetailController implements Initializable {

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
    private Button myLibraryButton;

    @FXML
    private Button backButton;

    @FXML
    private Button borrowBook;

    @FXML
    private Button returnBook;

    private BookController bookController;

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    @FXML
    public void backButtonHandler() {
        if (bookController != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();

            stage.setScene(bookController.getBookScene());
            stage.show();
        }
    }

    @FXML
    public void myLibraryButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/MyLibrary.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) profileButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    public void borrowBookHandler() {
        User currentUser = MainStaticObjectControl.getCurrentUser(); // Lấy đối tượng User hiện tại
        if (currentUser != null) {
            int bookId = getCurrentBookId(); // Lấy book_id của sách hiện tại
            if (bookId > 0) {
                if (!DatabaseHelper.isBookAlreadyBorrowed(currentUser.getId(), bookId)) {
                    DatabaseHelper.borrowBook(currentUser.getId(), bookId);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Borrow Book");
                    alert.setContentText("Book borrowed successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Borrow Book");
                    alert.setContentText("You have already borrowed this book.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Borrow Book");
                alert.setContentText("Book not found to borrow.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Borrow Book");
            alert.setContentText("User information not found. Please log in again.");
            alert.showAndWait();
        }
    }


    private int getCurrentBookId() {
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM books WHERE title = ? AND author = ? LIMIT 1")) {
            pstmt.setString(1, bookTitle.getText()); // Assuming bookTitle is the Label showing the title
            pstmt.setString(2, bookAuthor.getText()); // Assuming bookAuthor is the Label showing the author
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the book is not found
    }

    @FXML
    public void returnBookHandler() {
        User currentUser = MainStaticObjectControl.getCurrentUser(); // Lấy đối tượng User hiện tại
        if (currentUser != null) {
            int bookId = getCurrentBookId(); // Lấy book_id của sách hiện tại
            if (bookId > 0) {
                boolean success = DatabaseHelper.returnBook(currentUser.getId(), bookId);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Return Book");
                    alert.setContentText("Book returned successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Return Book");
                    alert.setContentText("This book is not found in your library.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Return Book");
                alert.setContentText("Book ID not found to return.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Return Book");
            alert.setContentText("User information not found. Please log in again.");
            alert.showAndWait();
        }
    }

    // Phương thức nhận dữ liệu từ BookController
    public void setBookDetail(Book book) {
        bookImage.setImage(new Image(book.getThumbnailUrl())); // Ảnh bìa
        bookTitle.setText(book.getTitle()); // Tiêu đề
        bookAuthor.setText(book.getAuthor()); // Tác giả
        bookYear.setText(book.getDate() == null ? "Unknown Date" : book.getDate()); // Năm sáng tác
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.createUserBooksTable();
    }
}