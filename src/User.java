import java.util.*;

public class User {
    public static final int MAX_BOOKS_LIMIT = 5;
    
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Date registrationDate;
    private List<String> borrowedBooks;
    private boolean isActive;
    
    public User(String userId, String name, String email) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        this.userId = userId.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.phoneNumber = "";
        this.registrationDate = new Date();
        this.borrowedBooks = new ArrayList<>();
        this.isActive = true;
    }
    
    public User(String userId, String name, String email, String phoneNumber) {
        this(userId, name, email);
        this.phoneNumber = (phoneNumber != null) ? phoneNumber.trim() : "";
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email.trim();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber != null) ? phoneNumber.trim() : "";
    }
    
    public Date getRegistrationDate() {
        return new Date(registrationDate.getTime());
    }
    
    public List<String> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public boolean canBorrowMoreBooks() {
        return isActive && borrowedBooks.size() < MAX_BOOKS_LIMIT;
    }
    
    public int getBorrowedBooksCount() {
        return borrowedBooks.size();
    }
    
    public int getRemainingBorrowLimit() {
        return MAX_BOOKS_LIMIT - borrowedBooks.size();
    }
    
    public boolean hasBorrowedBook(String isbn) {
        return borrowedBooks.contains(isbn);
    }
    
    public boolean borrowBook(String isbn) {
        if (!canBorrowMoreBooks()) {
            return false;
        }
        if (hasBorrowedBook(isbn)) {
            return false;
        }
        return borrowedBooks.add(isbn);
    }
    
    public boolean returnBook(String isbn) {
        return borrowedBooks.remove(isbn);
    }
    
    public void deactivateAccount() {
        this.isActive = false;
    }
    
    public void activateAccount() {
        this.isActive = true;
    }
    
    @Override
    public String toString() {
        return String.format("User{ID='%s', Name='%s', Email='%s', Books=%d/%d, Active=%s}",
                userId, name, email, borrowedBooks.size(), MAX_BOOKS_LIMIT, isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return userId.equals(user.userId);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode();
    }
    
    public String getFormattedInfo() {
        return String.format("👤 %s (%s) - %s | Books: %d/%d | Status: %s", 
                name, userId, email, 
                borrowedBooks.size(), MAX_BOOKS_LIMIT,
                isActive ? "Active" : "Inactive");
    }
    
    public String getBorrowingSummary() {
        if (borrowedBooks.isEmpty()) {
            return "No books currently borrowed";
        }
        return String.format("Currently borrowing %d book%s (Limit: %d)", 
                borrowedBooks.size(),
                borrowedBooks.size() == 1 ? "" : "s",
                MAX_BOOKS_LIMIT);
    }
}