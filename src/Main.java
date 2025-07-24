import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("  Welcome to Library Management  ");
        System.out.println("=================================");
        System.out.println();
        
        System.out.printf("Hello and welcome to our Library Management System!%n");
        System.out.println("This system will help you manage books, users, and transactions.");
        System.out.println();
        
        System.out.println("Features we'll be building:");
        String[] features = {
            "Book Management (Add, Update, Remove, Search)",
            "User Registration and Management", 
            "Borrowing and Returning Books",
            "Transaction Tracking",
            "Library Statistics and Reporting",
            "Graphical User Interface"
        };
        
        for (int i = 0; i < features.length; i++) {
            System.out.printf("%d. %s%n", i + 1, features[i]);
        }
        
        System.out.println();
        System.out.println("Let's start building this step by step!");
        
        System.out.println("\n--- Book Class Demo ---");
        
        Book book1 = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", "Programming", 2008);
        Book book2 = new Book("978-0-201-61622-4", "The Pragmatic Programmer", "Andy Hunt");
        Book book3 = new Book("978-0-321-35668-0", "Effective Java", "Joshua Bloch", "Programming", 2017);
        
        System.out.println("📖 Sample Books in our Library:");
        System.out.println("1. " + book1.getFormattedInfo());
        System.out.println("2. " + book2.getFormattedInfo());
        System.out.println("3. " + book3.getFormattedInfo());
        
        System.out.println("\n🔄 Testing Book Status Changes:");
        System.out.println("Before: " + book1.getTitle() + " is " + 
                          (book1.isAvailable() ? "available" : "not available"));
        
        book1.markAsBorrowed();
        System.out.println("After borrowing: " + book1.getTitle() + " is " + 
                          (book1.isAvailable() ? "available" : "not available"));
        
        book1.markAsAvailable();
        System.out.println("After returning: " + book1.getTitle() + " is " + 
                          (book1.isAvailable() ? "available" : "not available"));
        
        System.out.println("\n--- User Class Demo ---");
        
        User user1 = new User("U001", "Alice Johnson", "alice.johnson@email.com", "123-456-7890");
        User user2 = new User("U002", "Bob Smith", "bob.smith@email.com");
        
        System.out.println("👥 Sample Users:");
        System.out.println("1. " + user1.getFormattedInfo());
        System.out.println("2. " + user2.getFormattedInfo());
        
        System.out.println("\n📚 Testing Book Borrowing:");
        System.out.println("Before borrowing: " + user1.getBorrowingSummary());
        
        boolean borrowed1 = user1.borrowBook(book1.getIsbn());
        boolean borrowed2 = user1.borrowBook(book3.getIsbn());
        
        System.out.println("Borrowed Clean Code: " + borrowed1);
        System.out.println("Borrowed Effective Java: " + borrowed2);
        System.out.println("After borrowing: " + user1.getBorrowingSummary());
        
        System.out.println("\n📋 Transaction Demo:");
        BorrowTransaction transaction1 = new BorrowTransaction("T000001", book1.getIsbn(), user1.getUserId());
        BorrowTransaction transaction2 = new BorrowTransaction("T000002", book3.getIsbn(), user1.getUserId(), 21);
        
        System.out.println("1. " + transaction1.getFormattedInfo());
        System.out.println("2. " + transaction2.getFormattedInfo());
        
        System.out.println("\nDays until due for transaction 1: " + transaction1.getDaysUntilDue());
        System.out.println("Transaction 1 status: " + transaction1.getStatus());
        
        System.out.println("\n--- Librarian Class Demo ---");
        
        Librarian librarian1 = new Librarian("L001", "Sarah Wilson", "sarah.wilson@library.com", 
                                            "EMP2023001", "Reference");
        librarian1.promoteToAdministrator();
        
        System.out.println("👨‍💼 Sample Librarian:");
        System.out.println(librarian1.getFormattedInfo());
        System.out.println("Can add books: " + librarian1.canPerformAction("ADD_BOOKS"));
        System.out.println("Permission count: " + librarian1.getPermissions().size());
        
        System.out.println("\n--- Library System Demo ---");
        
        Library library = new Library("Central Public Library", "123 Main Street, Downtown");
        
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        
        library.registerUser(user1);
        library.registerUser(user2);
        library.registerUser(librarian1);
        
        System.out.println("📚 Library: " + library);
        
        System.out.println("\n🔍 Search Results for 'Java':");
        List<Book> javaBooks = library.searchBooksByTitle("Java");
        javaBooks.forEach(book -> System.out.println("  " + book.getFormattedInfo()));
        
        System.out.println("\n📊 Library Statistics:");
        Map<String, Integer> stats = library.getLibraryStatistics();
        stats.forEach((key, value) -> System.out.printf("%-20s: %d%n", key, value));
        
        System.out.println("\n🎯 Testing Borrowing Process:");
        String result1 = library.borrowBook(book1.getIsbn(), user1.getUserId());
        String result2 = library.borrowBook(book2.getIsbn(), user2.getUserId());
        
        System.out.println("Borrow result 1: " + result1);
        System.out.println("Borrow result 2: " + result2);
        
        System.out.println("\n📈 Updated Statistics After Borrowing:");
        Map<String, Integer> newStats = library.getLibraryStatistics();
        newStats.forEach((key, value) -> System.out.printf("%-20s: %d%n", key, value));
        
        System.out.println("\nAll steps complete! We now have a fully functional Library Management System!");
        System.out.println("Ready to launch the graphical user interface!");
        
        System.out.println("\n🚀 Starting GUI Application...");
        System.out.println("Note: This will open a new window with the complete Library Management System.");
        
        System.out.println("\n--- Database Integration Demo ---");
        
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            
            dbManager.printDatabaseInfo();
            
            boolean connectionTest = dbManager.testConnection();
            System.out.println("Database ready: " + connectionTest);
            
            if (connectionTest) {
                System.out.println("🎉 Database integration successful!");
                
                DatabaseSchema schema = new DatabaseSchema();
                
                if (!schema.tablesExist()) {
                    System.out.println("📋 Creating database schema...");
                    schema.createAllTables();
                } else {
                    System.out.println("📋 Database schema already exists");
                }
                
                schema.printAllTablesInfo();
                schema.printDataSummary();
                
                System.out.println("\n--- Book Data Access Layer Demo ---");
                BookDAO bookDAO = new BookDAO();
                
                if (bookDAO.getTotalBookCount() == 0) {
                    System.out.println("📚 Inserting sample books...");
                    bookDAO.insertSampleBooks();
                } else {
                    System.out.println("📚 Books already exist in database");
                }
                
                System.out.println("\n🔍 Testing Book Search Operations:");
                List<Book> javaBooks = bookDAO.findByTitle("Java");
                System.out.println("Found " + javaBooks.size() + " books with 'Java' in title:");
                javaBooks.forEach(book -> System.out.println("  - " + book.getFormattedInfo()));
                
                List<Book> availableBooks = bookDAO.findAvailableBooks();
                System.out.println("\nAvailable books: " + availableBooks.size());
                
                Map<String, Integer> bookStats = bookDAO.getBookStatistics();
                System.out.println("\n📊 Book Statistics:");
                bookStats.forEach((key, value) -> System.out.printf("%-20s: %d%n", key, value));
                
                System.out.println("\n--- User Data Access Layer Demo ---");
                UserDAO userDAO = new UserDAO();
                
                if (userDAO.getTotalUserCount() == 0) {
                    System.out.println("👥 Inserting sample users...");
                    userDAO.insertSampleUsers();
                } else {
                    System.out.println("👥 Users already exist in database");
                }
                
                List<User> activeUsers = userDAO.findActiveUsers();
                System.out.println("Active users: " + activeUsers.size());
                
                Map<String, Integer> userStats = userDAO.getUserStatistics();
                System.out.println("\n📊 User Statistics:");
                userStats.forEach((key, value) -> System.out.printf("%-20s: %d%n", key, value));
                
                System.out.println("\n--- Transaction Data Access Layer Demo ---");
                TransactionDAO transactionDAO = new TransactionDAO();
                
                Map<String, Integer> transactionStats = transactionDAO.getTransactionStatistics();
                System.out.println("📊 Transaction Statistics:");
                transactionStats.forEach((key, value) -> System.out.printf("%-20s: %d%n", key, value));
                
                System.out.println("\n--- Complete Database Integration Demo ---");
                
                DatabaseLibrary dbLibrary = new DatabaseLibrary("Java Tutorial Library", "Database Campus");
                
                dbLibrary.initializeSampleData();
                
                System.out.println("\n📚 Database Library: " + dbLibrary);
                
                // Test borrowing workflow
                System.out.println("\n🔄 Testing Database Borrowing Workflow:");
                List<Book> availableBooks = dbLibrary.getAvailableBooks();
                List<User> activeUsers = dbLibrary.getActiveUsers();
                
                if (!availableBooks.isEmpty() && !activeUsers.isEmpty()) {
                    Book testBook = availableBooks.get(0);
                    User testUser = activeUsers.get(0);
                    
                    System.out.println("Attempting to borrow: " + testBook.getTitle() + " for " + testUser.getName());
                    String borrowResult = dbLibrary.borrowBook(testBook.getIsbn(), testUser.getUserId());
                    System.out.println("Result: " + borrowResult);
                    
                    if (borrowResult.contains("successfully")) {
                        System.out.println("Attempting to return the same book...");
                        String returnResult = dbLibrary.returnBook(testBook.getIsbn(), testUser.getUserId());
                        System.out.println("Return result: " + returnResult);
                    }
                }
                
                System.out.println("\n📊 Final Database Statistics:");
                Map<String, Integer> finalStats = dbLibrary.getLibraryStatistics();
                finalStats.forEach((key, value) -> System.out.printf("%-25s: %d%n", key, value));
                
                System.out.println("🎉 Complete database integration is now functional!");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Database integration not available: " + e.getMessage());
            System.err.println("💡 To enable database features:");
            System.err.println("   1. Download sqlite-jdbc-3.x.x.jar");
            System.err.println("   2. Add to classpath: java -cp \".:sqlite-jdbc-3.x.x.jar\" Main");
            System.err.println("   3. Or add to IDE project libraries");
            System.out.println("\n📱 Continuing with in-memory storage for now...");
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                new LibraryManagementGUI().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting GUI: " + e.getMessage());
            }
        });
    }
}