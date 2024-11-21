package com.example.librabry_management;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

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
            Parent homeRoot = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
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
            Parent booksRoot = FXMLLoader.load(getClass().getResource("DonateUs.fxml"));
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
        ObservableList<BookTest> books;

        // Tìm trong cơ sở dữ liệu trước
        List<BookTest> dbBooks = DatabaseHelper.searchBooks(query);
        if (!dbBooks.isEmpty()) {
            books = FXCollections.observableArrayList(new LinkedHashSet<>(dbBooks)); // Lọc trùng lặp
        } else {
            // Nếu không tìm thấy, gọi Google Books API
            String jsonResponse = GoogleBooksApi.searchBooks(query);
            if (jsonResponse != null) {
                List<BookTest> apiBooks = JsonParserEx.parseBooks(jsonResponse);

                // Lưu dữ liệu mới vào cơ sở dữ liệu
                ExecutorService executor = Executors.newFixedThreadPool(4);
                for (BookTest book : apiBooks) {
                    executor.execute(() -> DatabaseHelper.saveBook(book, query));
                }
                executor.shutdown();

                // Hợp nhất và lọc dữ liệu từ API và DB
                Set<BookTest> uniqueBooks = new LinkedHashSet<>(apiBooks);
                uniqueBooks.addAll(dbBooks); // Hợp nhất với dữ liệu DB
                books = FXCollections.observableArrayList(uniqueBooks); // Chuyển thành ObservableList
            } else {
                books = FXCollections.observableArrayList();
            }
        }

        tilePane.getChildren().clear();
        for (BookTest book : books) {
            tilePane.getChildren().add(createBookCard(book));
        }
    }

    private VBox createBookCard(BookTest book) {
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
