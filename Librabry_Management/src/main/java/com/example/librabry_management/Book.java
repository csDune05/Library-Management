package com.example.librabry_management;

import java.util.Objects;


public class Book {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String title;
    private String author;
    private String description;
    private String thumbnailUrl;
    private String rating;
    private String datePublished;
    private String publisher;

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
    public Book(String title, String author, String description, String thumbnailUrl, String publisher, String datePublished, String rating) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.rating = rating;
        this.datePublished = datePublished;
        this.publisher = publisher;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setRating(String newRating) {
        rating = newRating;
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

    public String getRating() {
        return rating;
    }
}
