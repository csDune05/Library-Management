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

    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList(); // Danh sách sách đã mượn

    private User currentUser;

    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    @FXML
    public void bookButtonHandler() {
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

    public void homeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
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
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");
        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
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


    private void loadUserBooks() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

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