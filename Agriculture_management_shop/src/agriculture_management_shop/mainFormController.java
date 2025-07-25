package agriculture_management_shop;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.PrintWriter;
import javafx.scene.control.cell.PropertyValueFactory;

public class mainFormController implements Initializable {
    
    @FXML
    private AnchorPane main_form;

    @FXML
    private Label username;

    @FXML
    private Button dashboard_btn, inventory_btn, menu_btn, customers_btn, logout_btn, daily_reports;

    @FXML
    private AnchorPane inventory_form, menu_form, dashboard_form, customers_form;
    
    @FXML
    private TableView<productData> inventory_tableView;

    @FXML
    private TableColumn<productData, String> inventory_col_productID, inventory_col_productName, 
            inventory_col_type, inventory_col_stock, inventory_col_price, inventory_col_status, inventory_col_date;

    @FXML
    private ImageView inventory_imageView;

    @FXML
    private Button inventory_importBtn, inventory_addBtn, inventory_updateBtn, inventory_clearBtn, inventory_deleteBtn;

    @FXML
    private TextField inventory_productID, inventory_productName, inventory_stock, inventory_price;

    @FXML
    private ComboBox<String> inventory_status, inventory_type;

    @FXML
    private ScrollPane menu_scrollPane;

    @FXML
    private GridPane menu_gridPane;

    @FXML
    private TableView<productData> menu_tableView;

    @FXML
    private TableColumn<productData, String> menu_col_productName, menu_col_quantity, menu_col_price;

    @FXML
    private Label menu_total, menu_change;

    @FXML
    private TextField menu_amount;

    @FXML
    private Button menu_payBtn, menu_removeBtn, menu_receiptBtn;

    @FXML
    private TableView<customersData> customers_tableView;

    @FXML
    private TableColumn<customersData, String> customers_col_customerID, customers_col_total, 
            customers_col_date, customers_col_cashier;

    @FXML
    private Label dashboard_NC, dashboard_TI, dashboard_TotalI, dashboard_NSP;

    @FXML
    private AreaChart<?, ?> dashboard_incomeChart;

    @FXML
    private BarChart<?, ?> dashboard_CustomerChart;

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private Alert alert;
    private Image image;
    
    private ObservableList<productData> cardListData = FXCollections.observableArrayList();
    private ObservableList<productData> inventoryListData, menuOrderListData;
    private ObservableList<customersData> customersListData;
    
    private int cID;
    private int getid;
    private double totalP;
    private double amount;
    private double change;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayUsername();
        
        dashboardDisplayNC();
        dashboardDisplayTI();
        dashboardTotalI();
        dashboardNSP();
        dashboardIncomeChart();
        dashboardCustomerChart();
        
        inventoryTypeList();
        inventoryStatusList();
        inventoryShowData();
        
        menuDisplayCard();
        menuDisplayTotal();
        menuShowOrderData();
        
        customersShowData();
    }
    
    public void dashboardDisplayNC() {
        String sql = "SELECT COUNT(id) FROM receipt";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                dashboard_NC.setText(String.valueOf(result.getInt("COUNT(id)")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dashboardDisplayTI() {
        String sql = "SELECT SUM(total) FROM receipt WHERE date = CURRENT_DATE";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                dashboard_TI.setText("৳" + String.format("%.2f", result.getDouble("SUM(total)")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dashboardTotalI() {
        String sql = "SELECT SUM(total) FROM receipt";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                dashboard_TotalI.setText("৳" + String.format("%.2f", result.getDouble("SUM(total)")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dashboardNSP() {
        String sql = "SELECT COUNT(quantity) FROM customer";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                dashboard_NSP.setText(String.valueOf(result.getInt("COUNT(quantity)")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dashboardIncomeChart() {
        dashboard_incomeChart.getData().clear();
        String sql = "SELECT date, SUM(total) FROM receipt GROUP BY date ORDER BY TIMESTAMP(date)";
        connect = database.connectDB();
        
        XYChart.Series chart = new XYChart.Series();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                chart.getData().add(new XYChart.Data<>(result.getString(1), result.getFloat(2)));
            }
            
            dashboard_incomeChart.getData().add(chart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dashboardCustomerChart() {
        dashboard_CustomerChart.getData().clear();
        String sql = "SELECT date, COUNT(id) FROM receipt GROUP BY date ORDER BY TIMESTAMP(date)";
        connect = database.connectDB();
        
        XYChart.Series chart = new XYChart.Series();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                chart.getData().add(new XYChart.Data<>(result.getString(1), result.getInt(2)));
            }
            
            dashboard_CustomerChart.getData().add(chart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void inventoryAddBtn() {
        if (validateInventoryFields()) {
            String checkProdID = "SELECT prod_id FROM product WHERE prod_id = '" + inventory_productID.getText() + "'";
            connect = database.connectDB();
            
            try {
                statement = connect.createStatement();
                result = statement.executeQuery(checkProdID);
                
                if (result.next()) {
                    showAlert(AlertType.ERROR, "Error Message", inventory_productID.getText() + " is already taken");
                } else {
                    insertProductData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean validateInventoryFields() {
        if (inventory_productID.getText().isEmpty() || inventory_productName.getText().isEmpty() ||
            inventory_type.getSelectionModel().getSelectedItem() == null || inventory_stock.getText().isEmpty() ||
            inventory_price.getText().isEmpty() || inventory_status.getSelectionModel().getSelectedItem() == null ||
            data.path == null) {
            
            showAlert(AlertType.ERROR, "Error Message", "Please fill all blank fields");
            return false;
        }
        return true;
    }
    
    private void insertProductData() throws SQLException {
        String insertData = "INSERT INTO product (prod_id, prod_name, type, stock, price, status, image, date) VALUES(?,?,?,?,?,?,?,?)";
        prepare = connect.prepareStatement(insertData);
        
        prepare.setString(1, inventory_productID.getText());
        prepare.setString(2, inventory_productName.getText());
        prepare.setString(3, inventory_type.getSelectionModel().getSelectedItem());
        prepare.setString(4, inventory_stock.getText());
        prepare.setString(5, inventory_price.getText());
        prepare.setString(6, inventory_status.getSelectionModel().getSelectedItem());
        
        String path = data.path.replace("\\", "\\\\");
        prepare.setString(7, path);
        prepare.setString(8, String.valueOf(new java.sql.Date(new Date().getTime())));
        
        prepare.executeUpdate();
        
        showAlert(AlertType.INFORMATION, "Success Message", "Successfully Added!");
        inventoryShowData();
        inventoryClearBtn();
    }
    
    @FXML
    public void inventoryUpdateBtn() {
        if (validateInventoryFields() && data.id != 0) {
            String path = data.path.replace("\\", "\\\\");
            String updateData = "UPDATE product SET prod_id = ?, prod_name = ?, type = ?, stock = ?, price = ?, " +
                              "status = ?, image = ?, date = ? WHERE id = ?";
            
            connect = database.connectDB();
            
            try {
                Optional<ButtonType> option = showAlert(AlertType.CONFIRMATION, "Confirmation", 
                    "Are you sure you want to UPDATE Product ID: " + inventory_productID.getText() + "?");
                
                if (option.isPresent() && option.get() == ButtonType.OK) {
                    prepare = connect.prepareStatement(updateData);
                    prepare.setString(1, inventory_productID.getText());
                    prepare.setString(2, inventory_productName.getText());
                    prepare.setString(3, inventory_type.getSelectionModel().getSelectedItem());
                    prepare.setString(4, inventory_stock.getText());
                    prepare.setString(5, inventory_price.getText());
                    prepare.setString(6, inventory_status.getSelectionModel().getSelectedItem());
                    prepare.setString(7, path);
                    prepare.setString(8, data.date);
                    prepare.setInt(9, data.id);
                    
                    prepare.executeUpdate();
                    
                    showAlert(AlertType.INFORMATION, "Success Message", "Successfully Updated!");
                    inventoryShowData();
                    inventoryClearBtn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void inventoryDeleteBtn() {
        if (data.id == 0) {
            showAlert(AlertType.ERROR, "Error Message", "Please select an item to delete");
            return;
        }
        
        Optional<ButtonType> option = showAlert(AlertType.CONFIRMATION, "Confirmation", 
            "Are you sure you want to DELETE Product ID: " + inventory_productID.getText() + "?");
        
        if (option.isPresent() && option.get() == ButtonType.OK) {
            String deleteData = "DELETE FROM product WHERE id = " + data.id;
            try {
                prepare = connect.prepareStatement(deleteData);
                prepare.executeUpdate();
                
                showAlert(AlertType.INFORMATION, "Success Message", "Successfully Deleted!");
                inventoryShowData();
                inventoryClearBtn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void inventoryClearBtn() {
        inventory_productID.setText("");
        inventory_productName.setText("");
        inventory_type.getSelectionModel().clearSelection();
        inventory_stock.setText("");
        inventory_price.setText("");
        inventory_status.getSelectionModel().clearSelection();
        data.path = "";
        data.id = 0;
        inventory_imageView.setImage(null);
    }
    
    @FXML
    public void inventoryImportBtn() {
        FileChooser openFile = new FileChooser();
        openFile.getExtensionFilters().add(new ExtensionFilter("Open Image File", "*.png", "*.jpg"));
        
        File file = openFile.showOpenDialog(main_form.getScene().getWindow());
        
        if (file != null) {
            data.path = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 120, 127, false, true);
            inventory_imageView.setImage(image);
        }
    }
    
    public ObservableList<productData> inventoryDataList() {
        ObservableList<productData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                listData.add(new productData(
                    result.getInt("id"),
                    result.getString("prod_id"),
                    result.getString("prod_name"),
                    result.getString("type"),
                    result.getInt("stock"),
                    result.getDouble("price"),
                    result.getString("status"),
                    result.getString("image"),
                    result.getDate("date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    public void inventoryShowData() {
        inventoryListData = inventoryDataList();
        
        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        inventory_tableView.setItems(inventoryListData);
    }
    
    @FXML
    public void inventorySelectData() {
        productData prodData = inventory_tableView.getSelectionModel().getSelectedItem();
        int num = inventory_tableView.getSelectionModel().getSelectedIndex();
        
        if ((num - 1) < -1) return;
        
        inventory_productID.setText(prodData.getProductId());
        inventory_productName.setText(prodData.getProductName());
        inventory_stock.setText(String.valueOf(prodData.getStock()));
        inventory_price.setText(String.valueOf(prodData.getPrice()));
        
        data.path = prodData.getImage();
        data.date = String.valueOf(prodData.getDate());
        data.id = prodData.getId();
        
        image = new Image("File:" + prodData.getImage(), 120, 127, false, true);
        inventory_imageView.setImage(image);
    }
    
    private String[] typeList = {
        "Meals",
        "Drinks",
        "Seeds",
        "Fertilizers",
        "Pesticides",
        "Tools",
        "Equipment",
        "Animal Feed",
        "Irrigation",
        "Compost",
        "Soil Enhancers",
        "Crop Protection",
        "Plant Pots"
    };

    private String[] statusList = {"Available", "Unavailable"};
    
    public void inventoryTypeList() {
        ObservableList<String> listData = FXCollections.observableArrayList(typeList);
        inventory_type.setItems(listData);
    }
    
    public void inventoryStatusList() {
        ObservableList<String> listData = FXCollections.observableArrayList(statusList);
        inventory_status.setItems(listData);
    }
    
    public ObservableList<productData> menuGetData() {
        ObservableList<productData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                listData.add(new productData(
                    result.getInt("id"),
                    result.getString("prod_id"),
                    result.getString("prod_name"),
                    result.getString("type"),
                    result.getInt("stock"),
                    result.getDouble("price"),
                    result.getString("image"),
                    result.getDate("date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    public void menuDisplayCard() {
        cardListData.clear();
        cardListData.addAll(menuGetData());
        
        menu_gridPane.getChildren().clear();
        menu_gridPane.getRowConstraints().clear();
        menu_gridPane.getColumnConstraints().clear();
        
        int row = 0;
        int column = 0;
        
        for (int q = 0; q < cardListData.size(); q++) {
            try {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("cardProduct.fxml"));
                AnchorPane pane = load.load();
                cardProductController cardC = load.getController();
                cardC.setData(cardListData.get(q));
                
                if (column == 3) {
                    column = 0;
                    row += 1;
                }
                
                menu_gridPane.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(10));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public ObservableList<productData> menuGetOrder() {
        customerID();
        ObservableList<productData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer WHERE customer_id = " + cID;
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                listData.add(new productData(
                    result.getInt("id"),
                    result.getString("prod_id"),
                    result.getString("prod_name"),
                    result.getString("type"),
                    result.getInt("quantity"),
                    result.getDouble("price"),
                    result.getString("image"),
                    result.getDate("date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    public void menuShowOrderData() {
        menuOrderListData = menuGetOrder();
        
        menu_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        menu_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        menu_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        menu_tableView.setItems(menuOrderListData);
    }
    
    @FXML
    public void menuSelectOrder() {
        productData prod = menu_tableView.getSelectionModel().getSelectedItem();
        int num = menu_tableView.getSelectionModel().getSelectedIndex();
        
        if ((num - 1) < -1) return;
        
        getid = prod.getId();
    }
    
    public void menuGetTotal() {
        customerID();
        String total = "SELECT SUM(price) FROM customer WHERE customer_id = " + cID;
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(total);
            result = prepare.executeQuery();
            
            if (result.next()) {
                totalP = result.getDouble("SUM(price)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void menuDisplayTotal() {
        menuGetTotal();
        menu_total.setText("৳" + String.format("%.2f", totalP));
    }
    
    @FXML
    public void menuAmount() {
        menuGetTotal();
        if (menu_amount.getText().isEmpty() || totalP == 0) {
            showAlert(AlertType.ERROR, "Error Message", "Invalid amount");
            return;
        }
        
        try {
            amount = Double.parseDouble(menu_amount.getText());
            if (amount < totalP) {
                menu_amount.setText("");
                showAlert(AlertType.ERROR, "Error Message", "Amount is less than total");
            } else {
                change = (amount - totalP);
                menu_change.setText("৳" + String.format("%.2f", change));
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error Message", "Please enter a valid number");
        }
    }
    
    @FXML
    public void menuPayBtn() {
        if (totalP == 0) {
            showAlert(AlertType.ERROR, "Payment Error", "Please add items to your order first!");
            return;
        }

        if (menu_amount.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Payment Error", "Please enter payment amount");
            return;
        }

        try {
            amount = Double.parseDouble(menu_amount.getText());
            if (amount < totalP) {
                showAlert(AlertType.ERROR, "Payment Error", 
                    "Payment amount ৳" + String.format("%.2f", amount) + " is less than total ৳" + String.format("%.2f", totalP));
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Payment Error", "Invalid payment amount");
            return;
        }

        Optional<ButtonType> result = showAlert(AlertType.CONFIRMATION,
            "Confirm Payment",
            "Confirm payment of ৳" + String.format("%.2f", totalP) + "?\nAmount tendered: ৳" + String.format("%.2f", amount) + "\nChange: ৳" + String.format("%.2f", (amount - totalP)));
        
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = database.connectDB();
            conn.setAutoCommit(false);

            // Create customer record if doesn't exist
            String checkCustomer = "INSERT INTO customers (customer_id, date_created) " +
                                 "SELECT ?, CURRENT_DATE WHERE NOT EXISTS " +
                                 "(SELECT 1 FROM customers WHERE customer_id = ?)";
            ps = conn.prepareStatement(checkCustomer);
            ps.setInt(1, cID);
            ps.setInt(2, cID);
            ps.executeUpdate();

            // Save receipt
            String insertPay = "INSERT INTO receipt (customer_id, total, date, em_username) VALUES (?,?,?,?)";
            ps = conn.prepareStatement(insertPay);
            ps.setInt(1, cID);
            ps.setDouble(2, totalP);
            ps.setDate(3, new java.sql.Date(new Date().getTime()));
            ps.setString(4, data.username != null ? data.username : "SYSTEM");
            ps.executeUpdate();

            // Clear customer's current order
            String clearOrder = "DELETE FROM customer WHERE customer_id = ?";
            ps = conn.prepareStatement(clearOrder);
            ps.setInt(1, cID);
            int deletedRows = ps.executeUpdate();

            conn.commit();

            if (deletedRows > 0) {
                showReceiptAfterPayment();
                menuRestart();
                menuShowOrderData();
            } else {
                showAlert(AlertType.WARNING, "Payment Issue", 
                    "Payment recorded but no orders were cleared for the customer.");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert(AlertType.ERROR, "Database Error", "Failed to process payment: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showReceiptAfterPayment() {
        try {
            // Create a directory if it doesn't exist
            File dir = new File("receipts");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Create the receipt file
            String filename = "receipts/receipt_" + System.currentTimeMillis() + ".txt";
            File file = new File(filename);

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("===== Agriculture Shop Receipt =====");
                writer.println("Date: " + new java.util.Date());
                writer.println("Cashier: " + (data.username != null ? data.username : "System"));
                writer.println("Customer ID: " + cID);
                writer.println("------------------------------------");

                // Get order data
                for (productData p : menuOrderListData) {
                    writer.println("Item: " + p.getProductName());
                    writer.println("Qty : " + p.getQuantity());
                    writer.println("Price: ৳" + String.format("%.2f", p.getPrice()));
                    writer.println("------------------------------------");
                }

                writer.println("Total : ৳" + String.format("%.2f", totalP));
                writer.println("Paid  : ৳" + String.format("%.2f", amount));
                writer.println("Change: ৳" + String.format("%.2f", change));
                writer.println("====================================");
                writer.println("   Thank you for shopping with us!   ");
            }

            // Open the file using system default text viewer
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

            // Show success message with receipt info
            showAlert(AlertType.INFORMATION, "Payment Successful", 
                "Payment processed!\nTotal: ৳" + String.format("%.2f", totalP) + 
                "\nChange: ৳" + String.format("%.2f", change) + 
                "\n\nReceipt has been generated and saved as:\n" + filename);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Payment successful but failed to generate receipt:\n" + e.getMessage());
        }
    }

    @FXML
    public void menuRemoveBtn() {
        if (getid == 0) {
            showAlert(AlertType.ERROR, "Error Message", "Please select the order you want to remove");
            return;
        }
        
        Optional<ButtonType> option = showAlert(AlertType.CONFIRMATION, "Confirmation", 
            "Are you sure you want to delete this order?");
        
        if (option.isPresent() && option.get() == ButtonType.OK) {
            String deleteData = "DELETE FROM customer WHERE id = " + getid;
            try {
                prepare = connect.prepareStatement(deleteData);
                prepare.executeUpdate();
                menuShowOrderData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void menuReceiptBtn() {
        if (totalP == 0 || menu_amount.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Error Message", "Please order first");
            return;
        }

        try {
            // Create a directory if it doesn't exist
            File dir = new File("receipts");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Create the receipt file
            String filename = "receipts/receipt_" + System.currentTimeMillis() + ".txt";
            File file = new File(filename);

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("===== Agriculture Shop Receipt =====");
                writer.println("Date: " + new java.util.Date());
                writer.println("Cashier: " + (data.username != null ? data.username : "System"));
                writer.println("Customer ID: " + cID);
                writer.println("------------------------------------");

                // Get order data
                for (productData p : menuOrderListData) {
                    writer.println("Item: " + p.getProductName());
                    writer.println("Qty : " + p.getQuantity());
                    writer.println("Price: ৳" + String.format("%.2f", p.getPrice()));
                    writer.println("------------------------------------");
                }

                writer.println("Total : ৳" + String.format("%.2f", totalP));
                writer.println("Paid  : ৳" + String.format("%.2f", amount));
                writer.println("Change: ৳" + String.format("%.2f", change));
                writer.println("====================================");
                writer.println("   Thank you for shopping with us!   ");
            }

            // Open the file using system default text viewer
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

            // Reset menu
            menuRestart();
            menuShowOrderData();

            showAlert(AlertType.INFORMATION, "Receipt Generated", "Receipt saved as " + filename);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to generate receipt:\n" + e.getMessage());
        }
    }

    public void menuRestart() {
        totalP = 0;
        change = 0;
        amount = 0;
        menu_total.setText("৳0.0");
        menu_amount.setText("");
        menu_change.setText("৳0.0");
    }
    
    public void customerID() {
        String sql = "SELECT MAX(customer_id) FROM customers";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                cID = result.getInt(1) + 1;
            } else {
                cID = 1;
            }
            
            data.cID = cID;
        } catch (Exception e) {
            e.printStackTrace();
            cID = (cID == 0) ? 1 : cID + 1;
            data.cID = cID;
        }
    }
    
    public ObservableList<customersData> customersDataList() {
        ObservableList<customersData> listData = FXCollections.observableArrayList();
        String sql = "SELECT r.*, c.name, c.contact FROM receipt r " +
                     "LEFT JOIN customers c ON r.customer_id = c.customer_id " +
                     "ORDER BY r.date DESC";
        connect = database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                listData.add(new customersData(
                    result.getInt("id"),
                    result.getInt("customer_id"),
                    result.getDouble("total"),
                    result.getDate("date"),
                    result.getString("em_username"),
                    result.getString("name"),
                    result.getString("contact")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    public void customersShowData() {
        customersListData = customersDataList();
        
        customers_col_customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customers_col_total.setCellValueFactory(new PropertyValueFactory<>("total"));
        customers_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customers_col_cashier.setCellValueFactory(new PropertyValueFactory<>("emUsername"));
        
        customers_tableView.setItems(customersListData);
    }
    
    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == dashboard_btn) {
            showDashboard();
        } else if (event.getSource() == inventory_btn) {
            showInventory();
        } else if (event.getSource() == menu_btn) {
            showMenu();
        } else if (event.getSource() == customers_btn) {
            showCustomers();
        } else if (event.getSource() == daily_reports) {
            reports(event);
        }
    }
    
    private void showDashboard() {
        dashboard_form.setVisible(true);
        inventory_form.setVisible(false);
        menu_form.setVisible(false);
        customers_form.setVisible(false);
        
        dashboardDisplayNC();
        dashboardDisplayTI();
        dashboardTotalI();
        dashboardNSP();
        dashboardIncomeChart();
        dashboardCustomerChart();
    }
    
    private void showInventory() {
        dashboard_form.setVisible(false);
        inventory_form.setVisible(true);
        menu_form.setVisible(false);
        customers_form.setVisible(false);
        
        inventoryTypeList();
        inventoryStatusList();
        inventoryShowData();
    }
    
    private void showMenu() {
        dashboard_form.setVisible(false);
        inventory_form.setVisible(false);
        menu_form.setVisible(true);
        customers_form.setVisible(false);
        
        menuDisplayCard();
        menuDisplayTotal();
        menuShowOrderData();
    }
    
    private void showCustomers() {
        dashboard_form.setVisible(false);
        inventory_form.setVisible(false);
        menu_form.setVisible(false);
        customers_form.setVisible(true);
        
        customersShowData();
    }
    
    @FXML
    private void reports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DailyReports.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Daily Sales Report");
            stage.setScene(new Scene(root));
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load Daily Reports: " + e.getMessage());
        }
    }
    
    @FXML
    public void logout() {
        Optional<ButtonType> option = showAlert(AlertType.CONFIRMATION, "Confirmation", 
            "Are you sure you want to logout?");
        
        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                logout_btn.getScene().getWindow().hide();
                
                Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Agriculture Shop Management System");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void displayUsername() {
        if (data.username != null && !data.username.isEmpty()) {
            username.setText(data.username.substring(0, 1).toUpperCase() + data.username.substring(1));
        }
    }
    
    private Optional<ButtonType> showAlert(AlertType type, String title, String message) {
        alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}