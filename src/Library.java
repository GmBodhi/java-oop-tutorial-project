import java.util.*;
import java.util.stream.Collectors;

public class Library {
    private HashMap<String, Book> books;
    private HashMap<String, User> users;
    private HashMap<String, BorrowTransaction> transactions;
    private int transactionCounter;
    private String libraryName;
    private String address;
    
    public Library(String libraryName, String address) {
        this.libraryName = (libraryName != null) ? libraryName : "Community Library";
        this.address = (address != null) ? address : "Unknown Location";
        this.books = new HashMap<>();
        this.users = new HashMap<>();
        this.transactions = new HashMap<>();
        this.transactionCounter = 1;
    }
    
    public Library() {
        this("Community Library", "Main Street");
    }
    
    public String getLibraryName() {
        return libraryName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public boolean addBook(Book book) {
        if (book == null) return false;
        if (books.containsKey(book.getIsbn())) {
            return false;
        }
        books.put(book.getIsbn(), book);
        return true;
    }
    
    public boolean removeBook(String isbn) {
        Book book = books.get(isbn);
        if (book == null) return false;
        
        if (book.getStatus() == Book.BookStatus.BORROWED) {
            return false;
        }
        
        books.remove(isbn);
        return true;
    }
    
    public boolean updateBook(String isbn, String title, String author, String genre) {
        Book book = books.get(isbn);
        if (book == null) return false;
        
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        return true;
    }
    
    public Book getBook(String isbn) {
        return books.get(isbn);
    }
    
    public List<Book> searchBooksByTitle(String title) {
        return books.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Book> searchBooksByGenre(String genre) {
        return books.values().stream()
                .filter(book -> book.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Book> searchBooksByIsbn(String isbn) {
        return books.values().stream()
                .filter(book -> book.getIsbn().toLowerCase().contains(isbn.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
    
    public boolean registerUser(User user) {
        if (user == null) return false;
        if (users.containsKey(user.getUserId())) {
            return false;
        }
        users.put(user.getUserId(), user);
        return true;
    }
    
    public User getUser(String userId) {
        return users.get(userId);
    }
    
    public List<User> searchUsersByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<User> searchUsersById(String userId) {
        return users.values().stream()
                .filter(user -> user.getUserId().toLowerCase().contains(userId.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    public String borrowBook(String isbn, String userId) {
        Book book = books.get(isbn);
        User user = users.get(userId);
        
        if (book == null) return "Book not found";
        if (user == null) return "User not found";
        if (!book.isAvailable()) return "Book is not available";
        if (!user.canBorrowMoreBooks()) return "User has reached maximum book limit";
        
        String transactionId = "T" + String.format("%06d", transactionCounter++);
        BorrowTransaction transaction = new BorrowTransaction(transactionId, isbn, userId);
        
        book.setStatus(Book.BookStatus.BORROWED);
        user.borrowBook(isbn);
        transactions.put(transactionId, transaction);
        
        return "Book borrowed successfully. Transaction ID: " + transactionId;
    }
    
    public String returnBook(String isbn, String userId) {
        Book book = books.get(isbn);
        User user = users.get(userId);
        
        if (book == null) return "Book not found";
        if (user == null) return "User not found";
        if (book.isAvailable()) return "Book is not currently borrowed";
        if (!user.getBorrowedBooks().contains(isbn)) return "User has not borrowed this book";
        
        BorrowTransaction transaction = transactions.values().stream()
                .filter(t -> t.getBookIsbn().equals(isbn) && 
                           t.getUserId().equals(userId) && 
                           !t.isReturned())
                .findFirst()
                .orElse(null);
        
        if (transaction == null) return "No active transaction found";
        
        book.setStatus(Book.BookStatus.AVAILABLE);
        user.returnBook(isbn);
        transaction.markAsReturned();
        
        return "Book returned successfully";
    }
    
    public List<BorrowTransaction> getAllTransactions() {
        return new ArrayList<>(transactions.values());
    }
    
    public List<BorrowTransaction> getActiveTransactions() {
        return transactions.values().stream()
                .filter(t -> !t.isReturned())
                .collect(Collectors.toList());
    }
    
    public List<BorrowTransaction> getOverdueTransactions() {
        return transactions.values().stream()
                .filter(t -> !t.isReturned() && t.isOverdue())
                .collect(Collectors.toList());
    }
    
    public Map<String, Integer> getLibraryStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total Books", books.size());
        stats.put("Available Books", (int) books.values().stream().filter(Book::isAvailable).count());
        stats.put("Borrowed Books", (int) books.values().stream().filter(b -> !b.isAvailable()).count());
        stats.put("Total Users", users.size());
        stats.put("Active Transactions", getActiveTransactions().size());
        stats.put("Overdue Transactions", getOverdueTransactions().size());
        return stats;
    }
    
    public void generateLibraryReport() {
        System.out.println("=".repeat(50));
        System.out.println("📚 " + libraryName + " - Library Report");
        System.out.println("📍 " + address);
        System.out.println("=".repeat(50));
        
        Map<String, Integer> stats = getLibraryStatistics();
        stats.forEach((key, value) -> 
            System.out.printf("%-20s: %d%n", key, value));
        
        System.out.println("\n📊 Collection by Genre:");
        Map<String, Long> genreCount = books.values().stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
        genreCount.forEach((genre, count) -> 
            System.out.printf("%-15s: %d books%n", genre, count));
        
        if (!getOverdueTransactions().isEmpty()) {
            System.out.println("\n⚠️  Overdue Items:");
            getOverdueTransactions().forEach(t -> 
                System.out.println("  " + t.getFormattedInfo()));
        }
        
        System.out.println("=".repeat(50));
    }
    
    @Override
    public String toString() {
        return String.format("Library{Name='%s', Books=%d, Users=%d, Active Transactions=%d}",
                libraryName, books.size(), users.size(), getActiveTransactions().size());
    }
}