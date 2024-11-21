package com.example.Controller;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
import javafx.stage.Stage;
import com.example.librabry_management.*;


public class BookController {
    @FXML
    private TextField bookTitle;

    @FXML
    private Button homeButton;

    @FXML
    private Button donateUsButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TilePane tilePane;

    @FXML
    private ComboBox<String> optionsComboBox;

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
        DatabaseHelper.createTable();
        // Lấy 10 sách trong database
        List<Book> books = DatabaseHelper.getDefaultBooks();
        // Hiển thị sách trong giao diện
        for (Book book : books) {
            tilePane.getChildren().add(createBookCard(book));
        }
        searchButton.setOnAction(e -> performSearch());
        optionsComboBox.getItems().addAll("My Profile", "Log out");

        // bắt sự kiện nếu Options là log out thì thoát.
        optionsComboBox.setOnAction(event -> {
            String selectedOption = optionsComboBox.getValue();
            if (selectedOption.equals("Log out")) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmation");
                confirmationAlert.setHeaderText("Are you sure you want to log out?");
                confirmationAlert.setContentText("Press OK to log out, or Cancel to stay.");

                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        optionsComboBox.getScene().getWindow().hide();
                        StageManager.openWelcomeStage();
                    } else {
                        optionsComboBox.setValue(null);
                    }
                });
            }
        });
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
                            saveExecutor.awaitTermination(2, TimeUnit.SECONDS);
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
            tilePane.getChildren().clear();
            tilePane.getChildren().addAll(searchTask.getValue());
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Book Details");
            alert.setHeaderText(book.getTitle());
            alert.setContentText("Author: " + book.getAuthor() + "\n\nDescription: " + book.getDescription());
            alert.showAndWait();
        });

        VBox card = new VBox(5, thumbnail, title, knowMoreButton);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");
        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);
        return card;
    }
}
