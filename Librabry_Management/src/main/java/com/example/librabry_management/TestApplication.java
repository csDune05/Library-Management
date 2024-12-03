package com.example.librabry_management;


import com.example.Feature.*;
import com.example.Controller.*;
import com.example.Feature.VoskManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class TestApplication extends Application {

    private static MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Tạo các bảng trong cơ sở dữ liệu
            DatabaseHelper.createTable();
            DatabaseHelper.createUsersTable();
            DatabaseHelper.createUserBooksTable();

            String path = getClass().getResource("/com/example/librabry_management/DonateUs.fxml").getPath();
            path = path.substring(1, path.length() - 60);
            path += "src/main/resources/vosk-models/vosk-model-small-en-us-0.15";

            VoskManager.getInstance(path);

            // Tải giao diện Welcome
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Wellcome.fxml"));
            Parent root = loader.load();

            // Phát nhạc khi ứng dụng khởi động
            String musicFile = getClass().getResource("/com/example/librabry_management/Musics/Lofichill.mp3").toString(); // Đường dẫn đến file nhạc
            Media media = new Media(musicFile);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Phát lặp lại
            mediaPlayer.play();

            // Cài đặt giao diện chính
            Stage welcomeStage = new Stage();
            Scene scene = new Scene(root);
            welcomeStage.setTitle("Welcome");
            welcomeStage.setScene(scene);

            MainStaticObjectControl.setWelcomeStage(welcomeStage);

            // Thêm style.css
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            welcomeStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && newScene.getStylesheets().isEmpty()) {
                    newScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                }
            });

            welcomeStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void stop() throws Exception {
        // Dừng nhạc khi ứng dụng thoát
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        VoskManager.getInstance(null).close();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
