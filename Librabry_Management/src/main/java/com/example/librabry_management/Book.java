package com.example.librabry_management;

import java.util.Objects;


public class Book {
    private String title;
    private String author;
    private String description;
    private String thumbnailUrl;
    private String id;
    private String Genre;
    private String datePublished;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book bookTest = (Book) o;
        return title.equals(bookTest.title) && author.equals(bookTest.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
    // Constructor
    public Book(String title, String author, String description, String thumbnailUrl) {
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

    public String getId() {
        return id;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setGenre(String newGenre) {
        Genre = newGenre;
    }

    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }

    public void setDate(String newDate) {
        datePublished = newDate;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public void viewBook() {

    }

    public String getInfo() {
        return null;
    }

    public String getDate() {
        return datePublished;
    }

    public String getGenre() {
        return Genre;
    }
}
