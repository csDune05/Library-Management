package com.example.librabry_management;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> booksList;

    public Library() {
        this.booksList = new ArrayList<>();
    }

    public int numBooks() {
        return booksList.size();
    }

    public List<Book> getBooksList() {
        return booksList;
    }
}
