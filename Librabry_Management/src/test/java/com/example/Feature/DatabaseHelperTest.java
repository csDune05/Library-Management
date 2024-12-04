package com.example.Feature;

import com.example.librabry_management.Book;
import com.example.librabry_management.LoanRecord;
import com.example.librabry_management.User;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseHelperTest {

    @BeforeAll
    static void setup() throws Exception {
        DatabaseHelper.configureTestDataSource(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
        DatabaseHelper.createBookTable();
        DatabaseHelper.createUsersTable();
        DatabaseHelper.createUserBooksTable();
    }

    @AfterAll
    static void teardown() {
        DatabaseHelper.shutdown();
    }

    @Test
    void getDefaultBooks() {
        DatabaseHelper.saveBook(new Book("Default Book 1", "Author 1", "Description",
                null, "Publisher", "2024-01-01",
                "4.5", 10), "Test Query");
        DatabaseHelper.saveBook(new Book("Default Book 2", "Author 2", "Description",
                null, "Publisher", "2024-01-01",
                "4.0", 5), "Test Query");
        List<Book> books = DatabaseHelper.getDefaultBooks();
        assertNotNull(books);
        assertTrue(books.size() > 0);
        assertEquals("Default Book 1", books.get(0).getTitle());
    }

    @Test
    void getTopView() {
        Book book1 = new Book("Top View Book 1", "Author A", "Description",
                null, "Publisher", "2024-01-01", "5.0", 100);
        Book book2 = new Book("Top View Book 2", "Author B", "Description",
                null, "Publisher", "2024-01-01", "4.5", 50);
        DatabaseHelper.saveBook(book1, "Query 1");
        DatabaseHelper.saveBook(book2, "Query 2");
        Book topViewBook = DatabaseHelper.getTopView();
        assertNotNull(topViewBook);
        assertEquals("Top View Book 1", topViewBook.getTitle());
        assertEquals(100, topViewBook.getView());
    }

    @Test
    void getTopRateBooks() {
        DatabaseHelper.saveBook(new Book("Top Rated Book", "Author", "Description",
                null, "Publisher", "2024-01-01",
                "5.0", 0), "Test Query");
        List<Book> books = DatabaseHelper.getTopRateBooks();
        assertNotNull(books);
        assertEquals("Top Rated Book", books.get(0).getTitle());
    }

    @Test
    void createBookTable() {
        assertDoesNotThrow(() -> DatabaseHelper.createBookTable());
    }

    @Test
    void createUsersTable() {
        assertDoesNotThrow(() -> DatabaseHelper.createUsersTable());
    }

    @Test
    void incrementBookView() {
        Book book = new Book("Book View Test", "Author", "Description",
                null, "Publisher", "2024-01-01", "4.0", 0);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Book View Test", "Author");
        DatabaseHelper.incrementBookView(bookId);
        Book updatedBook = DatabaseHelper.getBookByTitleAndAuthor("Book View Test", "Author");
        assertEquals(1, updatedBook.getView());
    }

    @Test
    void saveBook() {
        Book book = new Book("Test Title", "Test Author", "Test Description",
                null, "Test Publisher", "2024-01-01", "5.0", 10);
        DatabaseHelper.saveBook(book, "Test Query");
        Book retrievedBook = DatabaseHelper.getBookByTitleAndAuthor("Test Title", "Test Author");
        assertNotNull(retrievedBook);
        assertEquals("Test Title", retrievedBook.getTitle());
        assertEquals("Test Author", retrievedBook.getAuthor());
    }

    @Test
    void getRelatedBooks() {
        DatabaseHelper.saveBook(new Book("Java Programming", "Author A", "A guide to Java",
                null, "Publisher", "2023-01-01", "4.5", 10), "Test Query");
        DatabaseHelper.saveBook(new Book("Advanced Java", "Author B", "Deep dive into Java",
                null, "Publisher", "2023-06-01", "4.8", 5), "Test Query");
        List<Book> relatedBooks = DatabaseHelper.getRelatedBooks("Java Programming", "Author A");
        assertNotNull(relatedBooks);
        assertTrue(relatedBooks.size() > 0);
        assertEquals("Advanced Java", relatedBooks.get(0).getTitle());
    }

    @Test
    void createUserBooksTable() {
        assertDoesNotThrow(() -> DatabaseHelper.createUserBooksTable());
    }

    @Test
    void saveLastReturnBook() {
        User user = new User("John Doe", "2000-01-01", "0123456789",
                "johndoe@example.com", "New York", "password123");
        DatabaseHelper.saveUser(user);
        int userId = DatabaseHelper.getUserByEmail("johndoe@example.com").getId();
        DatabaseHelper.saveLastReturnBook(userId, "Effective Java");
        String lastReturnBook = DatabaseHelper.getLastReturnBook(userId);
        assertEquals("Effective Java", lastReturnBook);
    }


    @Test
    void getLastReturnBook() {
        User user = new User(
                "John Doe",
                "1990-01-01",
                "0123456789",
                "johndoe@example.com",
                "New York",
                "password123"
        );
        DatabaseHelper.saveUser(user);
        int userId = DatabaseHelper.getUserByEmail("johndoe@example.com").getId();
        String lastBookTitle = "Effective Java";
        DatabaseHelper.saveLastReturnBook(userId, lastBookTitle);
        String returnedBook = DatabaseHelper.getLastReturnBook(userId);
        assertNotNull(returnedBook);
        assertEquals(lastBookTitle, returnedBook);
    }


    @Test
    void getBookByTitleAndAuthor() {
        Book book = new Book("Clean Code", "Robert C. Martin",
                "A handbook of Agile software craftsmanship", null,
                "Prentice Hall", "2008-08-11", "5.0", 50);
        DatabaseHelper.saveBook(book, "Test Query");
        Book retrievedBook = DatabaseHelper.getBookByTitleAndAuthor("Clean Code", "Robert C. Martin");
        assertNotNull(retrievedBook);
        assertEquals("Clean Code", retrievedBook.getTitle());
        assertEquals("Robert C. Martin", retrievedBook.getAuthor());
    }

    @Test
    void searchBooks() {
        DatabaseHelper.saveBook(new Book("Java Programming", "Author A"
                , "Description", null, "Publisher",
                "2024-01-01", "5.0", 0), "Query1");
        DatabaseHelper.saveBook(new Book("Python Programming", "Author B"
                , "Description", null, "Publisher",
                "2024-01-01", "4.5", 0), "Query2");
        List<Book> books = DatabaseHelper.searchBooks("Programming");
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().contains("Java")));
        assertTrue(books.stream().anyMatch(b -> b.getTitle().contains("Python")));
    }

    @Test
    void saveUser() {
        User user = new User(
                "Test User",             // name
                "2000-01-01",            // birthdate
                "1234567890",            // phone_number
                "testuser@example.com",  // email
                "Test Location",         // location
                "password123"            // password
        );
        DatabaseHelper.saveUser(user);
        User retrievedUser = DatabaseHelper.getUserByEmail("testuser@example.com");
        assertNotNull(retrievedUser); // Đảm bảo người dùng được lưu
        assertEquals("Test User", retrievedUser.getName());
        assertEquals("2000-01-01", retrievedUser.getBirthDate());
        assertEquals("1234567890", retrievedUser.getPhone_number());
        assertEquals("testuser@example.com", retrievedUser.getEmail());
        assertEquals("Test Location", retrievedUser.getLocation());
        assertEquals("password123", retrievedUser.getPassword());
    }


    @Test
    void isEmailExists() {
        User user = new User("dung", "21/01/2005", "0384327788",
                "dung@gmail.com", "Vietnam", "matkhau");
        DatabaseHelper.saveUser(user);
        boolean exists = DatabaseHelper.isEmailExists("dung@gmail.com");
        assertTrue(exists);
        boolean notExists = DatabaseHelper.isEmailExists("nonexistent@example.com");
        assertFalse(notExists);
    }

    @Test
    void getBooksForUser() {
        User user = new User(
                "Jane Doe",
                "1995-05-10",
                "0987654321",
                "janedoe@example.com",
                "Los Angeles",
                "securepassword"
        );
        DatabaseHelper.saveUser(user);
        Book book = new Book("Test Book", "Test Author", "Description",
                null, "Publisher", "2024-01-01", "5.0", 0);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Test Book", "Test Author");
        DatabaseHelper.borrowBook(user.getId(), bookId);
        List<Book> books = DatabaseHelper.getBooksForUser(user.getId());
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }


    @Test
    void borrowBook() {
        User user = new User(
                "Michael Smith",
                "1988-12-20",
                "0213456789",
                "michael@example.com",
                "Chicago",
                "mypassword"
        );
        DatabaseHelper.saveUser(user);
        Book book = new Book("Another Book", "Author A", "Description",
                null, "Publisher", "2024-01-01", "4.5", 0);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Another Book", "Author A");
        DatabaseHelper.borrowBook(user.getId(), bookId);
        List<Book> books = DatabaseHelper.getBooksForUser(user.getId());
        assertEquals(1, books.size());
        assertEquals("Another Book", books.get(0).getTitle());
    }

    @Test
    void returnBook() {
        User user = new User(
                "Sarah Johnson",
                "2000-03-15",
                "0112233445",
                "sarah@example.com",
                "San Francisco",
                "supersecret"
        );
        DatabaseHelper.saveUser(user);
        Book book = new Book("Returnable Book", "Author B", "Description",
                null, "Publisher", "2024-01-01", "3.5", 0);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Returnable Book", "Author B");
        DatabaseHelper.borrowBook(user.getId(), bookId);
        DatabaseHelper.returnBook(user.getId(), bookId);
        List<Book> books = DatabaseHelper.getBooksForUser(user.getId());
        assertTrue(books.isEmpty());
    }

    @Test
    void getBookId() {
        Book book = new Book("Refactoring", "Martin Fowler",
                "Improving the design of existing code", null,
                "Addison-Wesley", "1999-06-28", "4.7", 20);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Refactoring", "Martin Fowler");
        assertTrue(bookId > 0);
    }

    @Test
    void searchBooksForUser() {
        User user = new User("Jane Smith", "1990-05-10", "0987654321",
                "janesmith@example.com", "Los Angeles", "securepassword");
        DatabaseHelper.saveUser(user);
        int userId = DatabaseHelper.getUserByEmail("janesmith@example.com").getId();
        Book book = new Book("Test Driven Development", "Kent Beck",
                "By Example", null, "Addison-Wesley",
                "2002-11-18", "4.5", 15);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Test Driven Development", "Kent Beck");
        DatabaseHelper.borrowBook(userId, bookId);
        List<Book> books = DatabaseHelper.searchBooksForUser(userId, "Development");
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Test Driven Development", books.get(0).getTitle());
    }


    @Test
    void isBookAlreadyBorrowed() {
        User user = new User(
                "David Lee",
                "1985-07-25",
                "0998765432",
                "david@example.com",
                "Houston",
                "topsecret"
        );
        DatabaseHelper.saveUser(user);
        Book book = new Book("Borrowed Book", "Author C",
                "Description", null, "Publisher",
                "2024-01-01", "4.0", 0);
        DatabaseHelper.saveBook(book, "Test Query");
        int bookId = DatabaseHelper.getBookId("Borrowed Book", "Author C");
        DatabaseHelper.borrowBook(user.getId(), bookId);
        boolean isBorrowed = DatabaseHelper.isBookAlreadyBorrowed(user.getId(), bookId);
        assertTrue(isBorrowed);
    }


    @Test
    void getNewMember() {
        User user = new User("New Member", "2000-01-01",
                "0123456789", "newmember@example.com", "San Francisco", "mypassword");
        DatabaseHelper.saveUser(user);
        int newMembers = DatabaseHelper.getNewMember();
        assertTrue(newMembers > 0);
    }

    @Test
    void getTotalBook() {
        DatabaseHelper.saveBook(new Book("Book A", "Author A", "Description A",
                null, "Publisher A", "2023-01-01",
                "4.5", 10), "Query A");
        int totalBooks = DatabaseHelper.getTotalBook();
        assertTrue(totalBooks > 0);
    }

    @Test
    void getUserByEmail() {
        User user = new User("Alice Johnson", "1985-07-25",
                "0112233445", "alicej@example.com", "Houston", "topsecret");
        DatabaseHelper.saveUser(user);
        User retrievedUser = DatabaseHelper.getUserByEmail("alicej@example.com");
        assertNotNull(retrievedUser);
        assertEquals("Alice Johnson", retrievedUser.getName());
    }

    @Test
    void getTotalBorrowedBooks() {
        int borrowedBooks = DatabaseHelper.getTotalBorrowedBooks();
        assertTrue(borrowedBooks >= 0);
    }

    @Test
    void getTotalOverdueBooks() {
        int overdueBooks = DatabaseHelper.getTotalOverdueBooks();
        assertTrue(overdueBooks >= 0);
    }

    @Test
    void getTotalAccounts() {
        int totalAccounts = DatabaseHelper.getTotalAccounts();
        assertTrue(totalAccounts > 0);
    }

    @Test
    void getLoanRecords() {
        ObservableList<LoanRecord> loanRecords = DatabaseHelper.getLoanRecords();
        assertNotNull(loanRecords);
    }
}