package com.example.Controller;

import com.example.librabry_management.*;
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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.librabry_management.*;
import com.example.Feature.*;
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

    @FXML
    private VBox bookCardContainer;

    @FXML
    private VBox likeCardContainer;

    @FXML
    private Scene libraryScene;

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
        Platform.runLater(() -> {
            libraryScene = booksButton.getScene();
        });
        currentUser = MainStaticObjectControl.getCurrentUser(); // Lấy thông tin người dùng hiện tại
        if (currentUser != null) {
            String lastReturnBookInfo = DatabaseHelper.getLastReturnBook(currentUser.getId());
            if (lastReturnBookInfo != null && !lastReturnBookInfo.isEmpty()) {
                String[] details = lastReturnBookInfo.split(" - ");
                if (details.length == 2) {
                    // Tìm sách dựa trên title và author
                    Book lastBook = DatabaseHelper.getBookByTitleAndAuthor(details[0], details[1]);
                    if (lastBook != null) {
                        MainStaticObjectControl.setLastReturnBook(lastBook);
                    } else {
                        System.out.println("Book not found in database: " + lastReturnBookInfo);
                    }
                }
            }
            loadBookCards();
            if (MainStaticObjectControl.getLastReturnBook() != null) {
                loadLikeBookCards();
            }
        } else {
            System.out.println("No user logged in.");
        }
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
                    MainStaticObjectControl.setLastReturnBook(book);

                    // Lưu thông tin sách trả vào cơ sở dữ liệu
                    String lastReturnBookInfo = book.getTitle() + " - " + book.getAuthor();
                    DatabaseHelper.saveLastReturnBook(userId, lastReturnBookInfo);

                    loadBookCards();
                    loadLikeBookCards();
                    borrowedBooks.remove(book); // Xóa sách khỏi danh sách hiển thị
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

    private void loadBookCards() {
        try {
            Book sampleBook = MainStaticObjectControl.getLastReturnBook();
            if (sampleBook != null) {
                bookCardContainer.getChildren().clear(); // Xóa tất cả nội dung cũ
                VBox bookCard = createBookReturnCard(sampleBook); // Tạo BookCard mới
                if (bookCard != null) {
                    bookCardContainer.getChildren().add(bookCard); // Thêm BookCard mới vào VBox
                }
            } else {
                bookCardContainer.getChildren().clear(); // Xóa nếu không có sách trả về gần đây
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createBookReturnCard(Book book) {
        try {
            // Tạo card ban đầu từ BookCard
            VBox card = BookCard.createBookCard(book, this::viewBookDetails);
            card.setPrefHeight(260);
            card.setSpacing(5);

            // Nút Borrow Again
            Button borrowAgainButton = new Button("Borrow Again");
            borrowAgainButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;");
            borrowAgainButton.setOnMouseEntered(event ->
                    borrowAgainButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            borrowAgainButton.setOnMouseExited(event ->
                    borrowAgainButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            borrowAgainButton.setOnMousePressed(event ->
                    borrowAgainButton.setStyle("-fx-background-color: #3e8e41; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            borrowAgainButton.setOnMouseReleased(event ->
                    borrowAgainButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            borrowAgainButton.setOnAction(event -> handleBorrowAgain(book));

            // Nút Comment
            Button commentButton = new Button("Comment");
            commentButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px;");
            commentButton.setOnMouseEntered(event ->
                    commentButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            commentButton.setOnMouseExited(event ->
                    commentButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            commentButton.setOnMousePressed(event ->
                    commentButton.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            commentButton.setOnMouseReleased(event ->
                    commentButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-border-radius: 5px;")
            );
            commentButton.setOnAction(event -> handleComment(book));

            // Tạo HBox chứa các nút
            HBox buttonContainer = new HBox(10, borrowAgainButton, commentButton);
            buttonContainer.setAlignment(Pos.CENTER); // Căn giữa các nút

            // Thêm các nút vào card
            card.getChildren().add(buttonContainer);

            return card;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void handleBorrowAgain(Book book) {
        User currentUser = MainStaticObjectControl.getCurrentUser(); // Lấy đối tượng User hiện tại
        if (currentUser != null) {
            Book last = MainStaticObjectControl.getLastReturnBook();
            int bookId = DatabaseHelper.getBookId(last.getTitle(), last.getAuthor());
            if (bookId > 0) {
                if (!DatabaseHelper.isBookAlreadyBorrowed(currentUser.getId(), bookId)) {
                    DatabaseHelper.borrowBook(currentUser.getId(), bookId);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Borrow Book");
                    alert.setContentText("Book borrowed successfully!");
                    alert.showAndWait();

                    // Thêm thông báo mượn sách.
                    String bookTitle = last.getTitle();
                    String notification = "You borrowed the " + bookTitle + " book.";

                    MainStaticObjectControl.addNotificationToFile(notification);
                    MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
                    MainStaticObjectControl.updateNotificationIcon(notificationImageView);

                    loadUserBooks();
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

    private void handleComment(Book book) {
        System.out.println("Comment clicked for book: " + book.getTitle());
    }

    private void loadLikeBookCards() {
        Book last = MainStaticObjectControl.getLastReturnBook();
        List<Book> sampleBooks = DatabaseHelper.getRelatedBooks(last.getTitle(), last.getAuthor());
        // Xóa nội dung cũ nếu có
        likeCardContainer.getChildren().clear();

        // Thêm từng BookCard vào VBox
        for (Book book : sampleBooks) {
            VBox bookCard = createLikeBookCard(book);
            likeCardContainer.getChildren().add(bookCard);
        }
    }

    private VBox createLikeBookCard(Book book) {
        VBox card = BookCard.createBookCard(book, this::viewBookDetails);
        card.setStyle(
                "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-background-color: #f2fbff; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
        );

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
        return card;
    }

    private void viewBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetail(book);
            detailController.setLibraryController(this);

            Stage stage = (Stage) booksButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return libraryScene;
    }
}