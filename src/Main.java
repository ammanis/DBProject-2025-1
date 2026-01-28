// Main.java
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel (optional)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore if not available
        }

        // Create the main frame
        JFrame frame = new JFrame("Camper Van Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // You can set your preferred size

        // Set the content pane to the LoginPanel
        frame.setContentPane(new LoginScreen(frame));

        // Center the frame and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
