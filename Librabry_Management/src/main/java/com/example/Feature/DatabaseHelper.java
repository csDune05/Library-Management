package com.example.Feature;

import com.example.librabry_management.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class DatabaseHelper extends Application {

    private static HikariDataSource dataSource;

    static {
        // Cấu hình
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:Your_port/My_Library");
        config.setUsername("root");
        config.setPassword("Your_Password");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(10);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(0);
        config.setLeakDetectionThreshold(5000);
        config.addDataSourceProperty("housekeepingPeriodMs", "60000");
        dataSource = new HikariDataSource(config);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1")) {
            pstmt.execute();
            System.out.println("HikariCP warm-up completed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cấu hình lại DataSource để kiểm thử.
     * @param jdbcUrl
     * @param username
     * @param password
     */
    public static void configureTestDataSource(String jdbcUrl, String username, String password) {
        if (dataSource != null) {
            dataSource.close();
        }
        HikariConfig testConfig = new HikariConfig();
        testConfig.setJdbcUrl(jdbcUrl);
        testConfig.setUsername(username);
        testConfig.setPassword(password);
        testConfig.setMaximumPoolSize(5);
        testConfig.setMinimumIdle(2);
        dataSource = new HikariDataSource(testConfig);
        System.out.println("DataSource configured for testing.");
    }

    /**
     * Create Connect.
     * @return
     * @throws SQLException
     */
    public static Connection connect() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not configured");
        }
        return dataSource.getConnection();
    }

    /**
     * End connect when close program.
     */
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Get 50 books when open Books.
     */
    public static List<Book> getDefaultBooks() {
        String sql = "SELECT * FROM books ORDER BY RAND() LIMIT 50;";
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating"),
                        rs.getInt("view")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * get top book view.
     */
    public static Book getTopView() {
        String sql = "SELECT * FROM books ORDER BY view DESC LIMIT 1;";
        Book book = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                book = new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating"),
                        rs.getInt("view")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    /**
     * Get 5 books top rating.
     */
    public static List<Book> getTopRateBooks() {
        String sql = """
        SELECT * FROM books
        WHERE average_rating REGEXP '^[0-9]+(\\.[0-9]+)?$'
        ORDER BY CAST(average_rating AS DECIMAL) DESC
        LIMIT 5;
    """;
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating"),
                        rs.getInt("view")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Create table book if not exist.
     */
    public static void createBookTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS books (
                  id INTEGER PRIMARY KEY AUTO_INCREMENT,
                  title VARCHAR(255) NOT NULL,
                  author VARCHAR(255),
                  description TEXT,
                  thumbnail_url TEXT,
                  publisher TEXT,
                  published_date TEXT,
                  average_rating TEXT,
                  view INT DEFAULT 0,
                  UNIQUE (title, author)
              );
            """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Increment view when see book detail.
     */
    public static void incrementBookView(int bookId) {
        String sql = "UPDATE books SET view = view + 1 WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save book to database.
     * @param book
     * @param query
     */
    public static void saveBook(Book book, String query) {
        String sql = """
            INSERT INTO books (title, author, description, thumbnail_url, publisher, published_date, average_rating)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                description = VALUES(description),
                thumbnail_url = VALUES(thumbnail_url),
                publisher = VALUES(publisher),
                published_date = VALUES(published_date),
                average_rating = VALUES(average_rating);
            """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setString(4, book.getThumbnailUrl());
            pstmt.setString(5, book.getPublisher());
            pstmt.setString(6, book.getDate());
            pstmt.setString(7, book.getRating());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recommend same books.
     * @param title
     * @param author
     * @return
     */
    public static List<Book> getRelatedBooks(String title, String author) {
        List<Book> books = new ArrayList<>();
        String query = """
            SELECT *, 
            (CASE 
                WHEN title LIKE ? THEN 3
                WHEN author LIKE ? THEN 2
                ELSE 1
            END) AS relevance
            FROM books
            WHERE (title LIKE ? OR author LIKE ? OR description LIKE ?)
            AND NOT (title = ? AND author = ?)
            ORDER BY relevance DESC, title ASC
            LIMIT 5;
            """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            String keyword = "%" + title.split(" ")[0] + "%";
            pstmt.setString(1, "%" + title + "%");
            pstmt.setString(2, "%" + author + "%");
            pstmt.setString(3, keyword);
            pstmt.setString(4, keyword);
            pstmt.setString(5, keyword);
            pstmt.setString(6, title);
            pstmt.setString(7, author);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating"),
                        rs.getInt("view")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Create user table if not exist.
     */
    public static void createUsersTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL,
            birthdate DATE NOT NULL,
            phone_number VARCHAR(15) NOT NULL,
            location VARCHAR(255) NOT NULL,
            lastReturnBook VARCHAR(255),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create table have users and books they borrow.
     */
    public static void createUserBooksTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS user_books (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            book_id INT NOT NULL,
            borrowed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            must_return_at TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, 
            FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
        );
        """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save last book user return.
     * @param userId
     * @param lastReturnBookInfo
     */
    public static void saveLastReturnBook(int userId, String lastReturnBookInfo) {
        String sql = """
        UPDATE users
        SET lastReturnBook = ?
        WHERE id = ?;
    """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lastReturnBookInfo);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param userId
     * get last book users return.
     * @return
     */
    public static String getLastReturnBook(int userId) {
        String sql = """
        SELECT lastReturnBook
        FROM users
        WHERE id = ?;
    """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("lastReturnBook");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Nếu không có dữ liệu
    }

    /**
     * Search books on database by author and title.
     * @param title
     * @param author
     * @return
     */
    public static Book getBookByTitleAndAuthor(String title, String author) {
        String sql = """
        SELECT * FROM books
        WHERE title = ? AND author = ?;
    """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getString("title"), // Title
                            rs.getString("author"), // Author
                            rs.getString("description"), // Description
                            rs.getString("thumbnail_url"), // Thumbnail URL
                            rs.getString("publisher"), // Publisher
                            rs.getString("published_date"), // Date Published
                            rs.getString("average_rating"), // Rating
                            rs.getInt("view") // View count
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy sách
    }

    /**
     * Search books on database with query.
     * @param query
     * @return
     */
    public static List<Book> searchBooks(String query) {
        String sql = "SELECT * FROM books WHERE title LIKE ?" + "LIMIT 10;";
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1,  "%" + query + "%");
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    books.add(new Book(
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getString("thumbnail_url"),
                            rs.getString("publisher"),
                            rs.getString("published_date"),
                            rs.getString("average_rating"),
                            rs.getInt("view")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Add user to database.
     */
    public static void saveUser(User user) {
        String sql = """
    INSERT INTO users (name, email, password, birthdate, phone_number, location, created_at) 
    VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);
    """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setDate(4, Date.valueOf(user.getBirthDate()));
            pstmt.setString(5, user.getPhone_number());
            pstmt.setString(6, user.getLocation());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check email when sign up.
     * @param email
     * @return
     */
    public static boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get list books user borrow.
     * @param userId
     * @return
     */
    public static List<Book> getBooksForUser(int userId) {
        String sql = """
        SELECT b.title, b.author, b.description, b.thumbnail_url, b.publisher, 
               b.published_date, b.average_rating, b.view
        FROM user_books ub
        JOIN books b ON ub.book_id = b.id
        WHERE ub.user_id = ?;
    """;
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating"),
                        rs.getInt("view")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Add book user borrow to database.
     * @param userId
     * @param bookId
     */
    public static void borrowBook(int userId, int bookId) {
        String sql = """
        INSERT INTO user_books (user_id, book_id, borrowed_at, must_return_at)
        VALUES (?, ?, CURRENT_TIMESTAMP, ADDDATE(CURRENT_TIMESTAMP, INTERVAL 30 DAY))
        ON DUPLICATE KEY UPDATE
        borrowed_at = CURRENT_TIMESTAMP,
        must_return_at = ADDDATE(CURRENT_TIMESTAMP, INTERVAL 30 DAY);                                       
    """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete book user return.
     * @param userId
     * @param bookId
     * @return
     */
    public static boolean returnBook(int userId, int bookId) {
        String sql = "DELETE FROM user_books WHERE user_id = ? AND book_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get book id when have title and author.
     * @param title
     * @param author
     * @return
     */
    public static int getBookId(String title, String author) {
        String sql = "SELECT id FROM books WHERE title = ? AND author = ? LIMIT 1;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * search book in user library.
     * @param userId
     * @param query
     * @return
     */
    public static List<Book> searchBooksForUser(int userId, String query) {
        String sql = """
        SELECT b.id, b.title, b.author, b.description, b.thumbnail_url, 
               b.publisher, b.published_date, b.average_rating, b.view
        FROM books b
        JOIN user_books ub ON b.id = ub.book_id
        WHERE ub.user_id = ? AND 
              (b.title LIKE ? OR b.author LIKE ? OR b.description LIKE ?);
    """;
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            String keyword = "%" + query + "%";
            pstmt.setString(2, keyword);
            pstmt.setString(3, keyword);
            pstmt.setString(4, keyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getString("thumbnail_url"),
                            rs.getString("publisher"),
                            rs.getString("published_date"),
                            rs.getString("average_rating"),
                            rs.getInt("view")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Check if book already borrowed.
     * @param userId
     * @param bookId
     * @return
     */
    public static boolean isBookAlreadyBorrowed(int userId, int bookId) {
        String sql = "SELECT COUNT(*) FROM user_books WHERE user_id = ? AND book_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get new member have create account 1 day.
     * @return
     */
    public static int getNewMember() {
        String sql = "SELECT COUNT(*) FROM users WHERE DATEDIFF(CURRENT_TIMESTAMP, created_at) <= 1;";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * get total book in library.
     * @return
     */
    public static int getTotalBook() {
        String sql = "SELECT COUNT(*) FROM books;";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get user by email.
     * @param email
     * @return
     */
    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String birthdate = rs.getString("birthdate");
                String phoneNumber = rs.getString("phone_number");
                String location = rs.getString("location");
                String password = rs.getString("password");
                User user = new User(name, birthdate, phoneNumber, email, location, password);
                user.setId(id);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get total book users borrow.
     * @return
     */
    public static int getTotalBorrowedBooks() {
        String query = "SELECT COUNT(*) FROM user_books";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return
     * Get total overdue book.
     */
    public static int getTotalOverdueBooks() {
        String query = "SELECT COUNT(*) FROM user_books WHERE must_return_at < CURRENT_DATE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return
     * Get total account.
     */
    public static int getTotalAccounts() {
        String query = "SELECT COUNT(*) FROM users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return
     * Get loan record.
     */
    public static ObservableList<LoanRecord> getLoanRecords() {
        ObservableList<LoanRecord> data = FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT ub.id, u.name AS member_name, b.title AS book_title, b.author AS book_author, " +
                    "ub.borrowed_at, ub.must_return_at FROM user_books ub " +
                    "JOIN users u ON ub.user_id = u.id " +
                    "JOIN books b ON ub.book_id = b.id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String memberName = resultSet.getString("member_name");
                String bookTitle = resultSet.getString("book_title");
                String bookAuthor = resultSet.getString("book_author");
                Date borrowedAt = resultSet.getDate("borrowed_at");
                Date mustReturnAt = resultSet.getDate("must_return_at");
                // Tính toán số ngày quá hạn (nếu có)
                String overdue = "";
                if (mustReturnAt != null) {
                    LocalDate returnDate = mustReturnAt.toLocalDate();
                    LocalDate currentDate = LocalDate.now();
                    long diffInDays = ChronoUnit.DAYS.between(returnDate, currentDate);

                    if (diffInDays > 0) {
                        overdue = diffInDays + " days";
                    }
                }
                // Thêm đối tượng LoanRecord vào ObservableList
                data.add(new LoanRecord(id, memberName, bookTitle, bookAuthor, overdue, mustReturnAt != null ? mustReturnAt.toString() : ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * @param stage
     * @throws Exception
     * Start connection.
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Database Start");
    }

    /**
     * @throws Exception
     * Stop connection.
     */
    @Override
    public void stop() throws Exception {
        DatabaseHelper.shutdown();
        super.stop();
    }
}