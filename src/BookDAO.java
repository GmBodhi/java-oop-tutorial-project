import java.sql.*;
import java.util.*;

public class BookDAO {
    private DatabaseManager dbManager;
    
    public BookDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean createBook(Book book) {
        String sql = """
            INSERT INTO books (isbn, title, author, genre, publication_year, status, updated_date)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;
        
        try {
            dbManager.executeUpdate(sql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPublicationYear(),
                book.getStatus().toString()
            );
            dbManager.commit();
            System.out.println("✅ Book created in database: " + book.getTitle());
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            
            if (e.getErrorCode() == 19) { // SQLite constraint violation
                System.err.println("❌ Book with ISBN " + book.getIsbn() + " already exists");
            } else {
                System.err.println("❌ Failed to create book: " + e.getMessage());
            }
            return false;
        }
    }
    
    public Book findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (ResultSet rs = dbManager.executeQuery(sql, isbn)) {
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding book by ISBN: " + e.getMessage());
        }
        return null;
    }
    
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all books: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, "%" + title + "%")) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching books by title: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> findByAuthor(String author) {
        String sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, "%" + author + "%")) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching books by author: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> findByGenre(String genre) {
        String sql = "SELECT * FROM books WHERE genre LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, "%" + genre + "%")) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching books by genre: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> findByStatus(Book.BookStatus status) {
        String sql = "SELECT * FROM books WHERE status = ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, status.toString())) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching books by status: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> findAvailableBooks() {
        return findByStatus(Book.BookStatus.AVAILABLE);
    }
    
    public boolean updateBook(Book book) {
        String sql = """
            UPDATE books 
            SET title = ?, author = ?, genre = ?, publication_year = ?, 
                status = ?, updated_date = CURRENT_TIMESTAMP
            WHERE isbn = ?
            """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setInt(4, book.getPublicationYear());
            stmt.setString(5, book.getStatus().toString());
            stmt.setString(6, book.getIsbn());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                dbManager.commit();
                System.out.println("✅ Book updated: " + book.getTitle());
                return true;
            } else {
                System.err.println("❌ No book found with ISBN: " + book.getIsbn());
                return false;
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to update book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateBookStatus(String isbn, Book.BookStatus status) {
        String sql = "UPDATE books SET status = ?, updated_date = CURRENT_TIMESTAMP WHERE isbn = ?";
        
        try {
            dbManager.executeUpdate(sql, status.toString(), isbn);
            dbManager.commit();
            System.out.println("✅ Book status updated to " + status + " for ISBN: " + isbn);
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to update book status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBook(String isbn) {
        // First check if book is currently borrowed
        Book book = findByIsbn(isbn);
        if (book != null && book.getStatus() == Book.BookStatus.BORROWED) {
            System.err.println("❌ Cannot delete book - currently borrowed");
            return false;
        }
        
        String sql = "DELETE FROM books WHERE isbn = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, isbn);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                dbManager.commit();
                System.out.println("✅ Book deleted: " + isbn);
                return true;
            } else {
                System.err.println("❌ No book found with ISBN: " + isbn);
                return false;
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to delete book: " + e.getMessage());
            return false;
        }
    }
    
    public Map<String, Integer> getBookStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            // Total books
            String totalSql = "SELECT COUNT(*) as count FROM books";
            try (ResultSet rs = dbManager.executeQuery(totalSql)) {
                if (rs.next()) {
                    stats.put("Total Books", rs.getInt("count"));
                }
            }
            
            // Books by status
            String statusSql = "SELECT status, COUNT(*) as count FROM books GROUP BY status";
            try (ResultSet rs = dbManager.executeQuery(statusSql)) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    stats.put(status + " Books", count);
                }
            }
            
            // Books by genre (top 5)
            String genreSql = """
                SELECT genre, COUNT(*) as count 
                FROM books 
                GROUP BY genre 
                ORDER BY count DESC 
                LIMIT 5
                """;
            try (ResultSet rs = dbManager.executeQuery(genreSql)) {
                while (rs.next()) {
                    String genre = rs.getString("genre");
                    int count = rs.getInt("count");
                    stats.put("Genre: " + genre, count);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error calculating book statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    public List<Book> getRecentlyAddedBooks(int limit) {
        String sql = "SELECT * FROM books ORDER BY created_date DESC LIMIT ?";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, limit)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting recently added books: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> searchBooks(String searchTerm) {
        String sql = """
            SELECT * FROM books 
            WHERE title LIKE ? OR author LIKE ? OR genre LIKE ? OR isbn LIKE ?
            ORDER BY 
                CASE 
                    WHEN title LIKE ? THEN 1
                    WHEN author LIKE ? THEN 2
                    WHEN genre LIKE ? THEN 3
                    ELSE 4
                END,
                title
            """;
        
        List<Book> books = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        String exactPattern = searchTerm + "%";
        
        try (ResultSet rs = dbManager.executeQuery(sql, 
                searchPattern, searchPattern, searchPattern, searchPattern,
                exactPattern, exactPattern, exactPattern)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error performing global book search: " + e.getMessage());
        }
        
        return books;
    }
    
    public boolean bookExists(String isbn) {
        String sql = "SELECT 1 FROM books WHERE isbn = ? LIMIT 1";
        
        try (ResultSet rs = dbManager.executeQuery(sql, isbn)) {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error checking book existence: " + e.getMessage());
            return false;
        }
    }
    
    public int getTotalBookCount() {
        String sql = "SELECT COUNT(*) as count FROM books";
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total book count: " + e.getMessage());
        }
        return 0;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String genre = rs.getString("genre");
        int year = rs.getInt("publication_year");
        String statusStr = rs.getString("status");
        
        Book book = new Book(isbn, title, author, genre, year);
        book.setStatus(Book.BookStatus.valueOf(statusStr));
        
        return book;
    }
    
    public void insertSampleBooks() {
        Book[] sampleBooks = {
            new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", "Programming", 2008),
            new Book("978-0-201-61622-4", "The Pragmatic Programmer", "Andy Hunt", "Programming", 1999),
            new Book("978-0-321-35668-0", "Effective Java", "Joshua Bloch", "Programming", 2017),
            new Book("978-0-596-52068-7", "Head First Design Patterns", "Eric Freeman", "Programming", 2004),
            new Book("978-0-134-68514-4", "Java: The Complete Reference", "Herbert Schildt", "Programming", 2020),
            new Book("978-0-13-235088-4", "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", 2009),
            new Book("978-0-307-88789-6", "The Lean Startup", "Eric Ries", "Business", 2011),
            new Book("978-0-7432-7357-1", "The 7 Habits of Highly Effective People", "Stephen Covey", "Self-Help", 2004)
        };
        
        int insertedCount = 0;
        for (Book book : sampleBooks) {
            if (!bookExists(book.getIsbn()) && createBook(book)) {
                insertedCount++;
            }
        }
        
        System.out.println("📚 Inserted " + insertedCount + " sample books");
    }
}