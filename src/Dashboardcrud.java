import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dashboardcrud extends JFrame {

    private JLabel welcomeLabel;
    private User currentUser; // Menyimpan informasi pengguna yang sedang login
    private Connection connection;
    private JTable categoryTable, transactionTable, budgetTable;
    private DefaultTableModel categoryModel, transactionModel, budgetModel;

    public Dashboardcrud(User currentUser) {
        this.currentUser = currentUser; // Simpan objek User yang login

        // Initialize data
        connectToDatabase();

        // Initialize UI components
        setTitle("Budget Management Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menampilkan welcome message untuk pengguna yang login
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername(), JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // Tabbed Pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();

        // Categories Tab
        JPanel categoriesPanel = createCategoryPanel();
        tabbedPane.addTab("Categories", categoriesPanel);

        // Budgets Tab
        JPanel budgetsPanel = createBudgetPanel();
        tabbedPane.addTab("Budgets", budgetsPanel);

        // Transactions Tab
        JPanel transactionsPanel = createTransactionPanel();
        tabbedPane.addTab("Transactions", transactionsPanel);

        // Add tabbed pane to frame
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/management_system", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    // Categories Panel
private JPanel createCategoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Table for categories
    categoryModel = new DefaultTableModel(new String[]{"ID", "Category Name"}, 0);
    categoryTable = new JTable(categoryModel);
    JScrollPane scrollPane = new JScrollPane(categoryTable);

    // Buttons
    JPanel buttonPanel = new JPanel();
    JButton addButton = new JButton("Add");
    JButton updateButton = new JButton("Update");
    JButton deleteButton = new JButton("Delete");
    JButton logoutButton = new JButton("Logout"); // Tambahkan tombol Logout

    // Menambahkan tombol ke panel
    buttonPanel.add(addButton);
    buttonPanel.add(updateButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(logoutButton); // Tombol logout ditambahkan ke buttonPanel

    // Load categories
    loadCategories();

    // Add button listeners
    addButton.addActionListener(e -> addCategory());
    updateButton.addActionListener(e -> updateCategory());
    deleteButton.addActionListener(e -> deleteCategory());
    logoutButton.addActionListener(e -> logout()); // Menambahkan listener untuk tombol logout

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    return panel;
}


    // Transactions Panel
 private JPanel createTransactionPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Table for transactions - Mengubah model tabel agar hanya menampilkan kolom yang diinginkan
    transactionModel = new DefaultTableModel(new String[]{"ID", "Category Name", "Amount Transaction", "Budget Amount", "Date"}, 0);
    transactionTable = new JTable(transactionModel);
    JScrollPane scrollPane = new JScrollPane(transactionTable);

    // Buttons
    JPanel buttonPanel = new JPanel();
    JButton addButton = new JButton("Add");
    JButton updateButton = new JButton("Update");
    JButton deleteButton = new JButton("Delete");

    buttonPanel.add(addButton);
    buttonPanel.add(updateButton);
    buttonPanel.add(deleteButton);

    // Load transactions
    loadTransactions();

    // Add button listeners
    addButton.addActionListener(e -> addTransaction());
    updateButton.addActionListener(e -> {
        try {
            updateTransaction();
        } catch (SQLException ex) {
            Logger.getLogger(Dashboardcrud.class.getName()).log(Level.SEVERE, null, ex);
        }
    });
    deleteButton.addActionListener(e -> deleteTransaction());

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    return panel;
}


    // Budgets Panel
    private JPanel createBudgetPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for budgets
        budgetModel = new DefaultTableModel(new String[]{"Category Name", "Amount"}, 0);
        budgetTable = new JTable(budgetModel);
        JScrollPane scrollPane = new JScrollPane(budgetTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Load budgets
        loadBudgets();

        // Add button listeners
        addButton.addActionListener(e -> addBudget());
        updateButton.addActionListener(e -> updateBudget());
        deleteButton.addActionListener(e -> deleteBudget());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // CRUD for Categories
    private void loadCategories() {
        categoryModel.setRowCount(0); // Reset data model
        String query = "SELECT categories.id, categories.name_category FROM categories WHERE categories.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getId()); // Filter based on logged-in user ID
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categoryModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name_category")});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        }
    }

private void addCategory() {
    // Membuat panel input untuk memilih kategori
    String[] categories = {"makanan", "tagihan", "liburan", "bensin"};
    String categoryName = (String) JOptionPane.showInputDialog(
        this, 
        "Select category:", 
        "Add Category", 
        JOptionPane.QUESTION_MESSAGE, 
        null, 
        categories, 
        categories[0] // Default option
    );

    if (categoryName != null && !categoryName.isEmpty()) {
        // Pastikan nilai kategori sesuai dengan yang ada pada ENUM
        if (isValidCategory(categoryName)) {
            // Query untuk menambahkan kategori
            String query = "INSERT INTO categories (user_id, name_category) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, currentUser.getId()); // Menggunakan ID user yang login
                stmt.setString(2, categoryName); // Menyimpan nama kategori yang dipilih

                // Eksekusi query
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Category added successfully!");
                    loadCategories(); // Muat ulang tabel kategori
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add category.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid category. Please choose a valid category.");
        }
    }
}

private void updateCategory() {
    int selectedRow = categoryTable.getSelectedRow();
    if (selectedRow != -1) {
        int categoryId = (int) categoryModel.getValueAt(selectedRow, 0); // Ambil ID kategori yang dipilih

        // Membuat pilihan kategori yang valid
        String[] categories = {"makanan", "tagihan", "liburan", "bensin"};
        String newCategoryName = (String) JOptionPane.showInputDialog(
            this,
            "Select new category name:",
            "Update Category",
            JOptionPane.QUESTION_MESSAGE,
            null,
            categories,
            categories[0] // Default option
        );

        // Debugging: Print kategori yang dipilih
        System.out.println("Selected category to update: " + newCategoryName);

        // Jika kategori baru dipilih dan tidak kosong
        if (newCategoryName != null && !newCategoryName.isEmpty()) {
            // Pastikan nilai kategori baru sesuai dengan ENUM
            if (isValidCategory(newCategoryName)) {
                String query = "UPDATE categories SET name_category = ? WHERE id = ? AND user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, newCategoryName); // Set nama kategori baru
                    stmt.setInt(2, categoryId); // Set ID kategori yang ingin diperbarui
                    stmt.setInt(3, currentUser.getId()); // ID pengguna yang login

                    // Debugging: Tampilkan query
                    System.out.println("Executing query: " + stmt);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Category updated successfully!");
                        loadCategories(); // Muat ulang kategori
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update category.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating category: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid category. Please select a valid category.");
            }
        }
    }
}

// Memeriksa apakah kategori yang dimasukkan sesuai dengan nilai ENUM
private boolean isValidCategory(String category) {
    String[] validCategories = {"makanan", "tagihan", "liburan", "bensin"};
    for (String validCategory : validCategories) {
        if (validCategory.equals(category)) {
            return true;
        }
    }
    return false;
}



    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow != -1) {
            int categoryId = (int) categoryModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM categories WHERE id = ? AND user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, categoryId);
                stmt.setInt(2, currentUser.getId());
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete category.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting category: " + e.getMessage());
            }
        }
    }

    // CRUD for Transactions (Follow similar pattern as Categories)
private void loadTransactions() {
    transactionModel.setRowCount(0); // Reset data model

    // Query SQL untuk mengambil data transaksi dan menghitung jumlah anggaran yang tersisa
    String query = """
        SELECT t.id, c.name_category, t.amount_transaction, b.amount AS amount_budgets, 
               (b.amount - t.amount_transaction) AS remaining_budget, t.date
        FROM transaction t 
        JOIN categories c ON t.category_id = c.id 
        JOIN budgets b ON t.category_id = b.category_id AND t.user_id = b.user_id 
        WHERE t.user_id = ?
    """;


    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, currentUser.getId()); // Filter berdasarkan user_id yang login

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Mengambil data transaksi dan menghitung nilai remaining_budget
                double amountTransaction = rs.getDouble("amount_transaction");
                double amountBudgets = rs.getDouble("amount_budgets");
                double remainingBudget = amountBudgets - amountTransaction;

                // Menambahkan data ke dalam tabel, hanya menampilkan Remaining Budget
                transactionModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name_category"),  // Nama kategori
                    amountTransaction,              // Jumlah transaksi
                    remainingBudget,                // Anggaran yang tersisa
                    rs.getDate("date")              // Tanggal transaksi
                });
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load transactions: " + e.getMessage());
    }
}

  private void addTransaction() {
    // Ambil daftar kategori dari database
    JComboBox<String> categoryDropdown = new JComboBox<>();
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT name_category FROM categories WHERE user_id = " + currentUser.getId())) {
        while (rs.next()) {
            categoryDropdown.addItem(rs.getString("name_category"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        return;
    }

    // Ambil daftar user_id dari database
    JComboBox<String> userIdDropdown = new JComboBox<>();
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT DISTINCT user_id FROM budgets WHERE user_id = " + currentUser.getId())) {
        while (rs.next()) {
            userIdDropdown.addItem(String.valueOf(rs.getInt("user_id")));  // Menambahkan user_id sebagai item
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load user IDs: " + e.getMessage());
        return;
    }

    // Input jumlah transaksi
    JTextField amountField = new JTextField();
    JPanel inputPanel = new JPanel(new GridLayout(3, 2));
    inputPanel.add(new JLabel("Category:"));
    inputPanel.add(categoryDropdown);
    inputPanel.add(new JLabel("Amount:"));
    inputPanel.add(amountField);

    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String selectedCategory = (String) categoryDropdown.getSelectedItem();
        String selectedUserId = (String) userIdDropdown.getSelectedItem();
        if (selectedCategory == null || selectedUserId == null) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            int categoryId = getCategoryIdByName(selectedCategory);
            int userId = Integer.parseInt(selectedUserId);

            // Periksa apakah jumlah transaksi melebihi anggaran
            double budgetAmount = getBudgetAmount(userId, categoryId); // Mengambil jumlah anggaran berdasarkan user_id dan category_id
            if (amount > budgetAmount) {
                JOptionPane.showMessageDialog(this, "Transaction amount exceeds the budget!");
                return;
            }

            // Tambahkan transaksi ke database
            String query = "INSERT INTO transaction (user_id, category_id, amount_transaction, date) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId); // Set user_id
                stmt.setInt(2, categoryId); // Set category_id
                stmt.setDouble(3, amount);   // Set amount transaksi
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                loadTransactions(); // Muat ulang data transaksi
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add transaction: " + e.getMessage());
        }
    }
}
  
  private int getCategoryIdByName(String categoryName) throws SQLException {
    String query = "SELECT id FROM categories WHERE name_category = ?";
    
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, categoryName); // Set nama kategori yang dipilih
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("id"); // Mengembalikan ID kategori yang ditemukan
        }
    }
    
    throw new SQLException("Category not found: " + categoryName); // Jika kategori tidak ditemukan
}

  
  private double getBudgetAmount(int userId, int categoryId) throws SQLException {
    String query = "SELECT amount FROM budgets WHERE user_id = ? AND category_id = ?";
    
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, userId); // Set user_id
        stmt.setInt(2, categoryId); // Set category_id
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("amount"); // Mengembalikan jumlah anggaran yang ditemukan
            }
        }
    }
    
    return 0.0; // Jika tidak ada anggaran yang ditemukan, return 0.0
}




private void updateTransaction() throws SQLException {
    int selectedRow = transactionTable.getSelectedRow();

    // Memeriksa apakah baris transaksi dipilih
    if (selectedRow != -1) {
        int transactionId = (int) transactionModel.getValueAt(selectedRow, 0); // ID transaksi
        double currentAmount = (double) transactionModel.getValueAt(selectedRow, 2); // Jumlah transaksi
        String categoryName = (String) transactionModel.getValueAt(selectedRow, 1); // Nama kategori yang dipilih

        // Ambil ID kategori berdasarkan nama kategori yang dipilih
        int categoryId = getCategoryIdByName(categoryName);

        // Input untuk jumlah transaksi yang baru
        JTextField amountField = new JTextField(String.valueOf(currentAmount)); // Menampilkan jumlah transaksi yang dipilih
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);

        // Tampilkan dialog untuk mengubah transaksi
        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Update Transaction", JOptionPane.OK_CANCEL_OPTION);

        // Jika pengguna menekan OK, lanjutkan pembaruan
        if (result == JOptionPane.OK_OPTION) {
            String amountText = amountField.getText();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Amount cannot be empty!");
                return; // Pastikan jumlah transaksi diisi
            }

            try {
                double newAmount = Double.parseDouble(amountText); // Mengambil jumlah transaksi baru
                int userId = currentUser.getId(); // ID pengguna yang login

                // Periksa apakah jumlah transaksi yang baru melebihi anggaran
                double budgetAmount = getBudgetAmount(userId, categoryId); // Ambil anggaran yang tersedia berdasarkan user_id dan category_id
                if (newAmount > budgetAmount) {
                    JOptionPane.showMessageDialog(this, "Transaction amount exceeds the budget!");
                    return; // Jika jumlah transaksi melebihi anggaran, batalkan pembaruan
                }

                // Update transaksi di database hanya untuk amount_transaction dan category_id
                String query = "UPDATE transaction SET amount_transaction = ?, category_id = ?, date = NOW() WHERE id = ? AND user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setDouble(1, newAmount);   // Set jumlah transaksi baru
                    stmt.setInt(2, categoryId);     // Set kategori baru
                    stmt.setInt(3, transactionId);  // Set ID transaksi yang ingin diubah
                    stmt.setInt(4, userId);         // Set user_id yang login

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        // Setelah update, ambil remaining budget
                        double remainingBudget = budgetAmount - newAmount;
                        JOptionPane.showMessageDialog(this, "Transaction updated successfully! Remaining Budget: " + remainingBudget);
                        loadTransactions(); // Muat ulang data transaksi
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update transaction.");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format. Please enter a valid amount.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update transaction: " + e.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a transaction to update.");
    }
}




 private void deleteTransaction() {
    // Memeriksa apakah baris transaksi dipilih
    int selectedRow = transactionTable.getSelectedRow();
    
    if (selectedRow != -1) {
        // Mengambil ID transaksi yang dipilih
        int transactionId = (int) transactionModel.getValueAt(selectedRow, 0); // ID transaksi

        // Menampilkan konfirmasi sebelum menghapus transaksi
        int confirmation = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this transaction?", 
                "Delete Transaction", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            // Hapus transaksi dari database
            String query = "DELETE FROM transaction WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, transactionId); // Set ID transaksi yang akan dihapus

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Transaction deleted successfully!");
                    loadTransactions(); // Muat ulang data transaksi setelah penghapusan
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete transaction.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete transaction: " + e.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
    }
}


    // CRUD for Budgets (Follow similar pattern as Categories)
private void loadBudgets() {
    if (currentUser == null) {
        JOptionPane.showMessageDialog(this, "No user is logged in.");
        return; // Pastikan currentUser sudah didefinisikan sebelum melanjutkan
    }

    budgetModel.setRowCount(0); // Reset data model
    String query = "SELECT c.name_category, b.amount " +
                   "FROM budgets b " +
                   "JOIN categories c ON b.category_id = c.id " +
                   "WHERE b.user_id = ?";  // Mengambil data berdasarkan user_id yang login

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, currentUser.getId()); // Filter berdasarkan user_id yang login
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Menambah data kategori dan jumlah anggaran ke dalam model
                budgetModel.addRow(new Object[]{
                    rs.getString("name_category"), // Hanya tampilkan nama kategori
                    rs.getDouble("amount")         // Tampilkan jumlah anggaran
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load budgets: " + e.getMessage());
        e.printStackTrace(); // Menambahkan trace untuk debugging lebih lanjut
    }
}



private void addBudget() {
    // Dropdown untuk daftar kategori
    JComboBox<String> categoryDropdown = new JComboBox<>();
    Map<String, Integer> categoryMap = new HashMap<>(); // Map untuk menyimpan kategori dan ID-nya

    // Query untuk mengambil kategori berdasarkan user_id
    String query = "SELECT id, name_category FROM categories WHERE user_id = " + currentUser.getId();
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            String categoryName = rs.getString("name_category");
            int categoryId = rs.getInt("id");
            categoryDropdown.addItem(categoryName); // Tambahkan nama kategori ke dropdown
            categoryMap.put(categoryName, categoryId); // Simpan nama kategori dan ID-nya ke map
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        return;
    }

    // Input untuk jumlah budget
    JTextField amountField = new JTextField();

    // Panel untuk form input
    JPanel inputPanel = new JPanel(new GridLayout(3, 2));
    inputPanel.add(new JLabel("Category:"));
    inputPanel.add(categoryDropdown);
    inputPanel.add(new JLabel("Amount:"));
    inputPanel.add(amountField);

    // Tampilkan dialog input
    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Budget", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String selectedCategory = (String) categoryDropdown.getSelectedItem();
        
        // Pastikan kategori dipilih
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Please select a category.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText()); // Validasi jumlah

            // Ambil ID kategori dari Map berdasarkan nama kategori yang dipilih
            int categoryId = categoryMap.get(selectedCategory); 

            // Ambil ID pengguna dari currentUser
            int userId = currentUser.getId(); 

            // Masukkan data budget ke tabel budgets
            String queryInsert = "INSERT INTO budgets (user_id, category_id, amount, name_category) VALUES (" +
                                 userId + ", " + categoryId + ", " + amount + ", '" + selectedCategory + "')";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(queryInsert);
                JOptionPane.showMessageDialog(this, "Budget added successfully!");
                loadBudgets(); // Muat ulang data budget setelah ditambahkan
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add budget: " + e.getMessage());
        }
    }
}

private void updateBudget() {
    // Memeriksa apakah baris di tabel budget dipilih
    int selectedRow = budgetTable.getSelectedRow();
    if (selectedRow != -1) {
        // Ambil kategori dan jumlah budget dari baris yang dipilih
        String categoryName = (String) budgetModel.getValueAt(selectedRow, 0); // Kategori yang dipilih
        double currentBudget = (double) budgetModel.getValueAt(selectedRow, 1); // Jumlah budget saat ini

        // Input baru untuk nilai budget
        JTextField budgetField = new JTextField(String.valueOf(currentBudget)); // Konversi currentBudget menjadi String
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("New Budget Amount:"));
        inputPanel.add(budgetField);

        // Menampilkan dialog untuk mengubah budget
        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Update Budget", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Mendapatkan input jumlah budget baru
                String newBudgetText = budgetField.getText();
                double newBudget = Double.parseDouble(newBudgetText); // Pastikan input diubah menjadi angka (double)

                // Pastikan bahwa amount yang baru valid dan berbeda dengan yang lama
                if (newBudget <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.");
                    return;
                }

                // Query untuk memperbarui amount pada tabel budgets berdasarkan kategori dan user_id
                String query = """
                    UPDATE budgets b
                    JOIN categories c ON b.category_id = c.id
                    SET b.amount = ?
                    WHERE b.user_id = ? AND c.name_category = ?
                """;

                // Melakukan query untuk update
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setDouble(1, newBudget); // Set nilai baru amount
                    stmt.setInt(2, currentUser.getId()); // Set user_id berdasarkan pengguna yang sedang login
                    stmt.setString(3, categoryName); // Set nama kategori yang dipilih

                    // Eksekusi update
                    int rowsUpdated = stmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Budget updated successfully!");
                        loadBudgets(); // Memuat ulang tabel budgets
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update the budget.");
                    }
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please enter a valid number.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update budget: " + e.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a budget to update.");
    }
}


private void deleteBudget() {
    // Memeriksa apakah baris anggaran dipilih
    int selectedRow = budgetTable.getSelectedRow();

    if (selectedRow != -1) {
        // Mengambil nama kategori dan jumlah anggaran yang dipilih
        String categoryName = (String) budgetModel.getValueAt(selectedRow, 0); // Nama kategori yang dipilih
        double amount = (double) budgetModel.getValueAt(selectedRow, 1); // Jumlah anggaran yang dipilih

        // Menampilkan konfirmasi sebelum menghapus anggaran
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the budget for " + categoryName + "?",
                "Delete Budget",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            // Query untuk menghapus anggaran berdasarkan nama kategori dan user_id
            String query = "DELETE FROM budgets WHERE category_id = ? AND user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                // Dapatkan category_id berdasarkan nama kategori yang dipilih
                int categoryId = getCategoryIdByName(categoryName);
                stmt.setInt(1, categoryId); // Set category_id yang dipilih
                stmt.setInt(2, currentUser.getId()); // Set user_id yang login

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Budget deleted successfully!");
                    loadBudgets(); // Muat ulang data anggaran setelah penghapusan
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete budget.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete budget: " + e.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a budget to delete.");
    }
}

private void logout() {
    // Menghapus data pengguna yang sedang login
    currentUser = null;

    // Menampilkan pesan logout sukses
    JOptionPane.showMessageDialog(this, "Sure you want to logged out?");

    // Menutup jendela saat ini
    this.dispose();

    // Membuka jendela login baru
    LoginForm loginForm = new LoginForm(); // Gantilah LoginFrame dengan kelas login Anda
    loginForm.setVisible(true); // Menampilkan jendela login
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User currentUser = new User("user123", 1); // Example User
            new Dashboardcrud(currentUser).setVisible(true);
        });
    }
    private double getBudgetAmountByCategory(int categoryId) {
    String query = "SELECT amount FROM budgets WHERE category_id = ?";  // Query untuk mengambil amount anggaran berdasarkan category_id

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, categoryId);  // Set category_id ke query

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("amount");  // Ambil nilai amount dari result set
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching budget amount: " + e.getMessage());
    }

    return 0.0; // Jika tidak ada anggaran yang ditemukan, return 0.0
}

}
