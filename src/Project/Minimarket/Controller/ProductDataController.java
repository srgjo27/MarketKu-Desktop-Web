package Project.Minimarket.Controller;

import Project.Minimarket.Helper.help;
import Project.Minimarket.Model.Category;
import Project.Minimarket.Model.Product;

import java.net.URL;
import java.sql.*;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProductDataController extends help implements Initializable {

    private static final int ROWS_PER_PAGE = 17;

    private static final Logger LOGGER = Logger.getLogger(CatalogController.class.getName());

    @FXML
    private AnchorPane rootPane;

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
    private Button btnAddCategory;

    @FXML
    private Button btnUpdateCategory;

    @FXML
    private Button btnDeleteCategory;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnUpdateProduct;

    @FXML
    private Button btnDeleteProduct;

    @FXML
    private TableColumn<Product, String> colCategory;

    @FXML
    private TableColumn<Product, String> colIDProduct;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Category, String> colCategoryName;

    @FXML
    private TableColumn<Category, String> colIDCategory;

    @FXML
    private TextField liveSearch;

    @FXML
    private TextField txtIDCategory;

    @FXML
    private TextField txtCategoryName;

    @FXML
    private TextField txtIDProduct;

    @FXML
    private TextField txtProductName;

    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtUnit;

    @FXML
    private TextField txtPrice;

    @FXML
    private Pagination pagination;

    @FXML
    private TableView<Product> tblProduct;

    @FXML
    private TableView<Category> tblCategory;

    ObservableList<Product> listProduct = FXCollections.observableArrayList();

    ObservableList<Category> listCategory = FXCollections.observableArrayList();

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
        listProduct.clear();
        listCategory.clear();
        setupDataProduct();
        setupDataCategory();
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
        viewProduct();
        viewCategory();

        try {
            Connection conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT * FROM category";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                cmbCategory.getItems().add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }

    }

    public void setupDataProduct() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT product.id_product, product.product_name, category.category_name FROM product INNER JOIN category ON product.id_category = category.id_category";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Product product = new Product();
                product.setIDProduct(rs.getString("id_product"));
                product.setProductName(rs.getString("product_name"));
                product.setIDCategory(rs.getString("category_name"));
                listProduct.add(product);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void viewProduct() {
        setupDataProduct();
        filteredData = new FilteredList<>(listProduct, p -> true);
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
                } else if (product.getIDCategory().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }
            });
            changeTableView(pagination.getCurrentPageIndex(), ROWS_PER_PAGE);
        });

        colIDProduct.setCellValueFactory(new PropertyValueFactory<Product, String>("IDProduct"));
        colProductName.setCellValueFactory(new PropertyValueFactory<Product, String>("ProductName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Product, String>("IDCategory"));

        int totalPage = (int) (Math.ceil(listProduct.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
    }

    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, listProduct.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<Product> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());

        tblProduct.setItems(sortedData);
    }

    public void setupDataCategory() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT * FROM category";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Category category = new Category();
                category.setIDCategory(rs.getString("id_category"));
                category.setCategoryName(rs.getString("category_name"));
                listCategory.add(category);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void viewCategory() {
        setupDataCategory();
        colIDCategory.setCellValueFactory(new PropertyValueFactory<Category, String>("IDCategory"));
        colCategoryName.setCellValueFactory(new PropertyValueFactory<Category, String>("CategoryName"));
        tblCategory.setItems(listCategory);
    }

    public void addCategory(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "INSERT INTO category(id_category, category_name) VALUES(?, ?)";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDCategory.getText());
            stmt.setString(2, txtCategoryName.getText());
            if (txtIDCategory.getText().isEmpty() || txtCategoryName.getText().isEmpty()) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Data berhasil ditambahkan!", "/Project/Minimarket/img/success.png");
            }
            conn.commit();
            listCategory.clear();
            viewCategory();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void updateCategory(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "UPDATE category SET category_name = ? WHERE id_category = ?";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtCategoryName.getText());
            stmt.setString(2, txtIDCategory.getText());
            if (txtIDCategory.getText().isEmpty() || txtCategoryName.getText().isEmpty()) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Data berhasil diubah!", "/Project/Minimarket/img/success.png");
            }
            conn.commit();
            listCategory.clear();
            viewCategory();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void deleteCategory(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "DELETE FROM category WHERE id_category = ?";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDCategory.getText());
            if (txtIDCategory.getText().isEmpty()) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Data berhasil dihapus!", "/Project/Minimarket/img/success.png");
            }
            conn.commit();
            listCategory.clear();
            viewCategory();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void addProduct(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "INSERT INTO product(id_product, product_name, id_category, stock, unit, price) VALUES(?, ?, (SELECT id_category FROM category WHERE category_name = ?), ?, ?, ?)";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDProduct.getText());
            stmt.setString(2, txtProductName.getText());
            stmt.setString(3, cmbCategory.getValue());
            stmt.setString(4, txtStock.getText());
            stmt.setString(5, txtUnit.getText());
            stmt.setString(6, txtPrice.getText());
            if (txtIDProduct.getText().isEmpty() || txtProductName.getText().isEmpty()
                    || cmbCategory.getValue().isEmpty() || txtStock.getText().isEmpty()
                    || txtUnit.getText().isEmpty() || txtPrice.getText().isEmpty()) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Data berhasil ditambahkan!", "/Project/Minimarket/img/success.png");
            }
            conn.commit();
            listProduct.clear();
            viewProduct();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void updateProduct(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "UPDATE product SET product_name = ?, id_category = (SELECT id_category FROM category WHERE category_name = ?), stock = ?, unit = ?, price = ? WHERE id_product = ?";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtProductName.getText());
            stmt.setString(2, cmbCategory.getValue());
            stmt.setString(3, txtStock.getText());
            stmt.setString(4, txtUnit.getText());
            stmt.setString(5, txtPrice.getText());
            stmt.setString(6, txtIDProduct.getText());
            if (txtIDProduct.getText().isEmpty() || txtProductName.getText().isEmpty()
                    || cmbCategory.getValue().isEmpty() || txtStock.getText().isEmpty()
                    || txtUnit.getText().isEmpty() || txtPrice.getText().isEmpty()) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Data berhasil diubah!", "/Project/Minimarket/img/success.png");
            }
            conn.commit();
            listProduct.clear();
            viewProduct();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    public void deleteProduct(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "DELETE FROM product WHERE id_product = ?";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, txtIDProduct.getText());
            if (txtIDProduct.getText().isEmpty()) {
                notif("Error", "Field tidak boleh kosong", "/Project/Minimarket/img/error.png");
            } else {
                stmt.executeUpdate();
                notif("Success", "Data berhasil dihapus!", "/Project/Minimarket/img/success.png");
            }
            conn.commit();
            listProduct.clear();
            viewProduct();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }
}
