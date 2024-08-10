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
import lk.ijse.lms.model.*;
import lk.ijse.lms.model.tm.LessonScheduleDetailTm;
import lk.ijse.lms.model.tm.LessonScheduleTm;
import lk.ijse.lms.others.EmailSender;
import lk.ijse.lms.repository.*;
import lk.ijse.lms.util.Regex;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LessonScheduleController {

    @FXML
    private JFXComboBox<String> cmbInstructors;

    @FXML
    private JFXComboBox<String> cmbCourses;

    @FXML
    private JFXComboBox<String> cmbCourseSearch;

    @FXML
    private TableColumn<?, ?> columnCourse;

    @FXML
    private TableColumn<?, ?> columnDate;

    @FXML
    private TableColumn<?, ?> columnIId;

    @FXML
    private TableColumn<?, ?> columnIName;

    @FXML
    private TableColumn<?, ?> columnLId;

    @FXML
    private TableColumn<?, ?> columnSId;

    @FXML
    private TableColumn<?, ?> columnSName;

    @FXML
    private TableColumn<?, ?> columnTime;

    @FXML
    private DatePicker dpDate;

    @FXML
    private DatePicker dpDateSearch;

    @FXML
    private JFXTextField txtLessonId;

    @FXML
    private JFXTextField txtTimeSearch;

    @FXML
    private AnchorPane schedulesPane;

    @FXML
    private TableView<LessonScheduleTm> tableLesson;

    @FXML
    private TableView<LessonScheduleDetailTm> tableLessonDetails;

    @FXML
    private JFXTextField txtSearchByLId;

    @FXML
    private JFXTextField txtStudentId;

    @FXML
    private JFXTextField txtTime;

    private List<LessonSchedule> lessonList;

    private List<LessonScheduleDetails> lessonDetailList;

    private ObservableList<LessonScheduleTm> lTmList;

    private ObservableList<LessonScheduleDetailTm> ldTmList;


    public void initialize() {
        initializeLId();
        initializeCmb();

        this.lessonList = getAllLessons();
        setCellValueFactory();
        loadLessonTable();
    }

    private void loadLessonTable() {
        lTmList = FXCollections.observableArrayList();

        for (LessonSchedule ls : lessonList) {
            LessonScheduleTm lessonScheduleTm = null;
            try {
                lessonScheduleTm = new LessonScheduleTm(
                        ls.getLessonId(),
                        ls.getEmployeeId(),
                        EmployeeRepository.getEmName(ls.getEmployeeId()),
                        CourseRepository.getCourseName(ls.getCourseId()),
                        ls.getDate(),
                        formatTimeFromSQL(ls.getTime())
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            lTmList.add(lessonScheduleTm);
        }
        tableLesson.setItems(lTmList);
        tableLesson.refresh();
    }

    private void setCellValueFactory() {
        columnLId.setCellValueFactory(new PropertyValueFactory<>("lId"));
        columnIId.setCellValueFactory(new PropertyValueFactory<>("eId"));
        columnIName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        columnCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        columnSId.setCellValueFactory(new PropertyValueFactory<>("sId"));
        columnSName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private List<LessonSchedule> getAllLessons() {
        List<LessonSchedule> list = null;
        try {
            list = LessonRepository.getAllLessons();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void initializeCmb() {
        try {
            cmbCourses.setItems(CourseRepository.getCourses());
            cmbCourseSearch.setItems(CourseRepository.getCourses());
            cmbInstructors.setItems(EmployeeRepository.getAllInstructors());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeLId() {
        try {
            txtLessonId.setText(LessonRepository.getNextLId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshTableView() {
        this.lessonList = getAllLessons();
        loadLessonTable();
    }

    private void clearFields() {
        txtLessonId.setText("");
        txtTime.setText("");
        cmbInstructors.setValue(null);
        cmbCourses.setValue(null);
        dpDate.setValue(null);

        initializeLId();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtLessonId.getText();

        if(id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtLessonId)){
                try {
                    if(LessonRepository.isLIdExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal
                                try {
                                    boolean isRemoved = LessonRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Schedule removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeLId();
                                        txtLessonId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Schedule not Found!").show();
                        txtLessonId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Lesson ID!").show();
        }
    }

    @FXML
    void btnRemoveSidOnAction(ActionEvent event) {
        String lessonId = txtSearchByLId.getText();
        String studentId = txtStudentId.getText();

        LessonScheduleDetails ld = new LessonScheduleDetails(lessonId, studentId);

        if(lessonId != null && !lessonId.isEmpty() && studentId != null && !studentId.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByLId) && Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId)){
                try {
                    if(LessonRepository.isLIdExist(lessonId)){
                        if(LessonRepository.isLessonDetailExist(lessonId, studentId)){
                            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                            confirmationAlert.setHeaderText(null); // Remove header text

                            // Show the confirmation dialog and wait for user input
                            confirmationAlert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    // User clicked OK (Yes) button, perform employee removal
                                    try {
                                        boolean isRemoved = LessonRepository.removeLessonDetail(ld);
                                        if (isRemoved) {
                                            new Alert(Alert.AlertType.INFORMATION, "Student Record removed!").show();
                                            this.lessonDetailList = getAllLessonDetails(lessonId);
                                            loadLessonDetailsTable();

                                        }
                                    } catch (SQLException e) {
                                        new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                                    }
                                } else {
                                    // User clicked Cancel (No) button or closed the dialog
                                    // No action needed or you can handle accordingly
                                }
                            });
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Student Record not Found!").show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Schedule not Found!").show();

                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter Lesson ID and Student ID!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtLessonId.getText();
        String time = txtTime.getText();
        String employeeId = null;
        String courseId = null;
        String date = null;

        if (cmbInstructors.getValue() != null) {
            employeeId = cmbInstructors.getValue();
        }

        if (cmbCourses.getValue() != null) {
            try {
                courseId = CourseRepository.getCourseId(cmbCourses.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        if(id != null && !id.trim().isEmpty() && time != null && !time.trim().isEmpty()
                && date != null && !date.trim().isEmpty() && employeeId != null && !employeeId.trim().isEmpty()
                && courseId != null && !courseId.trim().isEmpty()){
            if(isValid()){
                time = formatTimeForSQL(txtTime.getText());

                LessonSchedule ls = new LessonSchedule(id, date, time, employeeId, courseId);

                try {
                    if(LessonRepository.isLIdAvailable(id)){
                        if(LessonRepository.isScheduleAvailable(ls)){
                            try {
                                boolean isSaved = LessonRepository.save(ls);
                                if(isSaved){
                                    new Alert(Alert.AlertType.CONFIRMATION, "Schedule saved!").show();
                                    refreshTableView();
                                    clearFields();
                                    initializeLId();
                                }
                            } catch (SQLException e) {
                                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Schedule already exist!").show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Schedule already exist!").show();
                        txtLessonId.requestFocus();
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
    void btnSaveSidOnAction(ActionEvent event) {
        String lessonId = txtSearchByLId.getText();
        String studentId = txtStudentId.getText();

        LessonScheduleDetails ld = new LessonScheduleDetails(lessonId, studentId);

        if(lessonId != null && !lessonId.isEmpty() && studentId != null && !studentId.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByLId) && Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId)){
                try {
                    if(LessonRepository.isLIdExist(lessonId)){
                        if(StudentRepository.StudentIdIsExist(studentId)){
                            if (LessonRepository.isSidAvailable(lessonId, studentId)){
                                try {
                                    boolean isSaved = LessonRepository.saveLessonDetails(ld);
                                    if(isSaved){
                                        new Alert(Alert.AlertType.CONFIRMATION, "Schedule detail saved!").show();
                                        this.lessonDetailList = getAllLessonDetails(lessonId);
                                        loadLessonDetailsTable();
                                    }
                                } catch (SQLException e) {
                                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                                }
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Student already exist!").show();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Student not found!").show();
                            txtStudentId.requestFocus();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Lesson not found!").show();
                        txtSearchByLId.requestFocus();
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
        String id = txtLessonId.getText();
        String time = txtTime.getText();
        String employeeId = null;
        String courseId = null;
        String date = null;

        if (cmbInstructors.getValue() != null) {
            employeeId = cmbInstructors.getValue();
        }

        if (cmbCourses.getValue() != null) {
            try {
                courseId = CourseRepository.getCourseId(cmbCourses.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        if(id != null && !id.trim().isEmpty() && time != null && !time.trim().isEmpty()
                && date != null && !date.trim().isEmpty() && employeeId != null && !employeeId.trim().isEmpty()
                && courseId != null && !courseId.trim().isEmpty()){
            if(isValid()){
                time = formatTimeForSQL(txtTime.getText());

                LessonSchedule ls = new LessonSchedule(id, date, time, employeeId, courseId);

                try {
                    if(LessonRepository.isLIdExist(id)){
                        try {
                            boolean isUpdated = LessonRepository.update(ls);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Schedule updated!").show();
                                refreshTableView();
                                clearFields();
                                initializeLId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Schedule not found!").show();
                        txtLessonId.requestFocus();
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
    void txtSearchByLIdOnAction(ActionEvent event) {
        String lessonId = txtSearchByLId.getText();

        if(lessonId != null && !lessonId.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByLId)){
                try {
                    if(LessonRepository.isLIdExist(lessonId)){
                        this.lessonDetailList = getAllLessonDetails(lessonId);
                        loadLessonDetailsTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Schedule not found!").show();
                        this.lessonDetailList = getAllLessonDetails(lessonId);
                        loadLessonDetailsTable();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter Lesson ID!").show();
        }
    }

    private void loadLessonDetailsTable() {
        ldTmList = FXCollections.observableArrayList();

        for (LessonScheduleDetails ld : lessonDetailList) {
            LessonScheduleDetailTm lsdTm = null;
            try {
                lsdTm = new LessonScheduleDetailTm(
                        ld.getStudentId(),
                        StudentRepository.getStName(ld.getStudentId())
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            ldTmList.add(lsdTm);
        }
        tableLessonDetails.setItems(ldTmList);
        tableLessonDetails.refresh();
    }

    private List<LessonScheduleDetails> getAllLessonDetails(String lessonId) {
        List<LessonScheduleDetails> list = null;
        try {
            list = LessonRepository.getAllLessonDetails(lessonId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String courseId = null;
        try {
            courseId = CourseRepository.getCourseId(cmbCourseSearch.getValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String time = txtTimeSearch.getText();
        String date = null;

        LocalDate dateRaw = dpDateSearch.getValue();

        if (dateRaw != null){
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        } else {
            date = null;
        }

        if (courseId != null && !courseId.isEmpty() && date != null && !date.isEmpty() && time != null && !time.trim().isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.TIME,txtTimeSearch)){
                time = formatTimeForSQL(txtTimeSearch.getText());

                try {
                    LessonSchedule ls = LessonRepository.searchById(courseId, date, time);

                    if (ls != null){
                        txtLessonId.setText(ls.getLessonId());
                        txtTime.setText(formatTimeFromSQL(ls.getTime()));
                        cmbInstructors.setValue(ls.getEmployeeId());
                        cmbCourses.setValue(CourseRepository.getCourseName(ls.getCourseId()));

                        LocalDate dateSRaw = LocalDate.parse(ls.getDate()); // Parse the SQL DATE string to LocalDate
                        dpDate.setValue(dateSRaw);
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Schedule not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter course, date and time!").show();
        }
    }

    public static String formatTimeForSQL(String inputTime) {
        try {
            // Parse input time string using SimpleDateFormat
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
            Date date = inputFormat.parse(inputTime);

            // Format date object into SQL Time format
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = outputFormat.format(date);

            return formattedTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatTimeFromSQL(String sqlTime) {
        try {
            // Parse SQL Time string using SimpleDateFormat
            SimpleDateFormat sqlFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = sqlFormat.parse(sqlTime);

            // Format date object into 12-hour format with AM/PM
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            String formattedTime = outputFormat.format(date);

            return formattedTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    void btnEmailOnAction(ActionEvent event) {
        String lessonId = txtSearchByLId.getText();

        if(lessonId != null && !lessonId.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByLId)){
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                confirmationAlert.setHeaderText(null); // Remove header text

                // Show the confirmation dialog and wait for user input
                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // User clicked OK (Yes) button, perform send email
                        LessonSchedule ls = null;
                        List<LessonScheduleDetails> lsdList = null;
                        try {
                            ls = LessonRepository.searchById(lessonId);
                            lsdList = LessonRepository.getAllLessonDetails(lessonId);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        if(lsdList != null && !lsdList.isEmpty()){
                            List<Student> studentList = new ArrayList<>();

                            for(LessonScheduleDetails ld : lsdList){
                                try {
                                    studentList.add(StudentRepository.searchById(ld.getStudentId()));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            String instructorName = null;
                            String courseName = null;
                            try {
                                instructorName = EmployeeRepository.getEmName(ls.getEmployeeId());
                                courseName = CourseRepository.getCourseName(ls.getCourseId());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }



                            String emailTitle = "Lesson Schedule";

                            boolean isSent = false;

                            for (Student student : studentList) {
                                String studentEmail = student.getEmail();

                                if(student.getEmail() != null && !student.getEmail().isEmpty()){
                                    String emailContent = "Dear " + student.getFname() + ",\n" +
                                            "\n" +
                                            "We are pleased to inform you about your upcoming driving lessons. Here are the details:\n" +
                                            "\n" +
                                            "Date: " + ls.getDate() + "\n" +
                                            "Time: " + formatTimeFromSQL(ls.getTime()) + "\n" +
                                            "Instructor: "+ instructorName + "\n" +
                                            "Course: " + courseName + "\n" +
                                            "\n" +
                                            "Please make sure to be on time for your lessons. If you have any questions, feel free to contact us.\n" +
                                            "(0766677409 / 0742634670)\n" +
                                            "\n" +
                                            "Best regards,\n" +
                                            "Sahan Learner's\n" +
                                            "\n";

                                    EmailSender emailSender = new EmailSender();
                                    isSent = emailSender.sendEmail(studentEmail, emailTitle, emailContent);
                                }
                            }
                            if(isSent){
                                new Alert(Alert.AlertType.CONFIRMATION, "Emails sent successfully!").show();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Emails not sent!").show();
                            }
                        } else {
                            new Alert(Alert.AlertType.INFORMATION, "Add Students first!").show();
                        }
                    } else {
                        // User clicked Cancel (No) button or closed the dialog
                        // No action needed or you can handle accordingly
                    }
                });
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Enter Lesson ID!").show();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtLessonId)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.TIME,txtTime)) return false;
        return true;
    }

    @FXML
    void txtLessonIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtLessonId);
    }

    @FXML
    void txtTimeOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.TIME,txtTime);
    }

    @FXML
    void txtTimeSearchOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.TIME,txtTimeSearch);
    }

    @FXML
    void txtSearchByLIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByLId);
    }

    @FXML
    void txtStudentIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId);
    }
}
