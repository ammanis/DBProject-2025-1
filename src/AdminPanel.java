// AdminPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class AdminPanel extends JPanel {
    private JComboBox<String> tableSelector;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea sqlArea;

    private static final String[] TABLES = {
        "Users", "Customer", "Company", "CamperVan", "Rental", "Employee",
        "Garage", "Maintenance", "Supplier", "Part", "PartUsage"
    };

    public AdminPanel(JFrame parentFrame) {
        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout());
        
        tableSelector = new JComboBox<>(TABLES);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnInitDB = new JButton("Initialize DB");
        JButton btnInsert = new JButton("Insert");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnViewAll = new JButton("View Table");
        JButton logoutBtn = new JButton("Logout");

        leftPanel.add(tableSelector);
        leftPanel.add(btnInitDB);
        leftPanel.add(btnInsert);
        leftPanel.add(btnUpdate);
        leftPanel.add(btnDelete);
        leftPanel.add(btnViewAll);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(logoutBtn);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table display
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Bottom SQL area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        sqlArea = new JTextArea(3, 50);
        JButton btnExecSQL = new JButton("Execute SELECT");
        JButton btnSampleQueries = new JButton("Sample Queries");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnExecSQL);
        btnPanel.add(btnSampleQueries);

        bottomPanel.add(new JLabel("Enter SELECT Query:"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(sqlArea), BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        btnInitDB.addActionListener(e -> initializeDatabase());
        btnViewAll.addActionListener(e -> viewTable());
        btnInsert.addActionListener(e -> insertRow());
        btnUpdate.addActionListener(e -> updateRow());
        btnDelete.addActionListener(e -> deleteRow());
        btnExecSQL.addActionListener(e -> executeSelectQuery());
        btnSampleQueries.addActionListener(e -> showSampleQueries());
        logoutBtn.addActionListener(e -> {
        	parentFrame.setContentPane(new LoginScreen(parentFrame));
        	parentFrame.revalidate();
        });
    }

    // 1. Database Initialization
    private void initializeDatabase() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to re-initialize the database? This will erase all data.", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String[] sqls = getDbInitSql();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                stmt.execute(sql);
            }
            JOptionPane.showMessageDialog(this, "Database initialized!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    // 2. View Table
    private void viewTable() {
        String table = (String) tableSelector.getSelectedItem();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {
            fillTable(rs);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 3. Insert Row
    private void insertRow() {
        String table = (String) tableSelector.getSelectedItem();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " LIMIT 1")) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            JPanel panel = new JPanel(new GridLayout(colCount, 2));
            JTextField[] fields = new JTextField[colCount];
            for (int i = 1; i <= colCount; i++) {
                panel.add(new JLabel(meta.getColumnName(i)));
                fields[i - 1] = new JTextField();
                panel.add(fields[i - 1]);
            }
            int ok = JOptionPane.showConfirmDialog(this, panel, "Insert into " + table, JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                StringBuilder cols = new StringBuilder();
                StringBuilder vals = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    if (fields[i - 1].getText().trim().isEmpty()) continue;
                    if (cols.length() > 0) { cols.append(","); vals.append(","); }
                    cols.append(meta.getColumnName(i));
                    vals.append("?");
                }
                String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int idx = 1;
                    for (int i = 1; i <= colCount; i++) {
                        if (fields[i - 1].getText().trim().isEmpty()) continue;
                        ps.setString(idx++, fields[i - 1].getText().trim());
                    }
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Inserted!");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 4. Update Row
    private void updateRow() {
        String table = (String) tableSelector.getSelectedItem();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " LIMIT 1")) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            JPanel panel = new JPanel(new GridLayout(colCount + 1, 2));
            JTextField[] fields = new JTextField[colCount];
            for (int i = 1; i <= colCount; i++) {
                panel.add(new JLabel(meta.getColumnName(i)));
                fields[i - 1] = new JTextField();
                panel.add(fields[i - 1]);
            }
            panel.add(new JLabel("WHERE condition (e.g., user_id=1):"));
            JTextField whereField = new JTextField();
            panel.add(whereField);

            int ok = JOptionPane.showConfirmDialog(this, panel, "Update " + table, JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                StringBuilder setClause = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    if (fields[i - 1].getText().trim().isEmpty()) continue;
                    if (setClause.length() > 0) setClause.append(", ");
                    setClause.append(meta.getColumnName(i)).append("=?");
                }
                if (setClause.length() == 0) {
                    JOptionPane.showMessageDialog(this, "No fields to update.");
                    return;
                }
                String sql = "UPDATE " + table + " SET " + setClause + " WHERE " + whereField.getText().trim();
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int idx = 1;
                    for (int i = 1; i <= colCount; i++) {
                        if (fields[i - 1].getText().trim().isEmpty()) continue;
                        ps.setString(idx++, fields[i - 1].getText().trim());
                    }
                    int affected = ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Updated rows: " + affected);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 5. Delete Row
    private void deleteRow() {
        String table = (String) tableSelector.getSelectedItem();
        String condition = JOptionPane.showInputDialog(this, "Enter WHERE condition (e.g., user_id=1):", "Delete from " + table, JOptionPane.QUESTION_MESSAGE);
        if (condition == null || condition.trim().isEmpty()) return;
        String sql = "DELETE FROM " + table + " WHERE " + condition;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int affected = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Deleted rows: " + affected);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 6. Arbitrary SELECT Query
    private void executeSelectQuery() {
        String sql = sqlArea.getText().trim();
        if (!sql.toLowerCase().startsWith("select")) {
            JOptionPane.showMessageDialog(this, "Only SELECT queries are allowed.");
            return;
        }
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            fillTable(rs);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Utility: Fill JTable from ResultSet
    private void fillTable(ResultSet rs) throws SQLException {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            tableModel.addColumn(meta.getColumnName(i));
        }
        while (rs.next()) {
            Object[] row = new Object[colCount];
            for (int i = 0; i < colCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            tableModel.addRow(row);
        }
    }

    // 7. Show Sample Queries
    private void showSampleQueries() {
        String[] queries = {
            "-- 1. Users, Rental, CamperVan, Company (subquery, group by)\n" +
            "SELECT u.username, c.name AS company_name, cv.name AS camper_name, COUNT(r.rental_id) AS rental_count\n" +
            "FROM Users u\n" +
            "JOIN Rental r ON u.user_id = r.user_id\n" +
            "JOIN CamperVan cv ON r.camper_id = cv.camper_id\n" +
            "JOIN Company c ON cv.company_id = c.company_id\n" +
            "WHERE u.user_id IN (SELECT user_id FROM Rental WHERE rental_cost > 100000)\n" +
            "GROUP BY u.username, c.name, cv.name;",

            "-- 2. Maintenance, PartUsage, Part, Supplier (group by)\n" +
            "SELECT cv.name AS camper_name, m.maintenance_type, m.maintenance_date, p.name AS part_name, s.name AS supplier_name, pu.quantity_used\n" +
            "FROM CamperVan cv\n" +
            "JOIN Maintenance m ON cv.camper_id = m.camper_id\n" +
            "JOIN PartUsage pu ON m.maintenance_id = pu.maintenance_id\n" +
            "JOIN Part p ON pu.part_id = p.part_id\n" +
            "JOIN Supplier s ON p.supplier_id = s.supplier_id\n" +
            "WHERE m.maintenance_type = 'ï¿½ï¿½ï¿½ï¿½'\n" +
            "GROUP BY cv.name, m.maintenance_type, m.maintenance_date, p.name, s.name, pu.quantity_used;",

            "-- 3. CamperVan, Maintenance, Garage (external maintenance)\n" +
            "SELECT cv.name AS camper_name, m.maintenance_date, g.name AS garage_name, g.address, g.phone, g.contact_person\n" +
            "FROM CamperVan cv\n" +
            "JOIN Maintenance m ON cv.camper_id = m.camper_id\n" +
            "JOIN Garage g ON m.garage_id = g.garage_id\n" +
            "WHERE m.maintenance_type = 'ï¿½Üºï¿½'\n" +
            "GROUP BY cv.name, m.maintenance_date, g.name, g.address, g.phone, g.contact_person;"
        };

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select a sample query:",
            "Sample Queries",
            JOptionPane.PLAIN_MESSAGE,
            null,
            queries,
            queries[0]
        );
        if (selected != null) {
            sqlArea.setText(selected.replaceAll("--.*\n", "")); // Remove comments for execution
        }
    }

    // Utility: DB Initialization SQL (drop/create tables)
    private String[] getDbInitSql() {
        return new String[]{
            "SET FOREIGN_KEY_CHECKS=0;",
            "DROP TABLE IF EXISTS PartUsage;",
            "DROP TABLE IF EXISTS Part;",
            "DROP TABLE IF EXISTS Supplier;",
            "DROP TABLE IF EXISTS Maintenance;",
            "DROP TABLE IF EXISTS Garage;",
            "DROP TABLE IF EXISTS Employee;",
            "DROP TABLE IF EXISTS Rental;",
            "DROP TABLE IF EXISTS CamperVan;",
            "DROP TABLE IF EXISTS Company;",
            "DROP TABLE IF EXISTS Customer;",
            "DROP TABLE IF EXISTS Users;",
            "SET FOREIGN_KEY_CHECKS=1;",

            // Users table
            "CREATE TABLE Users (user_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(100) NOT NULL, name VARCHAR(100) NOT NULL, phone VARCHAR(20) UNIQUE NULL, email VARCHAR(100) UNIQUE NULL, password VARCHAR(255) NOT NULL);",
            // Customer table
            "CREATE TABLE Customer (user_id INT PRIMARY KEY, license_number VARCHAR(50), address VARCHAR(255), previous_rental_date DATE, preferred_camper_type VARCHAR(50), FOREIGN KEY (user_id) REFERENCES Users(user_id));",
            // Company table
            "CREATE TABLE Company (company_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, address VARCHAR(255), phone VARCHAR(20), contact_person VARCHAR(50), email VARCHAR(100));",
            // CamperVan table
            "CREATE TABLE CamperVan (camper_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, license_plate VARCHAR(20) UNIQUE NOT NULL, capacity INT, fuel_type VARCHAR(20), status ENUM('´ë¿© °¡´É', '´ë¿© Áß', 'Á¤ºñ Áß'), daily_cost DECIMAL(10,2) NOT NULL, registration_date DATE, company_id INT, FOREIGN KEY (company_id) REFERENCES Company(company_id));",
            // Rental table
            "CREATE TABLE Rental (rental_id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, camper_id INT NOT NULL, start_date DATE NOT NULL, end_date DATE NOT NULL, rental_cost DECIMAL(10,2), additional_charge DECIMAL(10,2), total_cost DECIMAL(10,2), payment_due_date DATE, FOREIGN KEY (user_id) REFERENCES Users(user_id), FOREIGN KEY (camper_id) REFERENCES CamperVan(camper_id));",
            // Employee table
            "CREATE TABLE Employee (employee_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, phone VARCHAR(20), address VARCHAR(255), salary DECIMAL(10,2), department VARCHAR(50), dependents INT DEFAULT 0, role ENUM('°ü¸®', '»ç¹«', 'Á¤ºñ'));",
            // Garage table
            "CREATE TABLE Garage (garage_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, address VARCHAR(255), phone VARCHAR(20), contact_person VARCHAR(50), email VARCHAR(100));",
            // Maintenance table
            "CREATE TABLE Maintenance (maintenance_id INT AUTO_INCREMENT PRIMARY KEY, camper_id INT NOT NULL, maintenance_type ENUM('³»ºÎ','¿ÜºÎ') NOT NULL, maintenance_date DATE NOT NULL, maintenance_time INT, cost DECIMAL(10,2), employee_id INT, garage_id INT, user_id INT, FOREIGN KEY (camper_id) REFERENCES CamperVan(camper_id), FOREIGN KEY (employee_id) REFERENCES Employee(employee_id), FOREIGN KEY (garage_id) REFERENCES Garage(garage_id), FOREIGN KEY (user_id) REFERENCES Users(user_id));",
            // Supplier table
            "CREATE TABLE Supplier (supplier_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, address VARCHAR(255), phone VARCHAR(20));",
            // Part table
            "CREATE TABLE Part (part_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, unit_price DECIMAL(10,2), stock_quantity INT, supply_date DATE, supplier_id INT, FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id));",
            // PartUsage table
            "CREATE TABLE PartUsage(part_usage_id INT AUTO_INCREMENT PRIMARY KEY, maintenance_id INT NOT NULL, part_id INT NOT NULL, quantity_used INT NOT NULL, FOREIGN KEY (maintenance_id) REFERENCES Maintenance(maintenance_id), FOREIGN KEY (part_id) REFERENCES Part(part_id));"
        };
    }
}
