package com.example.librabry_management;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.util.Objects;
import com.example.Controller.*;


public class BookTest {
    private final String title;
    private final String author;
    private final String description;
    private final String thumbnailUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookTest bookTest = (BookTest) o;
        return title.equals(bookTest.title) && author.equals(bookTest.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
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
