package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.lms.model.User;
import lk.ijse.lms.repository.ChangeUserDetailsRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;

public class SettingsController {

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private JFXPasswordField txtFieldConfirmPassword;

    @FXML
    private JFXPasswordField txtFieldNewPassword;

    @FXML
    private JFXTextField txtFieldUserId;

    @FXML
    private JFXTextField txtFieldUserName;

    private User user;


    private void initializeTextFeildText() {
        txtFieldUserId.setText(user.getUserId());
        txtFieldUserName.setText(user.getUserName());
    }

    public void setUser(User user) {
        this.user = user;
        initializeTextFeildText();
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String userId = txtFieldUserId.getText();
        String userName = txtFieldUserName.getText();
        String newPassword = txtFieldNewPassword.getText();
        String confirmPassword = txtFieldConfirmPassword.getText();

        if(userId != null && !userId.isEmpty() && userName != null && !userName.isEmpty() && newPassword != null && !newPassword.isEmpty() && confirmPassword != null && !confirmPassword.isEmpty()) {
            if(Regex.setTextColor(TextField.NAME,txtFieldUserName)){
                try {
                    if (user.getUserId().equals(userId) || ChangeUserDetailsRepository.isUserIdAvailable(userId)){
                        if(confirmPassword.equals(newPassword)) {
                            User newUser = new User(userId, userName, newPassword,user.getEmployeeId());
                            try {
                                boolean isUpdated = ChangeUserDetailsRepository.update(newUser,user.getUserId());
                                if (isUpdated) {
                                    new Alert(Alert.AlertType.CONFIRMATION, "User details updated!").show();
                                    txtFieldNewPassword.setText("");
                                    txtFieldConfirmPassword.setText("");
                                    txtFieldUserId.requestFocus();
                                }else {
                                    new Alert(Alert.AlertType.WARNING, "Something went wrong!").show();
                                }
                            } catch (SQLException e) {
                                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Passwors is not matched!").show();
                            txtFieldNewPassword.setText("");
                            txtFieldConfirmPassword.setText("");
                            txtFieldNewPassword.requestFocus();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "User ID already exist!").show();
                        txtFieldUserId.requestFocus();
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
    void txtFieldUserNameOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtFieldUserName);
    }

}
