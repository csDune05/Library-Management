package com.example.Feature;

import org.vosk.Model;
import org.vosk.Recognizer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.sound.sampled.*;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class VoskManager {
    private static VoskManager instance;
    private final Model model;

    // Khởi tạo model từ đường dẫn
    private VoskManager(String modelPath) throws Exception {
        this.model = new Model(modelPath);
    }

    public static VoskManager getInstance(String modelPath) throws Exception {
        if (instance == null) {
            instance = new VoskManager(modelPath);
        }
        return instance;
    }

    public Model getModel() {
        return model;
    }

    private String extractText(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            return jsonObject.has("text") ? jsonObject.get("text").getAsString() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Trả về chuỗi rỗng nếu lỗi
        }
    }

    public String transcribeAudio(String audioFilePath) throws Exception {
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(audioFilePath));
            byte[] buffer = new byte[4096];
            int bytesRead;
            StringBuilder result = new StringBuilder();

            // Xử lý dữ liệu âm thanh và chuyển đổi
            while ((bytesRead = ais.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    result.append(extractText(recognizer.getResult())).append(" ");
                }
            }

            // Xử lý kết quả cuối cùng
            result.append(extractText(recognizer.getFinalResult()));
            return result.toString().trim();
        }
    }

    // Ghi âm giọng nói
    public void recordVoice(String outputFilePath, int recordTimeInSeconds) throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        TargetDataLine microphone = AudioSystem.getTargetDataLine(format);

        microphone.open(format);
        microphone.start();

        // Ghi âm vào file
        Thread stopper = new Thread(() -> {
            try {
                AudioSystem.write(new AudioInputStream(microphone), AudioFileFormat.Type.WAVE, new File(outputFilePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stopper.start();
        Thread.sleep(recordTimeInSeconds * 1000); // Thời gian ghi âm
        microphone.stop();
        microphone.close();
    }

    // Đóng model
    public void close() {
        model.close();
    }
}
