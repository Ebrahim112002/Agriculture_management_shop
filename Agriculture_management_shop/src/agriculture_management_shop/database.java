package agriculture_management_shop;

import java.sql.Connection;
import java.sql.DriverManager;

public class database {

    public static Connection connectDB() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to your actual database (replace with your DB name if different)
            Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriculture_shop?useSSL=false&serverTimezone=UTC",
                "root",        // 👉 your MySQL username
                "1234"         // 👉 your MySQL password
            );

            System.out.println("Database connected successfully");
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
