package Project.Minimarket.Controller;

import Project.Minimarket.Helper.help;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppController extends help implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnCatalog;

    @FXML
    private Button btnProductData;

    @FXML
    private Button btnTransaction;

    public void close(ActionEvent event) {
        System.exit(0);
    }

    public void minimize(ActionEvent event) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handlebuttonclicks(ActionEvent actionEvent) {
        if (((Button) actionEvent.getSource()).equals(btnCatalog)) {
            btnCatalog.getScene().getWindow().hide();
            loadStage("/Project/Minimarket/fxml/Catalog.fxml");
        } else if (((Button) actionEvent.getSource()).equals(btnProductData)) {
            btnProductData.getScene().getWindow().hide();
            loadStage("/Project/Minimarket/fxml/ProductData.fxml");
        } else if (((Button) actionEvent.getSource()).equals(btnTransaction)) {
            btnTransaction.getScene().getWindow().hide();
            loadStage("/Project/Minimarket/fxml/Transaction.fxml");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //
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
