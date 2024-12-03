package com.example.librabry_management;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.prefs.Preferences;
import com.example.Controller.*;
import com.example.Feature.*;

public class ThemeManager {

    private static final String DARK_MODE_PREF_KEY = "darkMode";
    private static final Preferences preferences = Preferences.userNodeForPackage(ThemeManager.class);
    private static final ThemeManager instance = new ThemeManager();

    // Lưu trạng thái Dark Mode (mặc định đọc từ Preferences)
    private final BooleanProperty darkModeEnabled = new SimpleBooleanProperty(
            preferences.getBoolean(DARK_MODE_PREF_KEY, false)
    );

    // Constructor riêng tư (Singleton Pattern)
    private ThemeManager() {
        // Lưu trạng thái khi có thay đổi
        darkModeEnabled.addListener((obs, oldVal, newVal) -> {
            preferences.putBoolean(DARK_MODE_PREF_KEY, newVal);
        });
    }

    // Lấy instance duy nhất của ThemeManager
    public static ThemeManager getInstance() {
        return instance;
    }

    // Truy cập trạng thái Dark Mode
    public BooleanProperty darkModeEnabledProperty() {
        return darkModeEnabled;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled.get();
    }

    public void setDarkModeEnabled(boolean enabled) {
        darkModeEnabled.set(enabled);
    }
}
