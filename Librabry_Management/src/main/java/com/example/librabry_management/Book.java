package com.example.librabry_management;

import com.example.Controller.*;
import com.example.Feature.*;
import java.sql.Timestamp;
import java.util.Objects;


public class Book {
    private String id;
    private String title;
    private String author;
    private String description;
    private String thumbnailUrl;
    private String rating;
    private String datePublished;
    private String publisher;
    private Timestamp borrowAt;
    private Timestamp mustReturnAt;
    private int view;

    /**
     * @param o
     * @return
     * Equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book bookTest = (Book) o;
        return title.equals(bookTest.title) && author.equals(bookTest.author);
    }

    /**
     * @return
     * Hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }

    /**
     * Constructor.
     */
    public Book(String title, String author, String description, String thumbnailUrl, String publisher, String datePublished, String rating, int view) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.rating = rating;
        this.datePublished = datePublished;
        this.publisher = publisher;
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description != null ? description : "No description available.";
    }

    public String getThumbnailUrl() {
        return thumbnailUrl != null ? thumbnailUrl : "https://via.placeholder.com/120x180.png?text=No+Image";
    }

    public String getPublisher() {
        return publisher == null ? "Unknown Publisher" : publisher;
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

    public String getDate() {
        return datePublished == null ? "Unknown Date" : datePublished;
    }

    public String getRating() {
        return rating == null ? "Unrated" : rating;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
