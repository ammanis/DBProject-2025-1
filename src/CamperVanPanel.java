// CamperVanPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CamperVanPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public CamperVanPanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"VanID", "Model", "Capacity", "PricePerDay"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT camper_id, name, capacity, daily_cost FROM CamperVan")) {

            model.setRowCount(0); // clear table
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("camper_id"),       // VanID
                    rs.getString("name"),         // Model
                    rs.getInt("capacity"),        // Capacity
                    rs.getDouble("daily_cost")    // PricePerDay
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }
}
