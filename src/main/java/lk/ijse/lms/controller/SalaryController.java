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
import lk.ijse.lms.model.Salary;
import lk.ijse.lms.model.tm.SalaryTm;
import lk.ijse.lms.repository.*;
import lk.ijse.lms.util.Regex;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalaryController {

    @FXML
    private JFXComboBox<String> cmbEmployees;

    @FXML
    private JFXComboBox<String> cmbMop;

    @FXML
    private TableColumn<?, ?> columnBasicP;

    @FXML
    private TableColumn<?, ?> columnDate;

    @FXML
    private TableColumn<?, ?> columnEId;

    @FXML
    private TableColumn<?, ?> columnEName;

    @FXML
    private TableColumn<?, ?> columnMop;

    @FXML
    private TableColumn<?, ?> columnOtP;

    @FXML
    private TableColumn<?, ?> columnTotalP;

    @FXML
    private DatePicker dpDate;

    @FXML
    private AnchorPane salariesPane;

    @FXML
    private TableView<SalaryTm> tableEmployee;

    @FXML
    private JFXTextField txtBasicP;

    @FXML
    private JFXTextField txtMonthOfPay;

    @FXML
    private JFXTextField txtOtP;

    @FXML
    private JFXTextField txtSearch;

    private List<Salary> salaryList;

    private ObservableList<SalaryTm> tmList;



    public void initialize() {
        initializeCmb();

        this.salaryList = getAllSalaries();
        setCellValueFactory();
        loadSalaryTable();
    }

    private void loadSalaryTable() {
        tmList = FXCollections.observableArrayList();

        for (Salary salary : salaryList) {
            SalaryTm salaryTm = null;
            try {
                salaryTm = new SalaryTm(
                        salary.getEmployeeId(),
                        EmployeeRepository.getEmName(salary.getEmployeeId()),
                        salary.getMonthOfPay(),
                        salary.getDate(),
                        salary.getBasicP(),
                        salary.getOtP(),
                        Double.parseDouble(salary.getBasicP()) + Double.parseDouble(salary.getOtP())
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            tmList.add(salaryTm);
        }
        tableEmployee.setItems(tmList);
        tableEmployee.refresh();
    }

    private void setCellValueFactory() {
        columnEId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        columnEName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnMop.setCellValueFactory(new PropertyValueFactory<>("monthOfPay"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnBasicP.setCellValueFactory(new PropertyValueFactory<>("basicP"));
        columnOtP.setCellValueFactory(new PropertyValueFactory<>("otP"));
        columnTotalP.setCellValueFactory(new PropertyValueFactory<>("totalP"));
    }

    private List<Salary> getAllSalaries() {
        List<Salary> list = null;
        try {
            list = SalaryRepository.getAllSalaries();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void refreshTableView() {
        this.salaryList = getAllSalaries();
        loadSalaryTable();
    }

    private void initializeCmb() {
        try {
            cmbMop.setItems(SalaryRepository.getAllPaymentMonths());
            cmbEmployees.setItems(EmployeeRepository.getAllEmployees());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        txtMonthOfPay.setText("");
        txtBasicP.setText("");
        txtOtP.setText("");
        cmbEmployees.setValue(null);
        dpDate.setValue(null);
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = cmbEmployees.getValue();
        String mop = txtMonthOfPay.getText();

        if(id != null && !id.isEmpty() && mop != null && !mop.isEmpty()) {
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.MONTHOFPAY,txtMonthOfPay)){
                try {
                    if(SalaryRepository.isSalaryExist(id, mop)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal
                                try {
                                    boolean isRemoved = SalaryRepository.remove(id, mop);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Salary removed!").show();
                                        initializeCmb();
                                        refreshTableView();
                                        clearFields();
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
                        new Alert(Alert.AlertType.ERROR, "Salary not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter Employee ID and Month of Pay!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String monthOfPay = txtMonthOfPay.getText();
        String basicPayment = txtBasicP.getText();
        String otPayment = txtOtP.getText();
        String employeeId = null;
        String date = null;

        if (cmbEmployees.getValue() != null) {
            employeeId = cmbEmployees.getValue();
        }

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        Salary salary = new Salary(monthOfPay, date, basicPayment, otPayment, employeeId);

        if(monthOfPay != null && !monthOfPay.trim().isEmpty() && basicPayment != null && !basicPayment.trim().isEmpty()
                && date != null && !date.trim().isEmpty() && employeeId != null && !employeeId.trim().isEmpty()
                && otPayment != null && !otPayment.trim().isEmpty()){
            if(isValid()){
                try {
                    if(SalaryRepository.isSalaryAvailable(salary)){
                        try {
                            boolean isSaved = SalaryRepository.save(salary);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Salary saved!").show();
                                initializeCmb();
                                refreshTableView();
                                clearFields();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Salary already exist!").show();
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
        String monthOfPay = txtMonthOfPay.getText();
        String basicPayment = txtBasicP.getText();
        String otPayment = txtOtP.getText();
        String employeeId = null;
        String date = null;

        if (cmbEmployees.getValue() != null) {
            employeeId = cmbEmployees.getValue();
        }

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        Salary salary = new Salary(monthOfPay, date, basicPayment, otPayment, employeeId);

        if(monthOfPay != null && !monthOfPay.trim().isEmpty() && basicPayment != null && !basicPayment.trim().isEmpty()
                && date != null && !date.trim().isEmpty() && employeeId != null && !employeeId.trim().isEmpty()
                && otPayment != null && !otPayment.trim().isEmpty()){
            if(isValid()){
                try {
                    if(SalaryRepository.isSalaryExist(employeeId, monthOfPay)){
                        try {
                            boolean isUpdated = SalaryRepository.update(salary);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Salary updated!").show();
                                initializeCmb();
                                refreshTableView();
                                clearFields();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Salary not found!").show();
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
        String mop = cmbMop.getValue();

        clearFields();

        if (id != null && !id.isEmpty() && mop != null && !mop.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID, txtSearch)){
                try {
                    Salary salary = SalaryRepository.searchById(id, mop);

                    if (salary != null){
                        txtMonthOfPay.setText(salary.getMonthOfPay());
                        txtBasicP.setText(salary.getBasicP());
                        txtOtP.setText(salary.getOtP());
                        cmbEmployees.setValue(salary.getEmployeeId());

                        LocalDate dateRaw = LocalDate.parse(salary.getDate()); // Parse the SQL DATE string to LocalDate
                        dpDate.setValue(dateRaw);
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Salary not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Employee Id and Month of Payment!").show();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.MONTHOFPAY,txtMonthOfPay)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.DOUBLE,txtBasicP)) return false;
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.DOUBLE,txtOtP)) return false;
        return true;
    }

    @FXML
    void txtBasicPOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.DOUBLE,txtBasicP);
    }

    @FXML
    void txtMonthOfPayOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.MONTHOFPAY,txtMonthOfPay);
    }

    @FXML
    void txtOtPOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.DOUBLE,txtOtP);
    }

    @FXML
    void txtSearchOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID, txtSearch);
    }
}
