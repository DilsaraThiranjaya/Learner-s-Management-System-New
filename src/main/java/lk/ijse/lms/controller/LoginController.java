package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.User;
import lk.ijse.lms.others.WindowController;
import lk.ijse.lms.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    public JFXTextField txtFieldUserID;
    @FXML
    public JFXPasswordField txtFieldPassword;
    @FXML
    private StackPane rootNode;
    @FXML
    private Rectangle loginBoxShape;

    private WindowController windowController;

    private User user;

    private double xOffset = 0;
    private double yOffset = 0;



    public void initialize() {
        Image img = new Image("/asserts/images/Login_box_side.jpg");
        loginBoxShape.setFill(new ImagePattern(img));
    }

    public void setWindowController(WindowController windowController) {
        this.windowController = windowController;
    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {
        String userID = txtFieldUserID.getText();
        String password = txtFieldPassword.getText();

        try {
            checkCredential(userID, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkCredential(String userID, String password) throws SQLException, IOException {
        String sql = "SELECT userId, userName, password, employeeId FROM user WHERE userId = ?";

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1, userID);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()){

            String dbPw = resultSet.getString(3);

            if(dbPw.equals(password)){

                user = new User();

                user.setUserId(resultSet.getString(1));
                user.setUserName(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
                user.setEmployeeId(resultSet.getString(4));

                navigateToTheDashboard();
            } else {
                new Alert(Alert.AlertType.ERROR, "Password is incorrect!").show();
            }
        } else {
            new Alert(Alert.AlertType.INFORMATION, "User ID not found!").show();
        }
    }

    private void navigateToTheDashboard() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Dashboard_page.fxml"));
        AnchorPane dashboardRoot = loader.load();

        DashboardController dashboardController = loader.getController();

        dashboardController.setUser(user); // Pass the User object to the DashboardController

        dashboardController.setWindowController(windowController);

        Stage dashboardStage = new Stage();

        Scene scene = new Scene(dashboardRoot);
        dashboardStage.initStyle(StageStyle.TRANSPARENT);
        dashboardStage.setScene(scene);
        dashboardStage.centerOnScreen();
        dashboardStage.setTitle("Dashboard");

        // Get the current stage and close it when the new stage is shown
        Stage currentStage = (Stage) rootNode.getScene().getWindow();
        dashboardStage.setOnShown(event -> currentStage.close());

        dashboardStage.show();
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Primary_Register_page.fxml"));

        Stage stage = new Stage();

        StackPane registerRoot = loader.load();

        Rectangle clip = new Rectangle(958, 622);
        clip.setArcHeight(90);
        clip.setArcWidth(90);

        registerRoot.setClip(clip);

        RegistorController registorController = loader.getController();

        WindowController windowController = new WindowController();
        registorController.setWindowController(windowController);

        Image img = new Image("/asserts/images/Login_box_side.jpg");
        registorController.setLoginBoxShape(img);

        Scene scene = new Scene(registerRoot);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.centerOnScreen();

        // Get the current stage and close it when the new stage is shown
        Stage currentStage = (Stage) rootNode.getScene().getWindow();

        // Create a transition animation
        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(10), currentStage.getScene().getRoot());
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(e -> {
            currentStage.hide(); // Hide the current stage
            stage.show(); // Show the registration stage

            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(10), stage.getScene().getRoot());
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            fadeInTransition.play();
        });
        fadeOutTransition.play();
    }

    @FXML
    void btnForgotPassOnAction(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Forgot_Password_page.fxml"));

        Stage stage = new Stage();

        StackPane registerRoot = null;
        try {
            registerRoot = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Rectangle clip = new Rectangle(958, 622);
        clip.setArcHeight(90);
        clip.setArcWidth(90);

        registerRoot.setClip(clip);

        ForgotPasswordController forgotPasswordController = loader.getController();

        WindowController windowController = new WindowController();
        forgotPasswordController.setWindowController(windowController);

        Image img = new Image("/asserts/images/Login_box_side.jpg");
        forgotPasswordController.setLoginBoxShape(img);

        Scene scene = new Scene(registerRoot);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.centerOnScreen();

        // Get the current stage and close it when the new stage is shown
        Stage currentStage = (Stage) rootNode.getScene().getWindow();

        // Create a transition animation
        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(10), currentStage.getScene().getRoot());
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(e -> {
            currentStage.hide(); // Hide the current stage
            stage.show(); // Show the registration stage

            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(10), stage.getScene().getRoot());
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            fadeInTransition.play();
        });
        fadeOutTransition.play();
    }

    @FXML
    void btnCloseOnMouseClicked(MouseEvent event) {
        Stage stage = (Stage) rootNode.getScene().getWindow();
        if (windowController != null) {
            windowController.close(stage);
        }
    }

    @FXML
    void btnMinimizeOnMouseClicked(MouseEvent event) {
        Stage stage = (Stage) rootNode.getScene().getWindow();
        if (windowController != null) {
            windowController.minimize(stage);
        }
    }

    @FXML
    void OnMousePressed(MouseEvent event) {
// Record the mouse position relative to the window
        Stage stage = (Stage) rootNode.getScene().getWindow();
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    void OnMouseDragged(MouseEvent event) {
// Move the window by the mouse delta
        Stage stage = (Stage) rootNode.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}
