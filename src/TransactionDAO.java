import java.sql.*;
import java.util.*;

public class TransactionDAO {
    private DatabaseManager dbManager;
    
    public TransactionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean createTransaction(BorrowTransaction transaction) {
        String sql = """
            INSERT INTO transactions (transaction_id, book_isbn, user_id, borrow_date, 
                                    due_date, return_date, is_returned, fine_amount, updated_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;
        
        try {
            dbManager.executeUpdate(sql,
                transaction.getTransactionId(),
                transaction.getBookIsbn(),
                transaction.getUserId(),
                transaction.getBorrowDate(),
                transaction.getDueDate(),
                transaction.getReturnDate(),
                transaction.isReturned(),
                transaction.getFineAmount()
            );
            dbManager.commit();
            System.out.println("✅ Transaction created: " + transaction.getTransactionId());
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to create transaction: " + e.getMessage());
            return false;
        }
    }
    
    public BorrowTransaction findById(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (ResultSet rs = dbManager.executeQuery(sql, transactionId)) {
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding transaction by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<BorrowTransaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY borrow_date DESC";
        List<BorrowTransaction> transactions = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<BorrowTransaction> findByUserId(String userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY borrow_date DESC";
        List<BorrowTransaction> transactions = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, userId)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding transactions by user ID: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<BorrowTransaction> findByBookIsbn(String isbn) {
        String sql = "SELECT * FROM transactions WHERE book_isbn = ? ORDER BY borrow_date DESC";
        List<BorrowTransaction> transactions = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, isbn)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding transactions by book ISBN: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<BorrowTransaction> findActiveTransactions() {
        String sql = "SELECT * FROM transactions WHERE is_returned = FALSE ORDER BY due_date";
        List<BorrowTransaction> transactions = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving active transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<BorrowTransaction> findOverdueTransactions() {
        String sql = """
            SELECT * FROM transactions 
            WHERE is_returned = FALSE AND due_date < CURRENT_TIMESTAMP 
            ORDER BY due_date
            """;
        List<BorrowTransaction> transactions = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving overdue transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public BorrowTransaction findActiveTransactionByBookAndUser(String isbn, String userId) {
        String sql = """
            SELECT * FROM transactions 
            WHERE book_isbn = ? AND user_id = ? AND is_returned = FALSE
            LIMIT 1
            """;
        
        try (ResultSet rs = dbManager.executeQuery(sql, isbn, userId)) {
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding active transaction: " + e.getMessage());
        }
        return null;
    }
    
    public boolean updateTransaction(BorrowTransaction transaction) {
        String sql = """
            UPDATE transactions 
            SET return_date = ?, is_returned = ?, fine_amount = ?, updated_date = CURRENT_TIMESTAMP
            WHERE transaction_id = ?
            """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, transaction.getReturnDate() != null ? 
                new Timestamp(transaction.getReturnDate().getTime()) : null);
            stmt.setBoolean(2, transaction.isReturned());
            stmt.setDouble(3, transaction.getFineAmount());
            stmt.setString(4, transaction.getTransactionId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                dbManager.commit();
                System.out.println("✅ Transaction updated: " + transaction.getTransactionId());
                return true;
            } else {
                System.err.println("❌ No transaction found with ID: " + transaction.getTransactionId());
                return false;
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to update transaction: " + e.getMessage());
            return false;
        }
    }
    
    public boolean markAsReturned(String transactionId) {
        String sql = """
            UPDATE transactions 
            SET return_date = CURRENT_TIMESTAMP, is_returned = TRUE, updated_date = CURRENT_TIMESTAMP
            WHERE transaction_id = ?
            """;
        
        try {
            dbManager.executeUpdate(sql, transactionId);
            dbManager.commit();
            System.out.println("✅ Transaction marked as returned: " + transactionId);
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to mark transaction as returned: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateFineAmount(String transactionId, double fineAmount) {
        String sql = "UPDATE transactions SET fine_amount = ?, updated_date = CURRENT_TIMESTAMP WHERE transaction_id = ?";
        
        try {
            dbManager.executeUpdate(sql, fineAmount, transactionId);
            dbManager.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to update fine amount: " + e.getMessage());
            return false;
        }
    }
    
    public Map<String, Integer> getTransactionStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            // Total transactions
            String totalSql = "SELECT COUNT(*) as count FROM transactions";
            try (ResultSet rs = dbManager.executeQuery(totalSql)) {
                if (rs.next()) {
                    stats.put("Total Transactions", rs.getInt("count"));
                }
            }
            
            // Active transactions
            String activeSql = "SELECT COUNT(*) as count FROM transactions WHERE is_returned = FALSE";
            try (ResultSet rs = dbManager.executeQuery(activeSql)) {
                if (rs.next()) {
                    stats.put("Active Transactions", rs.getInt("count"));
                }
            }
            
            // Overdue transactions
            String overdueSql = """
                SELECT COUNT(*) as count FROM transactions 
                WHERE is_returned = FALSE AND due_date < CURRENT_TIMESTAMP
                """;
            try (ResultSet rs = dbManager.executeQuery(overdueSql)) {
                if (rs.next()) {
                    stats.put("Overdue Transactions", rs.getInt("count"));
                }
            }
            
            // Transactions with fines
            String finesSql = "SELECT COUNT(*) as count FROM transactions WHERE fine_amount > 0";
            try (ResultSet rs = dbManager.executeQuery(finesSql)) {
                if (rs.next()) {
                    stats.put("Transactions with Fines", rs.getInt("count"));
                }
            }
            
            // Total fine amount
            String totalFinesSql = "SELECT SUM(fine_amount) as total FROM transactions";
            try (ResultSet rs = dbManager.executeQuery(totalFinesSql)) {
                if (rs.next()) {
                    double totalFines = rs.getDouble("total");
                    stats.put("Total Fines (cents)", (int) (totalFines * 100));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error calculating transaction statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    public List<BorrowTransaction> getRecentTransactions(int limit) {
        String sql = "SELECT * FROM transactions ORDER BY borrow_date DESC LIMIT ?";
        List<BorrowTransaction> transactions = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, limit)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting recent transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public boolean deleteTransaction(String transactionId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, transactionId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                dbManager.commit();
                System.out.println("✅ Transaction deleted: " + transactionId);
                return true;
            } else {
                System.err.println("❌ No transaction found with ID: " + transactionId);
                return false;
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to delete transaction: " + e.getMessage());
            return false;
        }
    }
    
    private BorrowTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        String transactionId = rs.getString("transaction_id");
        String bookIsbn = rs.getString("book_isbn");
        String userId = rs.getString("user_id");
        Timestamp borrowDate = rs.getTimestamp("borrow_date");
        Timestamp dueDate = rs.getTimestamp("due_date");
        Timestamp returnDate = rs.getTimestamp("return_date");
        boolean isReturned = rs.getBoolean("is_returned");
        double fineAmount = rs.getDouble("fine_amount");
        
        // Create transaction with basic constructor
        BorrowTransaction transaction = new BorrowTransaction(transactionId, bookIsbn, userId);
        
        // Use reflection or create a special constructor to set the dates properly
        // For now, we'll work with the existing structure
        if (isReturned && returnDate != null) {
            transaction.markAsReturned();
        }
        
        if (fineAmount > 0) {
            transaction.setFineAmount(fineAmount);
        }
        
        return transaction;
    }
    
    public int getTotalTransactionCount() {
        String sql = "SELECT COUNT(*) as count FROM transactions";
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total transaction count: " + e.getMessage());
        }
        return 0;
    }
}