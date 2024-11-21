package com.example.librabry_management;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LibrarySearchApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải giao diện từ FXML
        Parent root = FXMLLoader.load(getClass().getResource("Book.fxml"));

        // Tạo Scene và hiển thị Stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Google Books Search");
        primaryStage.show();
    }
}

class BookTest {
    private final String title;
    private final String author;
    private final String description;
    private final String thumbnailUrl;

    // Constructor
    public BookTest(String title, String author, String description, String thumbnailUrl) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getter cho tiêu đề
    public String getTitle() {
        return title;
    }

    // Getter cho tác giả
    public String getAuthor() {
        return author;
    }

    // Getter cho mô tả
    public String getDescription() {
        return description != null ? description : "No description available.";
    }

    // Getter cho URL ảnh bìa
    public String getThumbnailUrl() {
        return thumbnailUrl != null ? thumbnailUrl : "https://via.placeholder.com/120x180.png?text=No+Image";
    }
}