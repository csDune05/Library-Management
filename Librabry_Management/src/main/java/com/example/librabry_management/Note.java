package com.example.librabry_management;

public class Note {
    private String title;
    private String content;
    private String userEmail;

    public Note() {}

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters và setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
