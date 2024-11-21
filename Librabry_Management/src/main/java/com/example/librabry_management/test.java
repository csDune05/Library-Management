package com.example.librabry_management;
import com.example.Controller.*;

public class test {
    public static void main(String[] args) {
        Library library = new Library();

        Admin admin = new Admin("Admin", "05/06/2005", "123456789", "23021492@vnu.edu.vn", "Location", "23021492", library);
        User user1 = new User("User1", "06/05/2005", "987654321", "23021500@vnu.edu.vn", "Location", "23021500", library);
        User user2 = new User("User2", "01/02/2005", "135798462", "23021504@vnu.edu.vn", "Location", "23021504", library);

        Book book1 = new Book("Book A", "Java", "Author A", 2024, "Sach day lap trinh Java");
        Book book2 = new Book("Book B", "C++", "Author B", 2023, "Sach day lap trinh C++");

        admin.register(admin);
        user1.register(user1);
        user2.register(user2);

        admin.login(admin);
        user1.login(user1);
        user2.login(user2);

        admin.addBookToLibrary(book1);
        admin.addBookToLibrary(book2);

        user1.addToCollection(book1);
        user1.addToCollection(book2);

        user2.addToCollection(book1);

        admin.removeBookFromLibrary(book1);

        user1.viewCollection();
        user2.viewCollection();
    }
}
