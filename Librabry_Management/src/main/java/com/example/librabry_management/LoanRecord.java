package com.example.librabry_management;

import com.example.Controller.*;
import com.example.Feature.*;

public class LoanRecord {
    private String id;
    private String member;
    private String title;
    private String author;
    private String overdue;
    private String returnDate;

    public LoanRecord(String id, String member, String title, String author, String overdue, String returnDate) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.author = author;
        this.overdue = overdue;
        this.returnDate = returnDate;
    }

    public String getId() {
        return id;
    }

    public String getMember() {
        return member;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getOverdue() {
        return overdue;
    }

    public String getReturnDate() {
        return returnDate;
    }
}
