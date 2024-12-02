package com.example.Controller;

import com.example.librabry_management.Book;
import com.example.librabry_management.DatabaseHelper;
import com.example.librabry_management.MainStaticObjectControl;
import com.example.librabry_management.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyLibraryController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private GridPane gridPane;

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

    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList(); // Danh sách sách đã mượn

    private User currentUser;

    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
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
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if (!notificationPane.isVisible())
            MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void initialize() {
        currentUser = MainStaticObjectControl.getCurrentUser(); // Lấy thông tin người dùng hiện tại
        if (currentUser != null) {
            loadUserBooks(); // Tải sách của người dùng vào GridPane
        } else {
            System.out.println("No user logged in.");
        }

        try {
            // Cấu hình combobox options
            MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
            MainStaticObjectControl.updateNotificationIcon(notificationImageView);
            MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGridPane() {
        gridPane.getChildren().clear(); // Xóa toàn bộ nội dung cũ

        gridPane.setHgap(22);

        int row = 0, col = 0; // Vị trí bắt đầu trong GridPane
        int booksPerRow = 5; // Số sách mỗi hàng

        for (Book book : borrowedBooks) {
            VBox bookCard = createBookCard(book); // Tạo thẻ sách mới
            gridPane.add(bookCard, col, row); // Thêm vào GridPane
            col++;
            if (col >= booksPerRow) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void searchHandler() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        String query = searchField.getText();
        if (query == null || query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setContentText("Please enter a keyword to search.");
            alert.showAndWait();
            return;
        }

        borrowedBooks.clear(); // Xóa danh sách cũ
        gridPane.getChildren().clear(); // Xóa nội dung cũ trên GridPane

        CompletableFuture.supplyAsync(() -> DatabaseHelper.searchBooksForUser(currentUser.getId(), query))
                .thenAccept(searchResults -> {
                    if (searchResults == null || searchResults.isEmpty()) {
                        Platform.runLater(this::showNoResultsMessage); // Hiển thị thông báo
                    } else {
                        Platform.runLater(() -> {
                            borrowedBooks.setAll(searchResults); // Thêm sách tìm được
                            updateGridPane(); // Cập nhật GridPane
                        });
                    }
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void showNoResultsMessage() {
        Label noResults = new Label("No books found.");
        noResults.setStyle("-fx-font-size: 18px; -fx-text-fill: gray; -fx-font-weight: bold;");
        gridPane.getChildren().clear();
        gridPane.add(noResults, 0, 0);
        GridPane.setColumnSpan(noResults, 5); // Giãn nhãn ra toàn bộ 5 cột
    }


    private void loadUserBooks() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }
        gridPane.setHgap(20);

        borrowedBooks.clear(); // Xóa danh sách cũ

        CompletableFuture.supplyAsync(() -> {
            // Tải danh sách sách từ cơ sở dữ liệu trên luồng nền
            return DatabaseHelper.getBooksForUser(currentUser.getId());
        }).thenAccept(userBooks -> {
            if (userBooks == null || userBooks.isEmpty()) {
                System.out.println("No books found for user: " + currentUser.getName());
                return;
            }

            borrowedBooks.addAll(userBooks); // Thêm sách vào danh sách
            Platform.runLater(this::updateGridPane); // Cập nhật giao diện
        }).exceptionally(ex -> {
            // Xử lý ngoại lệ
            ex.printStackTrace();
            return null;
        });
    }


    private void handleReturnBook(Book book) {
        if (currentUser != null) {
            int userId = currentUser.getId();
            int bookId = DatabaseHelper.getBookId(book.getTitle(), book.getAuthor());
            if (bookId != -1) {
                boolean success = DatabaseHelper.returnBook(userId, bookId);
                if (success) {
                    borrowedBooks.remove(book); // Xóa sách khỏi danh sách
                    updateGridPane(); // Cập nhật lại GridPane

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Return Book");
                    alert.setContentText("Book returned successfully!");
                    alert.showAndWait();

                    String notification = "You returned the " + book.getTitle() + " book.";

                    MainStaticObjectControl.addNotificationToFile(notification);
                    MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
                    MainStaticObjectControl.updateNotificationIcon(notificationImageView);
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

    private VBox createBookCard(Book book) {
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(110);
        thumbnail.setFitHeight(160);

        CompletableFuture.runAsync(() -> {
            Image image = new Image(book.getThumbnailUrl(), true);
            Platform.runLater(() -> thumbnail.setImage(image));
        });

        Label title = new Label(book.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        title.setMaxWidth(120);
        title.setPrefHeight(40);

        Button returnButton = new Button("Return Book");
        returnButton.setStyle(
                "-fx-background-color: #ff6347; -fx-text-fill: white; -fx-border-radius: 5px;"
        );
        returnButton.setOnMouseEntered(event -> {
            returnButton.setStyle(
                    "-fx-background-color: #ff7f7f; -fx-text-fill: white; -fx-border-radius: 5px;"
            ); // Màu khi hover
        });
        returnButton.setOnMouseExited(event -> {
            returnButton.setStyle(
                    "-fx-background-color: #ff6347; -fx-text-fill: white; -fx-border-radius: 5px;"
            ); // Màu mặc định
        });
        returnButton.setPrefWidth(120);
        returnButton.setOnAction(event -> handleReturnBook(book)); // Gắn sự kiện cho nút

        VBox card = new VBox(5, thumbnail, title, returnButton);
        card.setPadding(new Insets(10));

        // Áp dụng viền và hiệu ứng bóng cho card
        card.setStyle(
                "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-background-color: #f2fbff; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
        );

        // Thêm hiệu ứng hover cho card
        card.setOnMouseEntered(event -> {
            card.setStyle(
                    "-fx-border-color: #007bff; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #cce7ff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 0, 4);"
            );
        });

        card.setOnMouseExited(event -> {
            card.setStyle(
                    "-fx-border-color: lightgray; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #f2fbff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
            );
        });

        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);

        return card;
    }
}