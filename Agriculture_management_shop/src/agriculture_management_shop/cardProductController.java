package agriculture_management_shop;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class cardProductController implements Initializable {

    private Label card_name;
    private Label card_price;
    private Label card_status;
    private ImageView card_image;
    @FXML
    private AnchorPane card_form;
    @FXML
    private Label prod_name;
    @FXML
    private Label prod_price;
    @FXML
    private ImageView prod_imageView;
    @FXML
    private Spinner<?> prod_spinner;
    @FXML
    private Button prod_addBtn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void setData(productData prodData) {
        card_name.setText(prodData.getProductName());
        card_price.setText("$" + String.valueOf(prodData.getPrice()));
        card_status.setText(prodData.getStatus());
        
        String path = "File:" + prodData.getImage();
        Image image = new Image(path, 125, 130, false, true);
        card_image.setImage(image);
    }

    @FXML
    private void addBtn(ActionEvent event) {
    }
}