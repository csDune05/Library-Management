package com.example.Feature;

import javafx.scene.Scene;
import java.net.URL;
import com.example.librabry_management.*;
import com.example.Controller.*;

public class SceneHelper {

    public static void applyTheme(Scene scene) {
        ThemeManager themeManager = ThemeManager.getInstance();

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

