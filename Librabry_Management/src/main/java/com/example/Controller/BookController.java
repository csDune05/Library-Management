package com.example.Controller;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import com.example.Feature.VoskManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import com.example.librabry_management.*;
import com.example.Feature.*;
import javax.sound.sampled.*;


public class BookController {

    @FXML
    private VBox topRateBook;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button donateUsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button clearNotificationsButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button myLibraryButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private ImageView notificationImageView;

    @FXML
    private Button notificationButton;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private ScrollPane notificationScrollPane;

    @FXML
    private VBox notificationList;

    @FXML
    private Button voiceButton;

    @FXML
    private ImageView voiceImageView;

    private Scene bookScene;
    private VoskManager voskManager;
    private boolean isRecording = false;
    private TargetDataLine microphone;
    private Thread recordingThread;

    public Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    /**
     * Handle switch to my library scene event.
     */
    @FXML
    public void myLibraryButtonHandle() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    /**
     * Handle switch to home scene event.
     */
    @FXML
    public void homeButtonHandle() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    /**
     * Handle switch to profile scene event.
     */
    @FXML
    public void profileButtonHandle() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    /**
     * Handle switch to donate us scene event.
     */
    @FXML
    public void donateUsButtonHandle() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    /**
     * Handle exit stage event.
     */
    @FXML
    public void logOutButtonHandle() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    /**
     * Handle search by voice event.
     */
    @FXML
    public void handleVoiceButton() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
            processAudio();
        }
    }

    /**
     * Handle clear all notification event.
     */
    @FXML
    public void clearALlButtonHandle() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * Start recording voice.
     */
    public void startRecording() {
        isRecording = true;
        voiceImageView.setImage(new Image(getClass().getResource("/com/example/librabry_management/Images/micOff.png").toExternalForm()));

        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
            microphone.start();

            recordingThread = new Thread(() -> {
                try {
                    File audioFile = new File(getClass().getResource("/com/example/librabry_management/Musics/WAVVoskSample.wav").getPath());
                    AudioSystem.write(new AudioInputStream(microphone), AudioFileFormat.Type.WAVE, audioFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            recordingThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop recording voice.
     */
    public void stopRecording() {
        isRecording = false;
        voiceImageView.setImage(new Image(getClass().getResource("/com/example/librabry_management/Images/micOn.png").toExternalForm()));

        try {
            if (microphone != null && microphone.isOpen()) {
                microphone.stop();
                microphone.close();
            }

            if (recordingThread != null && recordingThread.isAlive()) {
                recordingThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAudio() {
        new Thread(() -> {
            try {
                String result = voskManager.transcribeAudio(
                        getClass().getResource("/com/example/librabry_management/Musics/WAVVoskSample.wav").getPath()
                );

                Platform.runLater(() -> searchField.setText(result.trim()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handle show notification event.
     */
    @FXML
    public void notificationButtonHandle() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()){
            MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
        }
    }

    /**
     * Initialize default books scene.
     */
    @FXML
    public void initialize() {
        loadBookCards();
        try {
            voskManager = VoskManager.getInstance(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            bookScene = homeButton.getScene();
        });

        List<Book> books = DatabaseHelper.getDefaultBooks();
        displayBooks(books);
        searchButton.setOnAction(e -> performSearch());
        setupSearchSuggestions();

        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            return;
        }

        gridPane.getChildren().clear();

        int totalBooks = 40;
        int batchSize = 10;
        int booksPerRow = 5;

        CompletableFuture.runAsync(() -> {
            int row = 0, col = 0;

            for (int i = 0; i < totalBooks; i += batchSize) {
                int startIndex = i;
                String jsonResponse = GoogleBooksApi.searchBooks(query, startIndex, batchSize);
                List<Book> books = JsonParserEx.parseBooks(jsonResponse);

                if (books != null) {
                    for (Book book : books) {
                        DatabaseHelper.saveBook(book, query);

                        final int currentRow = row;
                        final int currentCol = col;

                        Platform.runLater(() -> {
                            VBox bookCard = createBookCard(book);
                            gridPane.add(bookCard, currentCol, currentRow);
                        });
                        col++;
                        if (col >= booksPerRow) {
                            col = 0;
                            row++;
                        }
                    }
                }
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void setupSearchSuggestions() {
        // Popup để hiển thị gợi ý
        Popup suggestionsPopup = new Popup();
        suggestionsPopup.setAutoHide(true);

        // ListView để hiển thị danh sách gợi ý
        ListView<String> suggestionsList = new ListView<>();
        suggestionsList.setPrefWidth(749); // Đảm bảo kích thước Popup phù hợp
        suggestionsList.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                searchField.setText(selectedSuggestion); // Điền vào TextField
                suggestionsPopup.hide(); // Ẩn Popup sau khi chọn
            }
        });

        suggestionsPopup.getContent().add(suggestionsList); // Thêm suggestionsList vào Popup

        // Lắng nghe sự thay đổi trong searchField
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                suggestionsPopup.hide(); // Ẩn Popup nếu không có dữ liệu
                return;
            }

            // Gọi đồng thời để lấy gợi ý từ database và API
            CompletableFuture.supplyAsync(() -> getSuggestions(newValue)) // Lấy gợi ý từ cơ sở dữ liệu
                    .thenCombineAsync(
                            CompletableFuture.supplyAsync(() -> getSuggestionsFromAPI(newValue)), // Lấy gợi ý từ API
                            (dbSuggestions, apiSuggestions) -> {
                                Set<String> allSuggestions = new LinkedHashSet<>(dbSuggestions); // Sử dụng LinkedHashSet để loại bỏ trùng lặp và duy trì thứ tự
                                allSuggestions.addAll(apiSuggestions);
                                return new ArrayList<>(allSuggestions);
                            })
                    .thenAcceptAsync(suggestions -> {
                        if (suggestions.isEmpty()) {
                            suggestionsPopup.hide(); // Ẩn Popup nếu không có gợi ý
                        } else {
                            Platform.runLater(() -> {
                                suggestionsList.getItems().setAll(suggestions); // Cập nhật danh sách gợi ý

                                // Hiển thị Popup tại vị trí của SearchField
                                if (!suggestionsPopup.isShowing()) {
                                    suggestionsPopup.show(searchField,
                                            searchField.localToScreen(searchField.getBoundsInLocal()).getMinX(),
                                            searchField.localToScreen(searchField.getBoundsInLocal()).getMaxY());
                                }
                            });
                        }
                    });
        });

        // Ẩn Popup khi SearchField mất focus
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                suggestionsPopup.hide();
            }
        });
    }

    public List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();

        String sql = "SELECT title FROM books WHERE title LIKE ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                suggestions.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    public List<String> getSuggestionsFromAPI(String query) {
        final List<String> suggestions = new ArrayList<>();
        String jsonResponse = GoogleBooksApi.searchBooksForSuggestions(query);  // Lấy phản hồi từ API
        // Log phản hồi từ API
        if (jsonResponse != null) {
            try {
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                JsonArray items = jsonObject.getAsJsonArray("items");

                if (items != null) {
                    // Lấy tiêu đề sách từ response
                    for (JsonElement item : items) {
                        JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                        String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : null;

                        // Thêm vào danh sách gợi ý
                        if (title != null && !title.trim().isEmpty()) {
                            suggestions.add(title);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error parsing API response: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return suggestions;
    }


    /**
     * Create book card.
     */
    public VBox createBookCard(Book book) {
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(110);
        thumbnail.setFitHeight(160);

        CompletableFuture.runAsync(() -> {
            Image image = new Image(book.getThumbnailUrl(), true);
            Platform.runLater(() -> thumbnail.setImage(image));
        });

        Label title = new Label(book.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        title.setMaxWidth(120);
        title.setPrefHeight(40);

        Button knowMoreButton = new Button("Know more");
        knowMoreButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        knowMoreButton.setPrefWidth(120);
        knowMoreButton.setPrefHeight(30);
        knowMoreButton.setOnMouseEntered(event -> {
            knowMoreButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-border-radius: 5px;");
        });

        knowMoreButton.setOnMouseExited(event -> {
            knowMoreButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5px;");
        });

        knowMoreButton.setOnAction(e -> viewBookDetails(book));

        VBox card = new VBox(5, thumbnail, title, knowMoreButton);
        card.setPadding(new Insets(10));

        card.setStyle(
                "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-background-color: #f2fbff; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
        );

        card.setOnMouseEntered(event -> {
            card.setStyle(
                    "-fx-border-color: #007bff; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #cce7ff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 0, 4);"
            );
        });

        card.setOnMouseExited(event -> {
            card.setStyle(
                    "-fx-border-color: lightgray; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #f2fbff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
            );
        });

        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);

        card.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
                Parent root = loader.load();

                BookDetailController detailController = loader.getController();
                detailController.setBookDetail(book);
                detailController.setBookController(this);

                Stage stage = (Stage) knowMoreButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }


    public void displayBooks(List<Book> books) {
        gridPane.getChildren().clear();
        gridPane.setHgap(22);

        int row = 0, col = 0;
        int booksPerRow = 5;

        for (Book book : books) {
            VBox bookCard = createBookCard(book);
            gridPane.add(bookCard, col, row);
            col++;
            if (col >= booksPerRow) {
                col = 0;
                row++;
            }
        }
    }

    public void loadBookCards() {
        List<Book> sampleBooks = DatabaseHelper.getTopRateBooks();
        topRateBook.getChildren().clear();

        // Thêm BookCard vào VBox
        for (Book book : sampleBooks) {
            VBox bookCard = createTopBookCard(book);
            topRateBook.getChildren().add(bookCard);
        }
    }

    /**
     * Create top book card.
     */
    public VBox createTopBookCard(Book book) {
        VBox card = BookCard.createBookCard(book, this::viewBookDetails);
        card.setStyle(
                "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-background-color: #f2fbff; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
        );

        card.setOnMouseEntered(event -> {
            card.setStyle(
                    "-fx-border-color: #007bff; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #cce7ff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 0, 4);"
            );
        });
        card.setOnMouseExited(event -> {
            card.setStyle(
                    "-fx-border-color: lightgray; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #f2fbff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
            );
        });
        return card;
    }

    /**
     * Handle show book details event.
     */
    public void viewBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetail(book);
            detailController.setBookController(this);

            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getBookScene() {
        return bookScene;
    }
}
