import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class ResourceDetailPage {
    private JPanel RDPPanel;
    private JComboBox<String> cbTopic;
    private JComboBox<String> cbTitle;
    private JTextField tfSearch;
    private JButton btnSearch;
    private JScrollPane SPtitle;
    private JTable tTitles;
    private JScrollPane SPArticle;
    private JTextPane tpArticle;
    private JCheckBox chRead;
    private JScrollPane spfeedback;
    private JTextPane tpComment;
    private JComboBox<String> cbRating;
    private JButton btnSubmit;
    private JLabel lblLoading;
    private JButton btnLoad;
    private JButton btnPrev;
    private JButton btnNext;
    private JComboBox<String> cbDifficuty;

    public ResourceDetailPage() {
        // Set the preferred size of tfSearch to match the size of the dropdowns
        Dimension preferredSize = cbTopic.getPreferredSize();
        tfSearch.setPreferredSize(preferredSize);

        // Set initial placeholder for tfSearch
        setPlaceholder(tfSearch, "Please Search");

        // Ensure lblLoading is properly initialized
        lblLoading.setVisible(false);

        // Add "Select Topic" item and disable it
        cbTopic.addItem("Select Topic");
        cbTopic.setSelectedIndex(0);
        cbTopic.setEnabled(false);
        cbRating.addItem("1");
        cbRating.addItem("2");
        cbRating.addItem("3");
        cbRating.addItem("4");
        cbRating.addItem("5");

        // Initially set cbTitle to "Select A Title" and disable it
        cbTitle.addItem("Select A Title");
        cbTitle.setSelectedIndex(0);
        cbTitle.setEnabled(false);

        // Add difficulty levels to cbDifficuty
        cbDifficuty.addItem("Select Difficulty");
        cbDifficuty.addItem("Beginner");
        cbDifficuty.addItem("Intermediate");
        cbDifficuty.addItem("Advance");
        cbDifficuty.setSelectedIndex(0);

        // Load topics from the database into cbTopic
        loadTopics();

        // Add action listener to cbTopic to load titles related to the selected topic
        cbTopic.addActionListener(e -> {
            if (cbTopic.getSelectedIndex() == 0) {
                cbTitle.removeAllItems();
                cbTitle.addItem("Select A Title");
                cbTitle.setEnabled(false);
                tfSearch.setEditable(true);
                setPlaceholder(tfSearch, "Please Search");
            } else {
                cbDifficuty.setSelectedIndex(0);
                cbTitle.removeAllItems();
                cbTitle.addItem("Select A Title");
                cbTitle.setEnabled(false);
                tfSearch.setEditable(true);
                setPlaceholder(tfSearch, "Please Search");
            }
        });

        // Add action listener to cbDifficuty to load titles based on the selected difficulty
        cbDifficuty.addActionListener(e -> {
            if (cbDifficuty.getSelectedIndex() > 0) {
                lblLoading.setVisible(true);
                loadTitles(cbTopic.getSelectedItem().toString(), cbDifficuty.getSelectedItem().toString());
            }
        });

        // Add action listener to cbTitle to block tfSearch if a title is selected
        cbTitle.addActionListener(e -> {
            if (cbTitle.getSelectedIndex() > 0) {
                tfSearch.setEditable(false);
                setPlaceholder(tfSearch, "Can't Select");
            } else {
                tfSearch.setEditable(true);
                setPlaceholder(tfSearch, "Please Search");
            }
        });

        // Add action listener to btnSearch to perform binary search
        btnSearch.addActionListener(e -> {
            String searchText = tfSearch.getText();
            if (!searchText.isEmpty()) {
                searchTitleInDatabase(searchText);
            }
        });

        // Add action listener to btnLoad to load article details in the text pane
        btnLoad.addActionListener(e -> {
            if (cbTitle.getSelectedIndex() > 0) {
                loadArticleDetails(cbTitle.getSelectedItem().toString());
            }
        });
    }

    private void loadTopics() {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";  // Update with your actual database URL
        String user = "root";  // Update with your database username
        String password = "Muslimwal@2004";  // Update with your database password

        lblLoading.setVisible(true);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT DISTINCT topic_name FROM Resources")) {

                while (resultSet.next()) {
                    cbTopic.addItem(resultSet.getString("topic_name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    lblLoading.setVisible(false);
                    cbTopic.setEnabled(true);
                });
            }
        }).start();
    }

    private void loadTitles(String topic, String difficulty) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";  // Update with your actual database URL
        String user = "root";  // Update with your database username
        String password = "Muslimwal@2004";  // Update with your database password

        cbTitle.removeAllItems();
        cbTitle.addItem("Loading...");
        cbTitle.setEnabled(false);

        new Thread(() -> {
            ArrayList<String> titles = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT resource_name FROM Resources WHERE topic_name = '" + topic + "' AND difficulty = '" + difficulty + "'")) {

                while (resultSet.next()) {
                    titles.add(resultSet.getString("resource_name"));
                }
                Collections.sort(titles);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(1500);  // Adjust this value to change the loading timing
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> {
                    cbTitle.removeAllItems();
                    cbTitle.addItem("Select A Title");
                    for (String title : titles) {
                        cbTitle.addItem(title);
                    }
                    cbTitle.setEnabled(true);
                    lblLoading.setVisible(false);
                });
            }
        }).start();
    }

    private void searchTitleInDatabase(String title) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";  // Update with your actual database URL
        String user = "root";  // Update with your database username
        String password = "Muslimwal@2004";  // Update with your database password

        new Thread(() -> {
            ArrayList<String> titles = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT resource_name FROM Resources WHERE resource_name LIKE '%" + title + "%'")) {

                while (resultSet.next()) {
                    titles.add(resultSet.getString("resource_name"));
                }
                Collections.sort(titles);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    if (titles.isEmpty()) {
                        JOptionPane.showMessageDialog(RDPPanel, "Title not found");
                    } else {
                        cbTitle.removeAllItems();
                        cbTitle.addItem("Select A Title");
                        for (String t : titles) {
                            cbTitle.addItem(t);
                        }
                        cbTitle.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private void loadArticleDetails(String title) {
        String url = "jdbc:mysql://localhost:3306/tsprojdb";  // Update with your actual database URL
        String user = "root";  // Update with your database username
        String password = "Muslimwal@2004";  // Update with your database password

        new Thread(() -> {
            StringBuilder articleDetails = new StringBuilder();
            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT resource_name, difficulty, description FROM Resources WHERE resource_name = '" + title + "'")) {

                if (resultSet.next()) {
                    String resourceName = resultSet.getString("resource_name");
                    String difficulty = resultSet.getString("difficulty");
                    String description = resultSet.getString("description");

                    articleDetails.append("Resource Name: ").append(resourceName).append("\n");
                    articleDetails.append("Difficulty: ").append(difficulty).append("\n\n");
                    articleDetails.append(description);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    tpArticle.setText(articleDetails.toString());
                });
            }
        }).start();
    }

    private void setPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Main function to display the frame
    public static void main(String[] args) {
        JFrame frame = new JFrame("ResourceDetailPage");
        frame.setContentPane(new ResourceDetailPage().RDPPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}