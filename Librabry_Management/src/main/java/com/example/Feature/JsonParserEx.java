package com.example.Feature;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.librabry_management.*;
import com.example.Controller.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParserEx {

    public static ObservableList<Book> parseBooks(String jsonResponse) {
        ObservableList<Book> books = FXCollections.observableArrayList();

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                int count = 0;
                for (JsonElement item : items) {
                    if (count >= 10) break;

                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";
                    String author = volumeInfo.has("authors")
                            ? String.join(", ", parseAuthors(volumeInfo.getAsJsonArray("authors")))
                            : "Unknown Author";
                    String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : null;
                    String thumbnailUrl = volumeInfo.has("imageLinks")
                            ? (volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                            ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                            : volumeInfo.getAsJsonObject("imageLinks").get("smallThumbnail").getAsString())
                            : null;
                    String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : null;
                    String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : null;
                    String averageRating = volumeInfo.has("averageRating") ? volumeInfo.get("averageRating").getAsString() : null;
                    Book book = new Book(title, author, description, thumbnailUrl, publisher, publishedDate, averageRating, 0);
                    books.add(book);

                    count++;
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

    public static List<String> parseSuggestions(String jsonResponse) {
        List<String> suggestions = new ArrayList<>();

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";

                    if (title != null && !title.trim().isEmpty()) {
                        suggestions.add(title);  // Thêm tiêu đề sách vào gợi ý
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    public static String getVideoUrl(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null && items.size() > 0) {
                JsonObject firstItem = items.get(0).getAsJsonObject();
                JsonObject idObject = firstItem.getAsJsonObject("id");

                if (idObject != null) {
                    String videoId = idObject.get("videoId").getAsString();
                    return "https://www.youtube.com/watch?v=" + videoId;  // Trả về URL video
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Nếu không tìm thấy hoặc có lỗi, trả về null
    }

}