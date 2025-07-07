package agriculture_management_shop;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane loginForm;
    @FXML
    private TextField username;
    @FXML
    private PasswordField pass;
    @FXML
    private Hyperlink forgot_pass;
    @FXML
    private Button loginBtn;
    @FXML
    private AnchorPane signupForm;
    @FXML
    private TextField Ruser;
    @FXML
    private PasswordField Rpass;
    @FXML
    private ComboBox<String> Rquestion;
    @FXML
    private TextField Ranswer;
    @FXML
    private Button signupBtn;
    @FXML
    private AnchorPane side_form;
    @FXML
    private Button side_createBtn;
    @FXML
    private Button side_alreadyHave;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private String[] questionList = {
        "What is your Full name?",
        "What is your favorite color?",
        "What is your Birth-Date?"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadQuestionList();
    }

    public void loadQuestionList() {
        List<String> listQ = new ArrayList<>();
        for (String data : questionList) {
            listQ.add(data);
        }
        ObservableList<String> listData = FXCollections.observableArrayList(listQ);
        Rquestion.setItems(listData);
    }

    @FXML
    public void login(ActionEvent event) {
        if (username.getText().isEmpty() || pass.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in both username and password!");
            alert.showAndWait();
            return;
        }

        connect = database.connectDB();
        if (connect == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to connect to database! Please check DB settings.");
            alert.showAndWait();
            return;
        }

        String query = "SELECT * FROM EMPLOYEE WHERE userName = ? AND password = ?";

        try {
            prepare = connect.prepareStatement(query);
            prepare.setString(1, username.getText());
            prepare.setString(2, pass.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + username.getText() + "!");
                alert.showAndWait();

                username.setText("");
                pass.setText("");

                // TODO: Navigate to dashboard
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect username or password!");
                alert.showAndWait();
            }

            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void signup(ActionEvent event) {
        if (Ruser.getText().isEmpty() || Rpass.getText().isEmpty()
                || Rquestion.getValue() == null || Ranswer.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all required fields!");
            alert.showAndWait();
            return;
        }

        connect = database.connectDB();
        if (connect == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to connect to database! Please check DB settings.");
            alert.showAndWait();
            return;
        }

        String regData = "INSERT INTO EMPLOYEE (userName, password, question, answer, date) VALUES (?, ?, ?, ?, ?)";

        try {
            String checkUsername = "SELECT userName FROM EMPLOYEE WHERE userName = ?";
            prepare = connect.prepareStatement(checkUsername);
            prepare.setString(1, Ruser.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists! Please choose another username.");
                alert.showAndWait();
            } else if (Rpass.getText().length() < 8) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Password must be at least 8 characters long!");
                alert.showAndWait();
            } else {
                prepare = connect.prepareStatement(regData);
                prepare.setString(1, Ruser.getText());
                prepare.setString(2, Rpass.getText());
                prepare.setString(3, Rquestion.getValue());
                prepare.setString(4, Ranswer.getText());

                Date date = new Date();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                prepare.setDate(5, sqlDate);

                prepare.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully registered account!");
                alert.showAndWait();

                Ruser.setText("");
                Rpass.setText("");
                Rquestion.getSelectionModel().clearSelection();
                Ranswer.setText("");

                TranslateTransition slider = new TranslateTransition();
                slider.setNode(side_form);
                slider.setDuration(Duration.seconds(0.5));
                slider.setToX(0);
                slider.setOnFinished((ActionEvent e) -> {
                    side_alreadyHave.setVisible(false);
                    side_createBtn.setVisible(true);
                });
                slider.play();
            }

            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(side_form);
        slider.setDuration(Duration.seconds(0.5));

        if (event.getSource() == side_createBtn) {
            slider.setToX(350);
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_createBtn.setVisible(false);
            });
        } else if (event.getSource() == side_alreadyHave) {
            slider.setToX(0);
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_createBtn.setVisible(true);
            });
        }

        slider.play();
    }
}
