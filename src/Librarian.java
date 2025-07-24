import java.util.*;

public class Librarian extends User {
    private String employeeId;
    private String department;
    private Date hireDate;
    private List<String> permissions;
    private boolean isAdministrator;
    
    public Librarian(String userId, String name, String email, String employeeId, String department) {
        super(userId, name, email);
        
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        
        this.employeeId = employeeId.trim();
        this.department = (department != null) ? department.trim() : "General";
        this.hireDate = new Date();
        this.permissions = new ArrayList<>();
        this.isAdministrator = false;
        
        initializeDefaultPermissions();
    }
    
    public Librarian(String userId, String name, String email, String phoneNumber, 
                    String employeeId, String department) {
        super(userId, name, email, phoneNumber);
        
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        
        this.employeeId = employeeId.trim();
        this.department = (department != null) ? department.trim() : "General";
        this.hireDate = new Date();
        this.permissions = new ArrayList<>();
        this.isAdministrator = false;
        
        initializeDefaultPermissions();
    }
    
    private void initializeDefaultPermissions() {
        permissions.add("VIEW_BOOKS");
        permissions.add("VIEW_USERS");
        permissions.add("PROCESS_BORROWING");
        permissions.add("PROCESS_RETURNS");
        permissions.add("VIEW_TRANSACTIONS");
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = (department != null) ? department.trim() : "General";
    }
    
    public Date getHireDate() {
        return new Date(hireDate.getTime());
    }
    
    public List<String> getPermissions() {
        return new ArrayList<>(permissions);
    }
    
    public boolean isAdministrator() {
        return isAdministrator;
    }
    
    public void promoteToAdministrator() {
        this.isAdministrator = true;
        addAdministratorPermissions();
    }
    
    public void demoteFromAdministrator() {
        this.isAdministrator = false;
        removeAdministratorPermissions();
    }
    
    private void addAdministratorPermissions() {
        List<String> adminPermissions = Arrays.asList(
            "ADD_BOOKS", "REMOVE_BOOKS", "EDIT_BOOKS",
            "ADD_USERS", "REMOVE_USERS", "EDIT_USERS",
            "MANAGE_LIBRARIANS", "VIEW_REPORTS",
            "SYSTEM_CONFIGURATION"
        );
        
        for (String permission : adminPermissions) {
            if (!permissions.contains(permission)) {
                permissions.add(permission);
            }
        }
    }
    
    private void removeAdministratorPermissions() {
        List<String> adminPermissions = Arrays.asList(
            "ADD_BOOKS", "REMOVE_BOOKS", "EDIT_BOOKS",
            "ADD_USERS", "REMOVE_USERS", "EDIT_USERS",
            "MANAGE_LIBRARIANS", "VIEW_REPORTS",
            "SYSTEM_CONFIGURATION"
        );
        
        permissions.removeAll(adminPermissions);
    }
    
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    public void addPermission(String permission) {
        if (permission != null && !permission.trim().isEmpty() && !permissions.contains(permission)) {
            permissions.add(permission.trim());
        }
    }
    
    public void removePermission(String permission) {
        permissions.remove(permission);
    }
    
    @Override
    public boolean canBorrowMoreBooks() {
        return isActive() && getBorrowedBooksCount() < (MAX_BOOKS_LIMIT + 5);
    }
    
    public String getRole() {
        return isAdministrator ? "Administrator" : "Librarian";
    }
    
    public long getDaysEmployed() {
        long diffInMillis = new Date().getTime() - hireDate.getTime();
        return diffInMillis / (1000 * 60 * 60 * 24);
    }
    
    @Override
    public String toString() {
        return String.format("Librarian{ID='%s', Name='%s', EmployeeID='%s', Role='%s', Department='%s'}",
                getUserId(), getName(), employeeId, getRole(), department);
    }
    
    @Override
    public String getFormattedInfo() {
        return String.format("👨‍💼 %s (%s) - %s | Employee: %s | %s - %s | Days Employed: %d", 
                getName(), getUserId(), getEmail(), employeeId, getRole(), department, getDaysEmployed());
    }
    
    public String getPermissionsSummary() {
        return String.format("Permissions (%d): %s", 
                permissions.size(), 
                String.join(", ", permissions));
    }
    
    public boolean canPerformAction(String action) {
        if (!isActive()) return false;
        
        return switch (action.toUpperCase()) {
            case "VIEW_BOOKS", "VIEW_USERS", "PROCESS_BORROWING", "PROCESS_RETURNS", "VIEW_TRANSACTIONS" -> true;
            case "ADD_BOOKS", "REMOVE_BOOKS", "EDIT_BOOKS" -> hasPermission("ADD_BOOKS") || isAdministrator;
            case "MANAGE_USERS" -> hasPermission("ADD_USERS") || isAdministrator;
            case "GENERATE_REPORTS" -> hasPermission("VIEW_REPORTS") || isAdministrator;
            case "SYSTEM_CONFIG" -> hasPermission("SYSTEM_CONFIGURATION") && isAdministrator;
            default -> false;
        };
    }
}