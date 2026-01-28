// LoginScreen.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JRadioButton adminRadio, memberRadio;
    private ButtonGroup loginTypeGroup;

    public LoginScreen(JFrame frame) {
        setLayout(new GridLayout(4, 2, 10, 10));
        
        Font font = new Font("Arial", Font.PLAIN, 24);

        adminRadio = new JRadioButton("Admin");
        adminRadio.setFont(font);
        memberRadio = new JRadioButton("Membership", true);
        memberRadio.setFont(font);
        loginTypeGroup = new ButtonGroup();
        loginTypeGroup.add(adminRadio);
        loginTypeGroup.add(memberRadio);

        add(adminRadio);
        add(memberRadio);
        
        // Login panel Username + Password Font
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(font);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(font);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(font);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setFont(font);
        add(loginButton);

        loginButton.addActionListener(e -> login(frame));
    }

    private void login(JFrame frame) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (adminRadio.isSelected()) {
            // Admin login
            String dbUser = "root";
            String dbPass = "1234";
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/newmadangdb", dbUser, dbPass)) {
                JOptionPane.showMessageDialog(this, "Admin login successful!");
                frame.setContentPane(new AdminPanel(frame));
                frame.revalidate();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Admin login failed: " + ex.getMessage());
            }
        } else {
            // Member login
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM Users WHERE username=? AND password=?")) {
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                	int user_id = rs.getInt("user_id");
                	
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    frame.setContentPane(new MemberPanel(user_id,frame));
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }
}