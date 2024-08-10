package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.lms.model.Student;
import lk.ijse.lms.model.tm.StudentTm;
import lk.ijse.lms.repository.CourseRepository;
import lk.ijse.lms.repository.StudentRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentController {

    @FXML
    private TableColumn<?, ?> columnStudentId;

    @FXML
    private TableColumn<?, ?> columnAddress;

    @FXML
    private TableColumn<?, ?> columnAdmissionDate;

    @FXML
    private TableColumn<?, ?> columnAge;

    @FXML
    private TableColumn<?, ?> columnCNo;

    @FXML
    private TableColumn<?, ?> columnDob;

    @FXML
    private TableColumn<?, ?> columnEmail;

    @FXML
    private TableColumn<?, ?> columnGender;

    @FXML
    private TableColumn<?, ?> columnName;

    @FXML
    private TableColumn<?, ?> columnNic;

    @FXML
    private DatePicker dpAdmissionDate;

    @FXML
    private DatePicker dpDob;

    @FXML
    private RadioButton rbFemale;

    @FXML
    private RadioButton rbMale;

    @FXML
    private AnchorPane studenstPane;

    @FXML
    private TableView<StudentTm> tableStudent;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtCNo;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtFName;

    @FXML
    private JFXTextField txtLName;

    @FXML
    private JFXTextField txtNic;

    @FXML
    private JFXTextField txtStudentId;

    @FXML
    private JFXTextField txtSearchbySId;

    @FXML
    private JFXTextField txtSearchbyCNo;

    private ToggleGroup genderToggleGroup;

    private List<Student> studentList;

    private ObservableList<StudentTm> tmList;


    public void initialize() {
        studentList = new ArrayList<>();

        initializeStudentId();
        initializeRadioButtons();

        this.studentList = getAllStudents();
        setCellValueFactory();
        loadStudentTable();
    }

    private void loadStudentTable() {
        tmList = FXCollections.observableArrayList();

        for (Student student : studentList) {

            LocalDate dateOfBirth = LocalDate.parse(student.getDOb());
            LocalDate currentDate = LocalDate.now();

            Period period = Period.between(dateOfBirth, currentDate);
            String age = String.valueOf(period.getYears());

            StudentTm studentTm = new StudentTm(
                    student.getId(),
                    student.getFname() + " " + student.getLname(),
                    student.getDOb(),
                    age,
                    student.getGender(),
                    student.getAdmissionDate(),
                    student.getNIC(),
                    student.getAddress(),
                    student.getCNo(),
                    student.getEmail()
            );

            tmList.add(studentTm);
        }
        tableStudent.setItems(tmList);
        tableStudent.refresh();
    }

    private void refreshTableView() {
        this.studentList = getAllStudents();
        loadStudentTable();
    }

    private void setCellValueFactory() {
        columnStudentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDob.setCellValueFactory(new PropertyValueFactory<>("dOB"));
        columnAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnAdmissionDate.setCellValueFactory(new PropertyValueFactory<>("admissionDate"));
        columnNic.setCellValueFactory(new PropertyValueFactory<>("nIC"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnCNo.setCellValueFactory(new PropertyValueFactory<>("cNo"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private List<Student> getAllStudents() {
        List<Student> list = null;
        try {
            list = StudentRepository.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void initializeStudentId() {
        try {
            txtStudentId.setText(StudentRepository.getNextStudentId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeRadioButtons() {
        // Create a ToggleGroup and assign it to the RadioButtons
        genderToggleGroup = new ToggleGroup();
        rbFemale.setToggleGroup(genderToggleGroup);
        rbMale.setToggleGroup(genderToggleGroup);

        // Select a default gender
        rbMale.setSelected(true);
    }

    private void clearFields() {
        txtStudentId.setText("");
        txtFName.setText("");
        txtLName.setText("");
        txtNic.setText("");
        txtAddress.setText("");
        txtCNo.setText("");
        txtEmail.setText("");
        dpDob.setValue(null);
        dpAdmissionDate.setValue(null);
        rbFemale.setSelected(false);
        rbMale.setSelected(true);

        initializeStudentId();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtStudentId.getText();

        if(id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtStudentId)){
                try {
                    if(StudentRepository.StudentIdIsExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal

                                try {
                                    boolean isRemoved = StudentRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Student removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeStudentId();
                                        txtStudentId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Student not Found!").show();
                        txtStudentId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Student Id!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtStudentId.getText();
        String fName = txtFName.getText();
        String lName = txtLName.getText();
        String nIC = txtNic.getText();
        String address = txtAddress.getText();
        String cNo = txtCNo.getText();
        String email = null;
        String dob = null;
        String admissionDate = null;

        LocalDate dOBRaw = dpDob.getValue();
        LocalDate admissionDateRaw = dpAdmissionDate.getValue();

        if (dOBRaw != null && admissionDateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dob = dOBRaw.format(formatter);
            // Now you can use the sqlDate string as needed
            admissionDate = admissionDateRaw.format(formatter);
        }


        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String gender = selectedRadioButton.getText();

        if(id != null && !id.isEmpty() && fName != null && !fName.isEmpty() && lName != null && !lName.isEmpty()
                && nIC != null && !nIC.isEmpty() && address != null && !address.isEmpty()
                && cNo != null && !cNo.isEmpty() && dob != null && admissionDate != null){
            if(isValid()){
                if(txtEmail != null && !txtEmail.getText().isEmpty()){
                    if(Regex.setTextColor(lk.ijse.lms.util.TextField.EMAIL,txtEmail)){
                        email = txtEmail.getText();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
                        return;
                    }
                }
                Student student = new Student(id, fName, lName, dob, gender, admissionDate, nIC, address, cNo, email);
                try {
                    if(StudentRepository.isStudentIdAvailable(id)){
                        try {
                            boolean isSaved = StudentRepository.save(student);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Student saved!").show();
                                refreshTableView();
                                clearFields();
                                initializeStudentId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Student already exist").show();
                        txtStudentId.requestFocus();
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
        String id = txtStudentId.getText();
        String fName = txtFName.getText();
        String lName = txtLName.getText();
        String nIC = txtNic.getText();
        String address = txtAddress.getText();
        String cNo = txtCNo.getText();
        String email = null;
        String dob = null;
        String admissionDate = null;

        LocalDate dOBRaw = dpDob.getValue();
        LocalDate admissionDateRaw = dpAdmissionDate.getValue();

        if (dOBRaw != null && admissionDateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dob = dOBRaw.format(formatter);
            // Now you can use the sqlDate string as needed
            admissionDate = admissionDateRaw.format(formatter);
        }


        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String gender = selectedRadioButton.getText();

        if(id != null && !id.isEmpty() && fName != null && !fName.isEmpty() && lName != null && !lName.isEmpty()
                && nIC != null && !nIC.isEmpty() && address != null && !address.isEmpty()
                && cNo != null && !cNo.isEmpty() && dob != null && admissionDate != null){
            if(isValid()){
                if(txtEmail != null && !txtEmail.getText().isEmpty()){
                    if(Regex.setTextColor(lk.ijse.lms.util.TextField.EMAIL,txtEmail)){
                        email = txtEmail.getText();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
                        return;
                    }
                }
                Student student = new Student(id, fName, lName, dob, gender, admissionDate, nIC, address, cNo, email);
                try {
                    if(StudentRepository.StudentIdIsExist(id)){
                        try {
                            boolean isUpdated = StudentRepository.update(student);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Student updated!").show();
                                refreshTableView();
                                clearFields();
                                initializeStudentId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Student not found!").show();
                        txtStudentId.requestFocus();
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
    void txtSearchbySIdOnAction(ActionEvent event) {
        String id = txtSearchbySId.getText();

        clearFields();
        txtStudentId.setText(id);

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearchbySId)){
                try {
                    Student student = StudentRepository.searchById(id);

                    if (student != null){
                        txtStudentId.setText(student.getId());
                        txtFName.setText(student.getFname());
                        txtLName.setText(student.getLname());
                        txtNic.setText(student.getNIC());
                        txtAddress.setText(student.getAddress());
                        txtCNo.setText(student.getCNo());
                        txtEmail.setText(student.getEmail());

                        LocalDate dOBRaw = LocalDate.parse(student.getDOb()); // Parse the SQL DATE string to LocalDate
                        dpDob.setValue(dOBRaw);

                        LocalDate joinDateRaw = LocalDate.parse(student.getAdmissionDate());
                        dpAdmissionDate.setValue(joinDateRaw);

                        rbFemale.setSelected(false);
                        rbMale.setSelected(true);

                        // Iterate through the radio buttons in the toggle group
                        for (Toggle toggle : genderToggleGroup.getToggles()) {
                            RadioButton radioButton = (RadioButton) toggle;
                            if (radioButton.getText().equals(student.getGender())) {
                                radioButton.setSelected(true); // Select the radio button with matching gender
                            } else {
                                radioButton.setSelected(false); // Unselect other radio buttons
                            }
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Student not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Student Id!").show();
        }
    }

    @FXML
    void txtSearchbyCNoOnAction(ActionEvent event) {
        String cNo = txtSearchbyCNo.getText();

        clearFields();

        if (cNo != null && !cNo.isEmpty()){
            if(Regex.setTextColor(TextField.PHONE,txtSearchbyCNo)){
                try {
                    Student student = StudentRepository.searchByCNo(cNo);

                    if (student != null){
                        txtStudentId.setText(student.getId());
                        txtFName.setText(student.getFname());
                        txtLName.setText(student.getLname());
                        txtNic.setText(student.getNIC());
                        txtAddress.setText(student.getAddress());
                        txtCNo.setText(student.getCNo());
                        txtEmail.setText(student.getEmail());

                        LocalDate dOBRaw = LocalDate.parse(student.getDOb()); // Parse the SQL DATE string to LocalDate
                        dpDob.setValue(dOBRaw);

                        LocalDate joinDateRaw = LocalDate.parse(student.getAdmissionDate());
                        dpAdmissionDate.setValue(joinDateRaw);

                        rbFemale.setSelected(false);
                        rbMale.setSelected(true);

                        // Iterate through the radio buttons in the toggle group
                        for (Toggle toggle : genderToggleGroup.getToggles()) {
                            RadioButton radioButton = (RadioButton) toggle;
                            if (radioButton.getText().equals(student.getGender())) {
                                radioButton.setSelected(true); // Select the radio button with matching gender
                            } else {
                                radioButton.setSelected(false); // Unselect other radio buttons
                            }
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Student not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Contact No!").show();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtStudentId)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtFName)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtLName)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NIC,txtNic)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ADDRESS,txtAddress)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.PHONE,txtCNo)) return false;
        return true;
    }

    @FXML
    void txtAddressOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ADDRESS,txtAddress);
    }

    @FXML
    void txtCNoOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.PHONE,txtCNo);
    }

    @FXML
    void txtEmailOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.EMAIL,txtEmail);
    }

    @FXML
    void txtFNameOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtFName);
    }

    @FXML
    void txtLNameOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtLName);
    }

    @FXML
    void txtNicOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.NIC,txtNic);
    }

    @FXML
    void txtStudentIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtStudentId);
    }

    @FXML
    void txtSearchbyCNoOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.PHONE,txtSearchbyCNo);
    }

    @FXML
    void txtSearchbySIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearchbySId);
    }

}
