package com.example.librabry_management;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.Controller.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:Your_Port/My_Library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Your_Password";

    // Kết nối cơ sở dữ liệu
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
    public static void saveBook(BookTest book, String query) {
        String sql = """
            INSERT INTO books (title, author, description, thumbnail_url)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                description = VALUES(description),
                thumbnail_url = VALUES(thumbnail_url);
            """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setString(4, book.getThumbnailUrl());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Tìm kiếm sách trong cơ sở dữ liệu
    public static List<BookTest> searchBooks(String query) {
        // Cập nhật câu lệnh SQL để tìm theo cột title
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        List<BookTest> books = new ArrayList<>();
        try (Connection conn = connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Tìm kiếm tiêu đề có chứa chuỗi query
                pstmt.setString(1, "%" + query + "%");
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    books.add(new BookTest(
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getString("thumbnail_url")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

}
