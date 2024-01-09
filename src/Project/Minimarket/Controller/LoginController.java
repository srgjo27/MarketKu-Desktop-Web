package Project.Minimarket.Controller;

import java.io.IOException;

import java.net.URL;

import java.sql.*;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    private double x = 0;
    private double y = 0;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnLogin;

    public void close(ActionEvent event) {
        System.exit(0);
    }

    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;

    public void login() throws IOException {
        try {
            conn = DBConnection.getDBConnection();
            String query = "SELECT * FROM account WHERE username = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username.getText());
            stmt.setString(2, password.getText());
            rs = stmt.executeQuery();
            if (username.getText().isEmpty() || password.getText().isEmpty()) {
                Notifications notification = Notifications.create()
                        .title("Warning")
                        .text("Username atau Password tidak boleh kosong!")
                        .graphic(null)
                        .hideAfter(javafx.util.Duration.seconds(5))
                        .position(Pos.TOP_RIGHT);
                notification.darkStyle();
                notification.showWarning();
            } else {
                if (rs.next()) {
                    Image img = new Image("/Project/Minimarket/img/success.png");
                    ImageView imgView = new ImageView(img);
                    imgView.setFitHeight(50);
                    imgView.setFitWidth(50);
                    Notifications notification = Notifications.create()
                            .title("Success")
                            .text("Login berhasil!")
                            .graphic(imgView)
                            .hideAfter(javafx.util.Duration.seconds(5))
                            .position(Pos.TOP_RIGHT);
                    notification.darkStyle();
                    notification.show();

                    btnLogin.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(getClass().getResource("/Project/Minimarket/fxml/App.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    root.setOnMousePressed((MouseEvent event) -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                        stage.setOpacity(0.7f);
                    });

                    root.setOnMouseReleased((MouseEvent event) -> {
                        stage.setOpacity(1.0f);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT);

                    stage.setScene(scene);
                    stage.show();
                } else {
                    Notifications notification = Notifications.create()
                            .title("Error")
                            .text("Username atau Password salah!")
                            .graphic(null)
                            .hideAfter(javafx.util.Duration.seconds(5))
                            .position(Pos.TOP_RIGHT);
                    notification.darkStyle();
                    notification.showError();
                }
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO
    }
}
