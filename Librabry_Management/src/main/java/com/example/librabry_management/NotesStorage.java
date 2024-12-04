package com.example.librabry_management;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotesStorage {

    private static final String FILE_PATH = "notes.json";

    public static void saveNotes(List<Note> notes) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(notes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Note> loadNotes() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Gson gson = new Gson();
            Type noteListType = new TypeToken<List<Note>>(){}.getType();

            // Đọc file vào một String để kiểm tra
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Kiểm tra nếu file rỗng
            if (jsonContent.toString().trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<Note> notes = gson.fromJson(jsonContent.toString(), noteListType);
            if (notes == null) {
                return new ArrayList<>();
            }

            // Lọc các ghi chú theo email của người dùng hiện tại
            String currentUserEmail = MainStaticObjectControl.getCurrentUser().getEmail();
            return notes.stream()
                    .filter(note -> currentUserEmail.equals(note.getUserEmail()))
                    .collect(Collectors.toList());
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
