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
    private Button borrowBook;

    @FXML
    private Button returnBook;

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
        int userId = MainStaticObjectControl.getCurrentUserId();
        if (userId > 0) {
            int bookId = getCurrentBookId();
            DatabaseHelper.borrowBook(userId, bookId);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Borrow Book");
            alert.setContentText("Book borrowed successfully!");
            alert.showAndWait();
        } else {
            System.out.println("No user logged in.");
        }
    }

    private int getCurrentBookId() {
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM books WHERE title = ? AND author = ? LIMIT 1")) {
            pstmt.setString(1, bookTitle.getText()); // Assuming `bookTitle` is the Label showing the title
            pstmt.setString(2, bookAuthor.getText()); // Assuming `bookAuthor` is the Label showing the author
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
        int userId = MainStaticObjectControl.getCurrentUserId(); // Lấy user_id từ tài khoản đăng nhập hiện tại
        if (userId > 0) {
            int bookId = getCurrentBookId(); // Lấy id của sách hiện tại
            if (bookId > 0) {
                try (Connection conn = DatabaseHelper.connect();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "DELETE FROM user_books WHERE user_id = ? AND book_id = ? LIMIT 1")) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, bookId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Trả sách");
                        alert.setContentText("Sách đã được trả thành công!");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Trả sách");
                        alert.setContentText("Không tìm thấy sách trong thư viện của bạn.");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setContentText("Có lỗi xảy ra khi trả sách. Vui lòng thử lại.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Trả sách");
                alert.setContentText("Không tìm thấy ID sách để trả.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Trả sách");
            alert.setContentText("Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.");
            alert.showAndWait();
        }
    }


    private Book getCurrentBookDetails() {
        return new Book(
                bookTitle.getText(),
                bookAuthor.getText(),
                bookDescription.getChildren().toString(), // Gộp mô tả nếu cần
                bookImage.getImage().getUrl(),           // Lấy URL ảnh
                bookPublisher.getText(),
                bookYear.getText(),
                ratingStarLabel.getText().replace("★", "") // Bỏ ký tự sao
        );
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
