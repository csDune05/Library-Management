package com.example.Controller;

import com.example.librabry_management.*;
import com.example.Feature.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.example.librabry_management.DatabaseHelper.incrementBookView;

public class BookDetailController implements Initializable {
    @FXML
    private VBox relatedBooksVBox;

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
    private Button booksButton;

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
    private Button logoutButton;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private ImageView notificationImageView;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private ScrollPane notificationScrollPane;

    @FXML
    private VBox notificationList;

    @FXML
    private Button notificationButton;

    @FXML
    private Button backButton;

    @FXML
    private Button borrowBook;

    @FXML
    private Button returnBook;

    @FXML
    private Button qrCodeButton;

    @FXML
    private Button clearNotificationsButton;

    private BookController bookController;

    private DashboardController dashboardController;

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    private Stage getCurrentStage() {
        return (Stage) booksButton.getScene().getWindow();
    }

    @FXML
    public void backButtonHandler() {
        if (bookController != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(bookController.getBookScene());
            stage.show();
        } else {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(dashboardController.getScene());
            stage.show();
        }
    }

    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    @FXML
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    @FXML
    public void ProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
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

                    // Thêm thông báo mượn sách.
                    String bookTitle = this.bookTitle.getText();
                    String notification = "You borrowed the " + bookTitle + " book.";

                    MainStaticObjectControl.addNotificationToFile(notification);
                    MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
                    MainStaticObjectControl.updateNotificationIcon(notificationImageView);
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

    @FXML
    public void qrCodeButtonHandler() throws URISyntaxException {
        Book currentBook = getCurrentBook();
        if (currentBook == null) return;
        String detail = "Title: " + currentBook.getTitle() + "\n" +
                        "Author: " +  currentBook.getAuthor() + "\n" +
                        "Publisher Date: " + currentBook.getDate() + "\n" +
                        "Publisher: " + currentBook.getPublisher()  + "\n" +
                        "Rating: " + currentBook.getRating();
        String path = getClass().
                getResource("/com/example/librabry_management/QRCode/QRCode.png").getPath();
        path = path.substring(1);
        QRCodeGenerator.generateQRCode(detail, path);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/QRCode.fxml"));
            Scene homeScene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    private Book getCurrentBook() {
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM books WHERE title = ? AND author = ? LIMIT 1")) {
            pstmt.setString(1, bookTitle.getText()); // Assuming bookTitle is the Label showing the title
            pstmt.setString(2, bookAuthor.getText()); // Assuming bookAuthor is the Label showing the author
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

                    // Thêm thông báo trả sách.
                    String bookTitle = this.bookTitle.getText();
                    String notification = "You returned the " + bookTitle + " book.";

                    MainStaticObjectControl.addNotificationToFile(notification);
                    MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
                    MainStaticObjectControl.updateNotificationIcon(notificationImageView);
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

    public void setBookDetail(Book book) {
        CompletableFuture.runAsync(() -> {
            Image image = new Image(book.getThumbnailUrl(), true);
            Platform.runLater(() -> bookImage.setImage(image));
        });
        bookTitle.setText(book.getTitle()); // Tiêu đề
        bookAuthor.setText(book.getAuthor()); // Tác giả
        bookYear.setText(book.getDate().equals("Unknown Date") ? "Unknown Date" : book.getDate()); // Năm sáng tác
        bookPublisher.setText(book.getPublisher().equals("UnKnown Publisher") ? "Unknown Publisher" : book.getPublisher()); // Nhà xuất bản
        ratingStarLabel.setText(book.getRating().equals("Unrated") ? "Unrated" : book.getRating() + "★");

        String Description = "Description: ";
        Text descriptionTextTitle = new Text(Description + "\n");
        descriptionTextTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
        Text descriptionText = new Text("\t" + book.getDescription());
        descriptionText.setStyle("-fx-font-size: 18;"); // Thiết lập font-size
        bookDescription.getChildren().clear(); // Xóa nội dung cũ nếu có
        bookDescription.getChildren().addAll(descriptionTextTitle, descriptionText);

        incrementBookView(book);
        // Gọi loadRelatedBooks để tải sách liên quan
        loadRelatedBooks();
    }

    private void loadRelatedBooks() {
        CompletableFuture.runAsync(() -> {
            // Lấy danh sách sách liên quan từ cơ sở dữ liệu
            List<Book> relatedBooks = DatabaseHelper.getRelatedBooks(bookTitle.getText(), bookAuthor.getText());

            Platform.runLater(() -> {
                relatedBooksVBox.getChildren().clear(); // Xóa nội dung cũ
                for (Book book : relatedBooks) {
                    VBox bookCard = createBookCard(book); // Tạo card sách
                    relatedBooksVBox.getChildren().add(bookCard); // Thêm card vào VBox
                }
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private VBox createBookCard(Book book) {
        return BookCard.createBookCard(book, this::openBookDetail);
    }


    private void incrementBookView(Book book) {
        int bookId = DatabaseHelper.getBookId(book.getTitle(), book.getAuthor());
        if (bookId > 0) {
            DatabaseHelper.incrementBookView(bookId);
        }
    }

    private void openBookDetail(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetail(book);
            detailController.setDashboardController(dashboardController); // Nếu cần chuyển lại Dashboard

            Stage stage = (Stage) relatedBooksVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.createUserBooksTable();



        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);

        // notification
        MainStaticObjectControl.configureNotificationButton(notificationImageView, notificationButton);
    }
}