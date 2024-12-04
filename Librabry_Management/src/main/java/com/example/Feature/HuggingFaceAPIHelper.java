package com.example.Feature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class HuggingFaceAPIHelper {
    private static final String API_URL = "https://api-inference.huggingface.co/models/google/flan-t5-large";
    private static final String API_KEY = "API_KEY";

    /**
     * @param term
     * @return
     * Create connect with hugging API.
     */
    public static String explainTerm(String term) {
        try {
            Map<String, String> requestData = Map.of("inputs", "Explain the term: " + term);
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(requestData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "Error: " + response.body();
            }
            JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
            if (jsonArray.size() > 0) {
                JsonElement firstElement = jsonArray.get(0);
                if (firstElement.getAsJsonObject().has("generated_text")) {
                    return firstElement.getAsJsonObject().get("generated_text").getAsString();
                }
            }
            return "No explanation available.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to connect to Hugging Face API.";
        }
    }
}
