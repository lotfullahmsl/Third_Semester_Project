import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// UserSession Class to Track Logged-In User
class UserSession {
    private static UserSession instance;
    private String username;

    private UserSession() { } // Private constructor

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void clearSession() {
        username = null;
    }
}

public class SignInForm {
    private JPanel mainPanel;
    private JPanel secoundPanel;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btRegister;
    private JButton btlogin;

    // Constructor for SignInForm
    public SignInForm() {
        // Action for the login button
        btlogin.addActionListener(e -> {
            // Get username and password entered by the user
            String username = tfUsername.getText();
            String password = new String(pfPassword.getPassword());

            // Check if the fields are empty
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
                return;
            }

            // Hash the entered password using SHA-256 (or the same method as during registration)
            String hashedPassword = hashPassword(password);

            // Check if the username and hashed password are correct in the database
            if (isValidUser(username, hashedPassword)) {
                JOptionPane.showMessageDialog(null, "Login Successful");
                // Set the user session after successful login
                UserSession session = UserSession.getInstance();
                session.setUser(username);
                System.out.println("Logged in as: " + session.getUsername());

                // Proceed with login actions (e.g., open another form or close the sign-in form)
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                frame.dispose();
                ResourceRecomanditionPage.main(new String[]{});
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password");
            }
        });

        // Action for the register button to go to SignUpForm
        btRegister.addActionListener(e -> {
            SignUpForm.main(new String[]{});
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            frame.dispose();
        });
    }

    // Method to validate the username and hashed password from the database
    private boolean isValidUser(String username, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb"; // Database URL
        String user = "root"; // Database username
        String pass = "Muslimwal@2004"; // Database password

        String query = "SELECT * FROM register WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);  // Compare with the hashed password stored in the database

            // Execute the query and get the result
            ResultSet rs = stmt.executeQuery();

            // Check if a matching record was found
            return rs.next();  // If there's a record, the login is valid

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
            return false;
        }
    }

    // Method to hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null, "Error hashing password");
            return null;
        }
    }

    // Main method to run the SignIn form
    public static void main(String[] args) {
        JFrame frame = new JFrame("SignInForm");
        frame.setContentPane(new SignInForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);  // Set size of the window
        frame.setLocationRelativeTo(null);  // Center the window
        frame.setVisible(true);
    }
}

