package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.lms.model.Course;
import lk.ijse.lms.model.CourseDetails;
import lk.ijse.lms.model.tm.CourseDetailTm;
import lk.ijse.lms.model.tm.CourseTm;
import lk.ijse.lms.repository.CourseRepository;
import lk.ijse.lms.repository.StudentRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;
import java.util.List;

public class CourseController {

    @FXML
    private JFXComboBox<String> cmbStatus;

    @FXML
    private TableColumn<?, ?> columnCDescription;

    @FXML
    private TableColumn<?, ?> columnCDuration;

    @FXML
    private TableColumn<?, ?> columnCId;

    @FXML
    private TableColumn<?, ?> columnCName;

    @FXML
    private TableColumn<?, ?> columnCPrice;

    @FXML
    private TableColumn<?, ?> columnSId;

    @FXML
    private TableColumn<?, ?> columnSName;

    @FXML
    private TableColumn<?, ?> columnSStatus;

    @FXML
    private AnchorPane coursesPane;

    @FXML
    private TableView<CourseTm> tableCourse;

    @FXML
    private TableView<CourseDetailTm> tableCourseDetail;

    @FXML
    private JFXTextField txtCourseId;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtDuration;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtSearchByCourse;

    @FXML
    private JFXTextField txtStudentId;

    private List<Course> courseList;

    private List<CourseDetails> courseDetailList;

    private ObservableList<CourseTm> cTmList;

    private ObservableList<CourseDetailTm> cdTmList;


    public void initialize() {
        initializeCourseId();
        initializeCmbStatus();

        this.courseList = getAllCourses();

        setCellValueFactory();
        loadCourseTable();
    }

    private void loadCourseTable() {
        cTmList = FXCollections.observableArrayList();

        for (Course course : courseList) {
            CourseTm courseTm = new CourseTm(
                    course.getId(),
                    course.getName(),
                    course.getDescription(),
                    course.getDuration(),
                    course.getPrice()
            );

            cTmList.add(courseTm);
        }
        tableCourse.setItems(cTmList);
        tableCourse.refresh();
    }

    private void setCellValueFactory() {
        columnCId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnCName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnCDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        columnCPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        columnSId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        columnSName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnSStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private List<CourseDetails> getAllCourseDetails(String id) {
        List<CourseDetails> list = null;
        try {
            list = CourseRepository.getAllCourseDetails(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private List<Course> getAllCourses() {
        List<Course> list = null;
        try {
            list = CourseRepository.getAllCourses();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void initializeCourseId() {
        try {
            txtCourseId.setText(CourseRepository.getNextCourseId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeCmbStatus() {
        cmbStatus.getItems().addAll("Ongoing", "Completed");
    }

    private void clearFields() {
        txtCourseId.setText("");
        txtName.setText("");
        txtDescription.setText("");
        txtDuration.setText("");
        txtPrice.setText("");

        initializeCourseId();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtCourseId.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtCourseId)){
                try {
                    if(CourseRepository.isCourseIdExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal
                                try {
                                    boolean isRemoved = CourseRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Course removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeCourseId();
                                        txtCourseId.requestFocus();
                                    }
                                } catch (SQLException e) {
                                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                                }
                            } else {
                                // User clicked Cancel (No) button or closed the dialog
                                // No action needed or you can handle accordingly
                            }
                        });
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Course not Found!").show();
                        txtCourseId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Course Id!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtCourseId.getText();
        String name = txtName.getText();
        String description = txtDescription.getText();
        String duration = txtDuration.getText();
        String price = txtPrice.getText();

        Course course = new Course(id, name, description, duration, price);

        if(id != null && !id.isEmpty() && name != null && !name.isEmpty() && duration != null && !duration.isEmpty() && price != null && !price.isEmpty()){
            if(isValid()){
                try {
                    if(CourseRepository.isCourseIdAvailable(id)){
                        try {
                            boolean isSaved = CourseRepository.save(course);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Course saved!").show();
                                refreshTableView();
                                initializeCourseId();
                                clearFields();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Course already exist!").show();
                        txtCourseId.requestFocus();
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
    void btnStatusUpdateOnAction(ActionEvent event) {
        String courseId = txtSearchByCourse.getText();
        String studentId = txtStudentId.getText();
        String status = cmbStatus.getValue();

        CourseDetails courseDetails = new CourseDetails(courseId, studentId, status);

        if(courseId != null && !courseId.isEmpty() && studentId != null && !studentId.isEmpty() && cmbStatus != null && !cmbStatus.getItems().isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId) && Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByCourse)){
                try {
                    if(CourseRepository.isCourseDetailExist(courseDetails)){
                        try {
                            boolean isUpdated = CourseRepository.updateCourseStatus(courseDetails);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Course status updated!").show();
                                this.courseDetailList = getAllCourseDetails(courseId);
                                loadCourseDetailsTable();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Record not found!").show();
                        txtSearchByCourse.requestFocus();
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
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtCourseId.getText();
        String name = txtName.getText();
        String description = txtDescription.getText();
        String duration = txtDuration.getText();
        String price = txtPrice.getText();

        Course course = new Course(id, name, description, duration, price);

        if(id != null && !id.isEmpty() && name != null && !name.isEmpty() && duration != null && !duration.isEmpty() && price != null && !price.isEmpty()){
            if(isValid()){
                try {
                    if(CourseRepository.isCourseIdExist(id)){
                        try {
                            boolean isUpdated = CourseRepository.update(course);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Course updated!").show();
                                refreshTableView();
                                initializeCourseId();
                                clearFields();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Course not found!").show();
                        txtCourseId.requestFocus();
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
    void txtSearchByCourseOnAction(ActionEvent event) {
        String id = txtSearchByCourse.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearchByCourse)){
                try {
                    if(CourseRepository.isCourseIdExist(id)){
                        this.courseDetailList = getAllCourseDetails(id);
                        loadCourseDetailsTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Course not found!").show();
                        this.courseDetailList = getAllCourseDetails(id);
                        loadCourseDetailsTable();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Course Id!").show();
        }
    }

    private void refreshTableView() {
        this.courseList = getAllCourses();

        loadCourseTable();
    }

    private void loadCourseDetailsTable() {
        cdTmList = FXCollections.observableArrayList();

        for (CourseDetails cd : courseDetailList) {
            CourseDetailTm courseDetailTm = null;
            try {
                courseDetailTm = new CourseDetailTm(
                        cd.getStudentId(),
                        StudentRepository.getStName(cd.getStudentId()),
                        cd.getStatus()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            cdTmList.add(courseDetailTm);
        }
        tableCourseDetail.setItems(cdTmList);
        tableCourseDetail.refresh();
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String id = txtSearch.getText();

        clearFields();
        txtCourseId.setText(id);

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearch)){
                try {
                    Course course = CourseRepository.searchById(id);

                    if (course != null){
                        txtCourseId.setText(course.getId());
                        txtName.setText(course.getName());
                        txtDescription.setText(course.getDescription());
                        txtDuration.setText(course.getDuration());
                        txtPrice.setText(course.getPrice());
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Course not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Course Id!").show();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtCourseId)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtName)) return false;
        if (!Regex.setTextColor(TextField.DURATION,txtDuration)) return false;
        if (!Regex.setTextColor(TextField.DOUBLE,txtPrice)) return false;
        return true;
    }


    @FXML
    void txtCourseIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtCourseId);
    }

    @FXML
    void txtDurationOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.DURATION,txtDuration);
    }

    @FXML
    void txtNameOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtName);
    }

    @FXML
    void txtPriceOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.DOUBLE,txtPrice);
    }

    @FXML
    void txtStudentIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtStudentId);
    }

    @FXML
    void txtSearchOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearch);
    }

    @FXML
    void txtSearchByCourseOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearchByCourse);
    }
}
