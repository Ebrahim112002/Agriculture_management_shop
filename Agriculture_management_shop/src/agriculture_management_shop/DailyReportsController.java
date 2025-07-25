package agriculture_management_shop;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class DailyReportsController {

    @FXML
    private VBox root;

    @FXML
    private TableView<SalesRecord> todayTable;

    @FXML
    private TableColumn<SalesRecord, String> todayCashierColumn;

    @FXML
    private TableColumn<SalesRecord, String> todayDateColumn;

    @FXML
    private TableColumn<SalesRecord, Double> todayTotalColumn;

    @FXML
    private Label todayTotalLabel;

    @FXML
    private TableView<SalesRecord> yesterdayTable;

    @FXML
    private TableColumn<SalesRecord, String> yesterdayCashierColumn;

    @FXML
    private TableColumn<SalesRecord, String> yesterdayDateColumn;

    @FXML
    private TableColumn<SalesRecord, Double> yesterdayTotalColumn;

    @FXML
    private Label yesterdayTotalLabel;

    @FXML
    private TableView<SalesRecord> monthlyTable;

    @FXML
    private TableColumn<SalesRecord, String> monthlyCashierColumn;

    @FXML
    private TableColumn<SalesRecord, String> monthlyDateColumn;

    @FXML
    private TableColumn<SalesRecord, Double> monthlyTotalColumn;

    @FXML
    private Label monthlyTotalLabel;

    @FXML
    private Label totalSalesLabel;

    @FXML
    private Label avgTransactionLabel;

    @FXML
    private Label topCashierLabel;

    private ObservableList<SalesRecord> todaySalesData = FXCollections.observableArrayList();
    private ObservableList<SalesRecord> yesterdaySalesData = FXCollections.observableArrayList();
    private ObservableList<SalesRecord> monthlySalesData = FXCollections.observableArrayList();
    @FXML
    private TabPane salesTabPane;

    public void initialize() {
        // Set up TableView columns
        todayCashierColumn.setCellValueFactory(cellData -> cellData.getValue().cashierProperty());
        todayDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        todayTotalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        yesterdayCashierColumn.setCellValueFactory(cellData -> cellData.getValue().cashierProperty());
        yesterdayDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        yesterdayTotalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        monthlyCashierColumn.setCellValueFactory(cellData -> cellData.getValue().cashierProperty());
        monthlyDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        monthlyTotalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        // Load data for all tabs
        loadTodaySalesData();
        loadYesterdaySalesData();
        loadMonthlySalesData();
        updatePerformanceMetrics();
    }

    private void loadTodaySalesData() {
        todaySalesData.clear();
        String sql = "SELECT em_username, date, SUM(total) as total FROM receipt " +
                     "WHERE date = ? GROUP BY em_username, date";
        Connection connect = database.connectDB();

        try {
            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet result = prepare.executeQuery();

            while (result.next()) {
                todaySalesData.add(new SalesRecord(
                    result.getString("em_username"),
                    result.getString("date"),
                    result.getDouble("total")
                ));
            }

            todayTable.setItems(todaySalesData);
            updateTodayTotalLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadYesterdaySalesData() {
        yesterdaySalesData.clear();
        String sql = "SELECT em_username, date, SUM(total) as total FROM receipt " +
                     "WHERE date = ? GROUP BY em_username, date";
        Connection connect = database.connectDB();

        try {
            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
            ResultSet result = prepare.executeQuery();

            while (result.next()) {
                yesterdaySalesData.add(new SalesRecord(
                    result.getString("em_username"),
                    result.getString("date"),
                    result.getDouble("total")
                ));
            }

            yesterdayTable.setItems(yesterdaySalesData);
            updateYesterdayTotalLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMonthlySalesData() {
        monthlySalesData.clear();
        String sql = "SELECT em_username, DATE_FORMAT(date, '%Y-%m') as month, SUM(total) as total FROM receipt " +
                     "WHERE YEAR(date) = ? AND MONTH(date) = ? GROUP BY em_username, DATE_FORMAT(date, '%Y-%m')";
        Connection connect = database.connectDB();

        try {
            PreparedStatement prepare = connect.prepareStatement(sql);
            LocalDate now = LocalDate.now();
            prepare.setInt(1, now.getYear());
            prepare.setInt(2, now.getMonthValue());
            ResultSet result = prepare.executeQuery();

            while (result.next()) {
                monthlySalesData.add(new SalesRecord(
                    result.getString("em_username"),
                    result.getString("month"),
                    result.getDouble("total")
                ));
            }

            monthlyTable.setItems(monthlySalesData);
            updateMonthlyTotalLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTodayTotalLabel() {
        double total = todaySalesData.stream().mapToDouble(SalesRecord::getTotal).sum();
        todayTotalLabel.setText("Total Sales: ৳" + String.format("%.2f", total));
    }

    private void updateYesterdayTotalLabel() {
        double total = yesterdaySalesData.stream().mapToDouble(SalesRecord::getTotal).sum();
        yesterdayTotalLabel.setText("Total Sales: ৳" + String.format("%.2f", total));
    }

    private void updateMonthlyTotalLabel() {
        double total = monthlySalesData.stream().mapToDouble(SalesRecord::getTotal).sum();
        monthlyTotalLabel.setText("Total Sales: ৳" + String.format("%.2f", total));
    }

    private void updatePerformanceMetrics() {
        // Calculate total sales across all periods
        double totalSales = todaySalesData.stream().mapToDouble(SalesRecord::getTotal).sum() +
                            yesterdaySalesData.stream().mapToDouble(SalesRecord::getTotal).sum() +
                            monthlySalesData.stream().mapToDouble(SalesRecord::getTotal).sum();
        totalSalesLabel.setText("Total Sales: ৳" + String.format("%.2f", totalSales));

        // Calculate average transaction value
        String sql = "SELECT AVG(total) as avg_total FROM receipt WHERE date >= ? AND date <= ?";
        Connection connect = database.connectDB();
        double avgTransaction = 0;
        try {
            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
            prepare.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet result = prepare.executeQuery();
            if (result.next()) {
                avgTransaction = result.getDouble("avg_total");
            }
            avgTransactionLabel.setText("Average Transaction: ৳" + String.format("%.2f", avgTransaction));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Find top-performing cashier
        String topCashierSql = "SELECT em_username, SUM(total) as total FROM receipt " +
                              "WHERE date >= ? AND date <= ? GROUP BY em_username ORDER BY total DESC LIMIT 1";
        connect = database.connectDB();
        try {
            PreparedStatement prepare = connect.prepareStatement(topCashierSql);
            prepare.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
            prepare.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet result = prepare.executeQuery();
            if (result.next()) {
                String topCashier = result.getString("em_username") != null ? result.getString("em_username") : "Unknown";
                topCashierLabel.setText("Top Cashier: " + topCashier);
            } else {
                topCashierLabel.setText("Top Cashier: None");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SalesRecord {
        private final SimpleStringProperty cashier;
        private final SimpleStringProperty date;
        private final SimpleDoubleProperty total;

        public SalesRecord(String cashier, String date, double total) {
            this.cashier = new SimpleStringProperty(cashier != null ? cashier : "Unknown");
            this.date = new SimpleStringProperty(date);
            this.total = new SimpleDoubleProperty(total);
        }

        public SimpleStringProperty cashierProperty() {
            return cashier;
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public SimpleDoubleProperty totalProperty() {
            return total;
        }

        public double getTotal() {
            return total.get();
        }
    }
}