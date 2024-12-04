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

import java.io.IOException;
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

    /**
     * Handle switch to books scene.
     */
    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    /**
     * Handle switch to profile scene.
     */
    @FXML
    public void ProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    /**
     * Handle switch to donate us scene.
     */
    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    /**
     * Handle switch to home scene.
     */
    @FXML
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    /**
     * Handle exit.
     */
    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    /**
     * Handle clear all notification event.
     */
    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * Handle show notifications.
     */
    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if (!notificationPane.isVisible())
            MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * Initialize my library scene.
     */
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

    /**
     * update grid pane.
     */
    public void updateGridPane() {
        gridPane.getChildren().clear();

        gridPane.setHgap(22);

        int row = 0, col = 0;
        int booksPerRow = 5;

        for (Book book : borrowedBooks) {
            VBox bookCard = createBookCard(book);
            gridPane.add(bookCard, col, row);
            col++;
            if (col >= booksPerRow) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Handel search event.
     */
    @FXML
    public void searchHandler() {
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

        borrowedBooks.clear();
        gridPane.getChildren().clear();

        CompletableFuture.supplyAsync(() -> DatabaseHelper.searchBooksForUser(currentUser.getId(), query))
                .thenAccept(searchResults -> {
                    if (searchResults == null || searchResults.isEmpty()) {
                        Platform.runLater(this::showNoResultsMessage);
                    } else {
                        Platform.runLater(() -> {
                            borrowedBooks.setAll(searchResults);
                            updateGridPane();
                        });
                    }
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    public void showNoResultsMessage() {
        Label noResults = new Label("No books found.");
        noResults.setStyle("-fx-font-size: 18px; -fx-text-fill: gray; -fx-font-weight: bold;");
        gridPane.getChildren().clear();
        gridPane.add(noResults, 0, 0);
        GridPane.setColumnSpan(noResults, 5);
    }

    /**
     * Load users_books from database.
     */
    public void loadUserBooks() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }
        gridPane.setHgap(20);

        borrowedBooks.clear();

        CompletableFuture.supplyAsync(() -> {
            // Tải danh sách sách từ cơ sở dữ liệu trên luồng nền
            return DatabaseHelper.getBooksForUser(currentUser.getId());
        }).thenAccept(userBooks -> {
            if (userBooks == null || userBooks.isEmpty()) {
                System.out.println("No books found for user: " + currentUser.getName());
                return;
            }

            borrowedBooks.addAll(userBooks);
            Platform.runLater(this::updateGridPane);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /**
     * Handle return book event.
     */
    public void handleReturnBook(Book book) {
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
                    updateGridPane();

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

    /**
     * Create card for book.
     */
    public VBox createBookCard(Book book) {
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
            );
        });
        returnButton.setOnMouseExited(event -> {
            returnButton.setStyle(
                    "-fx-background-color: #ff6347; -fx-text-fill: white; -fx-border-radius: 5px;"
            );
        });
        returnButton.setPrefWidth(120);
        returnButton.setOnAction(event -> handleReturnBook(book)); // Gắn sự kiện cho nút

        VBox card = new VBox(5, thumbnail, title, returnButton);
        card.setPadding(new Insets(10));

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

        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);

        return card;
    }

    /**
     * Load book card.
     */
    public void loadBookCards() {
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

    /**
     * Create book return card.
     */
    public VBox createBookReturnCard(Book book) {
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

            HBox buttonContainer = new HBox(10, borrowAgainButton);
            buttonContainer.setAlignment(Pos.CENTER);

            card.getChildren().add(buttonContainer);

            return card;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Handle borrow again from book return card.
     */
    public void handleBorrowAgain(Book book) {
        User currentUser = MainStaticObjectControl.getCurrentUser();
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

    public void handleComment(Book book) {
        System.out.println("Comment clicked for book: " + book.getTitle());
    }

    /**
     * Load like book card.
     */
    public void loadLikeBookCards() {
        Book last = MainStaticObjectControl.getLastReturnBook();
        List<Book> sampleBooks = DatabaseHelper.getRelatedBooks(last.getTitle(), last.getAuthor());

        likeCardContainer.getChildren().clear();

        for (Book book : sampleBooks) {
            VBox bookCard = createLikeBookCard(book);
            likeCardContainer.getChildren().add(bookCard);
        }
    }

    /**
     * Create like book card.
     */
    public VBox createLikeBookCard(Book book) {
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

    /**
     * Handle show book detail event.
     */
    public void viewBookDetails(Book book) {
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