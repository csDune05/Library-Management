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
        config.setJdbcUrl("jdbc:mysql://localhost:3306/My_Library"); // URL kết nối
        config.setUsername("root"); // Tên người dùng
        config.setPassword("dovandung05062005@mysql"); // Mật khẩu
        config.setMaximumPoolSize(20); // Số kết nối tối đa
        config.setMinimumIdle(10); // Số kết nối tối thiểu
        config.setIdleTimeout(600000); // Thời gian idle tối đa (10 phút)
        config.setMaxLifetime(1800000); // Thời gian sống tối đa của kết nối (30 phút)
        config.setConnectionTimeout(0); // Thời gian chờ kết nối (0 giây)
        config.setLeakDetectionThreshold(5000); // Phát hiện rò rỉ kết nối trong 5 giây
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

    public static boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu có email tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public static List<Book> getBorrowedBooks(User user) {
        if (user == null) {
            System.err.println("Người dùng không hợp lệ.");
            return new ArrayList<>();
        }

        String sql = """
        SELECT b.title, b.author, b.description, b.thumbnail_url, b.publisher, 
               b.published_date, b.average_rating, ub.borrowed_at, ub.must_return_at
        FROM user_books ub
        JOIN books b ON ub.book_id = b.id
        WHERE ub.user_id = ?;
    """;

        List<Book> borrowedBooks = new ArrayList<>();

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getId()); // Lấy ID của User từ phương thức `getId`
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Tạo đối tượng Book với thông tin từ cơ sở dữ liệu
                Book book = new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("average_rating")
                );

                // Gán thêm thông tin borrowed_at và must_return_at
                book.setBorrowAt(rs.getTimestamp("borrowed_at"));
                book.setMustReturnAt(rs.getTimestamp("must_return_at"));

                borrowedBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowedBooks;
    }

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
        return -1; // Trả về -1 nếu không tìm thấy sách
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

    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Lấy các thông tin từ ResultSet
                int id = rs.getInt("id"); // Lấy ID từ cơ sở dữ liệu
                String name = rs.getString("name");
                String birthdate = rs.getString("birthdate");
                String phoneNumber = rs.getString("phone_number");
                String location = rs.getString("location");
                String password = rs.getString("password");

                // Gán ID khi tạo User
                User user = new User(name, birthdate, phoneNumber, email, location, password);
                user.setId(id); // Gán ID cho User
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // lấy thông số cho chart ở
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

    public static int getTotalOverdueBooks() {
        String query = "SELECT COUNT(*) FROM user_books WHERE return_date < CURRENT_DATE";
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

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void stop() throws Exception {
        DatabaseHelper.shutdown(); // Đóng nguồn kết nối khi ứng dụng dừng
        super.stop();
    }
}