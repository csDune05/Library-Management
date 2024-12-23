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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class BookDetailController implements Initializable {
    @FXML
    private Button explainButton;

    @FXML
    private TextField queryTextField;

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
    private Label bookView;

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
    private Button qrCodeButton;

    @FXML
    private Button clearNotificationsButton;

    @FXML
    private AnchorPane CommentPane;

    @FXML
    private ScrollPane CommentScrollPane;

    @FXML
    private Button commentButton;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField commentTextField;

    @FXML
    private VBox commentListBox;

    @FXML
    private ComboBox<String> sortComboBox;

    private BookController bookController;

    private String currentBookName;

    private DashboardController dashboardController;

    private MyLibraryController myLibraryController;

    private Scene previousScene;

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setLibraryController(MyLibraryController libraryController) {
        myLibraryController = libraryController;
    }

    private Stage getCurrentStage() {
        return (Stage) booksButton.getScene().getWindow();
    }

    /**
     * Handle back to previous scene.
     */
    @FXML
    public void backButtonHandler() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        if (previousScene != null) {
            stage.setScene(previousScene);
        } else if (bookController != null) {
            stage.setScene(bookController.getBookScene());
        } else if (dashboardController != null) {
            stage.setScene(dashboardController.getScene());
        } else {
            stage.setScene(myLibraryController.getScene());
        }
        stage.show();
    }

    /**
     * Handle clear all notification.
     */
    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * Handle switch to my library scene.
     */
    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    /**
     * Handle switch to books scene.
     */
    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    /**
     * Handle switch to home scene.
     */
    @FXML
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    /**
     * Handle switch to profile scene.
     */
    @FXML
    public void ProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    /**
     * Handle switch to donate scene.
     */
    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    /**
     * Handle exit event.
     */
    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    /**
     * Handle borrow book event.
     */
    @FXML
    public void borrowBookHandler() {
        User currentUser = MainStaticObjectControl.getCurrentUser();
        if (currentUser != null) {
            int bookId = getCurrentBookId();
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

    /**
     * Handle get qr book.
     */
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

    /**
     * Handle show notification event.
     */
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
                        rs.getString("average_rating"),
                        rs.getInt("view")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handle exit comment event.
     */
    @FXML
    public void exitComment() {
        CommentPane.setVisible(false);
    }

    public int getCurrentBookId() {
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
        return -1;
    }

    public void setBookDetail(Book book) {
        CompletableFuture.runAsync(() -> {
            Image image = new Image(book.getThumbnailUrl(), true);
            Platform.runLater(() -> {
                bookImage.setImage(image);
                bookImage.setPreserveRatio(false);
                bookImage.setFitWidth(220);
                bookImage.setFitHeight(330);
            });
        });
        bookTitle.setText(book.getTitle());
        currentBookName = book.getTitle();
        bookAuthor.setText(book.getAuthor().toUpperCase());
        bookYear.setText(book.getDate().equals("Unknown Date") ? "Unknown Date" : book.getDate());
        bookPublisher.setText(book.getPublisher().equals("UnKnown Publisher") ? "Unknown Publisher" : book.getPublisher());
        ratingStarLabel.setText(book.getRating().equals("Unrated") ? "Unrated" : book.getRating() + " ★");
        bookView.setText(String.valueOf(book.getView()));


        String Description = "Description: ";
        Text descriptionTextTitle = new Text(Description + "\n");
        descriptionTextTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
        Text descriptionText = new Text("\t" + book.getDescription());
        descriptionText.setStyle("-fx-font-size: 14;");
        bookDescription.getChildren().clear();
        bookDescription.getChildren().addAll(descriptionTextTitle, descriptionText);

        incrementBookView(book);
        loadRelatedBooks();
    }

    public void loadRelatedBooks() {
        CompletableFuture.runAsync(() -> {
            // Lấy danh sách sách liên quan từ cơ sở dữ liệu
            List<Book> relatedBooks = DatabaseHelper.getRelatedBooks(bookTitle.getText(), bookAuthor.getText());

            Platform.runLater(() -> {
                relatedBooksVBox.getChildren().clear();
                for (Book book : relatedBooks) {
                    VBox bookCard = createBookCard(book);
                    relatedBooksVBox.getChildren().add(bookCard);
                }
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public VBox createBookCard(Book book) {
        return BookCard.createBookCard(book, this::openBookDetail);
    }


    public void incrementBookView(Book book) {
        int bookId = DatabaseHelper.getBookId(book.getTitle(), book.getAuthor());
        if (bookId > 0) {
            DatabaseHelper.incrementBookView(bookId);
        }
    }

    public void openBookDetail(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetail(book);
            detailController.setDashboardController(dashboardController);// Nếu cần chuyển lại Dashboard
            detailController.setPreviousScene(backButton.getScene());// chuyển lại detail của sách trước đó

            Stage stage = (Stage) relatedBooksVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle show comment scene.
     */
    @FXML
    public void commentButtonHandler() {
        Book currentBook = getCurrentBook();
        if (currentBook != null) {
            CommentPane.setVisible(true);
            updateComment(CommentScrollPane, commentListBox);
        }
    }

    /**
     * Handle push comment.
     */
    @FXML
    public void confirmButtonHandler() {
        String commentText = commentTextField.getText();
        if (!commentText.trim().isEmpty()) {
            String username = MainStaticObjectControl.getCurrentUser().getName();
            addCommentToFile(commentText, username, 0, 0);
            updateComment(CommentScrollPane, commentListBox);
            commentTextField.clear();
        }
    }

    /**
     * Update comment.
     */
    public void updateComment(ScrollPane CommentScrollPane, VBox commentListBox) {
        List<String> comments = readCommentsForBook();

        String sortOption = sortComboBox.getValue();
        if ("Newest".equals(sortOption)) {
            comments.sort((c1, c2) -> {
                String timestamp1 = c1.split("_")[2];
                String timestamp2 = c2.split("_")[2];
                return timestamp2.compareTo(timestamp1);
            });
        } else if ("Oldest".equals(sortOption)) {
            comments.sort((c1, c2) -> {
                String timestamp1 = c1.split("_")[2];
                String timestamp2 = c2.split("_")[2];
                return timestamp1.compareTo(timestamp2);
            });
        } else if ("Popular".equals(sortOption)) {
            comments.sort((c1, c2) -> {
                int likes1 = Integer.parseInt(c1.split("_")[3]);
                int likes2 = Integer.parseInt(c2.split("_")[3]);
                return Integer.compare(likes2, likes1);
            });
        }

        commentListBox.getChildren().clear();

        VBox commentList = new VBox();
        commentList.setSpacing(10);

        for (String comment : comments) {
            String[] parts = comment.split("_", 5);

            VBox commentItem = new VBox();
            commentItem.setStyle("-fx-padding: 10; -fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-width: 1;");

            // Username và thời gian
            HBox headerBox = new HBox();
            headerBox.setSpacing(10);
            headerBox.setAlignment(Pos.BASELINE_LEFT);

            Label usernameLabel = new Label(parts[0]);
            usernameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: navy;");

            Label timestampLabel = new Label("_" + parts[2] + "_");
            timestampLabel.setStyle("-fx-font-size: 12; -fx-font-style: italic; -fx-text-fill: gray;");

            headerBox.getChildren().addAll(usernameLabel, timestampLabel);

            // nội dung
            Text commentText = new Text(parts[1]);
            commentText.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
            commentText.setWrappingWidth(CommentScrollPane.getWidth());

            // nút like
            Button likeButton = new Button(parts[3]);
            ImageView likeImage = new ImageView(new Image(BookDetailController.class.getResource("/com/example/librabry_management/Images/like.png").toExternalForm()));
            likeImage.setFitHeight(20);
            likeImage.setFitWidth(20);
            likeButton.setGraphic(likeImage);
            likeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black;");
            likeButton.setAlignment(Pos.CENTER_LEFT);

            // nút unlike
            Button unlikeButton = new Button(parts[4]);
            ImageView dislikeImage = new ImageView(new Image(BookDetailController.class.getResource("/com/example/librabry_management/Images/dislike.png").toExternalForm()));
            dislikeImage.setFitHeight(20);
            dislikeImage.setFitWidth(20);
            unlikeButton.setGraphic(dislikeImage);
            unlikeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black;");
            unlikeButton.setAlignment(Pos.CENTER_LEFT);

            // xử lí sự kiện like
            likeButton.setOnAction(event -> {
                Boolean isLiked = (Boolean) likeButton.getProperties().getOrDefault("isLiked", false);
                Boolean isDisliked = (Boolean) unlikeButton.getProperties().getOrDefault("isDisliked", false);

                if (isLiked) {
                    likeButton.setStyle("-fx-background-color: transparent;");
                    likeButton.setText(String.valueOf(Integer.parseInt(likeButton.getText()) - 1));
                    likeButton.getProperties().put("isLiked", false);

                    updateLikesInJson(parts, -1, 0);
                } else {
                    likeButton.setStyle("-fx-background-color: lightblue;");
                    likeButton.setText(String.valueOf(Integer.parseInt(likeButton.getText()) + 1));
                    likeButton.getProperties().put("isLiked", true);

                    if (isDisliked) {
                        unlikeButton.setStyle("-fx-background-color: transparent;");
                        unlikeButton.setText(String.valueOf(Integer.parseInt(unlikeButton.getText()) - 1));
                        unlikeButton.getProperties().put("isDisliked", false);

                        updateLikesInJson(parts, 1, -1);
                    } else {
                        updateLikesInJson(parts, 1, 0);
                    }
                }
            });

            // xử lí sự kiện unlike
            unlikeButton.setOnAction(event -> {
                Boolean isLiked = (Boolean) likeButton.getProperties().getOrDefault("isLiked", false);
                Boolean isDisliked = (Boolean) unlikeButton.getProperties().getOrDefault("isDisliked", false);

                if (isDisliked) {
                    unlikeButton.setStyle("-fx-background-color: transparent;");
                    unlikeButton.setText(String.valueOf(Integer.parseInt(unlikeButton.getText()) - 1));
                    unlikeButton.getProperties().put("isDisliked", false);

                    updateLikesInJson(parts, 0, -1);
                } else {
                    unlikeButton.setStyle("-fx-background-color: lightcoral;");
                    unlikeButton.setText(String.valueOf(Integer.parseInt(unlikeButton.getText()) + 1));
                    unlikeButton.getProperties().put("isDisliked", true);

                    if (isLiked) {
                        likeButton.setStyle("-fx-background-color: transparent;");
                        likeButton.setText(String.valueOf(Integer.parseInt(likeButton.getText()) - 1));
                        likeButton.getProperties().put("isLiked", false);

                        updateLikesInJson(parts, -1, 1);
                    } else {
                        updateLikesInJson(parts, 0, 1);
                    }
                }
            });

            HBox emotionBox = new HBox();
            emotionBox.setSpacing(10);
            emotionBox.getChildren().addAll(likeButton, unlikeButton);
            VBox.setMargin(emotionBox, new Insets(10, 0, 0, 0));

            commentItem.getChildren().addAll(headerBox, commentText, emotionBox);
            commentList.getChildren().add(commentItem);
        }

        CommentScrollPane.setContent(commentList);
    }

    public void updateLikesInJson(String[] parts, int likeChange, int dislikeChange) {
        try (BufferedReader reader = new BufferedReader(new FileReader("comments.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject root = new JSONObject(jsonContent.toString());
            JSONArray comments = root.getJSONArray(currentBookName);

            for (int i = 0; i < comments.length(); i++) {
                JSONObject comment = comments.getJSONObject(i);
                if (comment.getString("username").equals(parts[0]) &&
                        comment.getString("timestamp").equals(parts[2])) {
                    // Cập nhật likes và dislikes
                    int newLikes = Math.max(0, comment.getInt("likes") + likeChange);
                    int newDislikes = Math.max(0, comment.getInt("dislikes") + dislikeChange);

                    comment.put("likes", newLikes);
                    comment.put("dislikes", newDislikes);
                    break;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("comments.json"))) {
                writer.write(root.toString(4));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void addCommentToFile(String comment, String username, int countLike, int countDislike) {
        try (BufferedReader reader = new BufferedReader(new FileReader("comments.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Taoj mục comment mới cho book nếu chưa có nội dung
            JSONObject root;
            if (jsonContent.length() > 0) {
                try {
                    root = new JSONObject(jsonContent.toString());
                } catch (JSONException e) {
                    root = new JSONObject();
                }
            } else {
                root = new JSONObject();
            }

            // Tạo mục bình luận cho sách nếu chưa tồn tại
            if (!root.has(currentBookName)) {
                JSONArray newBookComments = new JSONArray();
                root.put(currentBookName, newBookComments);
            }

            // Thêm bình luận
            JSONArray comments = root.getJSONArray(currentBookName);
            JSONObject commentObject = new JSONObject();
            commentObject.put("username", username);
            commentObject.put("timestamp", getCurrentTimestamp());
            commentObject.put("comment", comment);
            commentObject.put("likes", countLike);
            commentObject.put("dislikes", countDislike);
            comments.put(commentObject);
            root.put(currentBookName, comments);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("comments.json"))) {
                writer.write(root.toString(4));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH'h'mm dd/MM/yyyy");
        return now.format(formatter);
    }

    public List<String> readCommentsForBook() {
        List<String> comments = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("comments.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            if (jsonContent.length() > 0) {
                JSONObject root;
                try {
                    root = new JSONObject(jsonContent.toString());
                } catch (JSONException e) {
                    System.out.println("Error parsing JSON: " + e.getMessage());
                    return comments;
                }

                JSONArray bookComments = root.optJSONArray(currentBookName);
                if (bookComments != null) {
                    for (int i = 0; i < bookComments.length(); i++) {
                        JSONObject comment = bookComments.getJSONObject(i);
                        String username = comment.getString("username");
                        String commentText = comment.getString("comment");
                        String timestamp = comment.getString("timestamp");
                        int countLikes = comment.getInt("likes");
                        int countDislikes = comment.getInt("dislikes");
                        comments.add(username + "_" + commentText + "_" + timestamp + "_" + countLikes + "_" + countDislikes);
                    }
                }
            }
        } catch (JSONException | IOException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }

        return comments;
    }

    /**
     * Initialize book details.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.createUserBooksTable();
        Book currentBook = getCurrentBook();

        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);

        // combo box sort
        sortComboBox.getItems().addAll("Newest", "Oldest", "Popular");
        sortComboBox.setValue("Newest");
        // comment
        updateComment(CommentScrollPane, commentListBox);
        sortComboBox.setOnAction(event -> updateComment(CommentScrollPane, commentListBox));

        // notification
        MainStaticObjectControl.configureNotificationButton(notificationImageView, notificationButton);

        // comment
        updateComment(CommentScrollPane, commentListBox);
    }

    /**
     * Handle asking mini chat bot.
     */
    @FXML
    public void explainButtonHandler() {
        // Lấy truy vấn từ TextField
        String query = queryTextField.getText().trim();

        // Kiểm tra nếu truy vấn trống
        if (query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Query");
            alert.setContentText("Please enter a valid query!");
            alert.showAndWait();
            return;
        }

        // Gửi truy vấn đến API và lấy kết quả
        String explanation = HuggingFaceAPIHelper.explainTerm(query);

        // Hiển thị kết quả trong Alert
        Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
        resultAlert.setTitle("Explanation for " + query);
        resultAlert.setContentText(explanation);
        resultAlert.showAndWait();
    }
}