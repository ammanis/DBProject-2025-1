// MemberPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class MemberPanel extends JPanel {
    private int userId; // Set this after login!
    private JTabbedPane tabbedPane;
    private DefaultTableModel searchModel, rentalModel;

    public MemberPanel(int userId,	JFrame parentFrame) {
        this.userId = userId;
        setLayout(new BorderLayout());
        
     // Top panel with logout button aligned right
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        rightPanel.add(logoutBtn);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        // 1. Campervan Search Tab
        JPanel searchTab = new JPanel(new BorderLayout());
        searchModel = new DefaultTableModel(new String[]{"ID", "Name", "Capacity", "Fuel", "Status", "Daily Cost"}, 0);
        JTable searchTable = new JTable(searchModel);
        JPanel searchFilterPanel = new JPanel();
        JTextField nameField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        searchFilterPanel.add(new JLabel("Name:")); searchFilterPanel.add(nameField); searchFilterPanel.add(searchBtn);
        searchTab.add(searchFilterPanel, BorderLayout.NORTH);
        searchTab.add(new JScrollPane(searchTable), BorderLayout.CENTER);

        // 2. Availability Tab
        JButton showAvailBtn = new JButton("Show Availability");
        JTextArea availArea = new JTextArea(5, 40);
        availArea.setEditable(false);
        JPanel availPanel = new JPanel(new BorderLayout());
        availPanel.add(showAvailBtn, BorderLayout.NORTH);
        availPanel.add(new JScrollPane(availArea), BorderLayout.CENTER);
        searchTab.add(availPanel, BorderLayout.SOUTH);

        // 3. Rental Registration Tab
        JPanel rentTab = new JPanel(new GridLayout(0,2));
        JTextField camperIdField = new JTextField();
        JTextField startDateField = new JTextField("YYYY-MM-DD");
        JTextField endDateField = new JTextField("YYYY-MM-DD");
        JButton rentBtn = new JButton("Rent");
        rentTab.add(new JLabel("Camper ID:")); rentTab.add(camperIdField);
        rentTab.add(new JLabel("Start Date:")); rentTab.add(startDateField);
        rentTab.add(new JLabel("End Date:")); rentTab.add(endDateField);
        rentTab.add(new JLabel("")); rentTab.add(rentBtn);

        // 4. Show/Manage Rentals Tab
        JPanel myRentalTab = new JPanel(new BorderLayout());
        rentalModel = new DefaultTableModel(new String[]{"Rental ID", "Camper Name", "Start", "End", "Status"}, 0);
        JTable rentalTable = new JTable(rentalModel);
        JButton deleteBtn = new JButton("Delete Selected");
        JButton changeVanBtn = new JButton("Change CamperVan");
        JButton changeDateBtn = new JButton("Change Dates");
        JPanel rentalBtnPanel = new JPanel();
        rentalBtnPanel.add(deleteBtn); rentalBtnPanel.add(changeVanBtn); rentalBtnPanel.add(changeDateBtn);
        myRentalTab.add(new JScrollPane(rentalTable), BorderLayout.CENTER);
        myRentalTab.add(rentalBtnPanel, BorderLayout.SOUTH);

        // 5. Maintenance Request Tab
        JPanel maintTab = new JPanel(new GridLayout(0,2));
        JTextField rentalIdField = new JTextField();
        JTextField garageIdField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JButton reqMaintBtn = new JButton("Request Maintenance");
        maintTab.add(new JLabel("Rental ID:")); maintTab.add(rentalIdField);
        maintTab.add(new JLabel("Garage ID:")); maintTab.add(garageIdField);
        maintTab.add(new JLabel("Date:")); maintTab.add(dateField);
        maintTab.add(new JLabel("")); maintTab.add(reqMaintBtn);

        // Add tabs
        tabbedPane.addTab("Search Campervan", searchTab);
        // tabbedPane.addTab("Rental Availability", availTab);
        tabbedPane.addTab("Register Rental", rentTab);
        tabbedPane.addTab("My Rentals", myRentalTab);
        tabbedPane.addTab("Request Maintenance", maintTab);

        add(tabbedPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        
        logoutBtn.addActionListener(e -> {
            parentFrame.setContentPane(new LoginScreen(parentFrame));
            parentFrame.revalidate();
        });

        // 1. Campervan Search
        searchBtn.addActionListener(e -> {
            searchModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT camper_id, name, capacity, fuel_type, status, daily_cost FROM CamperVan WHERE name LIKE ?")) {
                ps.setString(1, "%" + nameField.getText().trim() + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    searchModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getDouble(6)
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        // 2. Show Rental Availability
        showAvailBtn.addActionListener(e -> {
            int row = searchTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a campervan from the table.");
                return;
            }
            int camperId = (int) searchModel.getValueAt(row, 0);
            availArea.setText("");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT start_date, end_date FROM Rental WHERE camper_id=?")) {
                ps.setInt(1, camperId);
                ResultSet rs = ps.executeQuery();
                availArea.append("Unavailable dates for camper #" + camperId + ":\n");
                boolean found = false;
                while (rs.next()) {
                    availArea.append(rs.getString(1) + " ~ " + rs.getString(2) + "\n");
                    found = true;
                }
                if (!found) availArea.append("No reservations. All dates available.\n");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });


        // 3. Register Rental
        rentBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement check = conn.prepareStatement(
                     "SELECT COUNT(*) FROM Rental WHERE camper_id=? AND NOT (end_date < ? OR start_date > ?)")) {
                int camperId = Integer.parseInt(camperIdField.getText().trim());
                String start = startDateField.getText().trim();
                String end = endDateField.getText().trim();
                check.setInt(1, camperId); check.setString(2, start); check.setString(3, end);
                ResultSet rs = check.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Selected dates are not available.");
                    return;
                }
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Rental (user_id, camper_id, start_date, end_date) VALUES (?, ?, ?, ?)");
                ps.setInt(1, userId); ps.setInt(2, camperId); ps.setString(3, start); ps.setString(4, end);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Rental registered!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        // 4. Show/Manage Rentals
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) loadMyRentals();
        });

        deleteBtn.addActionListener(e -> {
            int[] rows = rentalTable.getSelectedRows();
            if (rows.length == 0) return;
            try (Connection conn = DBConnection.getConnection()) {
                for (int row : rows) {
                    int rentalId = (int) rentalModel.getValueAt(row, 0);
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM Rental WHERE rental_id=? AND user_id=?");
                    ps.setInt(1, rentalId); ps.setInt(2, userId);
                    ps.executeUpdate();
                }
                loadMyRentals();
                JOptionPane.showMessageDialog(this, "Deleted selected rentals.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        changeVanBtn.addActionListener(e -> {
            int row = rentalTable.getSelectedRow();
            if (row == -1) return;
            int rentalId = (int) rentalModel.getValueAt(row, 0);
            String newCamperId = JOptionPane.showInputDialog(this, "Enter new camper ID:");
            if (newCamperId == null) return;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Rental SET camper_id=? WHERE rental_id=? AND user_id=?")) {
                ps.setInt(1, Integer.parseInt(newCamperId));
                ps.setInt(2, rentalId);
                ps.setInt(3, userId);
                ps.executeUpdate();
                loadMyRentals();
                JOptionPane.showMessageDialog(this, "Camper changed.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        changeDateBtn.addActionListener(e -> {
            int row = rentalTable.getSelectedRow();
            if (row == -1) return;
            int rentalId = (int) rentalModel.getValueAt(row, 0);
            String newStart = JOptionPane.showInputDialog(this, "Enter new start date (YYYY-MM-DD):");
            String newEnd = JOptionPane.showInputDialog(this, "Enter new end date (YYYY-MM-DD):");
            if (newStart == null || newEnd == null) return;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Rental SET start_date=?, end_date=? WHERE rental_id=? AND user_id=?")) {
                ps.setString(1, newStart);
                ps.setString(2, newEnd);
                ps.setInt(3, rentalId);
                ps.setInt(4, userId);
                ps.executeUpdate();
                loadMyRentals();
                JOptionPane.showMessageDialog(this, "Rental dates changed.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        // 5. Maintenance Request
        reqMaintBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Maintenance (camper_id, maintenance_type, maintenance_date, garage_id, user_id) " +
                     "SELECT camper_id, '¿ÜºÎ', ?, ?, ? FROM Rental WHERE rental_id=? AND user_id=?")) {
                ps.setString(1, dateField.getText().trim());
                ps.setInt(2, Integer.parseInt(garageIdField.getText().trim()));
                ps.setInt(3, userId);
                ps.setInt(4, Integer.parseInt(rentalIdField.getText().trim()));
                ps.setInt(5, userId);
                int affected = ps.executeUpdate();
                if (affected > 0)
                    JOptionPane.showMessageDialog(this, "Maintenance requested.");
                else
                    JOptionPane.showMessageDialog(this, "Rental not found or not yours.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });
    }

    private void loadMyRentals() {
        rentalModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT r.rental_id, cv.name, r.start_date, r.end_date, cv.status " +
                 "FROM Rental r JOIN CamperVan cv ON r.camper_id=cv.camper_id WHERE r.user_id=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rentalModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }
}
