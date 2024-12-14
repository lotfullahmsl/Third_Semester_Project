import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResourceDB {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/tsprojdb";  // Update with your actual database URL
    private static final String USER = "root";  // Update with your database username
    private static final String PASSWORD = "Muslimwal@2004";  // Update with your database password

    public static void main(String[] args) {
        // Data to insert into Resources table
        String[] resourceNames = {
                "Spring Boot Basics", "Hash Table Implementation", "Quick Sort Visualization", "Network Protocols Overview",
                "Data Modeling Techniques", "JavaScript Essentials", "Neural Networks Fundamentals", "File System Management",
                "CSS Grid and Flexbox", "Building Simple APIs",

                "Spring Boot Intermediate Guide", "Dynamic Programming Patterns", "Advanced Graph Theory",
                "Data Warehousing Concepts", "Python for Machine Learning", "ML Model Deployment",
                "OS Virtual Memory", "Vue.js Essentials", "iOS UI Design Principles",

                "Kubernetes for Beginners", "Persistent Data Structures", "Graph Neural Networks",
                "Cloud Database Management", "Functional Programming in Python", "GANs in Depth",
                "OS Kernel Development", "Svelte for Web Apps", "Cross-Platform Mobile Development"
        };

        String[] resourceTypes = {
                "Tutorial", "Article", "Video", "Article", "Article", "Video", "Video", "Video",
                "Tutorial", "Tutorial", "Article", "Article", "Article", "Article", "Video", "Video",
                "Video", "Tutorial", "Tutorial", "Article", "Article", "Article", "Article", "Video",
                "Video", "Video", "Tutorial", "Tutorial"
        };

        String[] descriptions = {
                "Learn the basics of Spring Boot for Java-based web development.", "Implement hash tables from scratch.",
                "Visualize the steps of quick sort algorithm.", "Introduction to basic network protocols like TCP/IP.",
                "Learn techniques for effective data modeling in databases.", "Essential JavaScript concepts for beginners.",
                "Understand the building blocks of neural networks.", "Overview of file system structures and operations.",
                "Master CSS Grid and Flexbox for modern web layouts.", "Learn how to build simple REST APIs.",

                "Build intermediate projects with Spring Boot.", "Understand dynamic programming and its applications.",
                "Explore advanced topics in graph theory.", "Learn about concepts in data warehousing and ETL.",
                "Apply Python libraries to machine learning tasks.", "Learn how to deploy machine learning models.",
                "Deep dive into virtual memory management in operating systems.", "Build interactive UIs with Vue.js.",
                "Design principles for creating intuitive iOS interfaces.",

                "Basics of container orchestration with Kubernetes.", "Introduction to persistent data structures.",
                "Graph neural networks for deep learning on graphs.", "Learn about cloud-based database systems.",
                "Introduction to functional programming paradigms in Python.", "Understand and build Generative Adversarial Networks.",
                "Learn the fundamentals of operating system kernel development.", "Build web apps with the Svelte framework.",
                "Develop cross-platform mobile apps with modern frameworks."
        };

        String[] difficulties = {
                "Beginner", "Beginner", "Beginner", "Beginner", "Beginner", "Beginner", "Beginner", "Beginner",
                "Beginner", "Beginner", "Intermediate", "Intermediate", "Intermediate", "Intermediate", "Intermediate",
                "Intermediate", "Intermediate", "Intermediate", "Intermediate", "Advanced", "Advanced", "Advanced",
                "Advanced", "Advanced", "Advanced", "Advanced", "Advanced", "Advanced"
        };

        String[] topicNames = {
                "Java", "DSA", "DSA", "Networking", "DBMS", "JavaScript", "ML", "OS",
                "Web_Development", "API_Development", "Java", "DSA", "DSA", "DBMS", "Python",
                "ML", "OS", "Web_Development", "Mobile_Development", "Cloud", "DSA", "ML",
                "DBMS", "Python", "ML", "OS", "Web_Development", "Mobile_Development"
        };

        int[] resourceIds = {
                54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
                64, 65, 66, 67, 68, 69, 70, 71, 72, 73,
                74, 75, 76, 77, 78, 79, 80, 81, 82
        };

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String resourceQuery = "INSERT INTO tsprojdb.Resources (resource_id, resource_name, resource_type, description, difficulty, topic_name) VALUES (?, ?, ?, ?, ?, ?)";
            String feedbackQuery = "INSERT INTO tsprojdb.Feedback (feedback_id, username, resource_id, rating, comments) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement resourceStmt = connection.prepareStatement(resourceQuery);
                 PreparedStatement feedbackStmt = connection.prepareStatement(feedbackQuery)) {

                // Insert resources
                for (int i = 0; i < resourceNames.length; i++) {
                    resourceStmt.setInt(1, resourceIds[i]);
                    resourceStmt.setString(2, resourceNames[i]);
                    resourceStmt.setString(3, resourceTypes[i]);
                    resourceStmt.setString(4, descriptions[i]);
                    resourceStmt.setString(5, difficulties[i]);
                    resourceStmt.setString(6, topicNames[i]);
                    resourceStmt.executeUpdate();
                    System.out.println("Inserted resource: " + resourceNames[i]);
                }

                // Insert sample feedback
                Object[][] feedbackData = {
                        {7, "user7", 44, 5, "Excellent resource for advanced Java topics."},
                        {8, "user8", 45, 4, "Very detailed and informative."},
                        {9, "user9", 46, 3, "Good content but a bit complex."},
                        {10, "user10", 47, 5, "Great explanations of graph algorithms."},
                        {11, "user11", 48, 4, "Useful techniques for database optimization."},
                        {12, "user12", 49, 5, "Advanced Python programming concepts well explained."},
                        {13, "user13", 50, 4, "Interesting machine learning projects."},
                        {14, "user14", 51, 3, "Good insights into OS internals."},
                        {15, "user15", 52, 5, "Comprehensive guide to React."},
                        {16, "user16", 53, 4, "Great introduction to Flutter for mobile development."}
                };

                for (Object[] feedback : feedbackData) {
                    feedbackStmt.setInt(1, (int) feedback[0]);
                    feedbackStmt.setString(2, (String) feedback[1]);
                    feedbackStmt.setInt(3, (int) feedback[2]);
                    feedbackStmt.setInt(4, (int) feedback[3]);
                    feedbackStmt.setString(5, (String) feedback[4]);
                    feedbackStmt.executeUpdate();
                    System.out.println("Inserted feedback for resource ID: " + feedback[2]);
                }
            } catch (SQLException e) {
                System.out.println("Error executing the query: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
}