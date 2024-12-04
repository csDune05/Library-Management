package com.example.Feature;

import org.vosk.Model;
import org.vosk.Recognizer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.librabry_management.*;
import com.example.Controller.*;
import javax.sound.sampled.*;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class VoskManager {
    private static VoskManager instance;
    private final Model model;

    /**
     * @param modelPath
     * @throws Exception
     * Constructor1.
     */
    private VoskManager(String modelPath) throws Exception {
        this.model = new Model(modelPath);
    }

    /**
     * @param modelPath
     * @return
     * @throws Exception
     * Check and create Singleton.
     */
    public static VoskManager getInstance(String modelPath) throws Exception {
        if (instance == null) {
            instance = new VoskManager(modelPath);
        }
        return instance;
    }

    /**
     * @param json
     * @return
     * Convert JSON.
     */
    private String extractText(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            return jsonObject.has("text") ? jsonObject.get("text").getAsString() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param audioFilePath
     * @return
     * @throws Exception
     * Listen voice.
     */
    public String transcribeAudio(String audioFilePath) throws Exception {
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(audioFilePath));
            byte[] buffer = new byte[4096];
            int bytesRead;
            StringBuilder result = new StringBuilder();
            while ((bytesRead = ais.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    result.append(extractText(recognizer.getResult())).append(" ");
                }
            }
            result.append(extractText(recognizer.getFinalResult()));
            return result.toString().trim();
        }
    }

    /**
     * Close program.
     */
    public void close() {
        model.close();
    }
}
