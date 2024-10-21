package com.example.librabry_management;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Document> documents;

    public Library() {
        this.documents = new ArrayList<>();
    }

    public void addDocument(Document doc) {
        documents.add(doc);
    }

    public int NumberOfDocuments() {
        return documents.size();
    }

    public void removeDocument(String id) {
        Document docToRemove = null;
        for (Document doc : documents) {
            if (doc.getId().equals(id)) {
                docToRemove = doc;
                break;
            }
        }
        if (docToRemove != null) {
            documents.remove(docToRemove);
            CreateId.existingDocIds.remove(id);
            System.out.println("Document with ID " + id + " removed successfully.");
        } else {
            System.out.println("Document with ID " + id + " not found.");
        }
    }

    public void viewAllDocuments() {
        System.out.println("=== Document List ===");
        for (int i = 0; i < documents.size(); i++) {
            System.out.println((i + 1) + ". " + documents.get(i).getInfo());
        }
    }

    public void searchByGenre(String genre) {
        System.out.println("\n=== Search by Genre: " + genre + " ===");
        List<Document> results = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getGenre().equals(genre)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchByAuthor(String author) {
        System.out.println("\n=== Search by Author: " + author + " ===");
        List<Document> results = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getAuthor().equals(author)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchByDate(int date) {
        System.out.println("\n=== Search by Date: " + date + " ===");
        List<Document> results = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getDate() == date) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchByTitle(String title) {
        System.out.println("\n=== Search by Title: " + title + " ===");
        List<Document> results = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getTitle().equals(title)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    public void searchById(String id) {
        System.out.println("\n=== Search by ID: " + id + " ===");
        List<Document> results = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getId().equalsIgnoreCase(id)) {
                results.add(doc);
            }
        }
        printSearchResults(results);
    }

    private void printSearchResults(List<Document> results) {
        if (results.isEmpty()) {
            System.out.println("No documents found.");
        } else {
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i).getInfo());
            }
        }
    }

    public void editDocument(String id, String newTitle, String newGenre, String newAuthor, Integer newDate, String newDescription) {
        boolean found = false;
        for (Document doc : documents) {
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
