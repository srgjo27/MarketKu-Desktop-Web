package Project.Minimarket.Helper;

import java.io.IOException;

import org.controlsfx.control.Notifications;

import javafx.fxml.FXMLLoader;

import javafx.geometry.Pos;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class help {
    private double x = 0;
    private double y = 0;

    protected void loadStage(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

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

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notif(String title, String text, String string) {
        Image img = new Image(string);
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(50);
        imgView.setFitWidth(50);
        Notifications notification = Notifications.create()
                .title(title)
                .text(text)
                .graphic(imgView)
                .hideAfter(javafx.util.Duration.seconds(5))
                .position(Pos.TOP_RIGHT);
        notification.darkStyle();
        notification.show();
    }
}
