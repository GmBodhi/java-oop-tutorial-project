public class Book {
    public enum BookStatus {
        AVAILABLE,
        BORROWED,
        RESERVED,
        MAINTENANCE
    }
    
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private BookStatus status;
    private int publicationYear;
    
    public Book(String isbn, String title, String author, String genre, int publicationYear) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        
        this.isbn = isbn.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.genre = (genre != null) ? genre.trim() : "Unknown";
        this.publicationYear = publicationYear;
        this.status = BookStatus.AVAILABLE;
    }
    
    public Book(String isbn, String title, String author) {
        this(isbn, title, author, "Unknown", 0);
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title.trim();
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        this.author = author.trim();
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = (genre != null) ? genre.trim() : "Unknown";
    }
    
    public int getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
    
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }
    
    public boolean isBorrowed() {
        return status == BookStatus.BORROWED;
    }
    
    public void markAsAvailable() {
        this.status = BookStatus.AVAILABLE;
    }
    
    public void markAsBorrowed() {
        this.status = BookStatus.BORROWED;
    }
    
    public void markAsReserved() {
        this.status = BookStatus.RESERVED;
    }
    
    public void markAsInMaintenance() {
        this.status = BookStatus.MAINTENANCE;
    }
    
    @Override
    public String toString() {
        return String.format("Book{ISBN='%s', Title='%s', Author='%s', Genre='%s', Year=%d, Status=%s}",
                isbn, title, author, genre, publicationYear, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }
    
    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
    
    public String getFormattedInfo() {
        return String.format("📚 %s by %s (%s) - %s [%s]", 
                title, author, 
                publicationYear > 0 ? String.valueOf(publicationYear) : "Unknown Year",
                genre, status.toString().toLowerCase());
    }
}