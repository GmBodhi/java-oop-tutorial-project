import java.sql.*;
import java.io.File;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:library.db";
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        initializeDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            
            File dbFile = new File("library.db");
            boolean isNewDatabase = !dbFile.exists();
            
            connection = DriverManager.getConnection(DATABASE_URL);
            
            connection.setAutoCommit(false);
            
            System.out.println("✅ Database connection established: " + DATABASE_URL);
            if (isNewDatabase) {
                System.out.println("📝 New database file created");
            } else {
                System.out.println("📂 Connected to existing database");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found!");
            System.err.println("Please download sqlite-jdbc jar file and add to classpath");
            throw new RuntimeException("Database driver not found", e);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeDatabase();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }
    
    public void executeUpdate(String sql, Object... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, parameters);
            statement.executeUpdate();
        }
    }
    
    public ResultSet executeQuery(String sql, Object... parameters) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        setParameters(statement, parameters);
        return statement.executeQuery();
    }
    
    private void setParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param instanceof String) {
                statement.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                statement.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                statement.setDouble(i + 1, (Double) param);
            } else if (param instanceof Boolean) {
                statement.setBoolean(i + 1, (Boolean) param);
            } else if (param instanceof java.util.Date) {
                statement.setTimestamp(i + 1, new Timestamp(((java.util.Date) param).getTime()));
            } else if (param == null) {
                statement.setNull(i + 1, Types.NULL);
            } else {
                statement.setObject(i + 1, param);
            }
        }
    }
    
    public void commit() throws SQLException {
        connection.commit();
    }
    
    public void rollback() throws SQLException {
        connection.rollback();
    }
    
    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }
    
    public void endTransaction() throws SQLException {
        connection.setAutoCommit(true);
    }
    
    public boolean testConnection() {
        try {
            String sql = "SELECT 1 as test";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                
                if (resultSet.next()) {
                    int result = resultSet.getInt("test");
                    System.out.println("🔍 Database connection test: " + (result == 1 ? "PASSED" : "FAILED"));
                    return result == 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
        return false;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("📝 Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error closing database connection: " + e.getMessage());
        }
    }
    
    public void executeBatch(String[] sqlStatements) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            beginTransaction();
            
            for (String sql : sqlStatements) {
                statement.addBatch(sql);
            }
            
            int[] results = statement.executeBatch();
            commit();
            
            System.out.println("✅ Executed " + results.length + " SQL statements in batch");
        } catch (SQLException e) {
            rollback();
            System.err.println("❌ Batch execution failed, rolled back: " + e.getMessage());
            throw e;
        }
    }
    
    public void printDatabaseInfo() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("\n📊 DATABASE INFORMATION:");
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("Database URL: " + metaData.getURL());
            
            String sql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                
                System.out.println("\n📋 EXISTING TABLES:");
                boolean hasTables = false;
                while (resultSet.next()) {
                    System.out.println("  - " + resultSet.getString("name"));
                    hasTables = true;
                }
                if (!hasTables) {
                    System.out.println("  No tables found (new database)");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting database info: " + e.getMessage());
        }
    }
    
    // Utility method for safe resource cleanup
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                // Log but don't throw - this is for cleanup
                System.err.println("Warning: Error closing resource: " + e.getMessage());
            }
        }
    }
    
    // Add shutdown hook to properly close database connection
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.closeConnection();
            }
        }));
    }
}