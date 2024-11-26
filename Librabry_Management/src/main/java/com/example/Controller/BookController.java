package com.example.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import com.example.librabry_management.*;


public class BookController {
    @FXML
    private TextField bookTitle;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button donateUsButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button myLibraryButton;

    @FXML
    private TilePane tilePane;

    @FXML
    private ComboBox<String> optionsComboBox;

    private Scene bookScene;

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
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        setupSearchSuggestions();
        Platform.runLater(() -> {
            bookScene = homeButton.getScene();
        });

        DatabaseHelper.createTable();
        // Lấy 10 sách trong database
        List<Book> books = DatabaseHelper.getDefaultBooks();
        // Hiển thị sách trong giao diện
        for (Book book : books) {
            tilePane.getChildren().add(createBookCard(book));
        }
        searchButton.setOnAction(e -> performSearch());

        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
    }

    @FXML
    public void performSearch() {
        String query = searchField.getText();

        // Tạo Task để xử lý công việc nặng
        Task<ObservableList<VBox>> searchTask = new Task<>() {
            @Override
            protected ObservableList<VBox> call() {
                ObservableList<VBox> bookCards = FXCollections.observableArrayList();

                try {
                    // 1. Tìm kiếm trong cơ sở dữ liệu
                    List<Book> dbBooks = DatabaseHelper.searchBooks(query);

                    if (!dbBooks.isEmpty()) {
                        // Nếu tìm thấy trong DB
                        for (Book book : dbBooks) {
                            bookCards.add(createBookCard(book));
                        }
                    } else {
                        // Nếu không có trong DB, gọi Google Books API
                        String jsonResponse = GoogleBooksApi.searchBooks(query);
                        if (jsonResponse != null) {
                            List<Book> apiBooks = JsonParserEx.parseBooks(jsonResponse);

                            // Lưu sách từ API vào DB
                            ExecutorService saveExecutor = Executors.newFixedThreadPool(4);
                            for (Book book : apiBooks) {
                                saveExecutor.submit(() -> DatabaseHelper.saveBook(book, query));
                                bookCards.add(createBookCard(book));
                            }
                            saveExecutor.shutdown();
                            saveExecutor.awaitTermination(5, TimeUnit.SECONDS);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return bookCards;
            }
        };

        // Cập nhật giao diện khi hoàn tất
        searchTask.setOnSucceeded(event -> {
            // Đảm bao giao diện cap nhat tren JavaFx Application Thread
            Platform.runLater(() -> {
                tilePane.getChildren().clear();
                tilePane.getChildren().addAll(searchTask.getValue());
            });
        });

        // Hiển thị lỗi nếu xảy ra
        searchTask.setOnFailed(event -> {
            Throwable exception = searchTask.getException();
            if (exception != null) exception.printStackTrace();
        });

        // Chạy Task trên ExecutorService
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(searchTask);
        executor.shutdown();
    }

    private void setupSearchSuggestions() {
        // Popup để hiển thị gợi ý
        Popup suggestionsPopup = new Popup();
        suggestionsPopup.setAutoHide(true);

        // ListView để hiển thị danh sách gợi ý
        ListView<String> suggestionsList = new ListView<>();
        suggestionsList.setPrefWidth(716);
        suggestionsList.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                searchField.setText(selectedSuggestion);
                suggestionsPopup.hide();
            }
        });

        suggestionsPopup.getContent().add(suggestionsList);

        // Xử lý sự kiện khi người dùng nhập vào SearchField
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            List<String> suggestions = getSuggestions(newValue).isEmpty() ? getSuggestionsFromAPI(newValue) : getSuggestions(newValue);

            if (suggestions.isEmpty()) {
                suggestionsPopup.hide();
            } else {
                suggestionsList.getItems().setAll(suggestions);

                // Hiển thị Popup tại vị trí của SearchField
                if (!suggestionsPopup.isShowing()) {
                    suggestionsPopup.show(searchField,
                            searchField.localToScreen(searchField.getBoundsInLocal()).getMinX(),
                            searchField.localToScreen(searchField.getBoundsInLocal()).getMaxY());
                }
            }
        });

        // Ẩn Popup khi SearchField mất focus
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                suggestionsPopup.hide();
            }
        });
    }

    private List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();

        // Lấy từ cơ sở dữ liệu
        String sql = "SELECT title FROM books WHERE title LIKE ? LIMIT 5";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                suggestions.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    private List<String> getSuggestionsFromAPI(String query) {
        List<String> suggestions = new ArrayList<>();
        String jsonResponse = GoogleBooksApi.searchBooks(query);
        if (jsonResponse != null) {
            List<Book> books = JsonParserEx.parseBooks(jsonResponse);
            for (Book book : books) {
                suggestions.add(book.getTitle());
            }
        }
        return suggestions;
    }

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

        Button knowMoreButton = new Button("Know more");
        knowMoreButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        knowMoreButton.setPrefWidth(120);
        knowMoreButton.setPrefHeight(30);
        knowMoreButton.setOnAction(e -> {
            try {
                // Load FXML và controller mới
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
                Parent root = loader.load();

                // Lấy controller của BookDetail
                BookDetailController detailController = loader.getController();

                // Truyền dữ liệu sách vào BookDetailController
                detailController.setBookDetail(book);
                detailController.setBookController(this);

                // Hiển thị giao diện mới
                Stage stage = (Stage) knowMoreButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox card = new VBox(5, thumbnail, title, knowMoreButton);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");
        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    public Scene getBookScene() {
        return bookScene;
    }
}
