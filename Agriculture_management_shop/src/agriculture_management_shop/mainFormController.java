package agriculture_management_shop;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.sql.Date;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.input.MouseEvent;

public class mainFormController implements Initializable {

    // UI Components
@FXML
private Label username;
    @FXML private AnchorPane main_form;
    @FXML
    private Button dashboard_btn;
    @FXML
    private Button inventory_btn;
    @FXML
    private Button menu_btn;
    
    // Navigation Buttons
    @FXML
    private Button customers_btn;
    // Navigation Buttons
    @FXML
    private Button logout_btn;
    
    // Dashboard Components
    @FXML private AnchorPane dashboard_form;
    @FXML private Label dashboard_NC, dashboard_TI, dashboard_TotalI;
    
    // Inventory Components
    @FXML private AnchorPane inventory_form;
    @FXML private TableView<productData> inventory_tableView;
    @FXML private TableColumn<productData, String> inventory_col_productID, inventory_col_productName, 
                                                  inventory_col_type, inventory_col_status;
    @FXML private TableColumn<productData, Integer> inventory_col_stock;
    @FXML private TableColumn<productData, Double> inventory_col_price;
    @FXML private TableColumn<productData, Date> inventory_col_date;
    @FXML private TextField inventory_productID, inventory_productName, inventory_stock, inventory_price;
    @FXML private ComboBox<String> inventory_type, inventory_status;
    @FXML private ImageView inventory_imageView;
    
    // Menu Components
    @FXML private AnchorPane menu_form;
    @FXML
    private GridPane menu_gridPane;
    
    // Customer Components
    @FXML private AnchorPane customers_form;

    // Database and Data
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Image image;
    private ObservableList<productData> cardListData = FXCollections.observableArrayList();
    @FXML
    private AreaChart<?, ?> dashboard_incomeChart;
    @FXML
    private BarChart<?, ?> dashboard_CustomerChart;
    @FXML
    private Button inventory_importBtn;
    @FXML
    private Button inventory_addBtn;
    @FXML
    private Button inventory_updateBtn;
    @FXML
    private Button inventory_clearBtn;
    @FXML
    private Button inventory_deleteBtn;
    @FXML
    private Label dashboard_NSP;
    @FXML
    private ScrollPane menu_scrollPane;
    @FXML
    private TableView<?> menu_tableView;
    @FXML
    private TableColumn<?, ?> menu_col_productName;
    @FXML
    private TableColumn<?, ?> menu_col_quantity;
    @FXML
    private TableColumn<?, ?> menu_col_price;
    @FXML
    private Label menu_total;
    @FXML
    private TextField menu_amount;
    @FXML
    private Label menu_change;
    @FXML
    private Button menu_payBtn;
    @FXML
    private Button menu_removeBtn;
    @FXML
    private Button menu_receiptBtn;
    @FXML
    private TableView<?> customers_tableView;
    @FXML
    private TableColumn<?, ?> customers_col_customerID;
    @FXML
    private TableColumn<?, ?> customers_col_total;
    @FXML
    private TableColumn<?, ?> customers_col_date;
    @FXML
    private TableColumn<?, ?> customers_col_cashier;

    public void setUsername(String name) {
        username.setText(name);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize dashboard data
        dashboardNC();
        dashboardTI();
        dashboardTotalI();

        // Initialize inventory
        inventoryTypeList();
        inventoryStatusList();
        inventoryShowData();

        // Initialize menu
        menuDisplayCard();
    }

    // Navigation Methods
    @FXML
    public void switchForm(ActionEvent event) {
        dashboard_form.setVisible(event.getSource() == dashboard_btn);
        inventory_form.setVisible(event.getSource() == inventory_btn);
        menu_form.setVisible(event.getSource() == menu_btn);
        customers_form.setVisible(event.getSource() == customers_btn);
    }

    @FXML
    public void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get().equals(ButtonType.OK)) {
                logout_btn.getScene().getWindow().hide();

                Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error during logout: " + e.getMessage());
        }
    }

    // Dashboard Methods
    private void dashboardNC() {
        String sql = "SELECT COUNT(id) FROM customer";
        try (Connection conn = database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                dashboard_NC.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading customer count: " + e.getMessage());
        }
    }

    private void dashboardTI() {
        String sql = "SELECT SUM(total) FROM customer_info WHERE date = ?";
        try (Connection conn = database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dashboard_TI.setText(String.format("$%.2f", rs.getDouble(1)));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading today's income: " + e.getMessage());
        }
    }

    private void dashboardTotalI() {
        String sql = "SELECT SUM(total) FROM customer_info";
        try (Connection conn = database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                dashboard_TotalI.setText(String.format("$%.2f", rs.getDouble(1)));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading total income: " + e.getMessage());
        }
    }

    // Inventory Methods
    private void inventoryTypeList() {
        ObservableList<String> list = FXCollections.observableArrayList(
            "Fruits", "Vegetables", "Grains", "Dairy", "Meat");
        inventory_type.setItems(list);
    }

    private void inventoryStatusList() {
        ObservableList<String> list = FXCollections.observableArrayList(
            "Available", "Unavailable");
        inventory_status.setItems(list);
    }

    private void inventoryShowData() {
        ObservableList<productData> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product";

        try (Connection conn = database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new productData(
                    rs.getInt("id"),
                    rs.getString("prod_id"),
                    rs.getString("prod_name"),
                    rs.getString("type"),
                    rs.getInt("stock"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("image"),
                    rs.getDate("date")
                ));
            }

            inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
            inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
            inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
            inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
            inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
            inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));

            inventory_tableView.setItems(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading inventory: " + e.getMessage());
        }
    }

    @FXML
    private void inventorySelectData() {
        productData data = inventory_tableView.getSelectionModel().getSelectedItem();
        if (data == null) return;

        inventory_productID.setText(data.getProductId());
        inventory_productName.setText(data.getProductName());
        inventory_type.setValue(data.getType());
        inventory_stock.setText(String.valueOf(data.getStock()));
        inventory_price.setText(String.valueOf(data.getPrice()));
        inventory_status.setValue(data.getStatus());

        try {
            image = new Image("file:" + data.getImage(), 120, 127, false, true);
            inventory_imageView.setImage(image);
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Error loading product image");
            inventory_imageView.setImage(null);
        }
    }

    @FXML
    private void inventoryImportBtn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        
        File file = fileChooser.showOpenDialog(main_form.getScene().getWindow());
        if (file != null) {
            try {
                image = new Image(file.toURI().toString(), 120, 127, false, true);
                inventory_imageView.setImage(image);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void inventoryAddBtn() {
        if (inventory_productID.getText().isEmpty() ||
            inventory_productName.getText().isEmpty() ||
            inventory_type.getValue() == null ||
            inventory_stock.getText().isEmpty() ||
            inventory_price.getText().isEmpty() ||
            inventory_status.getValue() == null ||
            inventory_imageView.getImage() == null) {
            
            showAlert(Alert.AlertType.ERROR, "Please fill all fields and select an image");
            return;
        }

        String checkSql = "SELECT prod_id FROM product WHERE prod_id = ?";
        String insertSql = "INSERT INTO product (prod_id, prod_name, type, stock, price, status, image, date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = database.connectDB()) {
            // Check for duplicate product ID
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, inventory_productID.getText());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        showAlert(Alert.AlertType.ERROR, "Product ID already exists!");
                        return;
                    }
                }
            }

            // Insert new product
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, inventory_productID.getText());
                insertStmt.setString(2, inventory_productName.getText());
                insertStmt.setString(3, inventory_type.getValue());
                insertStmt.setInt(4, Integer.parseInt(inventory_stock.getText()));
                insertStmt.setDouble(5, Double.parseDouble(inventory_price.getText()));
                insertStmt.setString(6, inventory_status.getValue());
                
                String imagePath = inventory_imageView.getImage().getUrl().replace("file:", "");
                insertStmt.setString(7, imagePath);
                
                insertStmt.setDate(8, Date.valueOf(LocalDate.now()));

                insertStmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Product added successfully!");
                inventoryShowData();
                inventoryClearBtn();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format in stock or price");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void inventoryUpdateBtn() {
        if (inventory_productID.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please select a product to update");
            return;
        }

        String sql = "UPDATE product SET prod_name = ?, type = ?, stock = ?, price = ?, status = ?, image = ? " +
                     "WHERE prod_id = ?";

        try (Connection conn = database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, inventory_productName.getText());
            pstmt.setString(2, inventory_type.getValue());
            pstmt.setInt(3, Integer.parseInt(inventory_stock.getText()));
            pstmt.setDouble(4, Double.parseDouble(inventory_price.getText()));
            pstmt.setString(5, inventory_status.getValue());
            
            String imagePath = inventory_imageView.getImage().getUrl().replace("file:", "");
            pstmt.setString(6, imagePath);
            
            pstmt.setString(7, inventory_productID.getText());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Product updated successfully!");
                inventoryShowData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Product update failed");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format in stock or price");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void inventoryDeleteBtn() {
        if (inventory_productID.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please select a product to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this product?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM product WHERE prod_id = ?";

            try (Connection conn = database.connectDB();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, inventory_productID.getText());
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Product deleted successfully!");
                    inventoryShowData();
                    inventoryClearBtn();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Product deletion failed");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void inventoryClearBtn() {
        inventory_productID.clear();
        inventory_productName.clear();
        inventory_type.getSelectionModel().clearSelection();
        inventory_stock.clear();
        inventory_price.clear();
        inventory_status.getSelectionModel().clearSelection();
        inventory_imageView.setImage(null);
        inventory_tableView.getSelectionModel().clearSelection();
    }

    // Menu Methods
    private ObservableList<productData> menuGetData() {
        ObservableList<productData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product WHERE status = 'Available'";

        try (Connection conn = database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                listData.add(new productData(
                    rs.getInt("id"),
                    rs.getString("prod_id"),
                    rs.getString("prod_name"),
                    rs.getString("type"),
                    rs.getInt("stock"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("image"),
                    rs.getDate("date")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading menu items: " + e.getMessage());
        }

        return listData;
    }

    private void menuDisplayCard() {
        cardListData.clear();
        cardListData.addAll(menuGetData());

        menu_gridPane.getChildren().clear();
        menu_gridPane.getRowConstraints().clear();
        menu_gridPane.getColumnConstraints().clear();

        int column = 0;
        int row = 1;

        try {
            for (productData data : cardListData) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("cardProduct.fxml"));
                AnchorPane pane = loader.load();
                cardProductController controller = loader.getController();
                controller.setData(data);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                menu_gridPane.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(10));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error loading product cards: " + e.getMessage());
        }
    }

    // Utility Methods
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error Message" : 
                      type == Alert.AlertType.INFORMATION ? "Information Message" : "Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void menuSelectOrder(MouseEvent event) {
    }

    @FXML
    private void menuAmount(ActionEvent event) {
    }

    @FXML
    private void menuPayBtn(ActionEvent event) {
    }

    @FXML
    private void menuRemoveBtn(ActionEvent event) {
    }

    @FXML
    private void menuReceiptBtn(ActionEvent event) {
    }

}