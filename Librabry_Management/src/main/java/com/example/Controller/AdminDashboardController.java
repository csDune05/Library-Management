package com.example.Controller;

import com.example.librabry_management.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdminDashboardController {

    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> publisherColumn;
    @FXML
    private TableColumn<Book, String> publishDateColumn;
    @FXML
    private TableColumn<Book, String> averageRatingColumn;
    @FXML
    private TableColumn<Book, String> descriptionColumn;
    @FXML
    private TableColumn<Book, Void> eraseColumn;
    @FXML
    private TableColumn<Book, Void> editColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userLocationColumn;
    @FXML
    private TableColumn<User, String> userBirthdateColumn;
    @FXML
    private TableColumn<User, String> userPhoneColumn;
    @FXML
    private TableColumn<User, String> userPasswordColumn;
    @FXML
    private TextField bookTitleField;
    @FXML
    private TextField bookAuthorField;
    @FXML
    private TextField bookDescriptionField;
    @FXML
    private TextField bookPublisherField;
    @FXML
    private TextField bookDateField;
    @FXML
    private TextField bookRatingField;
    @FXML
    private TextField bookAddTitleField;
    @FXML
    private TextField bookAddAuthorField;
    @FXML
    private TextField bookAddDescriptionField;
    @FXML
    private TextField bookAddPublisherField;
    @FXML
    private TextField bookAddDateField;
    @FXML
    private TextField bookAddRatingField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField userPhoneField;
    @FXML
    private TextField userLocationField;
    @FXML
    private TextField userPasswordField;
    @FXML
    private Button addUserButton;
    @FXML
    private TextField userBirthdateField;
    @FXML
    private TextField nameemailSearchField;
    @FXML
    private AnchorPane userManagerPane;
    @FXML
    private AnchorPane bookManagerPane;
    @FXML
    private Button userManagerButton;
    @FXML
    private Button bookManagerButton;
    @FXML
    private Button resetBookManagerButton;
    @FXML
    private Button resetUserManagerButton;
    @FXML
    private TableColumn<User, Void> actionColumn;
    @FXML
    private AnchorPane addUserPane;
    @FXML
    private Button userStackAdd;
    @FXML
    private Button bookStackAdd;
    @FXML
    private TabPane addBookStackPane;
    @FXML
    private TextField bookTitleAuthorSearchField;
    @FXML
    private AnchorPane editBookPane;
    @FXML
    private Label bookTitle;
    @FXML
    private Label bookAuthor;
    @FXML
    private Label bookPublisher;
    @FXML
    private Label bookPublishedDate;
    @FXML
    private Label bookRating;
    @FXML
    private ImageView bookImage;
    @FXML
    private Button addAPIButton;
    @FXML
    private TextField searchAPIField;

    private Popup suggestionsPopup;

    private ObservableList<Book> bookList;
    private ObservableList<User> userList;
    private ObservableList<Book> originalBookList;
    private ObservableList<User> originalUserList;

    @FXML
    public void initialize() {
        setupSearchSuggestions();
        editColumn.setCellFactory(column -> new TableCell<Book, Void>() {
            private final ImageView editIcon = new ImageView(new Image(getClass().getResource("/com/example/librabry_management/Images/edit.png").toExternalForm()));

            {
                editIcon.setFitWidth(16);
                editIcon.setFitHeight(16);
                editIcon.setPickOnBounds(true);

                // Thêm sự kiện vào toàn bộ ô
                this.setOnMouseClicked(event -> {
                    event.consume(); // Chặn sự kiện lan truyền
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        openEditBook();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    setGraphic(editIcon);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        eraseColumn.setCellFactory(column -> new TableCell<Book, Void>() {
            private final ImageView eraseIcon = new ImageView(new Image(getClass().getResource("/com/example/librabry_management/Images/Delete.png").toExternalForm()));

            {
                eraseIcon.setFitWidth(16);
                eraseIcon.setFitHeight(16);
                eraseIcon.setPickOnBounds(true);

                // Thêm sự kiện vào toàn bộ ô
                this.setOnMouseClicked(event -> {
                    event.consume(); // Chặn sự kiện lan truyền
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete book");
                        alert.setHeaderText(null);
                        alert.setContentText("Are you sure you want to delete this book?");
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.OK) {
                            deleteBookHandler();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    setGraphic(eraseIcon);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        actionColumn.setCellFactory(column -> new TableCell<User, Void>() {
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/com/example/librabry_management/Images/Delete.png").toExternalForm()));

            {
                deleteIcon.setFitWidth(16);
                deleteIcon.setFitHeight(16);
                deleteIcon.setPickOnBounds(true);

                // Thêm sự kiện vào toàn bộ ô
                this.setOnMouseClicked(event -> {
                    event.consume(); // Chặn sự kiện lan truyền
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete user");
                        alert.setHeaderText(null);
                        alert.setContentText("Are you sure you want to delete this user?");
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.OK) {
                            deleteUserHandler();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteIcon);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        setActiveButton(userManagerButton);

        originalBookList = FXCollections.observableArrayList();
        originalUserList = FXCollections.observableArrayList();
        bookList = FXCollections.observableArrayList();
        userList = FXCollections.observableArrayList();

        setupTableColumns();
        loadBooks();
        loadUsers();

        originalBookList.addAll(bookList);
        originalUserList.addAll(userList);

        bookTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateBookFields(newValue); // Điền thông tin vào các trường
            }
        });

        userBirthdateField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replaceAll("[^\\d/]", "");

            if (newValue.length() > 2 && !newValue.contains("/")) {
                newValue = newValue.substring(0, 2) + "/" + newValue.substring(2);
            }
            if (newValue.length() > 5 && newValue.charAt(5) != '/') {
                newValue = newValue.substring(0, 5) + "/" + newValue.substring(5);
            }

            if (newValue.length() > 10) {
                newValue = newValue.substring(0, 10);
            }
            userBirthdateField.setText(newValue);
        });
    }

    private void setupSearchSuggestions() {
        // Tạo Popup để chứa danh sách gợi ý
        suggestionsPopup = new Popup();
        suggestionsPopup.setAutoHide(true); // Tự động ẩn khi người dùng click ngoài Popup

        // Tạo ListView để hiển thị danh sách gợi ý
        ListView<String> suggestionsList = new ListView<>();
        suggestionsList.setPrefWidth(searchAPIField.getPrefWidth()); // Kích thước khớp với TextField
        suggestionsPopup.getContent().add(suggestionsList); // Thêm ListView vào Popup

        // Lắng nghe thay đổi trong searchField
        searchAPIField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                fetchSuggestions(newValue.trim(), suggestionsList); // Lấy gợi ý
            } else {
                suggestionsPopup.hide(); // Ẩn Popup nếu không có dữ liệu
            }
        });

        // Xử lý khi người dùng chọn gợi ý
        suggestionsList.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                searchAPIField.setText(selectedSuggestion); // Điền vào TextField
                fetchAndDisplayBookDetails(selectedSuggestion); // Hiển thị thông tin sách
                suggestionsPopup.hide(); // Ẩn Popup sau khi chọn
            }
        });

        // Ẩn Popup khi TextField mất focus
        searchAPIField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                suggestionsPopup.hide(); // Ẩn Popup khi mất focus
            }
        });
    }

    private void fetchAndDisplayBookDetails(String query) {
        CompletableFuture.runAsync(() -> {
            String jsonResponse = GoogleBooksApi.searchBooks(query, 0, 1); // Lấy 1 sách từ API
            ObservableList<Book> books = JsonParserEx.parseBooks(jsonResponse);

            if (!books.isEmpty()) {
                Book book = books.get(0);
                Platform.runLater(() -> displayBookDetails(book));
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void displayBookDetails(Book book) {
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookPublisher.setText(book.getPublisher());
        bookPublishedDate.setText(book.getDate());
        bookRating.setText(book.getRating());

        // Hiển thị hình ảnh sách (nếu có)
        Image thumbnail = new Image(book.getThumbnailUrl(), true);
        bookImage.setImage(thumbnail);

        // Gắn sự kiện cho nút "Add"
        addAPIButton.setOnAction(event -> saveBookToDatabase(book));

    }


    private void fetchSuggestions(String query, ListView<String> suggestionsList) {
        CompletableFuture.runAsync(() -> {
            // Gọi API để lấy gợi ý
            List<String> apiSuggestions = JsonParserEx.parseSuggestions(GoogleBooksApi.searchBooksForSuggestions(query));

            Platform.runLater(() -> {
                if (!apiSuggestions.isEmpty()) {
                    suggestionsList.getItems().setAll(apiSuggestions); // Cập nhật danh sách gợi ý
                    if (!suggestionsPopup.isShowing()) {
                        suggestionsPopup.show(searchAPIField,
                                searchAPIField.localToScreen(searchAPIField.getBoundsInLocal()).getMinX(),
                                searchAPIField.localToScreen(searchAPIField.getBoundsInLocal()).getMaxY());
                    }
                } else {
                    suggestionsPopup.hide(); // Ẩn Popup nếu không có gợi ý
                }
            });
        });
    }

    private void saveBookToDatabase(Book book) {
        DatabaseHelper.saveBook(book, "Thêm qua Add Book");

        bookList.add(book);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText("Thêm sách thành công");
        alert.setContentText("Sách đã được thêm vào cơ sở dữ liệu.");
        alert.showAndWait();

        clearBookDetails(); // Xóa thông tin sau khi thêm
    }

    private void clearBookDetails() {
        bookTitle.setText("");
        bookAuthor.setText("");
        bookPublisher.setText("");
        bookPublishedDate.setText("");
        bookRating.setText("");
        bookImage.setImage(null);
        searchAPIField.clear();
    }

    @FXML
    private void userStackAdd() {
        addUserPane.setVisible(true);
    }

    @FXML
    private void bookTabAdd() {
        addBookStackPane.setVisible(true);
    }

    @FXML
    private void showUserManager() {
        userManagerPane.setVisible(true);  // Hiển thị User Manager
        bookManagerPane.setVisible(false); // Ẩn Book Manager
        setActiveButton(userManagerButton);
    }

    @FXML
    private void showBookManager() {
        userManagerPane.setVisible(false); // Ẩn User Manager
        bookManagerPane.setVisible(true);  // Hiển thị Book Manager
        setActiveButton(bookManagerButton);
    }

    @FXML
    private void exitApplication() {
        System.exit(0);
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        averageRatingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        userBirthdateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        userPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    private void loadBooks() {
        bookList.clear();
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM books")) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookList.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating")
                ));
            }
            bookTableView.setItems(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        userList.clear();
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users")) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userList.add(new User(
                        rs.getString("name"),
                        rs.getString("birthdate"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("location"),
                        rs.getString("password")
                ));
            }
            userTableView.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addBookHandler() {
        String title = bookAddTitleField.getText();
        String author = bookAddAuthorField.getText();
        String description = bookAddDescriptionField.getText();
        String publisher = bookAddPublisherField.getText();
        String date = bookAddDateField.getText();
        String rating = bookAddRatingField.getText();

        if (title.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty Title");
            alert.setContentText("Title is required. Please enter a valid title.");
            alert.showAndWait();
            return;
        }

        Book newBook = new Book(title, author, description, "", publisher, date, rating);
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO books (title, author, description, publisher, published_date, average_rating) VALUES (?, ?, ?, ?, ?, ?)");) {
            pstmt.setString(1, newBook.getTitle());
            pstmt.setString(2, newBook.getAuthor());
            pstmt.setString(3, newBook.getDescription());
            pstmt.setString(4, newBook.getPublisher());
            pstmt.setString(5, newBook.getDate());
            pstmt.setString(6, newBook.getRating());
            pstmt.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Book added");
            alert.setContentText("Book added successfully.");
            alert.show();
            loadBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteBookHandler() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE title = ? AND author = ?")) {
                pstmt.setString(1, selectedBook.getTitle());
                pstmt.setString(2, selectedBook.getAuthor());
                pstmt.executeUpdate();

                // Xóa sách khỏi danh sách hiển thị
                bookList.remove(selectedBook);

                // Đồng thời xóa sách khỏi danh sách gốc
                originalBookList.remove(selectedBook);

                // Cập nhật lại bảng
                bookTableView.refresh(); // Refresh bảng để phản ánh thay đổi

                //Cap nhat lai danh sach search
                if (!bookTitleAuthorSearchField.getText().isEmpty()) {
                    searchByTitleAuthorHandler();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateBookHandler() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một cuốn sách để cập nhật!");
            alert.showAndWait();
            return;
        }

        // Lấy thông tin từ các TextField
        String newTitle = bookTitleField.getText();
        String newAuthor = bookAuthorField.getText();
        String newDescription = bookDescriptionField.getText();
        String newPublisher = bookPublisherField.getText();
        String newDate = bookDateField.getText();
        String newRating = bookRatingField.getText();

        // Kiểm tra thông tin nhập vào
        if (newTitle.trim().isEmpty() || newAuthor.trim().isEmpty() || newPublisher.trim().isEmpty() || newDate.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng điền đầy đủ thông tin!");
            alert.showAndWait();
            return;
        }

        // Cập nhật thông tin trong cơ sở dữ liệu
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE books SET title = ?, author = ?, description = ?, publisher = ?, published_date = ?, average_rating = ? WHERE title = ? AND author = ?")) {

            pstmt.setString(1, newTitle);
            pstmt.setString(2, newAuthor);
            pstmt.setString(3, newDescription);
            pstmt.setString(4, newPublisher);
            pstmt.setString(5, newDate);
            pstmt.setString(6, newRating);
            pstmt.setString(7, selectedBook.getTitle());
            pstmt.setString(8, selectedBook.getAuthor());
            pstmt.executeUpdate();

            // Cập nhật thông tin trong danh sách hiển thị
            selectedBook.setTitle(newTitle);
            selectedBook.setAuthor(newAuthor);
            selectedBook.setDescription(newDescription);
            selectedBook.setPublisher(newPublisher);
            selectedBook.setDate(newDate);
            selectedBook.setRating(newRating);

            // Refresh TableView để hiển thị thay đổi
            bookTableView.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cập nhật thông tin sách thành công!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi cập nhật thông tin sách!");
            alert.showAndWait();
        }
    }

    @FXML
    private void addUserHandler() {
        String name = userNameField.getText();
        String email = userEmailField.getText();
        String phone = userPhoneField.getText();
        String date = userBirthdateField.getText();
        String location = userLocationField.getText();
        String password = userPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || date.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty Content");
            alert.setContentText("Please retry");
            alert.showAndWait();
            return;
        }

        if (DatabaseHelper.isEmailExists(email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Email đã tồn tại!");
            alert.showAndWait();
            return;
        }

        User newUser = new User(name, date, phone, email, location, password);
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, email, password, birthdate, phone_number, location) VALUES (?, ?, ?, ?, ?, ?)");) {
            pstmt.setString(1, newUser.getName());
            pstmt.setString(2, newUser.getEmail());
            pstmt.setString(3, newUser.getPassword());
            pstmt.setString(4, formatBirthdate(newUser.getBirthDate()));
            pstmt.setString(5, newUser.getPhone_number());
            pstmt.setString(6, newUser.getLocation());
            pstmt.executeUpdate();
            loadUsers();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText("Add User Success");
            alert.showAndWait();
            clearUserFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelAddUserHandler() {
        addUserPane.setVisible(false);
    }

    @FXML
    private void cancelAddBookHandler() {
        addBookStackPane.setVisible(false);
    }

    @FXML
    private void cancelEditBookHandler() {
        editBookPane.setVisible(false);
    }

    @FXML
    private void deleteUserHandler() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE email = ?")) {
                pstmt.setString(1, selectedUser.getEmail());
                pstmt.executeUpdate();

                // Xóa người dùng khỏi danh sách hiển thị
                userList.remove(selectedUser);

                // Đồng thời xóa người dùng khỏi danh sách gốc
                originalUserList.remove(selectedUser);

                // Cập nhật lại bảng
                userTableView.refresh(); // Refresh bảng để phản ánh thay đổi

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một người dùng để xóa!");
            alert.showAndWait();
        }
    }

    @FXML
    private void searchByTitleAuthorHandler() {
        String Keyword = bookTitleAuthorSearchField.getText().trim();

        if (Keyword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập tiêu đề cần tìm!");
            alert.showAndWait();
            return;
        }

        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(Keyword.toLowerCase())
            || book.getAuthor().toLowerCase().contains(Keyword.toLowerCase())) {
                filteredList.add(book);
            }
        }
        bookTableView.setItems(filteredList); // Hiển thị danh sách đã lọc
    }

    @FXML
    private void searchByNameAndEmailHandler() {
        String nameKeyword = nameemailSearchField.getText().trim();

        if (nameKeyword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập tên cần tìm!");
            alert.showAndWait();
            return;
        }

        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User user : userList) {
            if (user.getName().toLowerCase().contains(nameKeyword.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(nameKeyword.toLowerCase())) {
                filteredList.add(user);
            }
        }

        userTableView.setItems(filteredList); // Hiển thị danh sách đã lọc

    }

    @FXML
    private void resetBookTable() {
        bookTableView.setItems(bookList); // Khôi phục về danh sách hiển thị
        bookTitleAuthorSearchField.clear();
    }

    @FXML
    private void resetUserTable() {
        userTableView.setItems(userList); // Khôi phục về danh sách hiển thị
        nameemailSearchField.clear();
        actionColumn.setCellFactory(column -> new TableCell<User, Void>() {
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/com/example/librabry_management/Images/Delete.png").toExternalForm()));

            {
                deleteIcon.setFitWidth(16);
                deleteIcon.setFitHeight(16);
                deleteIcon.setPickOnBounds(true);

                // Thêm sự kiện vào toàn bộ ô
                this.setOnMouseClicked(event -> {
                    event.consume(); // Chặn sự kiện lan truyền
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        deleteUserHandler();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteIcon);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private String formatBirthdate(String birthdate) throws IllegalArgumentException {
        String[] parts = birthdate.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format");
        }
        String formattedDate = parts[2] + "-" + parts[1] + "-" + parts[0]; // Định dạng yyyy-MM-dd
        return formattedDate;
    }

    private void populateBookFields(Book selectedBook) {
        bookTitleField.setText(selectedBook.getTitle());
        bookAuthorField.setText(selectedBook.getAuthor());
        bookDescriptionField.setText(selectedBook.getDescription());
        bookPublisherField.setText(selectedBook.getPublisher());
        bookDateField.setText(selectedBook.getDate());
        bookRatingField.setText(selectedBook.getRating());
    }

    @FXML
    private void clearUserFields() {
        userNameField.clear();
        userEmailField.clear();
        userLocationField.clear();
        userPhoneField.clear();
        userPasswordField.clear();
        userBirthdateField.clear();
    }

    @FXML
    public void openEditBook() {
        editBookPane.setVisible(true);
    }

    private void setActiveButton(Button activeButton) {
        userManagerButton.getStyleClass().remove("active");
        bookManagerButton.getStyleClass().remove("active");

        activeButton.getStyleClass().add("active");
    }
}