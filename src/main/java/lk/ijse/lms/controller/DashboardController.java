package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lk.ijse.lms.model.User;
import lk.ijse.lms.others.WindowController;
import lk.ijse.lms.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DashboardController {

    @FXML
    private JFXButton btnAttendances;

    @FXML
    private JFXButton btnEmployees;

    @FXML
    private JFXButton btnPayments;

    @FXML
    private JFXButton btnSalaries;

    @FXML
    private JFXButton btnStudents;

    @FXML
    private JFXButton btnVehicles;

    @FXML
    private JFXButton btnExams;

    @FXML
    private JFXButton btnSchedule;

    @FXML
    private JFXButton btnCourses;

    @FXML
    private JFXButton btnSettings;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblDate;

    @FXML
    private HBox iconSettings;

    @FXML
    private HBox iconAttendances;

    @FXML
    private HBox iconCourses;

    @FXML
    private HBox iconEmployees;

    @FXML
    private HBox iconExams;

    @FXML
    private HBox iconPayments;

    @FXML
    private HBox iconSalaries;

    @FXML
    private HBox iconSchedules;

    @FXML
    private HBox iconStudents;

    @FXML
    private HBox iconVehicles;

    @FXML
    private Label menu;

    @FXML
    private Label menuClose;

    @FXML
    private AnchorPane rootNode;

    @FXML
    private AnchorPane sidebar;

    @FXML
    private Label lblUiUsername;

    @FXML
    private ImageView ivLogoutIcon;

    @FXML
    private ImageView ivRegistorIcon;

    @FXML
    private Label lblLogout;

    @FXML
    private Label lblRegistor;

    @FXML
    private BorderPane borderPane;

    @FXML
    private AnchorPane taskBarPane;

    private HBox selectedIcon;

    private JFXButton selectedButton;

    private User user;

    private TaskBarController taskBarController;

    private WindowController windowController;

    private double xOffset = 0;
    private double yOffset = 0;



    public void initialize() {
        setSidebar();
        setTimeAndDate();

        sidebarButtonHoverUtilize();
    }

    public void disableSidebarButtons() {
        btnAttendances.setDisable(true);
        btnEmployees.setDisable(true);
        btnPayments.setDisable(true);
        btnSalaries.setDisable(true);
        btnStudents.setDisable(true);
        btnExams.setDisable(true);
        btnSchedule.setDisable(true);
        btnCourses.setDisable(true);
        iconAttendances.setDisable(true);
        iconEmployees.setDisable(true);
        iconPayments.setDisable(true);
        iconSalaries.setDisable(true);
        iconStudents.setDisable(true);
        iconExams.setDisable(true);
        iconSchedules.setDisable(true);
        iconCourses.setDisable(true);
    }

    private void setTimeAndDate() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // Update the label with the current time
                    LocalDateTime currentTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                    lblTime.setText(formatter.format(currentTime));
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();

        LocalDate date = LocalDate.now();
        lblDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
    }

    public void setWindowController(WindowController windowController) {
        this.windowController = windowController;
    }

    private void setTaskBar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskBar.fxml"));

        try {
            AnchorPane taskBar = loader.load();

            taskBarController = loader.getController();
            taskBarController.setUser(user);
            taskBarController.setRootNode(rootNode);

            taskBarPane.getChildren().add(taskBar); // Add the loaded task bar to the taskBarPane
            AnchorPane.setTopAnchor(taskBar, 0.0);
            AnchorPane.setLeftAnchor(taskBar, 0.0);
            AnchorPane.setRightAnchor(taskBar, 0.0);
            AnchorPane.setBottomAnchor(taskBar, 0.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUser(User user) {
        this.user = user;
        setUiUsername();
        setTaskBar();
        setDisabledButton();
    }

    private void setDisabledButton() {
        String role = null;
        try {
            role = EmployeeRepository.getRole(user.getEmployeeId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(role != null && role.equals("Instructor")){
            disableSidebarButtons();
            loadPage("Vehicle_page");
            setDefaultSelectedButtonStyle(role);
        } else {
            loadPage("Employee_page");
            setDefaultSelectedButtonStyle(role);
        }
    }

    private void setUiUsername() {
        if (user != null) {
            lblUiUsername.setText(user.getUserName());
        }
    }

    private void sidebarButtonHoverUtilize() {
        addHoverEffect(btnEmployees,iconEmployees);
        addHoverEffect(btnStudents,iconStudents);
        addHoverEffect(btnSalaries,iconSalaries);
        addHoverEffect(btnPayments,iconPayments);
        addHoverEffect(btnVehicles,iconVehicles);
        addHoverEffect(btnExams,iconExams);
        addHoverEffect(btnSchedule,iconSchedules);
        addHoverEffect(btnCourses,iconCourses);
        addHoverEffect(btnAttendances,iconAttendances);
        addHoverEffect(btnSettings,iconSettings);
        addHoverEffect(lblRegistor,ivRegistorIcon,"/asserts/icons/hover/Registor_icon_hover.png","/asserts/icons/Registor_icon.png");
        addHoverEffect(lblLogout,ivLogoutIcon,"/asserts/icons/hover/Logout_icon_hover.png","/asserts/icons/Logout_icon.png");
    }

    public void addHoverEffect(Label button, ImageView icon, String hoverPath, String defaultPath) {
        button.setOnMouseEntered(event -> {
                button.setStyle("-fx-text-fill: #0504AA; -fx-border-color: #0504AA; -fx-border-width: 0px 0px 2px 0px;");

                Image hoverIcon = new Image(hoverPath);
                icon.setImage(hoverIcon);
        });

        button.setOnMouseExited(event -> {
                button.setStyle("-fx-text-fill: White;");

                Image defaultIcon = new Image(defaultPath);
                icon.setImage(defaultIcon);
        });
    }

    public void addHoverEffect(JFXButton button, HBox icon) {
        button.setOnMouseEntered(event -> {
            if (button != selectedButton) {
                button.setStyle("-fx-background-color:  #4dcaf0a1; -fx-background-radius: 0px 30px 30px 0px; -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  #4dcaf0a1; -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;");
                icon.setStyle("-fx-background-color:  #4dcaf0a1; -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  #0504AA; -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;");
            }
        });

        button.setOnMouseExited(event -> {
            if (button != selectedButton) {
                button.setStyle("-fx-background-color:  transparent; -fx-background-radius: 0px 0px 0px 0px; -fx-border-width: 0px 0px 0px 0px;");
                icon.setStyle("-fx-background-color:  transparent; -fx-background-radius: 0px 0px 0px 0px; -fx-border-width: 0px 0px 0px 0px;");
            }
        });

        icon.setOnMouseEntered(event -> {
            if (button != selectedButton) {
                button.setStyle("-fx-background-color:  #4dcaf0a1; -fx-background-radius: 0px 30px 30px 0px; -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  #4dcaf0a1; -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;");
                icon.setStyle("-fx-background-color:  #4dcaf0a1; -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  #0504AA; -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;");
            }
        });

        icon.setOnMouseExited(event -> {
            if (button != selectedButton) {
                button.setStyle("-fx-background-color:  transparent; -fx-background-radius: 0px 0px 0px 0px; -fx-border-width: 0px 0px 0px 0px;");
                icon.setStyle("-fx-background-color:  transparent; -fx-background-radius: 0px 0px 0px 0px; -fx-border-width: 0px 0px 0px 0px;");
            }
        });
    }

    private void setDefaultSelectedButtonStyle(String role) {
        if(role != null && role.equals("Instructor")){
            selectedButton = btnVehicles; // Set the initial selected button
            selectedIcon = iconVehicles;
            } else {
            selectedButton = btnEmployees; // Set the initial selected button
            selectedIcon = iconEmployees;
        }
        selectedButton.setStyle("-fx-background-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #4169e1 0.0%, #4cc9f0 100.0%); -fx-background-radius: 0px 30px 30px 0px; -fx-effect: dropshadow(gaussian, #4CC9F0, 10, 0, 0, 0); -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #4169e1 0.0%, #4cc9f0 100.0%); -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;"); // Apply the selected style
        selectedIcon.setStyle("-fx-background-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #0504AA 0.0%, #4cc9f0 100.0%); -fx-effect: dropshadow(gaussian, #4CC9F0, 10, 0, 0, 0); -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #0504AA 0.0%, #4cc9f0 100.0%); -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;");
    }

    private void handleSelection(JFXButton button, HBox icon) {
        if (selectedButton != null) {
            selectedButton.setStyle(" -fx-background-color:  transparent; -fx-background-radius: 0px 0px 0px 0px; -fx-border-width: 0px 0px 0px 0px;"); // Deselect the previously selected button

        }
        selectedButton = button; // Set the new selected button
        selectedButton.setStyle("-fx-background-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #4169e1 0.0%, #4cc9f0 100.0%); -fx-background-radius: 0px 30px 30px 0px; -fx-effect: dropshadow(gaussian, #4CC9F0, 10, 0, 0, 0); -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #4169e1 0.0%, #4cc9f0 100.0%); -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;"); // Apply the selected style

        if (selectedIcon != null) {
            selectedIcon.setStyle(" -fx-background-color:  transparent; -fx-background-radius: 0px 0px 0px 0px; -fx-border-width: 0px 0px 0px 0px;");

        }
        selectedIcon = icon;
        selectedIcon.setStyle("-fx-background-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #0504AA 0.0%, #4cc9f0 100.0%); -fx-effect: dropshadow(gaussian, #4CC9F0, 10, 0, 0, 0); -fx-transition: background-color 0.3s, scale-x 0.3s, scale-y 0.3s; -fx-border-color:  linear-gradient(from 53.5545% 61.6114% to 53.5545% 100.0%, #0504AA 0.0%, #4cc9f0 100.0%); -fx-border-width: 0px 0px 0px 3px; -fx-border-radius: 5px;");
    }

    public void setSidebar(){
        menuClose.setVisible(false);
        sidebar.setTranslateX(-277);
        menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(sidebar);

            slide.setToX(0);
            slide.play();

            sidebar.setTranslateX(-277);

            slide.setOnFinished((ActionEvent e) -> {
                menu.setVisible(false);
                menuClose.setVisible(true);
            });
        });

        menuClose.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(sidebar);

            slide.setToX(-277);
            slide.play();

            sidebar.setTranslateX(0);

            slide.setOnFinished((ActionEvent e) -> {
                menu.setVisible(true);
                menuClose.setVisible(false);
            });
        });
    }

    public void loadPage(String page) {
        AnchorPane root = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + page + ".fxml"));
            root = loader.load();

            // If the page is Vehicle_page, get the VehicleController instance and set the DashboardController
            if (page.equals("Vehicle_page")) {
                VehicleController vehicleController = loader.getController();
                vehicleController.setDashboardController(this);
            }
            if (page.equals("Vehicle_maintenance_page")) {
                VehicleMaintenanceController vehicleMaintenanceController = loader.getController();
                vehicleMaintenanceController.setDashboardController(this);
            }
            if (page.equals("Setting_page")) {
                SettingsController settingsController = loader.getController();
                settingsController.setUser(user);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        borderPane.setCenter(root);

    }

    @FXML
    void btnLogoutOnClicked(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Login_page.fxml"));
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

        loginController.setWindowController(windowController);

        Scene scene = new Scene(loginRoot);
        scene.setFill(Color.TRANSPARENT);

        Stage stage = new Stage();

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Loging_page");
        stage.centerOnScreen();


        // Get the current stage and close it when the new stage is shown
        Stage currentStage = (Stage) rootNode.getScene().getWindow();
        stage.setOnShown( e -> currentStage.close());

        stage.show();
    }

    @FXML
    void btnRegistorOnClicked(MouseEvent event) {

        Parent root = null;
        try {
            root = FXMLLoader.load(this.getClass().getResource("/view/Register_page.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Registor Form");
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL); // Set as modal

        stage.showAndWait();
    }

    @FXML
    void btnAttendancesOnClicked(MouseEvent event) {
        loadPage("Attendance_page");
        handleSelection(btnAttendances,iconAttendances);
    }

    @FXML
    void btnCoursesOnClicked(MouseEvent event) {
        loadPage("Course_page");
        handleSelection(btnCourses,iconCourses);
    }

    @FXML
    void btnEmployeesOnClicked(MouseEvent event) {
        loadPage("Employee_page");
        handleSelection(btnEmployees,iconEmployees);
    }

    @FXML
    void btnExamsOnClicked(MouseEvent event) {
        loadPage("Exam_page");
        handleSelection(btnExams,iconExams);
    }

    @FXML
    void btnPaymentsOnClicked(MouseEvent event) {
        loadPage("Payment_page");
        handleSelection(btnPayments,iconPayments);
    }

    @FXML
    void btnSalariesOnClicked(MouseEvent event) {
        loadPage("Salary_page");
        handleSelection(btnSalaries,iconSalaries);
    }

    @FXML
    void btnStudentsOnClicked(MouseEvent event) {
        loadPage("Student_page");
        handleSelection(btnStudents,iconStudents);
    }

    @FXML
    void btnVehiclesOnClicked(MouseEvent event) {
        loadPage("Vehicle_page");
        handleSelection(btnVehicles,iconVehicles);
    }

    @FXML
    void btnSchedulesOnClicked(MouseEvent event) {
        loadPage("Lesson_schedule_page");
        handleSelection(btnSchedule,iconSchedules);
    }

    @FXML
    void btnSettingsOnClicked(MouseEvent event) {
        loadPage("Setting_page");
        handleSelection(btnSettings,iconSettings);
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
