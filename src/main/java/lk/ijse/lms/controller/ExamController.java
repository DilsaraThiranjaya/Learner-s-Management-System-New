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
import lk.ijse.lms.model.tm.CourseDetailTm;
import lk.ijse.lms.model.tm.ExamDetailTm;
import lk.ijse.lms.model.tm.ExamTm;
import lk.ijse.lms.others.EmailSender;
import lk.ijse.lms.repository.*;
import lk.ijse.lms.util.Regex;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExamController {

    @FXML
    private JFXComboBox<String> cmbResult;

    @FXML
    private JFXComboBox<String> cmbType;

    @FXML
    private TableColumn<?, ?> columnDate;

    @FXML
    private TableColumn<?, ?> columnDescription;

    @FXML
    private TableColumn<?, ?> columnExamId;

    @FXML
    private TableColumn<?, ?> columnSId;

    @FXML
    private TableColumn<?, ?> columnSName;

    @FXML
    private TableColumn<?, ?> columnSResult;

    @FXML
    private TableColumn<?, ?> columnType;

    @FXML
    private DatePicker dpDate;

    @FXML
    private AnchorPane examsPane;

    @FXML
    private TableView<ExamTm> tableExam;

    @FXML
    private TableView<ExamDetailTm> tableStudent;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtExamId;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtSearchByExamId;

    @FXML
    private JFXTextField txtStudentId;

    private List<ExamDetail> examDetaiList;

    private ObservableList<ExamDetailTm> edtmList;

    private List<Exam> examList;

    private ObservableList<ExamTm> etmList;



    public void initialize() {
        initializeExamId();
        initializeCmb();

        this.examList = getAllExams();
        setCellValueFactory();
        loadExamTable();
    }

    private void loadExamTable() {
        etmList = FXCollections.observableArrayList();

        for (Exam exam : examList) {
            ExamTm examTm = new ExamTm(
                    exam.getId(),
                    exam.getDate(),
                    exam.getType(),
                    exam.getDescription()
            );

            etmList.add(examTm);
        }
        tableExam.setItems(etmList);
        tableExam.refresh();
    }

    private void setCellValueFactory() {
        columnExamId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        columnSId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        columnSName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnSResult.setCellValueFactory(new PropertyValueFactory<>("result"));
    }

    private List<Exam> getAllExams() {
        List<Exam> list = null;
        try {
            list = ExamRepository.getAllExams();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void initializeCmb() {
        cmbResult.getItems().addAll("Pass", "Fail");
        cmbType.getItems().addAll("Theory", "Practical");
    }

    private void initializeExamId() {
        try {
            txtExamId.setText(ExamRepository.getNextExamId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshTableView() {
        this.examList = getAllExams();
        loadExamTable();
    }

    private void clearFields() {
        txtExamId.setText("");
        txtDescription.setText("");
        cmbType.setValue(null);
        dpDate.setValue(null);

        initializeExamId();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtExamId.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtExamId)){
                try {
                    if(ExamRepository.isExamIdExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal
                                try {
                                    boolean isRemoved = ExamRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Exam removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeExamId();
                                        txtExamId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Exam not Found!").show();
                        txtExamId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Exam Id!").show();
        }
    }

    @FXML
    void btnRemoveStOnAction(ActionEvent event) {
        String examId = txtSearchByExamId.getText();
        String studentId = txtStudentId.getText();

        if (examId != null && !examId.isEmpty() && studentId != null && !studentId.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId) && Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByExamId)){
                try {
                    if(ExamRepository.isExamDetailExist(examId, studentId)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal
                                try {
                                    boolean isRemoved = ExamRepository.removeExamDetail(examId, studentId);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Student Record removed!").show();
                                        this.examDetaiList = getAllExamDetails(examId);
                                        loadExamDetailsTable();
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
                        new Alert(Alert.AlertType.ERROR, "Student Record not Found!").show();
                        txtExamId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter Exam ID and Student ID!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtExamId.getText();
        String type = cmbType.getValue();
        String description = txtDescription.getText();
        String date = null;

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        Exam exam = new Exam(id, date, type, description);

        if(id != null && !id.trim().isEmpty() && type != null && !type.trim().isEmpty()
                && date != null && !date.trim().isEmpty() && description != null && !description.trim().isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtExamId)){
                try {
                    if(ExamRepository.isExamIdAvailable(id)){
                        try {
                            boolean isSaved = ExamRepository.save(exam);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Exam saved!").show();
                                refreshTableView();
                                clearFields();
                                initializeExamId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Exam already exist!").show();
                        txtExamId.requestFocus();
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
    void btnSaveStOnAction(ActionEvent event) {
        String examId = txtSearchByExamId.getText();
        String result = cmbResult.getValue();
        String studentId = txtStudentId.getText();

        ExamDetail ed = new ExamDetail(examId, studentId, result);

        if(examId != null && !examId.trim().isEmpty() && studentId != null && !studentId.trim().isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId) && Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByExamId)){
                try {
                    if(ExamRepository.isExamIdExist(examId)){
                        if(StudentRepository.StudentIdIsExist(studentId)){
                            if(!ExamRepository.isExamDetailAlreadyExist(ed)){
                                try {
                                    boolean isSaved = ExamRepository.saveExamDetail(ed);
                                    if(isSaved){
                                        new Alert(Alert.AlertType.CONFIRMATION, "Student Record saved!").show();
                                        this.examDetaiList = getAllExamDetails(examId);
                                        loadExamDetailsTable();
                                    }
                                } catch (SQLException e) {
                                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                                }
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Student Record already exist!").show();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Student not found!").show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Exam already exist!").show();
                        txtExamId.requestFocus();
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
        String id = txtExamId.getText();
        String type = cmbType.getValue();
        String description = txtDescription.getText();
        String date = null;

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        Exam exam = new Exam(id, date, type, description);

        if(id != null && !id.trim().isEmpty() && type != null && !type.trim().isEmpty()
                && date != null && !date.trim().isEmpty() && description != null && !description.trim().isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtExamId)){
                try {
                    if(ExamRepository.isExamIdExist(id)){
                        try {
                            boolean isUpdated = ExamRepository.update(exam);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Exam updated!").show();
                                refreshTableView();
                                clearFields();
                                initializeExamId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Exam not found!").show();
                        txtExamId.requestFocus();
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
    void btnUpdateStOnAction(ActionEvent event) {
        String examId = txtSearchByExamId.getText();
        String result = cmbResult.getValue();
        String studentId = txtStudentId.getText();

        ExamDetail ed = new ExamDetail(examId, studentId, result);

        if(examId != null && !examId.trim().isEmpty() && result != null && !result.trim().isEmpty()
                && studentId != null && !studentId.trim().isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId) && Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByExamId)){
                try {
                    if(ExamRepository.isExamIdExist(examId)){
                        if(ExamRepository.isExamDetailAlreadyExist(ed)){
                            try {
                                boolean isUpdated = ExamRepository.updateExamDetail(ed);
                                if(isUpdated){
                                    new Alert(Alert.AlertType.CONFIRMATION, "Student Record updated!").show();
                                    this.examDetaiList = getAllExamDetails(examId);
                                    loadExamDetailsTable();
                                }
                            } catch (SQLException e) {
                                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Student Record not found!").show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Exam not found!").show();
                        txtExamId.requestFocus();
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
    void txtSearchByExamIdOnAction(ActionEvent event) {
        String id = txtSearchByExamId.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByExamId)){
                try {
                    if(ExamRepository.isExamIdExist(id)){
                        this.examDetaiList = getAllExamDetails(id);
                        loadExamDetailsTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Exam not found!").show();
                        this.examDetaiList = getAllExamDetails(id);
                        loadExamDetailsTable();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Exam Id!").show();
        }
    }

    private void loadExamDetailsTable() {
        edtmList = FXCollections.observableArrayList();

        for (ExamDetail ed : examDetaiList) {
            ExamDetailTm examDetailTm = null;
            try {
                examDetailTm = new ExamDetailTm(
                        ed.getStudentId(),
                        StudentRepository.getStName(ed.getStudentId()),
                        ed.getResult());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            edtmList.add(examDetailTm);
        }
        tableStudent.setItems(edtmList);
        tableStudent.refresh();
    }

    private List<ExamDetail> getAllExamDetails(String id) {
        List<ExamDetail> list = null;
        try {
            list = ExamRepository.getAllExamDetails(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String id = txtSearch.getText();

        clearFields();
        txtExamId.setText(id);

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearch)){
                try {
                    Exam exam = ExamRepository.searchById(id);

                    if (exam != null){
                        txtExamId.setText(exam.getId());
                        txtDescription.setText(exam.getDescription());
                        cmbType.setValue(exam.getType());

                        LocalDate dateRaw = LocalDate.parse(exam.getDate()); // Parse the SQL DATE string to LocalDate
                        dpDate.setValue(dateRaw);
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Exam not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Exam Id!").show();
        }
    }

    @FXML
    void btnEmailOnAction(ActionEvent event) {
        String examId = txtSearchByExamId.getText();

        if(examId != null && !examId.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByExamId)){
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                confirmationAlert.setHeaderText(null); // Remove header text

                // Show the confirmation dialog and wait for user input
                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // User clicked OK (Yes) button, perform send email
                        Exam exam = null;
                        List<ExamDetail> edList = null;
                        try {
                            exam = ExamRepository.searchById(examId);
                            edList = ExamRepository.getAllExamDetails(examId);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        if(edList != null && !edList.isEmpty()){
                            List<Student> studentList = new ArrayList<>();

                            for(ExamDetail ed : edList){
                                try {
                                    studentList.add(StudentRepository.searchById(ed.getStudentId()));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            String emailTitle = "Exam Schedule";

                            boolean isSent = false;

                            for (Student student : studentList) {
                                String studentEmail = student.getEmail();

                                if(student.getEmail() != null && !student.getEmail().isEmpty()){
                                    String emailContent = "Dear " + student.getFname() + ",\n" +
                                            "\n" +
                                            "We are pleased to inform you about your upcoming exam schedule. Here are the details:\n" +
                                            "\n" +
                                            "Date: " + exam.getDate() + "\n" +
                                            "Type: " + exam.getType() + "\n" +
                                            "Description: " + exam.getDescription() + "\n" +
                                            "\n" +
                                            "Please make sure to be on time for your exams. If you have any questions, feel free to contact us.\n" +
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

    @FXML
    void txtExamIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtExamId);
    }

    @FXML
    void txtStudentIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId);
    }

    @FXML
    void txtSearchOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearch);
    }

    @FXML
    void txtSearchByExamIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtSearchByExamId);
    }
}
