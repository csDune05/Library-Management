package com.example.Feature;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class CommentStorage {
    private static final String FILE_PATH = "commentsforum.json";

    public static void saveComments(List<String> comments) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(comments, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadComments() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, List.class);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
