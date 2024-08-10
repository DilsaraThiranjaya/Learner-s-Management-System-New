
package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.lms.model.Vehicle;
import lk.ijse.lms.model.tm.VehicleTm;
import lk.ijse.lms.repository.EmployeeRepository;
import lk.ijse.lms.repository.VehicleRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleController {

    @FXML
    private JFXButton btnVm;

    @FXML
    private JFXComboBox<String> cmbTransmission;

    @FXML
    private JFXComboBox<String> cmbType;

    @FXML
    private JFXComboBox<String> cmbEmployees;

    @FXML
    private TableColumn<?, ?> columnAssignedEm;

    @FXML
    private TableColumn<?, ?> columnFType;

    @FXML
    private TableColumn<?, ?> columnModel;

    @FXML
    private TableColumn<?, ?> columnRNumber;

    @FXML
    private TableColumn<?, ?> columnStatus;

    @FXML
    private TableColumn<?, ?> columnTransmission;

    @FXML
    private TableColumn<?, ?> columnType;

    @FXML
    private TableColumn<?, ?> columnVId;

    @FXML
    private ListView<String> lvEmployees;

    @FXML
    private RadioButton rbActive;

    @FXML
    private RadioButton rbInactive;

    @FXML
    private TableView<VehicleTm> tableVehicle;

    @FXML
    private JFXTextField txtFuelType;

    @FXML
    private JFXTextField txtModel;

    @FXML
    private JFXTextField txtRNumber;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtVehicleId;

    @FXML
    private AnchorPane vehiclePane;

    private DashboardController dashboardController;

    private ToggleGroup statusToggleGroup;

    private List<Vehicle> vehicleList;

    private ObservableList<String> employeeList;

    private ObservableList<VehicleTm> tmList;



    public void initialize() {
        vehicleList = new ArrayList<>();
        employeeList = FXCollections.observableArrayList();
        lvEmployees.setItems(employeeList);

        initializeVehicleId();
        initializeCmb();
        initializeRadioButtons();

        this.vehicleList = getAllVehicles();
        setCellValueFactory();
        loadVehicleTable();
    }

    private void refreshTableView() {
        this.vehicleList = getAllVehicles();
        loadVehicleTable();
    }

    private void loadVehicleTable() {
        tmList = FXCollections.observableArrayList();

        for (Vehicle vehicle : vehicleList) {

            VehicleTm vehicleTm = new VehicleTm(
                    vehicle.getId(),
                    convertListToString(vehicle.getEmployeeList()),
                    vehicle.getType(),
                    vehicle.getModel(),
                    vehicle.getFType(),
                    vehicle.getTransmission(),
                    vehicle.getRNumber(),
                    vehicle.getStatus()
            );

            tmList.add(vehicleTm);
        }
        tableVehicle.setItems(tmList);
        tableVehicle.refresh();
    }

    public static String convertListToString(ObservableList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append(item).append(", ");
        }
        // Remove the trailing ", " if the list is not empty
        if (!list.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    private void setCellValueFactory() {
        columnVId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        columnFType.setCellValueFactory(new PropertyValueFactory<>("fType"));
        columnTransmission.setCellValueFactory(new PropertyValueFactory<>("transmission"));
        columnRNumber.setCellValueFactory(new PropertyValueFactory<>("rNumber"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        columnAssignedEm.setCellValueFactory(new PropertyValueFactory<>("assignedEm"));
    }

    private List<Vehicle> getAllVehicles() {
        List<Vehicle> list = null;
        try {
            list = VehicleRepository.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void initializeRadioButtons() {
        // Create a ToggleGroup and assign it to the RadioButtons
        statusToggleGroup = new ToggleGroup();
        rbActive.setToggleGroup(statusToggleGroup);
        rbInactive.setToggleGroup(statusToggleGroup);

        // Select a default status
        rbActive.setSelected(true);
    }

    private void initializeCmb() {
        cmbTransmission.getItems().addAll("Automatic", "Manual");
        cmbType.getItems().addAll("Car","Van", "Treewheel", "MotorBike");

        try {
            cmbEmployees.setItems(EmployeeRepository.getAllInstructors());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeVehicleId() {
        try {
            txtVehicleId.setText(VehicleRepository.getNextVehicleId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void btnVmOnClicked(MouseEvent event) {
        // Call the loadPage method from the DashboardController instance
        dashboardController.loadPage("Vehicle_maintenance_page");
    }

    @FXML
    void btnAddEmployeeOnAction(ActionEvent event) {
        String selectedCourse = cmbEmployees.getValue();
        if(selectedCourse != null){
            if ( !employeeList.contains(selectedCourse)) {
                employeeList.add(selectedCourse);
            } else if (employeeList.contains(selectedCourse)){
                new Alert(Alert.AlertType.ERROR, "Employee already added!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Select an Employee!").show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtVehicleId.setText("");
        txtFuelType.setText("");
        txtModel.setText("");
        txtRNumber.setText("");
        cmbEmployees.setValue(null);
        employeeList.clear();
        cmbType.setValue(null);
        cmbTransmission.setValue(null);

        rbInactive.setSelected(false);
        rbActive.setSelected(true);

        initializeVehicleId();
    }

    @FXML
    void btnRemoveEmployeeOnAction(ActionEvent event) {
        String selectedCourse = lvEmployees.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            employeeList.remove(selectedCourse);
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Select a Employee first!").show();
        }
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtVehicleId.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtVehicleId)){
                try {
                    if(VehicleRepository.isVehicleIdExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal

                                try {
                                    boolean isRemoved = VehicleRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Vehicle removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeVehicleId();
                                        txtVehicleId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Vehicle not Found!").show();
                        txtVehicleId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Vehicle Id!").show();
        }
    }


    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtVehicleId.getText();
        String model = txtModel.getText();
        String fType = txtFuelType.getText();
        String rNumber = txtRNumber.getText();
        String type = cmbType.getValue();
        String transmission = cmbTransmission.getValue();

        RadioButton selectedRadioButton = (RadioButton) statusToggleGroup.getSelectedToggle();
        String status = selectedRadioButton.getText();

        Vehicle vehicle = new Vehicle(id, type, model, fType, transmission, rNumber, status, employeeList);

        if(id != null && !id.isEmpty() && type != null && !type.isEmpty() && model != null && !model.isEmpty()
                && fType != null && !fType.isEmpty() && transmission != null && !transmission.isEmpty()
                && rNumber != null && !rNumber.isEmpty() && status != null && !status.isEmpty()
                && employeeList != null && !employeeList.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtVehicleId)){
                try {
                    if(VehicleRepository.isVehicleIdAvailable(id)){
                        try {
                            boolean isSaved = VehicleRepository.save(vehicle);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Vehicle saved!").show();
                                refreshTableView();
                                clearFields();
                                initializeVehicleId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Vehicle already exist").show();
                        txtVehicleId.requestFocus();
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
        String id = txtVehicleId.getText();
        String model = txtModel.getText();
        String fType = txtFuelType.getText();
        String rNumber = txtRNumber.getText();
        String type = cmbType.getValue();
        String transmission = cmbTransmission.getValue();

        RadioButton selectedRadioButton = (RadioButton) statusToggleGroup.getSelectedToggle();
        String status = selectedRadioButton.getText();

        Vehicle vehicle = new Vehicle(id, type, model, fType, transmission, rNumber, status, employeeList);

        if(id != null && !id.isEmpty() && type != null && !type.isEmpty() && model != null && !model.isEmpty()
                && fType != null && !fType.isEmpty() && transmission != null && !transmission.isEmpty()
                && rNumber != null && !rNumber.isEmpty() && status != null && !status.isEmpty()
                && employeeList != null && !employeeList.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtVehicleId)){
                try {
                    if(VehicleRepository.isVehicleIdExist(id)){
                        try {
                            boolean isUpdated = VehicleRepository.update(vehicle);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Vehicle updated!").show();
                                refreshTableView();
                                clearFields();
                                initializeVehicleId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Vehicle not found!").show();
                        txtVehicleId.requestFocus();
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

        clearFields();
        txtVehicleId.setText(id);

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearch)){
                try {
                    Vehicle vehicle = VehicleRepository.searchById(id);

                    if (vehicle != null){
                        txtVehicleId.setText(vehicle.getId());
                        txtRNumber.setText(vehicle.getRNumber());
                        txtModel.setText(vehicle.getModel());
                        txtFuelType.setText(vehicle.getFType());
                        cmbType.setValue(vehicle.getType());
                        cmbTransmission.setValue(vehicle.getTransmission());


                        // Update coursesList and lvCourses
                        employeeList.clear(); // Clear the existing items
                        employeeList.addAll(vehicle.getEmployeeList()); // Add new items
                        lvEmployees.setItems(employeeList); // Set the items to ListView

                        rbInactive.setSelected(false);
                        rbActive.setSelected(true);

                        // Iterate through the radio buttons in the toggle group
                        for (Toggle toggle : statusToggleGroup.getToggles()) {
                            RadioButton radioButton = (RadioButton) toggle;
                            if (radioButton.getText().equals(vehicle.getStatus())) {
                                radioButton.setSelected(true); // Select the radio button with matching gender
                            } else {
                                radioButton.setSelected(false); // Unselect other radio buttons
                            }
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Vehicle not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Vehicle Id!").show();
        }
    }

    @FXML
    void txtVehicleIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtVehicleId);
    }

    @FXML
    void txtSearchOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearch);
    }
}
