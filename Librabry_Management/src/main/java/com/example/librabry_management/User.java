package com.example.librabry_management;

import java.util.ArrayList;
import java.util.List;
import com.example.Controller.*;


public class User extends Account{

    public final Library library;
    protected List<Book> Collection = new ArrayList<>();

    public User(Library library) {
        super();
        this.library = library;
    }

    public User(String name, String birthdate, String phone_number, String email, String location, String password, Library library) {
        super(name, birthdate, phone_number, email, location, password);
        this.library = library;
    }

    public void viewProfile() {
        if (isLoggedIn) {
            System.out.println("\n === User Profile ===");
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

    public void viewAllBooks() {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("=== Book List ===");
            for (int i = 0; i < library.numBooks(); i++) {
                System.out.println((i + 1) + ". " + library.getBooksList().get(i).getInfo());
            }
        }
    }

    public void searchByGenre(String genre) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== Search by Genre: " + genre + " ===");
            List<Book> results = new ArrayList<>();
            for (Book book : library.getBooksList()) {
                if (book.getGenre().equals(genre)) {
                    results.add(book);
                }
            }
            printSearchResults(results);
        }
    }

    public void searchByAuthor(String author) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== Search by Author: " + author + " ===");
            List<Book> results = new ArrayList<>();
            for (Book book : library.getBooksList()) {
                if (book.getAuthor().equals(author)) {
                    results.add(book);
                }
            }
            printSearchResults(results);
        }
    }

    public void searchByDate(int date) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== Search by Date: " + date + " ===");
            List<Book> results = new ArrayList<>();
            for (Book book : library.getBooksList()) {
                if (book.getDate() == date) {
                    results.add(book);
                }
            }
            printSearchResults(results);
        }
    }

    public void searchByTitle(String title) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== Search by Title: " + title + " ===");
            List<Book> results = new ArrayList<>();
            for (Book book : library.getBooksList()) {
                if (book.getTitle().equals(title)) {
                    results.add(book);
                }
            }
            printSearchResults(results);
        }
    }

    public void searchById(String id) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== Search by ID: " + id + " ===");
            List<Book> results = new ArrayList<>();
            for (Book book : library.getBooksList()) {
                if (book.getId().equalsIgnoreCase(id)) {
                    results.add(book);
                }
            }
            printSearchResults(results);
        }
    }

    private void printSearchResults(List<Book> results) {
        if (results.isEmpty()) {
            System.out.println("No book found.");
        } else {
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i).getInfo());
            }
        }
    }

    public void addToCollection(Book book) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            if (!library.getBooksList().contains(book)) {
                System.out.println("Book does not exist.");
            } else {
                if (!Collection.contains(book)) {
                    Collection.add(book);
                    System.out.println("Collection added successfully.");
                } else {
                    System.out.println("Collection already in your collection.");
                }
            }
        }
    }

    public void removeFromCollection(Book book) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            if (Collection.contains(book)) {
                Collection.remove(book);
                System.out.println("Collection removed successfully.");
            } else {
                System.out.println("Collection not in your collection.");
            }
        }
    }

    public int numsOfCollection() {
        return Collection.size();
    }

    public void viewCollection() {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== View Collection ===");
            if (Collection.isEmpty()) {
                System.out.println("Collection is empty.");
            } else {
                for (int i = 0; i < Collection.size(); i++) {
                    System.out.println((i + 1) + ". " + Collection.get(i).getInfo());
                }
            }
        }
    }

    public void viewBookDetails(Book book) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
        } else {
            System.out.println("\n=== View Book Details ===");
            if (!library.getBooksList().contains(book)) {
                System.out.println("Book does not exist.");
            } else {
                book.viewBook();
            }
        }
    }
}
