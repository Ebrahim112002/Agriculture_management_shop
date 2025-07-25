package agriculture_management_shop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class database {

    public static Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriculture_shop?useSSL=false&serverTimezone=UTC",
                "root", // Replace with your MySQL username
                "1234"  // Replace with your MySQL password
            );
            System.out.println("Database connected successfully");
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}