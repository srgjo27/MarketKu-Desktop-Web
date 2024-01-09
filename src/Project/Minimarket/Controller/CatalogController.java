package Project.Minimarket.Controller;

import Project.Minimarket.Model.Product;
import Project.Minimarket.Helper.help;

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

public class CatalogController extends help implements Initializable {

    private static final int ROWS_PER_PAGE = 17;

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
    private AnchorPane rootPane;

    @FXML
    private TextField liveSearch;

    @FXML
    private TableColumn<Product, String> colCategory;

    @FXML
    private TableColumn<Product, String> colIDProduct;

    @FXML
    private TableColumn<Product, String> colPrice;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Product, String> colStock;

    @FXML
    private TableColumn<Product, String> colUnit;

    @FXML
    private TableView<Product> tblProduct;

    @FXML
    private Pagination pagination;

    ObservableList<Product> list = FXCollections.observableArrayList();

    private FilteredList<Product> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupData();
        filteredData = new FilteredList<>(list, p -> true);
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
                } else if (product.getUnit().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(product.getPrice()).indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(product.getStock()).indexOf(lowerCaseFilter) != -1) {
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
        colStock.setCellValueFactory(new PropertyValueFactory<Product, String>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<Product, String>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Product, String>("price"));

        int totalPage = (int) (Math.ceil(list.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
    }

    public void setupData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);
            String query = "SELECT product.id_product, product.product_name, category.category_name, product.price, product.stock, product.unit FROM product INNER JOIN category ON product.id_category = category.id_category";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Product product = new Product();
                product.setIDProduct(rs.getString("id_product"));
                product.setProductName(rs.getString("product_name"));
                product.setIDCategory(rs.getString("category_name"));
                product.setStock(rs.getInt("stock"));
                product.setUnit(rs.getString("unit"));
                product.setPrice(rs.getDouble("price"));
                list.add(product);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error: " + e);
        }
    }

    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, list.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<Product> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());

        tblProduct.setItems(sortedData);
    }

    public void close(ActionEvent event) {
        System.exit(0);
    }

    public void minimize(ActionEvent event) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    public void refresh(MouseEvent event) {
        list.clear();
        setupData();
        int totalPage = (int) (Math.ceil(list.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
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
}
