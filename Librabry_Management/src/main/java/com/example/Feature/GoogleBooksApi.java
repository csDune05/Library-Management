package com.example.Feature;

import com.example.librabry_management.*;
import com.example.Controller.*;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GoogleBooksApi {
    private static final String API_KEY = "API_Key";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    /**
     * Get 40 book from API.
     * @param query
     * @param startIndex
     * @param maxResults
     * @return
     */
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

    /**
     * @param query
     * @return
     * Give suggestion when searching.
     */
    public static String searchBooksForSuggestions(String query) {
        String url = BASE_URL + "?q=" + query.replace(" ", "+") +
                "&maxResults=17" +
                "&fields=items(volumeInfo(title,authors))" +
                "&key=" + API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
