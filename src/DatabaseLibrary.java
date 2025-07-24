import java.util.*;

public class DatabaseLibrary {
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private String libraryName;
    private String address;
    private int transactionCounter;
    
    public DatabaseLibrary(String libraryName, String address) {
        this.libraryName = (libraryName != null) ? libraryName : "Community Library";
        this.address = (address != null) ? address : "Unknown Location";
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
        this.transactionCounter = getNextTransactionCounter();
    }
    
    public DatabaseLibrary() {
        this("Community Library", "Main Street");
    }
    
    private int getNextTransactionCounter() {
        // Get the highest transaction number from database
        List<BorrowTransaction> allTransactions = transactionDAO.findAll();
        int maxCounter = 0;
        
        for (BorrowTransaction transaction : allTransactions) {
            String id = transaction.getTransactionId();
            if (id.startsWith("T")) {
                try {
                    int counter = Integer.parseInt(id.substring(1));
                    maxCounter = Math.max(maxCounter, counter);
                } catch (NumberFormatException e) {
                    // Ignore invalid transaction IDs
                }
            }
        }
        
        return maxCounter + 1;
    }
    
    public String getLibraryName() {
        return libraryName;
    }
    
    public String getAddress() {
        return address;
    }
    
    // Book Operations
    public boolean addBook(Book book) {
        return bookDAO.createBook(book);
    }
    
    public boolean removeBook(String isbn) {
        return bookDAO.deleteBook(isbn);
    }
    
    public boolean updateBook(String isbn, String title, String author, String genre) {
        Book book = bookDAO.findByIsbn(isbn);
        if (book == null) return false;
        
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        
        return bookDAO.updateBook(book);
    }
    
    public Book getBook(String isbn) {
        return bookDAO.findByIsbn(isbn);
    }
    
    public List<Book> searchBooksByTitle(String title) {
        return bookDAO.findByTitle(title);
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        return bookDAO.findByAuthor(author);
    }
    
    public List<Book> searchBooksByGenre(String genre) {
        return bookDAO.findByGenre(genre);
    }
    
    public List<Book> searchBooksByIsbn(String isbn) {
        List<Book> results = new ArrayList<>();
        if (isbn != null && !isbn.trim().isEmpty()) {
            Book book = bookDAO.findByIsbn(isbn);
            if (book != null) {
                results.add(book);
            }
        }
        return results;
    }
    
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    
    public List<Book> getAvailableBooks() {
        return bookDAO.findAvailableBooks();
    }
    
    // User Operations
    public boolean registerUser(User user) {
        return userDAO.createUser(user);
    }
    
    public User getUser(String userId) {
        return userDAO.findById(userId);
    }
    
    public List<User> searchUsersByName(String name) {
        return userDAO.findByName(name);
    }
    
    public List<User> searchUsersById(String userId) {
        List<User> results = new ArrayList<>();
        if (userId != null && !userId.trim().isEmpty()) {
            User user = userDAO.findById(userId);
            if (user != null) {
                results.add(user);
            }
        }
        return results;
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    // Transaction Operations
    public String borrowBook(String isbn, String userId) {
        try {
            Book book = bookDAO.findByIsbn(isbn);
            User user = userDAO.findById(userId);
            
            if (book == null) return "Book not found";
            if (user == null) return "User not found";
            if (!book.isAvailable()) return "Book is not available";
            if (!user.canBorrowMoreBooks()) return "User has reached maximum book limit";
            
            // Check if there's already an active transaction for this book and user
            BorrowTransaction existingTransaction = transactionDAO.findActiveTransactionByBookAndUser(isbn, userId);
            if (existingTransaction != null) {
                return "User already has an active borrowing for this book";
            }
            
            // Create transaction
            String transactionId = "T" + String.format("%06d", transactionCounter++);
            BorrowTransaction transaction = new BorrowTransaction(transactionId, isbn, userId);
            
            // Begin database transaction
            DatabaseManager.getInstance().beginTransaction();
            
            // Update book status
            book.setStatus(Book.BookStatus.BORROWED);
            if (!bookDAO.updateBookStatus(isbn, Book.BookStatus.BORROWED)) {
                DatabaseManager.getInstance().rollback();
                return "Failed to update book status";
            }
            
            // Update user's borrowed books count (simplified approach)
            User updatedUser = userDAO.findById(userId);
            if (!userDAO.updateBorrowedBooksCount(userId, updatedUser.getBorrowedBooksCount() + 1)) {
                DatabaseManager.getInstance().rollback();
                return "Failed to update user borrowing count";
            }
            
            // Create transaction record
            if (!transactionDAO.createTransaction(transaction)) {
                DatabaseManager.getInstance().rollback();
                return "Failed to create transaction record";
            }
            
            DatabaseManager.getInstance().commit();
            DatabaseManager.getInstance().endTransaction();
            
            return "Book borrowed successfully. Transaction ID: " + transactionId;
            
        } catch (Exception e) {
            try {
                DatabaseManager.getInstance().rollback();
            } catch (Exception rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            return "Error during borrowing: " + e.getMessage();
        }
    }
    
    public String returnBook(String isbn, String userId) {
        try {
            Book book = bookDAO.findByIsbn(isbn);
            User user = userDAO.findById(userId);
            
            if (book == null) return "Book not found";
            if (user == null) return "User not found";
            if (book.isAvailable()) return "Book is not currently borrowed";
            
            // Find active transaction
            BorrowTransaction transaction = transactionDAO.findActiveTransactionByBookAndUser(isbn, userId);
            if (transaction == null) return "No active transaction found";
            
            // Begin database transaction
            DatabaseManager.getInstance().beginTransaction();
            
            // Update book status
            book.setStatus(Book.BookStatus.AVAILABLE);
            if (!bookDAO.updateBookStatus(isbn, Book.BookStatus.AVAILABLE)) {
                DatabaseManager.getInstance().rollback();
                return "Failed to update book status";
            }
            
            // Update user's borrowed books count
            User updatedUser = userDAO.findById(userId);
            if (!userDAO.updateBorrowedBooksCount(userId, Math.max(0, updatedUser.getBorrowedBooksCount() - 1))) {
                DatabaseManager.getInstance().rollback();
                return "Failed to update user borrowing count";
            }
            
            // Mark transaction as returned and calculate fine if overdue
            transaction.markAsReturned();
            if (!transactionDAO.updateTransaction(transaction)) {
                DatabaseManager.getInstance().rollback();
                return "Failed to update transaction record";
            }
            
            DatabaseManager.getInstance().commit();
            DatabaseManager.getInstance().endTransaction();
            
            String message = "Book returned successfully";
            if (transaction.getFineAmount() > 0) {
                message += String.format(". Fine: $%.2f", transaction.getFineAmount());
            }
            
            return message;
            
        } catch (Exception e) {
            try {
                DatabaseManager.getInstance().rollback();
            } catch (Exception rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            return "Error during return: " + e.getMessage();
        }
    }
    
    public List<BorrowTransaction> getAllTransactions() {
        return transactionDAO.findAll();
    }
    
    public List<BorrowTransaction> getActiveTransactions() {
        return transactionDAO.findActiveTransactions();
    }
    
    public List<BorrowTransaction> getOverdueTransactions() {
        return transactionDAO.findOverdueTransactions();
    }
    
    public Map<String, Integer> getLibraryStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        // Combine statistics from all DAOs
        Map<String, Integer> bookStats = bookDAO.getBookStatistics();
        Map<String, Integer> userStats = userDAO.getUserStatistics();
        Map<String, Integer> transactionStats = transactionDAO.getTransactionStatistics();
        
        stats.putAll(bookStats);
        stats.putAll(userStats);
        stats.putAll(transactionStats);
        
        return stats;
    }
    
    public void generateLibraryReport() {
        System.out.println("=".repeat(50));
        System.out.println("📚 " + libraryName + " - Database Library Report");
        System.out.println("📍 " + address);
        System.out.println("=".repeat(50));
        
        Map<String, Integer> stats = getLibraryStatistics();
        stats.forEach((key, value) -> 
            System.out.printf("%-25s: %d%n", key, value));
        
        System.out.println("\n📊 Collection by Genre:");
        Map<String, Integer> bookStats = bookDAO.getBookStatistics();
        bookStats.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("Genre:"))
            .forEach(entry -> 
                System.out.printf("%-20s: %d books%n", 
                    entry.getKey().substring(7), entry.getValue()));
        
        List<BorrowTransaction> overdueTransactions = getOverdueTransactions();
        if (!overdueTransactions.isEmpty()) {
            System.out.println("\n⚠️  Overdue Items:");
            overdueTransactions.forEach(t -> 
                System.out.println("  " + t.getFormattedInfo()));
        }
        
        System.out.println("=".repeat(50));
    }
    
    public List<Book> searchBooks(String searchTerm) {
        return bookDAO.searchBooks(searchTerm);
    }
    
    public List<User> getActiveUsers() {
        return userDAO.findActiveUsers();
    }
    
    public List<BorrowTransaction> getTransactionsByUser(String userId) {
        return transactionDAO.findByUserId(userId);
    }
    
    public List<BorrowTransaction> getTransactionsByBook(String isbn) {
        return transactionDAO.findByBookIsbn(isbn);
    }
    
    public boolean updateUserStatus(String userId, boolean isActive) {
        User user = userDAO.findById(userId);
        if (user == null) return false;
        
        user.setActive(isActive);
        return userDAO.updateUser(user);
    }
    
    public void initializeSampleData() {
        System.out.println("🔄 Initializing sample data...");
        
        // Initialize sample books
        if (bookDAO.getTotalBookCount() == 0) {
            bookDAO.insertSampleBooks();
        }
        
        // Initialize sample users
        if (userDAO.getTotalUserCount() == 0) {
            userDAO.insertSampleUsers();
        }
        
        System.out.println("✅ Sample data initialization complete");
    }
    
    @Override
    public String toString() {
        return String.format("DatabaseLibrary{Name='%s', Books=%d, Users=%d, Active Transactions=%d}",
                libraryName, 
                bookDAO.getTotalBookCount(), 
                userDAO.getTotalUserCount(), 
                getActiveTransactions().size());
    }
}