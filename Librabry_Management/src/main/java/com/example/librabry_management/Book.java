package com.example.librabry_management;

public class Book {
    private String title;
    private String author;
    private int date;
    private String genre;
    private String description;
    private String id;

    public Book(String title, String genre, String author, int date, String description) {
        this.title = title;
        this.id = CreateId.CreateDocId();
        this.genre = genre;
        this.author = author;
        this.date = date;
        this.description = description;
    }

    public Book(String title, String author) {
    }

    public void viewBook() {
        System.out.println("Title: " + title);
        System.out.println("ID: " + id);
        System.out.println("Genre: " + genre);
        System.out.println("Author: " + author);
        System.out.println("Date: " + date);
        System.out.println("Description: " + description);
    }

    public String getInfo() {
        return String.format("%s - %s - %s - %s - %d", title, id, genre, author, date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
