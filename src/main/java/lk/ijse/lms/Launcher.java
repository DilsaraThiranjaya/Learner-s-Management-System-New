package lk.ijse.lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.lms.controller.DashboardController;
import lk.ijse.lms.controller.LoginController;
import lk.ijse.lms.others.WindowController;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Login_page.fxml"));

        stage = new Stage();

        StackPane loginRoot = loader.load();

        Rectangle clip = new Rectangle(958, 622);
        clip.setArcHeight(90);
        clip.setArcWidth(90);

        loginRoot.setClip(clip);

        LoginController loginController = loader.getController();

        WindowController windowController = new WindowController();
        loginController.setWindowController(windowController);

        Scene scene = new Scene(loginRoot);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Login_page");
        stage.centerOnScreen();
        stage.show();

    }
}
