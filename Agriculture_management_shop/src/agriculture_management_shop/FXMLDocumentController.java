package agriculture_management_shop;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane loginForm;
    @FXML private TextField username;
    @FXML private PasswordField pass;
    @FXML private Button loginBtn;
    @FXML private TextField Ruser;
    @FXML private PasswordField Rpass;
    @FXML private ComboBox<String> Rquestion;
    @FXML private TextField Ranswer;
    @FXML private AnchorPane side_form;
    @FXML private Button side_createBtn;
    @FXML private Button side_alreadyHave;
    @FXML private AnchorPane fp_question_form;
    @FXML private ComboBox<String> fp_question;
    @FXML private TextField fp_answer;
    @FXML private AnchorPane loginForm11;
    @FXML private PasswordField np_newpass;
    @FXML private PasswordField np_confirmpass;
    @FXML private Hyperlink forgot_pass;
    @FXML private Button fp_proceedBtn;
    @FXML private Button fp_back;
    @FXML private Button np_changepassBtn;
    @FXML private Button np_back;
    @FXML private AnchorPane signupForm;
    @FXML private Button signupBtn;

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
        ObservableList<String> listData = FXCollections.observableArrayList(questionList);
        Rquestion.setItems(listData);
    }

    public void loadForgotQuestionList() {
        ObservableList<String> listData = FXCollections.observableArrayList(questionList);
        fp_question.setItems(listData);
    }

    @FXML
    public void login(ActionEvent event) {
        if (username.getText().isEmpty() || pass.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in both username and password!");
            return;
        }
        

        connect = database.connectDB();
        String query = "SELECT * FROM EMPLOYEE WHERE userName = ? AND password = ?";

        try {
            prepare = connect.prepareStatement(query);
            prepare.setString(1, username.getText());
            prepare.setString(2, pass.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Welcome, " + username.getText() + "!");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainForm.fxml"));
                Parent root = loader.load();

                mainFormController controller = loader.getController();
                controller.setUsername(username.getText());

                Stage stage = new Stage();
                stage.setTitle("Agriculture Shop Management");
                stage.setMinWidth(1100);
                stage.setMinHeight(600);
                stage.setScene(new Scene(root));
                stage.show();

                // Optionally comment this out for debugging:
                loginBtn.getScene().getWindow().hide();

                username.setText("");
                pass.setText("");
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect username or password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {}
            try { if(prepare != null) prepare.close(); } catch(Exception e) {}
            try { if(connect != null) connect.close(); } catch(Exception e) {}
        }
    }

    @FXML
    public void signup(ActionEvent event) {
        if (Ruser.getText().isEmpty() || Rpass.getText().isEmpty() || 
            Rquestion.getValue() == null || Ranswer.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all required fields!");
            return;
        }

        connect = database.connectDB();
        String regData = "INSERT INTO EMPLOYEE (userName, password, question, answer, date) VALUES (?, ?, ?, ?, ?)";

        try {
            String checkUsername = "SELECT userName FROM EMPLOYEE WHERE userName = ?";
            prepare = connect.prepareStatement(checkUsername);
            prepare.setString(1, Ruser.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                showAlert(Alert.AlertType.ERROR, "Username already exists!");
            } else if (Rpass.getText().length() < 8) {
                showAlert(Alert.AlertType.ERROR, "Password must be at least 8 characters!");
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

                showAlert(Alert.AlertType.INFORMATION, "Account registered successfully!");

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
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {}
            try { if(prepare != null) prepare.close(); } catch(Exception e) {}
            try { if(connect != null) connect.close(); } catch(Exception e) {}
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

    @FXML
    public void forgotPassword() {
        fp_question_form.setVisible(true);
        loginForm.setVisible(false);
        loadForgotQuestionList();
    }

    @FXML
    public void backToLoginForm() {
        fp_question_form.setVisible(false);
        loginForm.setVisible(true);
    }

    @FXML
    public void proceedToChangePassword() {
        if (fp_question.getValue() == null || fp_answer.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please answer the security question.");
            return;
        }

        connect = database.connectDB();
        String query = "SELECT * FROM EMPLOYEE WHERE question = ? AND answer = ?";

        try {
            prepare = connect.prepareStatement(query);
            prepare.setString(1, fp_question.getValue());
            prepare.setString(2, fp_answer.getText());

            result = prepare.executeQuery();

            if (result.next()) {
                fp_question_form.setVisible(false);
                loginForm11.setVisible(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect question or answer.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {}
            try { if(prepare != null) prepare.close(); } catch(Exception e) {}
            try { if(connect != null) connect.close(); } catch(Exception e) {}
        }
    }

    @FXML
    public void backToQuestionForm() {
        loginForm11.setVisible(false);
        fp_question_form.setVisible(true);
    }

    @FXML
    public void changePassword() {
        if (np_newpass.getText().isEmpty() || np_confirmpass.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all password fields.");
            return;
        }

        if (!np_newpass.getText().equals(np_confirmpass.getText())) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match.");
            return;
        }

        try {
            String update = "UPDATE EMPLOYEE SET password = ? WHERE question = ? AND answer = ?";
            connect = database.connectDB();
            prepare = connect.prepareStatement(update);
            prepare.setString(1, np_newpass.getText());
            prepare.setString(2, fp_question.getValue());
            prepare.setString(3, fp_answer.getText());

            int rows = prepare.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Password updated successfully!");
                loginForm11.setVisible(false);
                loginForm.setVisible(true);
                np_newpass.setText("");
                np_confirmpass.setText("");
                fp_question.getSelectionModel().clearSelection();
                fp_answer.setText("");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        } finally {
            try { if(prepare != null) prepare.close(); } catch(Exception e) {}
            try { if(connect != null) connect.close(); } catch(Exception e) {}
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
