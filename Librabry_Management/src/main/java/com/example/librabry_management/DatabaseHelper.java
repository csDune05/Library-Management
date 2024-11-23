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

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void stop() throws Exception {
        DatabaseHelper.shutdown(); // Đóng nguồn kết nối khi ứng dụng dừng
        super.stop();
    }


}
