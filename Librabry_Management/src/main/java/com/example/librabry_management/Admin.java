package com.example.librabry_management;

import com.example.Controller.*;

import java.util.concurrent.Flow;

public class Admin extends User {
    public Admin(Library library) {
        super(library);
    }

    public Admin(String name, String birthdate, String phone_number, String email, String location, String password, Library library) {
        super(name, birthdate, phone_number, email, location, password, library);
    }

    public void addBookToLibrary(Book book) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in");
        } else {
            if (!library.getBooksList().contains(book)) {
                library.getBooksList().add(book);
                System.out.println("Book added to library");
            } else {
                System.out.println("Book already exists");
            }
        }
    }

    public void removeBookFromLibrary(Book book) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in");
        } else {
            if (library.getBooksList().contains(book)) {
                library.getBooksList().remove(book);
                System.out.println("Book removed from library");

                for (User user : userList) {
                    if (user.Collection.contains(book)) {
                        user.Collection.remove(book);
                    }
                }
            } else {
                System.out.println("Book does not exist");
            }
        }
    }

    public void editBookInLibrary(String newTitle, String newAuthor, String newDate, String newDescription, String newPublisher) {
        if (!isLoggedIn) {
            System.out.println("You are not logged in");
        } else {
            boolean found = false;
            for (Book book : library.getBooksList()) {
                if (book.getId().equals(id)) {
                    found = true;
                    if (newTitle != null) {
                        book.setTitle(newTitle);
                    }
                    if (newPublisher != null) {
                        book.setPublisher(newPublisher);
                    }
                    if (newAuthor != null) {
                        book.setAuthor(newAuthor);
                    }
                    if (newDate != null) {
                        book.setDate(newDate);
                    }
                    if (newDescription != null) {
                        book.setDescription(newDescription);
                    }
                    System.out.println("Book with ID " + id + " updated successfully.");
                    break;
                }
                if (!found) {
                    System.out.println("Book with ID " + id + " not found in the library.");
                }
            }
        }
    }
}
