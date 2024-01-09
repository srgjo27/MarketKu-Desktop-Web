package Project.Minimarket.Controller;

import java.net.URL;

import java.sql.*;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import Project.Minimarket.Helper.help;
import Project.Minimarket.Model.Product;
import Project.Minimarket.Model.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TransactionController extends help implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CatalogController.class.getName());

    @FXML
    private Button btnBack;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnPay;

    @FXML
    private Button btnCalculate;

    @FXML
    private TableView<Transaction> tblTransaction;

    @FXML
    private TableView<Product> tblProduct;

    @FXML
    private TableColumn<Transaction, String> colAmount;

    @FXML
    private TableColumn<Transaction, String> colDiscount;

    @FXML
    private TableColumn<Transaction, String> colIDProduct;

    @FXML
    private TableColumn<Transaction, String> colIDTransaction;

    @FXML
    private TableColumn<Transaction, String> colTotal;

    @FXML
    private TableColumn<Product, String> colIdProduct;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Product, String> colPrice;

    @FXML
    private TextField liveSearch;

    @FXML
    private TextField txtAmount;

    @FXML
    private TextField txtChange;

    @FXML
    private TextField txtDiscount;

    @FXML
    private TextField txtTotal;

    @FXML
    private TextField txtIDProduct;

    @FXML
    private TextField txtIDTransaction;

    @FXML
    private TextField txtPay;

    ObservableList<Transaction> listTr = FXCollections.observableArrayList();

    ObservableList<Product> listP = FXCollections.observableArrayList();

    private FilteredList<Product> filteredData;

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    public void minimize(ActionEvent event) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    public void refresh(MouseEvent event) {
        //
    }

    public void back(ActionEvent event) {
        btnBack.getScene().getWindow().hide();
        loadStage("/Project/Minimarket/fxml/App.fxml");
    }

    public void logout(ActionEvent event) {
        Image img = new Image("/Project/Minimarket/img/confirm.png");
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(50);
        imgView.setFitWidth(50);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setTitle("Logout");
        alert.setGraphic(imgView);
        alert.setHeaderText("Apakah anda yakin ingin keluar?");
        alert.showAndWait();
        if (alert.getResult().getText().equals("OK")) {
            btnLogOut.getScene().getWindow().hide();
            loadStage("/Project/Minimarket/fxml/Login.fxml");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewTransaction();
        viewProduct();
    }

    public void setupDataTransaction() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT * FROM transaction ORDER BY id_transaction DESC";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Transaction tr = new Transaction();
                tr.setIDTransaction(rs.getString("id_transaction"));
                tr.setIDProduct(rs.getString("id_product"));
                tr.setAmount(rs.getInt("amount"));
                tr.setDiscount(rs.getDouble("discount"));
                tr.setTotal(rs.getDouble("total"));
                listTr.add(tr);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void viewTransaction() {
        setupDataTransaction();
        colIDTransaction.setCellValueFactory(new PropertyValueFactory<Transaction, String>("IDTransaction"));
        colIDProduct.setCellValueFactory(new PropertyValueFactory<Transaction, String>("IDProduct"));
        colAmount.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Amount"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Total"));
        tblTransaction.setItems(listTr);
    }

    public void calculateTotal() {
        double total = 0;
        double discount = 0;
        int amount = 0;
        double price;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT price FROM product WHERE id_product = (SELECT id_product FROM product WHERE product_name = ?)";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDProduct.getText());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                price = rs.getDouble("price");
                amount = Integer.parseInt(txtAmount.getText());
                discount = Double.parseDouble(txtDiscount.getText());
                total = (price * amount) - ((price * amount) * (discount / 100));
            }
            txtTotal.setText(String.valueOf(total));
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void purchase() {
        double pay = Double.parseDouble(txtPay.getText());
        double total = Double.parseDouble(txtTotal.getText());
        double change = pay - total;
        txtChange.setText(String.valueOf(change));
    }

    public void pay(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "INSERT INTO transaction(id_transaction, id_product, id_account, amount, discount, total) VALUES(?,(SELECT id_product FROM product WHERE product_name = ?),?, ?, ?, ?)";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDTransaction.getText());
            stmt.setString(2, txtIDProduct.getText());
            stmt.setString(3, "1");
            stmt.setInt(4, Integer.parseInt(txtAmount.getText()));
            stmt.setDouble(5, Double.parseDouble(txtDiscount.getText()));
            stmt.setDouble(6, Double.parseDouble(txtTotal.getText()));
            if (txtIDTransaction.getText().equals("") || txtIDProduct.getText().equals("")
                    || txtAmount.getText().equals("") || txtDiscount.getText().equals("")
                    || txtTotal.getText().equals("") || txtPay.getText().equals("") || txtChange.getText().equals("")) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Pembayaran berhasil", "/Project/Minimarket/img/success.png");
            }
            purchase();
            conn.commit();
            reduceStock();
            listTr.clear();
            viewTransaction();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void newTransaction(ActionEvent event) {
        txtIDTransaction.setText("");
        txtIDProduct.setText("");
        txtAmount.setText("");
        txtDiscount.setText("");
        txtTotal.setText("");
        txtPay.setText("");
        txtChange.setText("");
    }

    public void setupDataProduct() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT * FROM product";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Product p = new Product();
                p.setIDProduct(rs.getString("id_product"));
                p.setProductName(rs.getString("product_name"));
                p.setPrice(rs.getDouble("price"));
                listP.add(p);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void viewProduct() {
        setupDataProduct();
        colIdProduct.setCellValueFactory(new PropertyValueFactory<Product, String>("IDProduct"));
        colProductName.setCellValueFactory(new PropertyValueFactory<Product, String>("ProductName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Product, String>("Price"));
        tblProduct.setItems(listP);

        filteredData = new FilteredList<>(listP, p -> true);
        liveSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (product.getIDProduct().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (product.getProductName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(product.getPrice()).indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedData);
    }

    public void reduceStock() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "UPDATE product AS p INNER JOIN transaction AS t ON p.id_product = t.id_product SET p.stock = p.stock - t.amount WHERE t.id_transaction = ?";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDTransaction.getText());
            stmt.executeUpdate();
            conn.commit();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }
}
