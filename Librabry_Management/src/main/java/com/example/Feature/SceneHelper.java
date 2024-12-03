package com.example.Feature;

import javafx.scene.Scene;
import java.net.URL;
import com.example.librabry_management.*;
import com.example.Controller.*;

public class SceneHelper {

    public static void applyTheme(Scene scene) {
        ThemeManager themeManager = ThemeManager.getInstance();

        // Kiểm tra trạng thái hiện tại của Dark Mode và áp dụng stylesheet tương ứng
        URL darkThemeUrl = SceneHelper.class.getResource("/com/example/librabry_management/dark-theme.css");
        URL lightThemeUrl = SceneHelper.class.getResource("/com/example/librabry_management/style.css");

        if (darkThemeUrl == null || lightThemeUrl == null) {
            System.err.println("Không tìm thấy file CSS!");
            return;
        }

        if (themeManager.isDarkModeEnabled()) {
            scene.getStylesheets().add(darkThemeUrl.toExternalForm());
        } else {
            scene.getStylesheets().add(lightThemeUrl.toExternalForm());
        }

        // Lắng nghe thay đổi từ ThemeManager để cập nhật giao diện khi cần
        themeManager.darkModeEnabledProperty().addListener((obs, oldVal, newVal) -> {
            scene.getStylesheets().clear();
            if (newVal) {
                scene.getStylesheets().add(darkThemeUrl.toExternalForm());
            } else {
                scene.getStylesheets().add(lightThemeUrl.toExternalForm());
            }
        });
    }
}

