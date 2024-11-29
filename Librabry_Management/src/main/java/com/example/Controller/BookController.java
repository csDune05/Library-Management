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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import com.example.librabry_management.*;

import javax.sound.sampled.*;


public class BookController {
    @FXML
    private TextField bookTitle;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button donateUsButton;

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
    private TextArea notificationText;

    @FXML
    private Button voiceButton;

    private Scene bookScene;

    private VoskManager voskManager;

    private boolean isRecording = false;
    private TargetDataLine microphone;
    private Thread recordingThread;

    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    @FXML
    public void homeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    @FXML
    public void ProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    @FXML
    private void handleVoiceButton() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
            processAudio();
        }
    }

    private void startRecording() {
        isRecording = true;
        voiceButton.setText("Dừng");

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

    private void stopRecording() {
        isRecording = false;
        voiceButton.setText("Ghi");

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

    private void processAudio() {
        new Thread(() -> {
            try {
                // Lấy kết quả từ Vosk và hiển thị trực tiếp
                String result = voskManager.transcribeAudio(
                        getClass().getResource("/com/example/librabry_management/Musics/WAVVoskSample.wav").getPath()
                );

                // Hiển thị kết quả trong searchField trên thread JavaFX
                Platform.runLater(() -> searchField.setText(result.trim()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void initialize() {
        try {
            voskManager = VoskManager.getInstance(null); // Lấy instance đã khởi tạo từ Singleton
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            bookScene = homeButton.getScene();
        });

        List<Book> books = DatabaseHelper.getDefaultBooks(); // Lấy 40 sách từ cơ sở dữ liệu
        displayBooks(books); // Hiển thị sách lên GridPane

        searchButton.setOnAction(e -> performSearch());

        // Khởi tạo gợi ý tìm kiếm
        setupSearchSuggestions();

        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            return; // Không tìm kiếm nếu query trống
        }

        gridPane.getChildren().clear(); // Xóa các sách cũ khỏi giao diện

        int totalBooks = 40; // Tổng số sách cần tải
        int batchSize = 10;  // Số sách mỗi lần tải
        int booksPerRow = 5; // Số sách trên mỗi hàng

        // Tạo CompletableFuture để tải sách từ API và xử lý đồng thời
        CompletableFuture.runAsync(() -> {
            int row = 0, col = 0;

            for (int i = 0; i < totalBooks; i += batchSize) {
                int startIndex = i;
                // Lấy dữ liệu sách từ Google Books API
                String jsonResponse = GoogleBooksApi.searchBooks(query, startIndex, batchSize);
                List<Book> books = JsonParserEx.parseBooks(jsonResponse);

                if (books != null) {
                    for (Book book : books) {
                        // Lưu sách vào cơ sở dữ liệu
                        DatabaseHelper.saveBook(book, query);

                        // Đảm bảo cập nhật giao diện trên thread chính
                        final int currentRow = row;
                        final int currentCol = col;

                        // Hiển thị sách trên giao diện
                        Platform.runLater(() -> {
                            VBox bookCard = createBookCard(book);
                            gridPane.add(bookCard, currentCol, currentRow);
                        });

                        // Cập nhật vị trí sách trong GridPane
                        col++;
                        if (col >= booksPerRow) {
                            col = 0;
                            row++;
                        }
                    }
                }
            }
        }).exceptionally(ex -> {
            // Xử lý ngoại lệ nếu có lỗi
            ex.printStackTrace();
            return null;
        });
    }

    private void setupSearchSuggestions() {
        // Popup để hiển thị gợi ý
        Popup suggestionsPopup = new Popup();
        suggestionsPopup.setAutoHide(true);

        // ListView để hiển thị danh sách gợi ý
        ListView<String> suggestionsList = new ListView<>();
        suggestionsList.setPrefWidth(659); // Đảm bảo kích thước Popup phù hợp
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
                                return new ArrayList<>(allSuggestions); // Chuyển lại thành danh sách
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
                suggestionsPopup.hide(); // Ẩn Popup khi SearchField không còn focus
            }
        });
    }

    private List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();

        // Truy vấn cơ sở dữ liệu để tìm các sách có tên giống với query
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

    private List<String> getSuggestionsFromAPI(String query) {
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

                        // Nếu tiêu đề sách hợp lệ, thêm vào danh sách gợi ý
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


    private VBox createBookCard(Book book) {
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

        knowMoreButton.setOnAction(e -> {
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

        VBox card = new VBox(5, thumbnail, title, knowMoreButton);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");
        card.setPrefWidth(150);
        card.setPrefHeight(270);
        card.setAlignment(Pos.CENTER);

        return card;
    }

    private void displayBooks(List<Book> books) {
        gridPane.getChildren().clear(); // Xóa giao diện cũ

        int row = 0, col = 0; // Vị trí bắt đầu trong GridPane
        int booksPerRow = 5; // Số sách trên mỗi hàng

        for (Book book : books) {
            VBox bookCard = createBookCard(book); // Tạo thẻ sách
            gridPane.add(bookCard, col, row); // Thêm thẻ sách vào GridPane
            col++;
            if (col >= booksPerRow) {
                col = 0;
                row++;
            }
        }
    }


    public Scene getBookScene() {
        return bookScene;
    }
}
