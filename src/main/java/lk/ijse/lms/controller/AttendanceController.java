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
import lk.ijse.lms.model.Attendance;
import lk.ijse.lms.model.tm.AttendanceTm;
import lk.ijse.lms.repository.AttendanceRepository;
import lk.ijse.lms.repository.EmployeeRepository;
import lk.ijse.lms.repository.StudentRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class AttendanceController {
    @FXML
    private JFXComboBox<String> cmbRole;

    @FXML
    private TableColumn<?, ?> columnEDate;

    @FXML
    private TableColumn<?, ?> columnEInTime;

    @FXML
    private TableColumn<?, ?> columnEName;

    @FXML
    private TableColumn<?, ?> columnEOutTime;

    @FXML
    private TableColumn<?, ?> columnEStatus;

    @FXML
    private TableColumn<?, ?> columnEmployeeId;

    @FXML
    private TableColumn<?, ?> columnSDate;

    @FXML
    private TableColumn<?, ?> columnSInTime;

    @FXML
    private TableColumn<?, ?> columnSName;

    @FXML
    private TableColumn<?, ?> columnSOutTime;

    @FXML
    private TableColumn<?, ?> columnSStatus;

    @FXML
    private TableColumn<?, ?> columnStudentId;

    @FXML
    private DatePicker dpDate;

    @FXML
    private RadioButton rbAbsent;

    @FXML
    private RadioButton rbPresent;

    @FXML
    private TableView<AttendanceTm> tableEmplyee;

    @FXML
    private TableView<AttendanceTm> tableStudent;

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtInTime;

    @FXML
    private JFXTextField txtOutTime;

    @FXML
    private DatePicker dpDateSearch;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXComboBox<String> cmbRoleSearch;

    private ToggleGroup genderToggleGroup;

    private List<Attendance> sAttendanceList;

    private List<Attendance> eAttendanceList;

    private ObservableList<AttendanceTm> emTmList;

    private ObservableList<AttendanceTm> stTmList;


    public void initialize() {
        initializeCmbRole();
        initializeRadioButtons();

        this.eAttendanceList = getAllEmployeeAttendance();
        this.sAttendanceList = getAllStudentAttendance();

        setCellValueFactory();
        loadStudentAttendanceTable();
        loadEmployeeAttendanceTable();
    }

    private void loadEmployeeAttendanceTable() {
        emTmList = FXCollections.observableArrayList();

        for (Attendance attendance : eAttendanceList) {
            AttendanceTm attendanceTm = null;
            try {
                attendanceTm = new AttendanceTm(
                        attendance.getId(),
                        EmployeeRepository.getEmName(attendance.getId()),
                        attendance.getDate(),
                        formatTimeFromSQL(attendance.getInTime()),
                        attendance.getOutTime() != null ? formatTimeFromSQL(attendance.getOutTime()) : null,
                        attendance.getStatus()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            emTmList.add(attendanceTm);
        }
        tableEmplyee.setItems(emTmList);
        tableEmplyee.refresh();
    }

    private void loadStudentAttendanceTable() {
        stTmList = FXCollections.observableArrayList();

        for (Attendance attendance : sAttendanceList) {
            AttendanceTm attendanceTm = null;
            try {
                attendanceTm = new AttendanceTm(
                       attendance.getId(),
                        StudentRepository.getStName(attendance.getId()),
                        attendance.getDate(),
                        formatTimeFromSQL(attendance.getInTime()),
                        attendance.getOutTime() != null ? formatTimeFromSQL(attendance.getOutTime()) : null,
                        attendance.getStatus()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            stTmList.add(attendanceTm);
        }
        tableStudent.setItems(stTmList);
        tableStudent.refresh();
    }

    private List<Attendance> getAllStudentAttendance() {
        List<Attendance> list = null;
        try {
            list = AttendanceRepository.getAllStAttendance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    private List<Attendance> getAllEmployeeAttendance() {
        List<Attendance> list = null;
        try {
            list = AttendanceRepository.getAllEmAttendance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;

    }

    private void refreshTableView() {
        this.eAttendanceList = getAllEmployeeAttendance();
        this.sAttendanceList = getAllStudentAttendance();

        loadStudentAttendanceTable();
        loadEmployeeAttendanceTable();
    }

    private void setCellValueFactory() {
        columnStudentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnSDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnSName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnSInTime.setCellValueFactory(new PropertyValueFactory<>("inTime"));
        columnSOutTime.setCellValueFactory(new PropertyValueFactory<>("outTime"));
        columnSStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        columnEmployeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnEDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnEName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnEInTime.setCellValueFactory(new PropertyValueFactory<>("inTime"));
        columnEOutTime.setCellValueFactory(new PropertyValueFactory<>("outTime"));
        columnEStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void initializeRadioButtons() {
        // Create a ToggleGroup and assign it to the RadioButtons
        genderToggleGroup = new ToggleGroup();
        rbPresent.setToggleGroup(genderToggleGroup);
        rbAbsent.setToggleGroup(genderToggleGroup);

        // Select a default gender
        rbPresent.setSelected(true);
    }

    private void initializeCmbRole() {
            cmbRole.getItems().addAll("Employee", "Student");
            cmbRoleSearch.getItems().addAll("Employee", "Student");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtId.setText("");
        txtInTime.setText("");
        txtOutTime.setText("");
        cmbRole.setValue("");
        dpDate.setValue(null);
        rbPresent.setSelected(true);
        rbAbsent.setSelected(false);
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String role = cmbRole.getValue();
        String date;

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null){
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        } else {
            date = null;
        }

        if(id != null && !id.isEmpty() && role != null && !role.isEmpty() && date != null && !date.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtId)){
                try {
                    if(AttendanceRepository.isAttendanceExist(id, date, role)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal

                                try {
                                    boolean isRemoved = AttendanceRepository.remove(id, date, role);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Attendance removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        txtId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Attendance not Found!").show();
                        txtId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter id, role and date!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String inTime = txtInTime.getText();
        String outTime = null;
        String role = cmbRole.getValue();
        String date = null;

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null){
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String status = selectedRadioButton.getText();

        if(id != null && !id.isEmpty() && inTime != null && !inTime.isEmpty() && cmbRole != null && !cmbRole.getItems().isEmpty() && date != null && !date.isEmpty()){
            if(isValid()){
                inTime = formatTimeForSQL(txtInTime.getText());
                if(txtOutTime.getText() != null && !txtOutTime.getText().isEmpty()){
                    if(Regex.setTextColor(TextField.TIME,txtOutTime)){
                        outTime = formatTimeForSQL(txtOutTime.getText());
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
                        return;
                    }
                }
                Attendance attendance = new Attendance(role, date, inTime, outTime, status, id);
                try {
                    if(AttendanceRepository.isEmployeeOrStudentExist(attendance)){
                        if(AttendanceRepository.isDateAvailable(attendance)){
                            try {
                                boolean isSaved = AttendanceRepository.save(attendance);
                                if(isSaved){
                                    new Alert(Alert.AlertType.CONFIRMATION, "Attendance saved!").show();
                                    refreshTableView();
                                    clearFields();
                                }
                            } catch (SQLException e) {
                                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                            }
                        }else {
                            new Alert(Alert.AlertType.ERROR, "Attendance already exist on this date!").show();
                            txtId.requestFocus();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Employee or Student not exist!").show();
                        txtId.requestFocus();
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
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtId.getText();
        String inTime = txtInTime.getText();
        String outTime = null;
        String role = cmbRole.getValue();
        String date = null;

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null){
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String status = selectedRadioButton.getText();

        if(id != null && !id.isEmpty() && inTime != null && !inTime.isEmpty() && cmbRole != null && !cmbRole.getItems().isEmpty() && date != null && !date.isEmpty()){
            if(isValid()){
                inTime = formatTimeForSQL(txtInTime.getText());
                if(txtOutTime.getText() != null && !txtOutTime.getText().isEmpty()){
                    if(Regex.setTextColor(TextField.TIME,txtOutTime)){
                        outTime = formatTimeForSQL(txtOutTime.getText());
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
                        return;
                    }
                }
                Attendance attendance = new Attendance(role, date, inTime, outTime, status, id);
                try {
                    if(AttendanceRepository.isEmployeeOrStudentExist(attendance)){
                        try {
                            boolean isUpdated = AttendanceRepository.update(attendance);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Attendance updated!").show();
                                refreshTableView();
                                clearFields();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Employee or Student not found!").show();
                        txtId.requestFocus();
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
    void txtSearchOnAction(ActionEvent event) {
        String id = txtSearch.getText();
        String role = cmbRoleSearch.getValue();
        String date;

        LocalDate dateRaw = dpDateSearch.getValue();

        if (dateRaw != null){
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        } else {
            date = null;
        }

        clearFields();

        if(id != null && !id.isEmpty() && role != null && !role.isEmpty() && date != null && !date.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearch)){
                try {
                    Attendance attendance = AttendanceRepository.searchById(id, date, role);

                    if (attendance != null){
                        txtId.setText(attendance.getId());
                        txtInTime.setText(formatTimeFromSQL(attendance.getInTime()));
                        if(attendance.getOutTime() != null && !attendance.getOutTime().isEmpty()){
                            txtOutTime.setText(formatTimeFromSQL(attendance.getOutTime()));
                        }
                        cmbRole.setValue(attendance.getRole());

                        LocalDate attendanceDateRaw = LocalDate.parse(attendance.getDate()); // Parse the SQL DATE string to LocalDate
                        dpDate.setValue(attendanceDateRaw);

                        rbAbsent.setSelected(false);
                        rbPresent.setSelected(true);

                        // Iterate through the radio buttons in the toggle group
                        for (Toggle toggle : genderToggleGroup.getToggles()) {
                            RadioButton radioButton = (RadioButton) toggle;
                            if (radioButton.getText().equals(attendance.getStatus())) {
                                radioButton.setSelected(true); // Select the radio button with matching status
                            } else {
                                radioButton.setSelected(false); // Unselect other radio buttons
                            }
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Attendance not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter id, role and date!").show();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtId)) return false;
        if (!Regex.setTextColor(TextField.TIME,txtInTime)) return false;
        return true;
    }

    @FXML
    void txtIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtId);
    }

    @FXML
    void txtInTimeOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.TIME,txtInTime);
    }

    @FXML
    void txtOutTimeOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.TIME,txtOutTime);
    }

    @FXML
    void txtSearchOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearch);
    }
}
