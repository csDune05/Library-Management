module com.example.librabry_management {
    requires java.sql;
    requires com.google.gson;
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires com.zaxxer.hikari;

    requires java.prefs;
    requires json;
    requires javafx.media;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    opens com.example.Controller to javafx.fxml;
    opens com.example.librabry_management to javafx.fxml;
    exports com.example.librabry_management;
    exports com.example.Controller;
    exports com.example.QRCode;
}