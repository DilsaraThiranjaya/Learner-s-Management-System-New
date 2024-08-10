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
import lk.ijse.lms.model.Employee;
import lk.ijse.lms.model.tm.EmployeeTm;
import lk.ijse.lms.repository.EmployeeRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController {

    @FXML
    private JFXComboBox<String> cmbRole;

    @FXML
    private TableColumn<?, ?> columnAddress;

    @FXML
    private TableColumn<?, ?> columnAge;

    @FXML
    private TableColumn<?, ?> columnCNo;

    @FXML
    private TableColumn<?, ?> columnDOB;

    @FXML
    private TableColumn<?, ?> columnEmail;

    @FXML
    private TableColumn<?, ?> columnEmployeeId;

    @FXML
    private TableColumn<?, ?> columnGender;

    @FXML
    private TableColumn<?, ?> columnJoinDate;

    @FXML
    private TableColumn<?, ?> columnNIC;

    @FXML
    private TableColumn<?, ?> columnName;

    @FXML
    private TableColumn<?, ?> columnRole;

    @FXML
    private DatePicker dpDoB;

    @FXML
    private DatePicker dpJoinDate;

    @FXML
    private RadioButton rbFemale;

    @FXML
    private RadioButton rbMale;

    @FXML
    private TableView<EmployeeTm> tableEmployee;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtCNo;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtEmployeeId;

    @FXML
    private JFXTextField txtFName;

    @FXML
    private JFXTextField txtLName;

    @FXML
    private JFXTextField txtNIC;

    @FXML
    private JFXTextField txtSearchByEId;

    @FXML
    private JFXTextField txtSearchByCNo;

    private ToggleGroup genderToggleGroup;

    private List<Employee> employeeList = new ArrayList<>();

    private ObservableList<EmployeeTm> tmList;



    public void initialize() {
        initializeEmployeeId();
        initializeCmbRole();
        initializeRadioButtons();

        this.employeeList = getAllEmployees();
        setCellValueFactory();
        loadEmployeeTable();
    }

    private void loadEmployeeTable() {
        tmList = FXCollections.observableArrayList();

        for (Employee employee : employeeList) {

            LocalDate dateOfBirth = LocalDate.parse(employee.getDOb());
            LocalDate currentDate = LocalDate.now();

            Period period = Period.between(dateOfBirth, currentDate);
            String age = String.valueOf(period.getYears());

            EmployeeTm employeeTm = new EmployeeTm(
                   employee.getId(),
                   employee.getRole(),
                    employee.getFname() + " " + employee.getLname(),
                    employee.getDOb(),
                    age,
                    employee.getGender(),
                    employee.getJoinDate(),
                    employee.getNIC(),
                    employee.getAddress(),
                    employee.getCNo(),
                    employee.getEmail()
            );

            tmList.add(employeeTm);
        }
        tableEmployee.setItems(tmList);
        tableEmployee.refresh();
    }

    private void setCellValueFactory() {
        columnEmployeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDOB.setCellValueFactory(new PropertyValueFactory<>("dOB"));
        columnAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnJoinDate.setCellValueFactory(new PropertyValueFactory<>("joinDate"));
        columnNIC.setCellValueFactory(new PropertyValueFactory<>("nIC"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnCNo.setCellValueFactory(new PropertyValueFactory<>("cNo"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private List<Employee> getAllEmployees() {
        List<Employee> employeeList = null;
        try {
            employeeList = EmployeeRepository.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return employeeList;
    }

    private void initializeEmployeeId() {
        try {
            txtEmployeeId.setText(EmployeeRepository.getNextEmployeeId());
            txtEmployeeId.requestLayout();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeCmbRole() {
        cmbRole.getItems().addAll("Admin", "Instructor");
    }

    private void initializeRadioButtons() {
        // Create a ToggleGroup and assign it to the RadioButtons
        genderToggleGroup = new ToggleGroup();
        rbFemale.setToggleGroup(genderToggleGroup);
        rbMale.setToggleGroup(genderToggleGroup);

        // Select a default gender
        rbMale.setSelected(true);
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtEmployeeId.setText("");
        txtFName.setText("");
        txtLName.setText("");
        txtNIC.setText("");
        txtAddress.setText("");
        txtCNo.setText("");
        txtEmail.setText("");
        cmbRole.setValue("");
        dpDoB.setValue(null);
        dpJoinDate.setValue(null);
        rbFemale.setSelected(false);
        rbMale.setSelected(true);

        initializeEmployeeId();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtEmployeeId.getText();

        if (id != null && !id.isEmpty()){
            if (Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtEmployeeId)){
                try {
                    if(EmployeeRepository.EmployeeIdIsExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal

                                try {
                                    boolean isRemoved = EmployeeRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Employee removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeEmployeeId();
                                        txtEmployeeId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Employee not Found!").show();
                        txtEmployeeId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Employee Id!").show();
        }
    }

    private void refreshTableView() {
        this.employeeList = getAllEmployees();
        loadEmployeeTable();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtEmployeeId.getText();
        String fName = txtFName.getText();
        String lName = txtLName.getText();
        String nIC = txtNIC.getText();
        String address = txtAddress.getText();
        String cNo = txtCNo.getText();
        String email = null;
        String role = null;
        String dOB = null;
        String joinDate = null;

        if (cmbRole.getValue() != null) {
            role = cmbRole.getValue();
        }

        LocalDate dOBRaw = dpDoB.getValue();
        LocalDate joinDateRaw = dpJoinDate.getValue();

        if (dOBRaw != null && joinDateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dOB = dOBRaw.format(formatter);
            joinDate = joinDateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }


        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String gender = selectedRadioButton.getText();

        if(id != null && !id.trim().isEmpty() && fName != null && !fName.trim().isEmpty()
                && lName != null && !lName.trim().isEmpty() && nIC != null && !nIC.trim().isEmpty()
                && address != null && !address.trim().isEmpty() && cNo != null && !cNo.trim().isEmpty()
                && role != null && !role.trim().isEmpty() && dOB != null
                && !dOB.isEmpty() && joinDate != null
                && !joinDate.isEmpty()){
            if(isValid()){
                if(txtEmail != null && !txtEmail.getText().isEmpty()){
                    if(Regex.setTextColor(lk.ijse.lms.util.TextField.EMAIL,txtEmail)){
                        email = txtEmail.getText();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
                        return;
                    }
                }
                Employee employee = new Employee(id, fName, lName, dOB, gender, joinDate, nIC, address, cNo, email, role);
                try {
                    if(EmployeeRepository.isEmployeeIdAvailable(id)){
                        try {
                            boolean isSaved = EmployeeRepository.save(employee);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Employee saved!").show();
                                refreshTableView();
                                clearFields();
                                initializeEmployeeId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Employee already exist!!").show();
                        txtEmployeeId.requestFocus();
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
        String id = txtEmployeeId.getText();
        String fName = txtFName.getText();
        String lName = txtLName.getText();
        String nIC = txtNIC.getText();
        String address = txtAddress.getText();
        String cNo = txtCNo.getText();
        String email = null;
        String role = null;
        String dOB = null;
        String joinDate = null;

        if (cmbRole.getValue() != null) {
            role = cmbRole.getValue();
        }

        LocalDate dOBRaw = dpDoB.getValue();
        LocalDate joinDateRaw = dpJoinDate.getValue();

        if (dOBRaw != null && joinDateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dOB = dOBRaw.format(formatter);
            joinDate = joinDateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }


        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String gender = selectedRadioButton.getText();

        if(id != null && !id.trim().isEmpty() && fName != null && !fName.trim().isEmpty()
                && lName != null && !lName.trim().isEmpty() && nIC != null && !nIC.trim().isEmpty()
                && address != null && !address.trim().isEmpty() && cNo != null && !cNo.trim().isEmpty()
                && role != null && !role.trim().isEmpty() && dOB != null
                && !dOB.isEmpty() && joinDate != null
                && !joinDate.isEmpty()){
            if(isValid()){
                if(txtEmail != null && !txtEmail.getText().isEmpty()){
                    if(Regex.setTextColor(lk.ijse.lms.util.TextField.EMAIL,txtEmail)){
                        email = txtEmail.getText();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
                        return;
                    }
                }
                Employee employee = new Employee(id, fName, lName, dOB, gender, joinDate, nIC, address, cNo, email, role);
                try {
                    if(EmployeeRepository.EmployeeIdIsExist(id)){
                        try {
                            boolean isUpdated = EmployeeRepository.update(employee);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Employee updated!").show();
                                refreshTableView();
                                clearFields();
                                initializeEmployeeId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Employee not Found!!").show();
                        txtEmployeeId.requestFocus();
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
    void txtSearchByEIdOnAction(ActionEvent event) {
        String id = txtSearchByEId.getText();

        clearFields();
        txtEmployeeId.setText(id);

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearchByEId)){
                try {
                    Employee employee = EmployeeRepository.searchById(id);

                    if (employee != null){
                        txtEmployeeId.setText(employee.getId());
                        txtFName.setText(employee.getFname());
                        txtLName.setText(employee.getLname());
                        txtNIC.setText(employee.getNIC());
                        txtAddress.setText(employee.getAddress());
                        txtCNo.setText(employee.getCNo());
                        txtEmail.setText(employee.getEmail());
                        cmbRole.setValue(employee.getRole());

                        LocalDate dOBRaw = LocalDate.parse(employee.getDOb()); // Parse the SQL DATE string to LocalDate
                        dpDoB.setValue(dOBRaw);

                        LocalDate joinDateRaw = LocalDate.parse(employee.getJoinDate());
                        dpJoinDate.setValue(joinDateRaw);

                        rbFemale.setSelected(false);
                        rbMale.setSelected(true);

                        // Iterate through the radio buttons in the toggle group
                        for (Toggle toggle : genderToggleGroup.getToggles()) {
                            RadioButton radioButton = (RadioButton) toggle;
                            if (radioButton.getText().equals(employee.getGender())) {
                                radioButton.setSelected(true); // Select the radio button with matching gender
                            } else {
                                radioButton.setSelected(false); // Unselect other radio buttons
                            }
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Employee not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Employee Id!").show();
        }
    }

    @FXML
    void txtSearchByCNoOnAction(ActionEvent event) {
        String cNo = txtSearchByCNo.getText();

        clearFields();

        if (cNo != null && !cNo.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.PHONE,txtSearchByCNo)){
                try {
                    Employee employee = EmployeeRepository.searchByCNo(cNo);

                    if (employee != null){
                        txtEmployeeId.setText(employee.getId());
                        txtFName.setText(employee.getFname());
                        txtLName.setText(employee.getLname());
                        txtNIC.setText(employee.getNIC());
                        txtAddress.setText(employee.getAddress());
                        txtCNo.setText(employee.getCNo());
                        txtEmail.setText(employee.getEmail());
                        cmbRole.setValue(employee.getRole());

                        LocalDate dOBRaw = LocalDate.parse(employee.getDOb()); // Parse the SQL DATE string to LocalDate
                        dpDoB.setValue(dOBRaw);

                        LocalDate joinDateRaw = LocalDate.parse(employee.getJoinDate());
                        dpJoinDate.setValue(joinDateRaw);

                        rbFemale.setSelected(false);
                        rbMale.setSelected(true);

                        // Iterate through the radio buttons in the toggle group
                        for (Toggle toggle : genderToggleGroup.getToggles()) {
                            RadioButton radioButton = (RadioButton) toggle;
                            if (radioButton.getText().equals(employee.getGender())) {
                                radioButton.setSelected(true); // Select the radio button with matching gender
                            } else {
                                radioButton.setSelected(false); // Unselect other radio buttons
                            }
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Employee not Found!").show();
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
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtEmployeeId)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtFName)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtLName)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.NIC,txtNIC)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ADDRESS,txtAddress)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.PHONE,txtCNo)) return false;

        return true;
    }

    @FXML
    void txtAddressOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ADDRESS,txtAddress);
    }

    @FXML
    void txtCNoOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.PHONE,txtCNo);
    }

    @FXML
    void txtEmailOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.EMAIL,txtEmail);
    }

    @FXML
    void txtEmployeeIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtEmployeeId);
    }

    @FXML
    void txtFNameOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtFName);
    }

    @FXML
    void txtLNameOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.NAME,txtLName);
    }

    @FXML
    void txtNICOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.NIC,txtNIC);
    }

    @FXML
    void txtSearchByCNoOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.PHONE,txtSearchByCNo);
    }

    @FXML
    void txtSearchByEIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearchByEId);
    }

}
