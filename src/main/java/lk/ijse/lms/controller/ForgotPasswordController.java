package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lk.ijse.lms.model.Employee;
import lk.ijse.lms.others.EmailSender;
import lk.ijse.lms.others.WindowController;
import lk.ijse.lms.repository.EmployeeRepository;
import lk.ijse.lms.repository.LoginRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML
    private JFXButton btnChangePass;

    @FXML
    private Rectangle loginBoxShape;

    @FXML
    private StackPane rootNode;

    @FXML
    private JFXPasswordField txtFieldConfirmPassword;

    @FXML
    private JFXPasswordField txtFieldPassword;

    @FXML
    private JFXTextField txtFieldUserid;

    @FXML
    private JFXTextField txtOtpCode;

    private WindowController windowController;

    private double xOffset = 0;
    private double yOffset = 0;

    private int otpCode;


    public void initialize() {
        otpCode = -1;
    }

    public void setWindowController(WindowController windowController) {
        this.windowController = windowController;
    }

    public void setLoginBoxShape(Image img) {
        loginBoxShape.setFill(new ImagePattern(img));
    }

    public static int generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000; // Generate a random number between 100000 and 999999
        return otp;
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

    @FXML
    void btnChangePassOnAction(ActionEvent event) {
        String userId = txtFieldUserid.getText();
        String otp = txtOtpCode.getText();
        String pass = txtFieldPassword.getText();
        String confirmPass = txtFieldConfirmPassword.getText();

        if(userId != null && !userId.isEmpty() && otp != null && !otp.isEmpty() && pass != null && !pass.isEmpty() && confirmPass != null && !confirmPass.isEmpty()){
            if(isValid()){
                try {
                    if(LoginRepository.isUserExist(userId)){
                        if(confirmPass.equals(pass)) {
                            if(Integer.parseInt(otp) == otpCode){
                                try {
                                    boolean isUpdated = LoginRepository.changePassword(userId, pass);
                                    if (isUpdated) {
                                        new Alert(Alert.AlertType.CONFIRMATION, "Password Changed!").show();
                                        otpCode = -1;
                                        txtOtpCode.setText("");
                                        txtFieldUserid.setText("");
                                        txtFieldPassword.setText("");
                                        txtFieldConfirmPassword.setText("");
                                        txtFieldUserid.requestFocus();
                                    }else {
                                        new Alert(Alert.AlertType.WARNING, "Something went wrong!").show();
                                    }
                                } catch (SQLException e) {
                                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                                }
                            } else {
                                new Alert(Alert.AlertType.ERROR, "OTP not matched!").show();
                                txtOtpCode.requestFocus();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Passwors is not matched!").show();
                            txtFieldPassword.setText("");
                            txtFieldConfirmPassword.setText("");
                            txtFieldPassword.requestFocus();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "User not found!").show();
                        txtFieldUserid.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter all mandatory details!").show();
        }
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Login_page.fxml"));

        Stage stage = new Stage();

        StackPane loginRoot = null;
        try {
            loginRoot = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    void btnSendOtpOnAction(ActionEvent event) {
        otpCode = generateOTP();

        String userId = txtFieldUserid.getText();

        if(userId != null && !userId.isEmpty()){
            try {
                if(LoginRepository.isUserExist(userId)){
                    String employeeId = null;
                    try {
                        employeeId = LoginRepository.getEmployeeId(userId);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    if(employeeId != null){
                        Employee employee = null;
                        try {
                            employee = EmployeeRepository.searchById(employeeId);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        String emailTitle = "OTP Code";
                        String emailContent = "Your One Time OTP Code: " + String.valueOf(otpCode);

                        EmailSender emailSender = new EmailSender();
                        boolean isSent = emailSender.sendEmail(employee.getEmail(), emailTitle, emailContent);

                        if(isSent){
                            new Alert(Alert.AlertType.CONFIRMATION, "OTP sent to your email successfully!").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "OTP not sent!").show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Employee not found!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "User not found!").show();
                    txtFieldUserid.requestFocus();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a User ID!").show();
            txtFieldUserid.requestFocus();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(TextField.OTP,txtOtpCode)) return false;
        return true;
    }

    @FXML
    void txtOtpCodeOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.OTP,txtOtpCode);
    }

}
