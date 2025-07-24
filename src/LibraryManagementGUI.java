import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class LibraryManagementGUI extends JFrame {
    private Library library;
    private JTabbedPane tabbedPane;
    
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    
    private int nextBookId = 1;
    private int nextUserId = 1;
    
    public LibraryManagementGUI() {
        library = new Library("Java Learning Library", "Tutorial Campus");
        initializeGUI();
        loadSampleData();
        refreshAllTables();
    }
    
    private void initializeGUI() {
        setTitle("Library Management System - " + library.getLibraryName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        createBooksPanel();
        createUsersPanel();
        createTransactionsPanel();
        createReportsPanel();
        
        add(tabbedPane);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    private void createBooksPanel() {
        JPanel bookPanel = new JPanel(new BorderLayout());
        
        String[] bookColumns = {"ISBN", "Title", "Author", "Genre", "Year", "Status"};
        bookTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);
        
        JPanel bookButtonPanel = new JPanel(new FlowLayout());
        JButton addBookBtn = new JButton("Add Book");
        JButton editBookBtn = new JButton("Edit Book");
        JButton removeBookBtn = new JButton("Remove Book");
        JButton searchBookBtn = new JButton("Search Books");
        
        addBookBtn.addActionListener(e -> showAddBookDialog());
        editBookBtn.addActionListener(e -> showEditBookDialog());
        removeBookBtn.addActionListener(e -> removeSelectedBook());
        searchBookBtn.addActionListener(e -> showSearchBooksDialog());
        
        bookButtonPanel.add(addBookBtn);
        bookButtonPanel.add(editBookBtn);
        bookButtonPanel.add(removeBookBtn);
        bookButtonPanel.add(searchBookBtn);
        
        bookPanel.add(bookButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Books", bookPanel);
    }
    
    private void createUsersPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        
        String[] userColumns = {"User ID", "Name", "Email", "Phone", "Books Borrowed", "Status"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userPanel.add(userScrollPane, BorderLayout.CENTER);
        
        JPanel userButtonPanel = new JPanel(new FlowLayout());
        JButton addUserBtn = new JButton("Add User");
        JButton editUserBtn = new JButton("Edit User");
        JButton borrowBookBtn = new JButton("Borrow Book");
        JButton returnBookBtn = new JButton("Return Book");
        
        addUserBtn.addActionListener(e -> showAddUserDialog());
        editUserBtn.addActionListener(e -> showEditUserDialog());
        borrowBookBtn.addActionListener(e -> showBorrowBookDialog());
        returnBookBtn.addActionListener(e -> showReturnBookDialog());
        
        userButtonPanel.add(addUserBtn);
        userButtonPanel.add(editUserBtn);
        userButtonPanel.add(borrowBookBtn);
        userButtonPanel.add(returnBookBtn);
        
        userPanel.add(userButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Users", userPanel);
    }
    
    private void createTransactionsPanel() {
        JPanel transactionPanel = new JPanel(new BorderLayout());
        
        String[] transactionColumns = {"Transaction ID", "Book ISBN", "User ID", "Borrow Date", "Due Date", "Status", "Fine"};
        transactionTableModel = new DefaultTableModel(transactionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        transactionPanel.add(transactionScrollPane, BorderLayout.CENTER);
        
        JPanel transactionButtonPanel = new JPanel(new FlowLayout());
        JButton viewAllBtn = new JButton("View All");
        JButton viewActiveBtn = new JButton("View Active");
        JButton viewOverdueBtn = new JButton("View Overdue");
        JButton refreshBtn = new JButton("Refresh");
        
        viewAllBtn.addActionListener(e -> refreshTransactionTable(library.getAllTransactions()));
        viewActiveBtn.addActionListener(e -> refreshTransactionTable(library.getActiveTransactions()));
        viewOverdueBtn.addActionListener(e -> refreshTransactionTable(library.getOverdueTransactions()));
        refreshBtn.addActionListener(e -> refreshTransactionTable(library.getAllTransactions()));
        
        transactionButtonPanel.add(viewAllBtn);
        transactionButtonPanel.add(viewActiveBtn);
        transactionButtonPanel.add(viewOverdueBtn);
        transactionButtonPanel.add(refreshBtn);
        
        transactionPanel.add(transactionButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Transactions", transactionPanel);
    }
    
    private void createReportsPanel() {
        JPanel reportPanel = new JPanel(new BorderLayout());
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane reportScrollPane = new JScrollPane(reportArea);
        
        JPanel reportButtonPanel = new JPanel(new FlowLayout());
        JButton generateReportBtn = new JButton("Generate Report");
        JButton statisticsBtn = new JButton("Show Statistics");
        
        generateReportBtn.addActionListener(e -> {
            reportArea.setText("");
            library.generateLibraryReport();
            updateReportArea(reportArea);
        });
        
        statisticsBtn.addActionListener(e -> {
            Map<String, Integer> stats = library.getLibraryStatistics();
            StringBuilder sb = new StringBuilder();
            sb.append("=== LIBRARY STATISTICS ===\n\n");
            stats.forEach((key, value) -> 
                sb.append(String.format("%-20s: %d\n", key, value)));
            reportArea.setText(sb.toString());
        });
        
        reportButtonPanel.add(generateReportBtn);
        reportButtonPanel.add(statisticsBtn);
        
        reportPanel.add(reportScrollPane, BorderLayout.CENTER);
        reportPanel.add(reportButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Reports", reportPanel);
    }
    
    private void loadSampleData() {
        Book book1 = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", "Programming", 2008);
        Book book2 = new Book("978-0-201-61622-4", "The Pragmatic Programmer", "Andy Hunt", "Programming", 1999);
        Book book3 = new Book("978-0-321-35668-0", "Effective Java", "Joshua Bloch", "Programming", 2017);
        Book book4 = new Book("978-0-596-52068-7", "Head First Design Patterns", "Eric Freeman", "Programming", 2004);
        
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        library.addBook(book4);
        
        User user1 = new User("U001", "Alice Johnson", "alice@email.com", "123-456-7890");
        User user2 = new User("U002", "Bob Smith", "bob@email.com", "987-654-3210");
        User user3 = new User("U003", "Carol Brown", "carol@email.com");
        
        library.registerUser(user1);
        library.registerUser(user2);
        library.registerUser(user3);
        
        Librarian librarian = new Librarian("L001", "Sarah Wilson", "sarah@library.com", 
                                          "555-0123", "EMP001", "Reference");
        library.registerUser(librarian);
        
        library.borrowBook(book1.getIsbn(), user1.getUserId());
        library.borrowBook(book2.getIsbn(), user2.getUserId());
    }
    
    private void refreshAllTables() {
        refreshBookTable();
        refreshUserTable();
        refreshTransactionTable(library.getAllTransactions());
    }
    
    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        for (Book book : library.getAllBooks()) {
            Object[] row = {
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPublicationYear() > 0 ? book.getPublicationYear() : "Unknown",
                book.getStatus()
            };
            bookTableModel.addRow(row);
        }
    }
    
    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (User user : library.getAllUsers()) {
            Object[] row = {
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getBorrowedBooksCount(),
                user.isActive() ? "Active" : "Inactive"
            };
            userTableModel.addRow(row);
        }
    }
    
    private void refreshTransactionTable(List<BorrowTransaction> transactions) {
        transactionTableModel.setRowCount(0);
        for (BorrowTransaction transaction : transactions) {
            Object[] row = {
                transaction.getTransactionId(),
                transaction.getBookIsbn(),
                transaction.getUserId(),
                transaction.getBorrowDate(),
                transaction.getDueDate(),
                transaction.getStatus(),
                transaction.getFineAmount() > 0 ? String.format("$%.2f", transaction.getFineAmount()) : "-"
            };
            transactionTableModel.addRow(row);
        }
    }
    
    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Add New Book", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField isbnField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField genreField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; panel.add(isbnField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; panel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; panel.add(authorField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; panel.add(genreField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; panel.add(yearField, gbc);
        
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            try {
                int year = yearField.getText().trim().isEmpty() ? 0 : Integer.parseInt(yearField.getText());
                Book book = new Book(isbnField.getText(), titleField.getText(), 
                                   authorField.getText(), genreField.getText(), year);
                if (library.addBook(book)) {
                    refreshBookTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Book added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Book with this ISBN already exists!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField userIdField = new JTextField("U" + String.format("%03d", nextUserId++), 20);
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1; panel.add(userIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; panel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; panel.add(phoneField, gbc);
        
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            try {
                User user = new User(userIdField.getText(), nameField.getText(), 
                                   emailField.getText(), phoneField.getText());
                if (library.registerUser(user)) {
                    refreshUserTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "User with this ID already exists!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showBorrowBookDialog() {
        String[] availableBooks = library.getAvailableBooks().stream()
                .map(book -> book.getIsbn() + " - " + book.getTitle())
                .toArray(String[]::new);
        
        String[] activeUsers = library.getAllUsers().stream()
                .filter(User::isActive)
                .map(user -> user.getUserId() + " - " + user.getName())
                .toArray(String[]::new);
        
        if (availableBooks.length == 0) {
            JOptionPane.showMessageDialog(this, "No books available for borrowing!");
            return;
        }
        
        if (activeUsers.length == 0) {
            JOptionPane.showMessageDialog(this, "No active users found!");
            return;
        }
        
        String selectedBook = (String) JOptionPane.showInputDialog(this, "Select book to borrow:",
                "Borrow Book", JOptionPane.QUESTION_MESSAGE, null, availableBooks, availableBooks[0]);
        
        if (selectedBook != null) {
            String selectedUser = (String) JOptionPane.showInputDialog(this, "Select user:",
                    "Borrow Book", JOptionPane.QUESTION_MESSAGE, null, activeUsers, activeUsers[0]);
            
            if (selectedUser != null) {
                String isbn = selectedBook.split(" - ")[0];
                String userId = selectedUser.split(" - ")[0];
                
                String result = library.borrowBook(isbn, userId);
                JOptionPane.showMessageDialog(this, result);
                refreshAllTables();
            }
        }
    }
    
    private void showReturnBookDialog() {
        List<BorrowTransaction> activeTransactions = library.getActiveTransactions();
        
        if (activeTransactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books currently borrowed!");
            return;
        }
        
        String[] borrowedBooks = activeTransactions.stream()
                .map(t -> t.getTransactionId() + " - " + t.getBookIsbn() + " - " + t.getUserId())
                .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this, "Select book to return:",
                "Return Book", JOptionPane.QUESTION_MESSAGE, null, borrowedBooks, borrowedBooks[0]);
        
        if (selected != null) {
            String[] parts = selected.split(" - ");
            String isbn = parts[1];
            String userId = parts[2];
            
            String result = library.returnBook(isbn, userId);
            JOptionPane.showMessageDialog(this, result);
            refreshAllTables();
        }
    }
    
    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit!");
            return;
        }
        
        String isbn = (String) bookTableModel.getValueAt(selectedRow, 0);
        Book book = library.getBook(isbn);
        
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Book not found!");
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Book", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JTextField genreField = new JTextField(book.getGenre(), 20);
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; panel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; panel.add(authorField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; panel.add(genreField, gbc);
        
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            try {
                if (library.updateBook(isbn, titleField.getText(), authorField.getText(), genreField.getText())) {
                    refreshBookTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Book updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update book!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit!");
            return;
        }
        
        String userId = (String) userTableModel.getValueAt(selectedRow, 0);
        User user = library.getUser(userId);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found!");
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JTextField phoneField = new JTextField(user.getPhoneNumber(), 20);
        JCheckBox activeCheckBox = new JCheckBox("Active", user.isActive());
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; panel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; panel.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; panel.add(activeCheckBox, gbc);
        
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            try {
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setPhoneNumber(phoneField.getText());
                user.setActive(activeCheckBox.isSelected());
                
                refreshUserTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void removeSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to remove!");
            return;
        }
        
        String isbn = (String) bookTableModel.getValueAt(selectedRow, 0);
        String title = (String) bookTableModel.getValueAt(selectedRow, 1);
        
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove '" + title + "'?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (library.removeBook(isbn)) {
                refreshBookTable();
                JOptionPane.showMessageDialog(this, "Book removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot remove book - it may be currently borrowed!");
            }
        }
    }
    
    private void showSearchBooksDialog() {
        String[] searchTypes = {"Title", "Author", "Genre", "ISBN"};
        String searchType = (String) JOptionPane.showInputDialog(this, "Search by:",
                "Search Books", JOptionPane.QUESTION_MESSAGE, null, searchTypes, searchTypes[0]);
        
        if (searchType != null) {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                List<Book> results = switch (searchType) {
                    case "Title" -> library.searchBooksByTitle(searchTerm);
                    case "Author" -> library.searchBooksByAuthor(searchTerm);
                    case "Genre" -> library.searchBooksByGenre(searchTerm);
                    case "ISBN" -> library.searchBooksByIsbn(searchTerm);
                    default -> List.of();
                };
                
                showSearchResults(results, searchType, searchTerm);
            }
        }
    }
    
    private void showSearchResults(List<Book> results, String searchType, String searchTerm) {
        JDialog dialog = new JDialog(this, "Search Results", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] columns = {"ISBN", "Title", "Author", "Genre", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        for (Book book : results) {
            Object[] row = {book.getIsbn(), book.getTitle(), book.getAuthor(), 
                          book.getGenre(), book.getStatus()};
            model.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JLabel resultLabel = new JLabel(String.format("Found %d book(s) matching '%s' in %s", 
                                                     results.size(), searchTerm, searchType));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(resultLabel, BorderLayout.NORTH);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void updateReportArea(JTextArea reportArea) {
        StringBuilder sb = new StringBuilder(reportArea.getText());
        sb.append("\n\n=== DETAILED ANALYSIS ===\n");
        
        Map<String, Integer> stats = library.getLibraryStatistics();
        sb.append(String.format("Collection Utilization: %.1f%%\n", 
                (stats.get("Borrowed Books") * 100.0 / stats.get("Total Books"))));
        
        List<BorrowTransaction> overdue = library.getOverdueTransactions();
        if (!overdue.isEmpty()) {
            sb.append(String.format("\nOverdue Books (%d):\n", overdue.size()));
            overdue.forEach(t -> sb.append("  ").append(t.getFormattedInfo()).append("\n"));
        }
        
        reportArea.setText(sb.toString());
    }
    
    private void showAboutDialog() {
        JDialog dialog = new JDialog(this, "About", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        JTextArea aboutText = new JTextArea();
        aboutText.setEditable(false);
        aboutText.setText("""
                Library Management System
                Version 1.0
                
                A comprehensive Java application for managing library operations including:
                • Book catalog management
                • User registration and management
                • Borrowing and returning books
                • Transaction tracking
                • Library statistics and reporting
                
                Built using:
                • Java Swing for GUI
                • Object-oriented programming principles
                • Collections framework
                • Stream API for data processing
                
                Developed as part of Java programming tutorial.
                """);
        
        aboutText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        aboutText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okBtn);
        
        panel.add(new JScrollPane(aboutText), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new LibraryManagementGUI().setVisible(true);
        });
    }
}