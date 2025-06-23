/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package agriculture_management_shop;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 *
 * @author LENOVO
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
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
    private ComboBox<?> Rquestion;
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
    
    @FXML
   public void switchForm(ActionEvent event){
      TranslateTransition slider = new TranslateTransition();
      
       if(event.getSource()==side_createBtn){
           slider.setNode(side_form);
           slider.setToX(350);
           slider.setDuration(Duration.seconds(.5));
           
           slider.setOnFinished((ActionEvent e)->{
           side_alreadyHave.setVisible(true);
           side_createBtn.setVisible(false);
       });
           slider.play();
       }else if(event.getSource()==side_alreadyHave){
             slider.setNode(side_form);
           slider.setToX(0);
           slider.setDuration(Duration.seconds(.5));
           
           slider.setOnFinished((ActionEvent e)->{
           side_alreadyHave.setVisible(false);
           side_createBtn.setVisible(true);
       });
           slider.play();
       }
   
   }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
