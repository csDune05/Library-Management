module com.example.librabry_management {
    requires com.google.gson;
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.example.librabry_management to javafx.fxml;
    exports com.example.librabry_management;
}