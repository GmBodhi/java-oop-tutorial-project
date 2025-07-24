import java.sql.*;
import java.util.*;

public class UserDAO {
    private DatabaseManager dbManager;
    
    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean createUser(User user) {
        String sql = """
            INSERT INTO users (user_id, name, email, phone_number, registration_date, 
                              is_active, borrowed_books_count, max_books_limit, updated_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;
        
        try {
            dbManager.executeUpdate(sql,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRegistrationDate(),
                user.isActive(),
                user.getBorrowedBooksCount(),
                User.MAX_BOOKS_LIMIT
            );
            dbManager.commit();
            System.out.println("✅ User created in database: " + user.getName());
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            
            if (e.getErrorCode() == 19) { // SQLite constraint violation
                System.err.println("❌ User with ID " + user.getUserId() + " or email already exists");
            } else {
                System.err.println("❌ Failed to create user: " + e.getMessage());
            }
            return false;
        }
    }
    
    public User findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (ResultSet rs = dbManager.executeQuery(sql, userId)) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user by ID: " + e.getMessage());
        }
        return null;
    }
    
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (ResultSet rs = dbManager.executeQuery(sql, email)) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user by email: " + e.getMessage());
        }
        return null;
    }
    
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all users: " + e.getMessage());
        }
        
        return users;
    }
    
    public List<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name LIKE ? ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql, "%" + name + "%")) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching users by name: " + e.getMessage());
        }
        
        return users;
    }
    
    public List<User> findActiveUsers() {
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving active users: " + e.getMessage());
        }
        
        return users;
    }
    
    public boolean updateUser(User user) {
        String sql = """
            UPDATE users 
            SET name = ?, email = ?, phone_number = ?, is_active = ?, 
                borrowed_books_count = ?, max_books_limit = ?, updated_date = CURRENT_TIMESTAMP
            WHERE user_id = ?
            """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setBoolean(4, user.isActive());
            stmt.setInt(5, user.getBorrowedBooksCount());
            stmt.setInt(6, User.MAX_BOOKS_LIMIT);
            stmt.setString(7, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                dbManager.commit();
                System.out.println("✅ User updated: " + user.getName());
                return true;
            } else {
                System.err.println("❌ No user found with ID: " + user.getUserId());
                return false;
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to update user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateBorrowedBooksCount(String userId, int newCount) {
        String sql = "UPDATE users SET borrowed_books_count = ?, updated_date = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try {
            dbManager.executeUpdate(sql, newCount, userId);
            dbManager.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to update borrowed books count: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteUser(String userId) {
        // Check if user has active borrowings
        if (hasActiveBorrowings(userId)) {
            System.err.println("❌ Cannot delete user - has active book borrowings");
            return false;
        }
        
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                dbManager.commit();
                System.out.println("✅ User deleted: " + userId);
                return true;
            } else {
                System.err.println("❌ No user found with ID: " + userId);
                return false;
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to delete user: " + e.getMessage());
            return false;
        }
    }
    
    private boolean hasActiveBorrowings(String userId) {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE user_id = ? AND is_returned = FALSE";
        
        try (ResultSet rs = dbManager.executeQuery(sql, userId)) {
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking active borrowings: " + e.getMessage());
        }
        return false;
    }
    
    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            // Total users
            String totalSql = "SELECT COUNT(*) as count FROM users";
            try (ResultSet rs = dbManager.executeQuery(totalSql)) {
                if (rs.next()) {
                    stats.put("Total Users", rs.getInt("count"));
                }
            }
            
            // Active users
            String activeSql = "SELECT COUNT(*) as count FROM users WHERE is_active = TRUE";
            try (ResultSet rs = dbManager.executeQuery(activeSql)) {
                if (rs.next()) {
                    stats.put("Active Users", rs.getInt("count"));
                }
            }
            
            // Users with borrowings
            String borrowingSql = "SELECT COUNT(*) as count FROM users WHERE borrowed_books_count > 0";
            try (ResultSet rs = dbManager.executeQuery(borrowingSql)) {
                if (rs.next()) {
                    stats.put("Users with Books", rs.getInt("count"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error calculating user statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        boolean isActive = rs.getBoolean("is_active");
        int borrowedBooksCount = rs.getInt("borrowed_books_count");
        
        User user = new User(userId, name, email, phoneNumber);
        user.setActive(isActive);
        
        // Restore borrowed books count (simplified - in real system would load actual ISBNs)
        for (int i = 0; i < borrowedBooksCount; i++) {
            user.borrowBook("dummy-isbn-" + i); // Placeholder
        }
        
        return user;
    }
    
    public void insertSampleUsers() {
        User[] sampleUsers = {
            new User("U001", "Alice Johnson", "alice.johnson@email.com", "123-456-7890"),
            new User("U002", "Bob Smith", "bob.smith@email.com", "987-654-3210"),
            new User("U003", "Carol Brown", "carol.brown@email.com", "555-123-4567"),
            new User("U004", "David Wilson", "david.wilson@email.com", "444-789-0123"),
            new User("U005", "Emma Davis", "emma.davis@email.com", "333-456-7890")
        };
        
        int insertedCount = 0;
        for (User user : sampleUsers) {
            if (findById(user.getUserId()) == null && createUser(user)) {
                insertedCount++;
            }
        }
        
        System.out.println("👥 Inserted " + insertedCount + " sample users");
    }
    
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) as count FROM users";
        
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total user count: " + e.getMessage());
        }
        return 0;
    }
}