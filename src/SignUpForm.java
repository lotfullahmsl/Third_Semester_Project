import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUpForm {
    private JPanel mainPanel;
    private JPanel secoundPanel;
    private JTextField tfName;
    private JTextField tfUsername;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JPasswordField pfCPassword;
    private JButton btRegister;
    private JButton btnSignin;
    // Panel size
    private static final int WIDTH = 600;
    private static final int HEIGHT = 450;

    // Constructor
    public SignUpForm() {
        // Ensure that the name, username, and email are not empty
        btRegister.addActionListener(e -> {
            if (tfName.getText().isEmpty() || tfUsername.getText().isEmpty() || tfEmail.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
                return;
            }

            // Validate email format
            String email = tfEmail.getText();
            if (!email.matches("^(.+)@(.+)$")) {
                JOptionPane.showMessageDialog(null, "Email is not valid");
                return;
            }

            // Ensure passwords match
            String password = new String(pfPassword.getPassword());
            String cPassword = new String(pfCPassword.getPassword());
            if (!password.equals(cPassword)) {
                JOptionPane.showMessageDialog(null, "Password Not Matched");
                return;
            }

            // Ensure password strength
            if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\W.*")) {
                JOptionPane.showMessageDialog(null, "Password is Weak");
                return;
            }

            // Encrypt the password
            String encryptedPassword = encryptPassword(password);

            // Check if username or email already exists
            if (isUsernameOrEmailExists(tfUsername.getText(), tfEmail.getText())) {
                return; // If either username or email exists, return early
            }

            // Insert data into the database
            if (insertIntoDatabase(tfName.getText(), tfUsername.getText(), tfEmail.getText(), encryptedPassword)) {
                JOptionPane.showMessageDialog(null, "Registration Successful");
                onUserSignUp(tfUsername.getText()); // Create a table for the user
            } else {
                JOptionPane.showMessageDialog(null, "Registration Failed");
            }
        });

        // Placeholder for transitioning to the sign-in form
        btnSignin.addActionListener(e -> {
            // go to sign in form
            SignInForm.main(new String[]{});
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            frame.dispose();
        });
    }

    // Method to encrypt the password using SHA-256
    private String encryptPassword(String password) {
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
            JOptionPane.showMessageDialog(null, "Error encrypting password");
            return null;
        }
    }

    // Method to check if username or email already exists in the database
    private boolean isUsernameOrEmailExists(String username, String email) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String pass = "Muslimwal@2004"; // Replace with your database password

        String query = "SELECT COUNT(*) FROM register WHERE username = ? OR email = ?";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the values in the query
            stmt.setString(1, username);
            stmt.setString(2, email);

            // Execute the query
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Username or Email is already in use");
                return true;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
        return false;
    }

    // Method to insert data into the database
    private boolean insertIntoDatabase(String name, String username, String email, String password) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String pass = "Muslimwal@2004"; // Replace with your database password

        String query = "INSERT INTO register (name, username, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the values in the query
            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, password);

            // Execute the query
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;


        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
            return false;
        }
    }

    // Method to create a new table for the user in the tsprojdb schema
    private void createUserTable(String username) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String pass = "Muslimwal@2004"; // Replace with your database password

        String tableName = username + "_data"; // User-specific table name
        String queryCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "track_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "action ENUM('login', 'like', 'rating', 'comment', 'view') NOT NULL, " +
                "article_id INT DEFAULT NULL, " +
                "rating DECIMAL(2,1) DEFAULT NULL, " +
                "view_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "comment TEXT DEFAULT NULL, " +
                "INDEX (username), " +
                "FOREIGN KEY (article_id) REFERENCES tsprojdb.resources(resource_id)" +
                ");";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            // Execute the query to create the table
            stmt.executeUpdate(queryCreateTable);
            JOptionPane.showMessageDialog(null, "Table '" + tableName + "' created in 'tsprojdb' schema.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Table Creation Error: " + ex.getMessage());
        }
    }

    // Call this method after a successful registration
    private void onUserSignUp(String username) {
        createUserTable(username);  // Create table for the user
        // Additional sign-up logic here
    }

    // Main method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Sign Up Form");
        frame.setContentPane(new SignUpForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}