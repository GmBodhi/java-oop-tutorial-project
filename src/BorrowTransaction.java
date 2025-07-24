import java.util.*;

public class BorrowTransaction {
    public static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    
    private String transactionId;
    private String bookIsbn;
    private String userId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private boolean isReturned;
    private double fineAmount;
    
    public BorrowTransaction(String transactionId, String bookIsbn, String userId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        if (bookIsbn == null || bookIsbn.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        this.transactionId = transactionId.trim();
        this.bookIsbn = bookIsbn.trim();
        this.userId = userId.trim();
        this.borrowDate = new Date();
        this.dueDate = calculateDueDate(borrowDate, DEFAULT_LOAN_PERIOD_DAYS);
        this.returnDate = null;
        this.isReturned = false;
        this.fineAmount = 0.0;
    }
    
    public BorrowTransaction(String transactionId, String bookIsbn, String userId, int loanPeriodDays) {
        this(transactionId, bookIsbn, userId);
        this.dueDate = calculateDueDate(borrowDate, loanPeriodDays);
    }
    
private Date calculateDueDate(Date borrowDate, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public Date getBorrowDate() {
        return new Date(borrowDate.getTime());
    }
    
    public Date getDueDate() {
        return new Date(dueDate.getTime());
    }
    
    public Date getReturnDate() {
        return returnDate != null ? new Date(returnDate.getTime()) : null;
    }
    
    public boolean isReturned() {
        return isReturned;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(double fineAmount) {
        this.fineAmount = Math.max(0, fineAmount);
    }
    
    public boolean isOverdue() {
        if (isReturned) {
            return returnDate.after(dueDate);
        }
        return new Date().after(dueDate);
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        
        Date comparisonDate = isReturned ? returnDate : new Date();
        long diffInMillis = comparisonDate.getTime() - dueDate.getTime();
        return diffInMillis / (1000 * 60 * 60 * 24);
    }
    
    public long getDaysUntilDue() {
        if (isReturned) return 0;
        
        long diffInMillis = dueDate.getTime() - new Date().getTime();
        return Math.max(0, diffInMillis / (1000 * 60 * 60 * 24));
    }
    
    public void markAsReturned() {
        if (!isReturned) {
            this.returnDate = new Date();
            this.isReturned = true;
            
            if (isOverdue()) {
                calculateFine();
            }
        }
    }
    
    private void calculateFine() {
        long daysOverdue = getDaysOverdue();
        if (daysOverdue > 0) {
            this.fineAmount = daysOverdue * 0.50;
        }
    }
    
    public String getStatus() {
        if (isReturned) {
            return isOverdue() ? "Returned Late" : "Returned On Time";
        } else {
            return isOverdue() ? "Overdue" : "Active";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Transaction{ID='%s', Book='%s', User='%s', Status='%s', Fine=%.2f}",
                transactionId, bookIsbn, userId, getStatus(), fineAmount);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BorrowTransaction that = (BorrowTransaction) obj;
        return transactionId.equals(that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return transactionId.hashCode();
    }
    
    public String getFormattedInfo() {
        String statusIcon = isReturned ? "✅" : (isOverdue() ? "⚠️" : "📖");
        String fineInfo = fineAmount > 0 ? String.format(" | Fine: $%.2f", fineAmount) : "";
        
        return String.format("%s Transaction %s - Book: %s | User: %s | Due: %s%s",
                statusIcon, transactionId, bookIsbn, userId, 
                formatDate(dueDate), fineInfo);
    }
    
    private String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.format("%02d/%02d/%d", 
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.YEAR));
    }
}