package agriculture_management_shop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the Agriculture Shop Management System.
 */
public class Agriculture_management_shop extends Application {

    private static final Logger LOGGER = Logger.getLogger(Agriculture_management_shop.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            // Initialize database tables
           
            
            // Load the main FXML file
            LOGGER.info("Loading FXMLDocument.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agriculture_management_shop/FXMLDocument.fxml"));
            Parent root = loader.load();

            // Set up the scene and stage
            Scene scene = new Scene(root);
            stage.setTitle("Agriculture Shop Management System");
            stage.setMinHeight(450);
            stage.setMinWidth(650);
            stage.setScene(scene);
            stage.show();
            LOGGER.info("Application started successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start the application: " + e.getMessage(), e);
            showErrorAlert(stage, "Startup Error", "Failed to start the application: " + e.getMessage());
        }
    }

    /**
     * Displays an error alert to the user.
     *
     * @param owner  The stage that owns the alert
     * @param title  The title of the alert
     * @param message The error message to display
     */
    private void showErrorAlert(Stage owner, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}