package com.example.librabry_management;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.Controller.*;

public class GoogleBooksApi {

    private static final String API_KEY = "AIzaSyCYoCFtwwbOF4HI6gnkAHJL1dpXSvnKYp8";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static String searchBooks(String query) {
        OkHttpClient client = new OkHttpClient();

        // Tạo URL yêu cầu
        String url = BASE_URL + "?q=" + query.replace(" ", "+") + "&maxResults=10&key=" + API_KEY;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                System.err.println("Request failed: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

