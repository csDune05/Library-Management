package com.example.librabry_management;

import javafx.stage.Stage;

public class StageManager {
    private static Stage welcomeStage;
    private static Stage loginStage;
    private static Stage dashboardStage;

    public static void setWelcomeStage(Stage stage) {
        welcomeStage = stage;
    }

    public static void setLoginStage(Stage stage) {
        loginStage = stage;
    }

    public static void setDashboardStage(Stage stage) {
        dashboardStage = stage;
    }

    public static void closeWelcomeStage() {
        if (welcomeStage != null) {
            welcomeStage.close();
        }
    }

    public static void closeLoginStage() {
        if (loginStage != null) {
            loginStage.close();
        }
    }

    public static void openWelcomeStage() {
        welcomeStage.show();
    }
}
