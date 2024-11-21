package com.example.librabry_management;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3310/My_Library";
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
                    title TEXT NOT NULL,
                    author TEXT,
                    description TEXT,
                    thumbnail_url TEXT,
                    search_query TEXT,
                    UNIQUE (title(100), author(100))
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
        String sql = "INSERT OR IGNORE INTO books (title, author, description, thumbnail_url, search_query) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setString(4, book.getThumbnailUrl());
            pstmt.setString(5, query);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tìm kiếm sách trong cơ sở dữ liệu
    public static List<BookTest> searchBooks(String query) {
        String sql = "SELECT * FROM books WHERE search_query LIKE ?";
        List<BookTest> books = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
