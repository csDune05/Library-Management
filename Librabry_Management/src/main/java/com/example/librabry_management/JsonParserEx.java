package com.example.librabry_management;

import com.example.librabry_management.BookTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.Controller.*;

public class JsonParserEx {

    public static ObservableList<BookTest> parseBooks(String jsonResponse) {
        ObservableList<BookTest> books = FXCollections.observableArrayList();

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";
                    String author = volumeInfo.has("authors")
                            ? String.join(", ", parseAuthors(volumeInfo.getAsJsonArray("authors")))
                            : "Unknown Author";
                    String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : null;
                    String thumbnailUrl = volumeInfo.has("imageLinks")
                            ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                            : null;

                    // Thêm sách vào danh sách
                    books.add(new BookTest(title, author, description, thumbnailUrl));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    private static String[] parseAuthors(JsonArray authors) {
        String[] authorList = new String[authors.size()];
        for (int i = 0; i < authors.size(); i++) {
            authorList[i] = authors.get(i).getAsString();
        }
        return authorList;
    }
}