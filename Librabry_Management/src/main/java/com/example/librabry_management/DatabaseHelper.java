package com.example.librabry_management;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.example.Controller.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class DatabaseHelper extends Application {

    private static HikariDataSource dataSource;

    static {
        // Cấu hình HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3310/My_Library"); // URL kết nối
        config.setUsername("root"); // Tên người dùng
        config.setPassword("#Matkhau01234"); // Mật khẩu
        config.setMaximumPoolSize(20); // Số kết nối tối đa
        config.setMinimumIdle(10); // Số kết nối tối thiểu
        config.setIdleTimeout(600000); // Thời gian idle tối đa (10 phút)
        config.setMaxLifetime(1800000); // Thời gian sống tối đa của kết nối (30 phút)
        config.setConnectionTimeout(0); // Thời gian chờ kết nối (0 giây)
        config.setLeakDetectionThreshold(2000); // Phát hiện rò rỉ kết nối trong 2 giây
        config.addDataSourceProperty("housekeepingPeriodMs", "60000");

        dataSource = new HikariDataSource(config); // Tạo nguồn kết nối

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1")) {
            pstmt.execute();
            System.out.println("HikariCP warm-up completed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm kết nối với cơ sở dữ liệu
    public static Connection connect() throws SQLException {
        return dataSource.getConnection(); // Lấy kết nối từ pool
    }

    // Đóng dataSource khi ứng dụng dừng
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Get 10 books when open Books.
     */
    public static List<Book> getDefaultBooks() {
        String sql = "SELECT * FROM books LIMIT 10;";
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
                        rs.getString("average_rating")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Tạo bảng nếu chưa tồn tại
    public static void createTable() {
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
                  UNIQUE (title, author)
              );
            """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Lưu sách vào cơ sở dữ liệu
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

    // Tạo bảng users
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
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo bảng user_books
    public static void createUserBooksTable() {
        // Khóa ngoài cho id
        String sql = """
        CREATE TABLE IF NOT EXISTS user_books (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            book_id INT NOT NULL,
            borrowed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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

    // Tìm kiếm sách trong cơ sở dữ liệu
    public static List<Book> searchBooks(String query) {
        // Cập nhật câu lệnh SQL để tìm theo cột title
        String sql = "SELECT * FROM books WHERE title LIKE ?" + "LIMIT 10;";
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Tìm kiếm tiêu đề có chứa chuỗi query
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
                            rs.getString("average_rating")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Thêm user mới vào database;
    public static void saveUser(String name, String email, String password, String birthdate, String phoneNumber, String location) {
        String sql = """
        INSERT INTO users (name, email, password, birthdate, phone_number, location, created_at) 
        VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);
        """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setDate(4, Date.valueOf(birthdate));
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, location);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean doesUserExist(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có ít nhất 1 bản ghi thì user tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void addBookToUserLibrary(int userId, Book book) {
        if (!doesUserExist(userId)) {
            throw new IllegalArgumentException("User ID does not exist in the users table.");
        }

        String insertBookSql = """
        INSERT INTO books (title, author, description, thumbnail_url, publisher, published_date, average_rating)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE 
            description = VALUES(description),
            thumbnail_url = VALUES(thumbnail_url),
            publisher = VALUES(publisher),
            published_date = VALUES(published_date),
            average_rating = VALUES(average_rating);
        """;

        String insertUserBookSql = """
        INSERT INTO user_books (user_id, book_id)
        VALUES (?, (SELECT id FROM books WHERE title = ? AND author = ?))
        ON DUPLICATE KEY UPDATE borrowed_at = CURRENT_TIMESTAMP;
        """;

        try (Connection conn = connect()) {
            // Thêm sách vào bảng books nếu chưa tồn tại
            try (PreparedStatement pstmt = conn.prepareStatement(insertBookSql)) {
                pstmt.setString(1, book.getTitle());
                pstmt.setString(2, book.getAuthor());
                pstmt.setString(3, book.getDescription());
                pstmt.setString(4, book.getThumbnailUrl());
                pstmt.setString(5, book.getPublisher());
                pstmt.setString(6, book.getDate());
                pstmt.setString(7, book.getRating());
                pstmt.executeUpdate();
            }

            // Thêm bản ghi vào bảng user_books
            try (PreparedStatement pstmt = conn.prepareStatement(insertUserBookSql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getAuthor());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Book> getBooksForUser(int userId) {
        String sql = """
        SELECT b.title, b.author, b.description, b.thumbnail_url, b.publisher, 
               b.published_date, b.average_rating
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
                        rs.getString("average_rating")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static void borrowBook(int userId, int bookId) {
        String sql = """
        INSERT INTO user_books (user_id, book_id) VALUES (?, ?)
        ON DUPLICATE KEY UPDATE borrowed_at = CURRENT_TIMESTAMP;
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

    public static boolean isBookAlreadyBorrowed(int userId, int bookId) {
        String sql = "SELECT COUNT(*) FROM user_books WHERE user_id = ? AND book_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu sách đã được mượn
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Mặc định trả về false nếu có lỗi xảy ra
    }



    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void stop() throws Exception {
        DatabaseHelper.shutdown(); // Đóng nguồn kết nối khi ứng dụng dừng
        super.stop();
    }
}
