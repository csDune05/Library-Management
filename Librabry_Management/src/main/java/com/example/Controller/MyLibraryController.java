package com.example.Controller;

import com.example.librabry_management.Book;
import com.example.librabry_management.DatabaseHelper;
import com.example.librabry_management.MainStaticObjectControl;
import com.example.librabry_management.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MyLibraryController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TilePane tilePane;

    @FXML
    private Button homeButton;

    @FXML
    private Button donateUsButton;

    @FXML
    private Button booksButton;

    @FXML
    private Button profileButton;

    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList(); // Danh sách sách đã mượn

    private User currentUser;

    @FXML
    public void bookButtonHandler() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Book.fxml"));
            Scene homeScene = new Scene(homeRoot);
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ProfileButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Profile.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) profileButton.getScene().getWindow();

            stage.setScene(booksScene);
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

            Stage stage = (Stage) booksButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void updateLibraryView() {
        tilePane.getChildren().clear(); // Xóa nội dung cũ
        for (Book book : borrowedBooks) {
            tilePane.getChildren().add(createBookCard(book)); // Tạo thẻ sách và thêm vào TilePane
        }
    }

    // Phương thức tạo thẻ sách
    private VBox createBookCard(Book book) {
        ImageView thumbnail = new ImageView();
        thumbnail.setImage(new Image(book.getThumbnailUrl(), 120, 180, true, true));
        thumbnail.setFitWidth(110);
        thumbnail.setFitHeight(160);

        Label title = new Label(book.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        title.setMaxWidth(120);
        title.setPrefHeight(40);

        Button returnButton = new Button("Return Book");
        returnButton.setStyle("-fx-background-color: #ff6347; -fx-text-fill: white;"); // Style cho nút
        returnButton.setPrefWidth(120);
        returnButton.setOnAction(event -> handleReturnBook(book)); // Gắn sự kiện cho nút

        VBox card = new VBox(5, thumbnail, title, returnButton);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");
        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);
        return card;
    }



    @FXML
    public void initialize() {
        currentUser = MainStaticObjectControl.getCurrentUser(); // Lấy thông tin người dùng hiện tại
        if (currentUser != null) {
            loadUserBooks();
        } else {
            System.out.println("No user logged in.");
        }
    }

    // Phương thức để tải sách của người dùng
    private void loadUserBooks() {
        if (currentUser != null) {
            List<Book> userBooks = DatabaseHelper.getBooksForUser(currentUser.getId()); // Lấy sách dựa trên user ID
            if (!userBooks.isEmpty()) {
                tilePane.getChildren().clear(); // Xóa nội dung cũ
                for (Book book : userBooks) {
                    tilePane.getChildren().add(createBookCard(book)); // Thêm sách vào TilePane
                }
            } else {
                System.out.println("No books found for user: " + currentUser.getName());
            }
        }
    }

    private void handleReturnBook(Book book) {
        if (currentUser != null) {
            int userId = currentUser.getId();
            int bookId = DatabaseHelper.getBookId(book.getTitle(), book.getAuthor());
            if (bookId != -1) {
                boolean success = DatabaseHelper.returnBook(userId, bookId);
                if (success) {
                    borrowedBooks.remove(book);
                    tilePane.getChildren().removeIf(node -> {
                        if (node instanceof VBox) {
                            VBox card = (VBox) node;
                            Label titleLabel = (Label) card.getChildren().get(1); // Tiêu đề ở vị trí thứ 2
                            return titleLabel.getText().equals(book.getTitle());
                        }
                        return false;
                    });
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Return Book");
                    alert.setContentText("Book returned successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to return the book. Please try again.");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid user information.");
            alert.showAndWait();
        }
    }
}