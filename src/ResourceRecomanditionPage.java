import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class ResourceRecomanditionPage {
    private JPanel mainPanel;
    private JTable tbResource;
    private JScrollPane SPResource;
    private JButton btnDetail;
    private JButton btnLogOut;
    private JLabel lblUsername;
    private JComboBox<String> cbTopicSelection;
    private JButton btnCheck;
    private JButton btnLoadAll;
    private JButton btnTopViewed;

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    public ResourceRecomanditionPage() {
        cbTopicSelection.addItem("All");
        cbTopicSelection.addItem("Java");
        cbTopicSelection.addItem("DSA");
        cbTopicSelection.addItem("DBMS");
        cbTopicSelection.addItem("Python");
        cbTopicSelection.addItem("ML");
        cbTopicSelection.addItem("OS");
        cbTopicSelection.addItem("Web_Development");
        cbTopicSelection.addItem("Mobile_Development");
        cbTopicSelection.addItem("NoSQL");
        cbTopicSelection.addItem("Backend");

        String loggedInUser = UserSession.getInstance().getUsername();
        lblUsername.setText("Logged in as: " + loggedInUser);

        btnLogOut.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            JOptionPane.showMessageDialog(null, "Logged out successfully!");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            frame.dispose();
            SignInForm.main(new String[]{});
        });

        btnCheck.addActionListener(e -> {
            String selectedTopic = cbTopicSelection.getSelectedItem().toString();
            if (!selectedTopic.isEmpty()) {
                loadResourceData(selectedTopic, "rating");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a topic to check.");
            }
        });

        btnLoadAll.addActionListener(e -> {
            String selectedTopic = cbTopicSelection.getSelectedItem().toString();
            if (!selectedTopic.isEmpty()) {
                loadAllResources(selectedTopic, "name");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a topic to load all resources.");
            }
        });

        btnTopViewed.addActionListener(e -> {
            String selectedTopic = cbTopicSelection.getSelectedItem().toString();
            if (!selectedTopic.isEmpty()) {
                loadTopViewedResources(selectedTopic, "views");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a topic to load top viewed resources.");
            }
        });

        tbResource.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tbResource.columnAtPoint(e.getPoint());
                int row = tbResource.rowAtPoint(e.getPoint());

                if (column == 0) {
                    String resourceTitle = tbResource.getValueAt(row, 0).toString();
                    incrementViewCounter(resourceTitle);

                } else if (column == 1) {
                    String resourceTitle = tbResource.getValueAt(row, 0).toString();
                    showComments(resourceTitle);
                }
            }
        });
    }

    private void loadResourceData(String topic, String sortOption) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";
        String user = "root";
        String pass = "Muslimwal@2004";

        String query = "SELECT r.resource_name, " +
                "COALESCE(AVG(f_people.rating), 0) AS people_rating " +
                "FROM Resources r " +
                "LEFT JOIN Feedback f_people ON r.resource_id = f_people.resource_id " +
                (topic.equals("All") ? "" : "WHERE r.topic_name = ? ") +
                "GROUP BY r.resource_id " +
                "HAVING AVG(f_people.rating) > 2.5 " +
                "ORDER BY " + getOrderByClause(sortOption);

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!topic.equals("All")) {
                stmt.setString(1, topic);
            }

            ResultSet rs = stmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Resource Title (Tap to Read Articles)");
            columnNames.add("People Rating (Tap to read comments)");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("resource_name"));

                double peopleRating = rs.getDouble("people_rating");
                row.add(peopleRating == 0 ? "No Ratings Yet" : String.format("%.2f", peopleRating));

                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            tbResource.setModel(tableModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading resources: " + ex.getMessage());
        }
    }

    private void loadAllResources(String topic, String sortOption) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";
        String user = "root";
        String pass = "Muslimwal@2004";

        String query = "SELECT resource_name FROM Resources " +
                (topic.equals("All") ? "" : "WHERE topic_name = ? ") +
                "ORDER BY " + getOrderByClause(sortOption);

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!topic.equals("All")) {
                stmt.setString(1, topic);
            }

            ResultSet rs = stmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Resource Title (Tap to Read Articles)");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("resource_name"));
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            tbResource.setModel(tableModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading all resources: " + ex.getMessage());
        }
    }

    private void loadTopViewedResources(String topic, String sortOption) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";
        String user = "root";
        String pass = "Muslimwal@2004";

        String query = "SELECT resource_name, view_count FROM Resources " +
                (topic.equals("All") ? "WHERE view_count > 0 " : "WHERE topic_name = ? AND view_count > 0 ") +
                "ORDER BY " + getOrderByClause(sortOption);

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!topic.equals("All")) {
                stmt.setString(1, topic);
            }

            ResultSet rs = stmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Resource Title");
            columnNames.add("Views");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("resource_name"));
                row.add(rs.getInt("view_count"));
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            tbResource.setModel(tableModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading top viewed resources: " + ex.getMessage());
        }
    }

    private String getOrderByClause(String sortOption) {
        switch (sortOption) {
            case "rating":
                return "people_rating DESC";
            case "views":
                return "view_count DESC";
            case "name":
            default:
                return "resource_name ASC";
        }
    }

    private void incrementViewCounter(String resourceTitle) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";
        String user = "root";
        String pass = "Muslimwal@2004";

        String query = "UPDATE Resources SET view_count = view_count + 1 WHERE resource_name = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, resourceTitle);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error incrementing view counter: " + ex.getMessage());
        }
    }

    private void showComments(String resourceTitle) {
        JFrame commentFrame = new JFrame("Comments for " + resourceTitle);
        commentFrame.setSize(400, 300);
        commentFrame.setLayout(new BorderLayout());

        JLabel lblComment = new JLabel("", SwingConstants.CENTER);
        lblComment.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton btnNext = new JButton("Next");
        JButton btnPrevious = new JButton("Previous");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnPrevious);
        buttonPanel.add(btnNext);

        commentFrame.add(lblComment, BorderLayout.CENTER);
        commentFrame.add(buttonPanel, BorderLayout.SOUTH);

        ArrayList<String> comments = fetchComments(resourceTitle);
        final int[] index = {0};

        if (comments.isEmpty()) {
            lblComment.setText("No comments available.");
        } else {
            lblComment.setText(comments.get(index[0]));
        }

        btnNext.addActionListener(e -> {
            if (index[0] < comments.size() - 1) {
                index[0]++;
                lblComment.setText(comments.get(index[0]));
            }
        });

        btnPrevious.addActionListener(e -> {
            if (index[0] > 0) {
                index[0]--;
                lblComment.setText(comments.get(index[0]));
            }
        });

        commentFrame.setLocationRelativeTo(null);
        commentFrame.setVisible(true);
    }

    private ArrayList<String> fetchComments(String resourceTitle) {
        ArrayList<String> comments = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/tsprojdb";
        String user = "root";
        String pass = "Muslimwal@2004";

        String query = "SELECT comments FROM feedback f " +
                "JOIN Resources r ON f.resource_id = r.resource_id " +
                "WHERE r.resource_name = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, resourceTitle);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(rs.getString("comments"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching comments: " + ex.getMessage());
        }

        return comments;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Resource Recommendation Page");
            frame.setContentPane(new ResourceRecomanditionPage().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WIDTH, HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}