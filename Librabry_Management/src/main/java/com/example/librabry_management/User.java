package com.example.librabry_management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class User extends Account{

    private List<Book> books = new ArrayList<>();

    public User() {
        super();
    }

    public User(String name, String birthdate, String phone_number, String email, String location, String password) {
        super(name, birthdate, phone_number, email, location, password);
    }

    public void registerAccount(String name, String birthdate, String phone_number, String email, String location, String password) {
        if (!isLoggedIn) {
            if (isEmailRegistered(email)) {
                System.out.println("This email already has an account.");
            } else {
                Account newAccount = new Account(name, birthdate, phone_number, email, location, password);
                accountList.add(newAccount);
                System.out.println("Account registered successfully.");
            }
        }
    }

    private boolean isEmailRegistered(String email) {
        if (accountList.contains(email)) {
            return true;
        }
        return false;
    }

    public void login(String email, String password) {
        if (!isLoggedIn) {
            for (Account account : accountList) {
                if (account.getEmail().equals(email) && account.getPassword().equals(password)) {
                    System.out.println("Login successful.");
                    isLoggedIn = true;
                }
            }
            System.out.println("Login failed: Incorrect username or password.");
        }
    }

    public void logout() {
        isLoggedIn = false;
        System.out.println("Logout successful.");
    }

    public void viewProfile() {
        if (isLoggedIn) {
            System.out.println("ID: " + this.id);
            System.out.println("Name: " + this.name);
            System.out.println("Birthdate: " + this.birthdate);
            System.out.println("Phone: " + this.phone_number);
            System.out.println("Email: " + this.email);
            System.out.println("Location: " + this.location);
            System.out.println("Password: " + this.password);
        } else {
            System.out.println("Not logged in.");
        }
    }

    public void editProfile(String newName, String newBirthdate, String newPhone_number, String newLocation, String newPassword) {
        if (isLoggedIn) {
            if (newName != null) {
                this.name = newName;
            }
            if (newBirthdate != null) {
                this.birthdate = newBirthdate;
            }
            if (newPhone_number != null) {
                this.phone_number = newPhone_number;
            }
            if (newLocation != null) {
                this.location = newLocation;
            }
            if (newPassword != null) {
                this.password = newPassword;
            }
            System.out.println("Edit profile successful.");
        } else {
            System.out.println("Not logged in.");
        }
    }


    public void addBook(Book doc) {
        books.add(doc);
    }

    public int NumberOfBook() {
        return books.size();
    }

    public void removeBook(String id) {
        Book docToRemove = null;
        for (Book doc : books) {
            if (doc.getId().equals(id)) {
                docToRemove = doc;
                break;
            }
        }
        if (docToRemove != null) {
            books.remove(docToRemove);
            CreateId.existingDocIds.remove(id);
            System.out.println("Document with ID " + id + " removed successfully.");
        } else {
            System.out.println("Document with ID " + id + " not found.");
        }
    }

    public void viewAllBooks() {
        System.out.println("=== Document List ===");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i).getInfo());
        }
    }

    public void searchByGenre(String genre) {
        System.out.println("\n=== Search by Genre: " + genre + " ===");
        List<Book> results = new ArrayList<>();
        for (Book doc : books) {
            if (doc.getGenre().equals(genre)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchByAuthor(String author) {
        System.out.println("\n=== Search by Author: " + author + " ===");
        List<Book> results = new ArrayList<>();
        for (Book doc : books) {
            if (doc.getAuthor().equals(author)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchByDate(int date) {
        System.out.println("\n=== Search by Date: " + date + " ===");
        List<Book> results = new ArrayList<>();
        for (Book doc : books) {
            if (doc.getDate() == date) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchByTitle(String title) {
        System.out.println("\n=== Search by Title: " + title + " ===");
        List<Book> results = new ArrayList<>();
        for (Book doc : books) {
            if (doc.getTitle().equals(title)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchById(String id) {
        System.out.println("\n=== Search by ID: " + id + " ===");
        List<Book> results = new ArrayList<>();
        for (Book doc : books) {
            if (doc.getId().equalsIgnoreCase(id)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    private void printSearchResults(List<Book> results) {
        if (results.isEmpty()) {
            System.out.println("No documents found.");
        } else {
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i).getInfo());
            }
        }
    }

    public void editBook(String id, String newTitle, String newGenre, String newAuthor, Integer newDate, String newDescription) {
        boolean found = false;
        for (Book doc : books) {
            if (doc.getId().equals(id)) {
                found = true;
                if (newTitle != null) {
                    doc.setTitle(newTitle);
                }
                if (newGenre != null) {
                    doc.setGenre(newGenre);
                }
                if (newAuthor != null) {
                    doc.setAuthor(newAuthor);
                }
                if (newDate != null) {
                    doc.setDate(newDate);
                }
                if (newDescription != null) {
                    doc.setDescription(newDescription);
                }
                break;
            }
        }
        if (!found) {
            System.out.println("Document with ID " + id + " not found.");
        }
    }
}
