import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSchema {
    private DatabaseManager dbManager;
    
    public DatabaseSchema() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public void createAllTables() {
        try {
            System.out.println("🏗️  Creating database schema...");
            
            createBooksTable();
            createUsersTable();
            createLibrariansTable();
            createTransactionsTable();
            createLibrarySettingsTable();
            
            dbManager.commit();
            System.out.println("✅ Database schema created successfully!");
            
        } catch (SQLException e) {
            try {
                dbManager.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Failed to create database schema: " + e.getMessage());
            throw new RuntimeException("Schema creation failed", e);
        }
    }
    
    private void createBooksTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS books (
                isbn TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                genre TEXT DEFAULT 'Unknown',
                publication_year INTEGER DEFAULT 0,
                status TEXT NOT NULL DEFAULT 'AVAILABLE',
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT chk_status CHECK (status IN ('AVAILABLE', 'BORROWED', 'RESERVED', 'MAINTENANCE'))
            )
            """;
        
        dbManager.executeUpdate(sql);
        System.out.println("📚 Books table created");
        
        // Create indexes for better search performance
        String indexSql1 = "CREATE INDEX IF NOT EXISTS idx_books_title ON books(title)";
        String indexSql2 = "CREATE INDEX IF NOT EXISTS idx_books_author ON books(author)";
        String indexSql3 = "CREATE INDEX IF NOT EXISTS idx_books_genre ON books(genre)";
        String indexSql4 = "CREATE INDEX IF NOT EXISTS idx_books_status ON books(status)";
        
        dbManager.executeUpdate(indexSql1);
        dbManager.executeUpdate(indexSql2);
        dbManager.executeUpdate(indexSql3);
        dbManager.executeUpdate(indexSql4);
    }
    
    private void createUsersTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                user_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                phone_number TEXT DEFAULT '',
                registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                borrowed_books_count INTEGER DEFAULT 0,
                max_books_limit INTEGER DEFAULT 5,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT chk_borrowed_books CHECK (borrowed_books_count >= 0),
                CONSTRAINT chk_max_books CHECK (max_books_limit > 0)
            )
            """;
        
        dbManager.executeUpdate(sql);
        System.out.println("👤 Users table created");
        
        // Create indexes
        String indexSql1 = "CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)";
        String indexSql2 = "CREATE INDEX IF NOT EXISTS idx_users_name ON users(name)";
        String indexSql3 = "CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active)";
        
        dbManager.executeUpdate(indexSql1);
        dbManager.executeUpdate(indexSql2);
        dbManager.executeUpdate(indexSql3);
    }
    
    private void createLibrariansTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS librarians (
                user_id TEXT PRIMARY KEY,
                employee_id TEXT NOT NULL UNIQUE,
                department TEXT DEFAULT 'General',
                hire_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_administrator BOOLEAN DEFAULT FALSE,
                permissions TEXT DEFAULT '',
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
            )
            """;
        
        dbManager.executeUpdate(sql);
        System.out.println("👨‍💼 Librarians table created");
        
        // Create indexes
        String indexSql1 = "CREATE INDEX IF NOT EXISTS idx_librarians_employee_id ON librarians(employee_id)";
        String indexSql2 = "CREATE INDEX IF NOT EXISTS idx_librarians_department ON librarians(department)";
        String indexSql3 = "CREATE INDEX IF NOT EXISTS idx_librarians_admin ON librarians(is_administrator)";
        
        dbManager.executeUpdate(indexSql1);
        dbManager.executeUpdate(indexSql2);
        dbManager.executeUpdate(indexSql3);
    }
    
    private void createTransactionsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id TEXT PRIMARY KEY,
                book_isbn TEXT NOT NULL,
                user_id TEXT NOT NULL,
                borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                due_date TIMESTAMP NOT NULL,
                return_date TIMESTAMP NULL,
                is_returned BOOLEAN DEFAULT FALSE,
                fine_amount DECIMAL(10,2) DEFAULT 0.00,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (book_isbn) REFERENCES books(isbn) ON DELETE RESTRICT,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
                CONSTRAINT chk_fine_amount CHECK (fine_amount >= 0),
                CONSTRAINT chk_dates CHECK (due_date >= borrow_date),
                CONSTRAINT chk_return_date CHECK (return_date IS NULL OR return_date >= borrow_date)
            )
            """;
        
        dbManager.executeUpdate(sql);
        System.out.println("📋 Transactions table created");
        
        // Create indexes
        String indexSql1 = "CREATE INDEX IF NOT EXISTS idx_transactions_book ON transactions(book_isbn)";
        String indexSql2 = "CREATE INDEX IF NOT EXISTS idx_transactions_user ON transactions(user_id)";
        String indexSql3 = "CREATE INDEX IF NOT EXISTS idx_transactions_dates ON transactions(borrow_date, due_date)";
        String indexSql4 = "CREATE INDEX IF NOT EXISTS idx_transactions_returned ON transactions(is_returned)";
        String indexSql5 = "CREATE INDEX IF NOT EXISTS idx_transactions_overdue ON transactions(due_date, is_returned)";
        
        dbManager.executeUpdate(indexSql1);
        dbManager.executeUpdate(indexSql2);
        dbManager.executeUpdate(indexSql3);
        dbManager.executeUpdate(indexSql4);
        dbManager.executeUpdate(indexSql5);
    }
    
    private void createLibrarySettingsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS library_settings (
                setting_key TEXT PRIMARY KEY,
                setting_value TEXT NOT NULL,
                description TEXT DEFAULT '',
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        dbManager.executeUpdate(sql);
        System.out.println("⚙️  Library settings table created");
        
        // Insert default settings
        insertDefaultSettings();
    }
    
    private void insertDefaultSettings() throws SQLException {
        String[] defaultSettings = {
            "INSERT OR IGNORE INTO library_settings (setting_key, setting_value, description) VALUES " +
            "('library_name', 'Community Library', 'Name of the library')",
            
            "INSERT OR IGNORE INTO library_settings (setting_key, setting_value, description) VALUES " +
            "('library_address', 'Main Street', 'Library address')",
            
            "INSERT OR IGNORE INTO library_settings (setting_key, setting_value, description) VALUES " +
            "('default_loan_period_days', '14', 'Default loan period in days')",
            
            "INSERT OR IGNORE INTO library_settings (setting_key, setting_value, description) VALUES " +
            "('max_books_per_user', '5', 'Maximum books a user can borrow')",
            
            "INSERT OR IGNORE INTO library_settings (setting_key, setting_value, description) VALUES " +
            "('fine_per_day', '0.50', 'Fine amount per day for overdue books')",
            
            "INSERT OR IGNORE INTO library_settings (setting_key, setting_value, description) VALUES " +
            "('max_books_librarian', '10', 'Maximum books a librarian can borrow')"
        };
        
        for (String setting : defaultSettings) {
            dbManager.executeUpdate(setting);
        }
        
        System.out.println("⚙️  Default settings inserted");
    }
    
    public boolean tablesExist() {
        try {
            String[] requiredTables = {"books", "users", "librarians", "transactions", "library_settings"};
            
            for (String tableName : requiredTables) {
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
                try (ResultSet rs = dbManager.executeQuery(sql, tableName)) {
                    if (!rs.next()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error checking table existence: " + e.getMessage());
            return false;
        }
    }
    
    public void dropAllTables() {
        try {
            System.out.println("🗑️  Dropping all tables...");
            
            // Drop in reverse order due to foreign key constraints
            String[] dropStatements = {
                "DROP TABLE IF EXISTS transactions",
                "DROP TABLE IF EXISTS librarians", 
                "DROP TABLE IF EXISTS library_settings",
                "DROP TABLE IF EXISTS users",
                "DROP TABLE IF EXISTS books"
            };
            
            dbManager.executeBatch(dropStatements);
            System.out.println("✅ All tables dropped successfully");
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to drop tables: " + e.getMessage());
            throw new RuntimeException("Failed to drop tables", e);
        }
    }
    
    public void recreateSchema() {
        dropAllTables();
        createAllTables();
    }
    
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        try {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
            try (ResultSet rs = dbManager.executeQuery(sql)) {
                while (rs.next()) {
                    tableNames.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting table names: " + e.getMessage());
        }
        return tableNames;
    }
    
    public void printTableInfo(String tableName) {
        try {
            String sql = "PRAGMA table_info(" + tableName + ")";
            try (ResultSet rs = dbManager.executeQuery(sql)) {
                System.out.println("\n📋 Table: " + tableName.toUpperCase());
                System.out.println("Columns:");
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    String dataType = rs.getString("type");
                    boolean notNull = rs.getBoolean("notnull");
                    String defaultValue = rs.getString("dflt_value");
                    boolean primaryKey = rs.getBoolean("pk");
                    
                    System.out.printf("  %-20s %-15s %s %s %s%n",
                        columnName,
                        dataType,
                        notNull ? "NOT NULL" : "",
                        defaultValue != null ? "DEFAULT " + defaultValue : "",
                        primaryKey ? "PRIMARY KEY" : "");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting table info: " + e.getMessage());
        }
    }
    
    public void printAllTablesInfo() {
        List<String> tableNames = getTableNames();
        System.out.println("\n📊 DATABASE SCHEMA INFORMATION");
        System.out.println("Found " + tableNames.size() + " tables:");
        
        for (String tableName : tableNames) {
            printTableInfo(tableName);
        }
    }
    
    public int getTableRowCount(String tableName) {
        try {
            String sql = "SELECT COUNT(*) as count FROM " + tableName;
            try (ResultSet rs = dbManager.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting row count for " + tableName + ": " + e.getMessage());
        }
        return 0;
    }
    
    public void printDataSummary() {
        List<String> tableNames = getTableNames();
        System.out.println("\n📈 DATABASE DATA SUMMARY");
        
        for (String tableName : tableNames) {
            int rowCount = getTableRowCount(tableName);
            System.out.printf("%-20s: %d rows%n", tableName, rowCount);
        }
    }
}