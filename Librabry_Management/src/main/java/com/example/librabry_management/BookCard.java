package com.example.librabry_management;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BookCard {
    public static VBox createBookCard(Book book, Consumer<Book> onClick) {
        try {
            if (book == null) {
                return new VBox();
            }
            ImageView thumbnail = new ImageView();
            thumbnail.setFitWidth(100);
            thumbnail.setFitHeight(150);

            CompletableFuture.runAsync(() -> {
                Image image = new Image(book.getThumbnailUrl(), true);
                Platform.runLater(() -> thumbnail.setImage(image));
            });

            Label title = new Label(book.getTitle());
            title.setWrapText(true);
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label author = new Label("By: " + book.getAuthor());
            author.setStyle("-fx-font-size: 12px;");

            VBox card = new VBox(10, thumbnail, title, author);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(10));
            card.setStyle(
                    "-fx-border-color: lightgray; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #f5fcff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
            );

            card.setOnMouseEntered(event -> {
                card.setStyle(
                        "-fx-border-color: #007bff; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-background-radius: 10px; " +
                                "-fx-background-color: #e6f7ff; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 0, 4);"
                );
            });

            card.setOnMouseExited(event -> {
                card.setStyle(
                        "-fx-border-color: lightgray; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-background-radius: 10px; " +
                                "-fx-background-color: #f5fcff; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
                );
            });

            card.setOnMouseClicked(event -> onClick.accept(book));

            return card;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
