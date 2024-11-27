package com.example.librabry_management;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GoogleBooksApi {

    private static final String API_KEY = "AIzaSyCYoCFtwwbOF4HI6gnkAHJL1dpXSvnKYp8";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static String searchBooks(String query, int startIndex, int maxResults) {
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "?q=" + query.replace(" ", "+") +
                    "&startIndex=" + startIndex +
                    "&maxResults=" + maxResults +
                    "&fields=items(volumeInfo(title,authors,description,publisher,publishedDate,averageRating,imageLinks,industryIdentifiers))" +
                    "&key=" + API_KEY;

            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    System.err.println("Request failed: " + response.code());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String searchBooksForSuggestions(String query) {
        // Tạo URL yêu cầu để lấy gợi ý từ Google Books API
        String url = BASE_URL + "?q=" + query.replace(" ", "+") +
                "&maxResults=17" + // Lấy tối đa 5 kết quả gợi ý
                "&fields=items(volumeInfo(title,authors))" + // Yêu cầu chỉ các trường cần thiết cho gợi ý
                "&key=" + API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string(); // Trả về JSON response từ Google Books API
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
