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
import lk.ijse.lms.model.VehicleMaintenance;
import lk.ijse.lms.model.tm.VehicleMaintenanceTm;
import lk.ijse.lms.repository.VehicleManitenanceRepository;
import lk.ijse.lms.repository.VehicleRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VehicleMaintenanceController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXComboBox<String> cmbVehicles;

    @FXML
    private TableColumn<?, ?> columnCost;

    @FXML
    private TableColumn<?, ?> columnDate;

    @FXML
    private TableColumn<?, ?> columnDesc;

    @FXML
    private TableColumn<?, ?> columnMId;

    @FXML
    private TableColumn<?, ?> columnMdCost;

    @FXML
    private TableColumn<?, ?> columnMdDate;

    @FXML
    private TableColumn<?, ?> columnMdDesc;

    @FXML
    private TableColumn<?, ?> columnMdMId;

    @FXML
    private TableColumn<?, ?> columnVId;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TableView<VehicleMaintenanceTm> tableMDetails;

    @FXML
    private TableView<VehicleMaintenanceTm> tableMaintenance;

    @FXML
    private JFXTextField txtCost;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtMId;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtSearchByVId;

    @FXML
    private AnchorPane vehicleMaintenancePane;

    private DashboardController dashboardController;

    private List<VehicleMaintenance> maintenanceList;

    private List<VehicleMaintenance> maintenancesDetailList;

    private ObservableList<VehicleMaintenanceTm> mTmList;

    private ObservableList<VehicleMaintenanceTm> mdTmList;


    public void initialize() {
        initializeMId();
        initializeCbVId();

        this.maintenanceList = getAllMaintenances();
        setCellValueFactory();
        loadMaintenanceTable();
    }

    private void refreshTableView() {
        this.maintenanceList = getAllMaintenances();
        loadMaintenanceTable();
    }

    private void loadMaintenanceTable() {
        mTmList = FXCollections.observableArrayList();

        for (VehicleMaintenance vm : maintenanceList) {
            VehicleMaintenanceTm vmTm = new VehicleMaintenanceTm(
                    vm.getMId(),
                    vm.getDescription(),
                    vm.getDate(),
                    vm.getCost(),
                    vm.getVId()
            );

            mTmList.add(vmTm);
        }
        tableMaintenance.setItems(mTmList);
        tableMaintenance.refresh();
    }

    private void setCellValueFactory() {
        columnMId.setCellValueFactory(new PropertyValueFactory<>("mId"));
        columnVId.setCellValueFactory(new PropertyValueFactory<>("vId"));
        columnDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        columnMdMId.setCellValueFactory(new PropertyValueFactory<>("mId"));
        columnMdDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnMdDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnMdCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private List<VehicleMaintenance> getAllMaintenances() {
        List<VehicleMaintenance> list = null;
        try {
            list = VehicleManitenanceRepository.getAllMaintenances();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void initializeCbVId() {
        try {
            cmbVehicles.setItems(VehicleRepository.getAllVehicles());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeMId() {
        try {
            txtMId.setText(VehicleManitenanceRepository.getNextMId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        txtMId.setText("");
        txtDescription.setText("");
        txtCost.setText("");
        cmbVehicles.setValue(null);
        dpDate.setValue(null);

        initializeMId();
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void btnBackOnClicked(MouseEvent event) {
        dashboardController.loadPage("Vehicle_page");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String id = txtMId.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtMId)){
                try {
                    if(VehicleManitenanceRepository.isMIdExist(id)){
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
                        confirmationAlert.setHeaderText(null); // Remove header text

                        // Show the confirmation dialog and wait for user input
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // User clicked OK (Yes) button, perform employee removal
                                try {
                                    boolean isRemoved = VehicleManitenanceRepository.remove(id);
                                    if (isRemoved) {
                                        new Alert(Alert.AlertType.INFORMATION, "Maintenance removed!").show();
                                        refreshTableView();
                                        clearFields();
                                        initializeMId();
                                        txtMId.requestFocus();
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
                        new Alert(Alert.AlertType.ERROR, "Maintenance not Found!").show();
                        txtMId.requestFocus();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Maintenance Id!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtMId.getText();
        String description = txtDescription.getText();
        String cost = txtCost.getText();
        String vehicleId = null;
        String date = null;

        if (cmbVehicles.getValue() != null) {
            vehicleId = cmbVehicles.getValue();
        }

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        VehicleMaintenance vm = new VehicleMaintenance(id, description, date , cost , vehicleId);

        if(id != null && !id.trim().isEmpty() && description != null && !description.trim().isEmpty()
                && cost != null && !cost.trim().isEmpty() && date != null && !date.trim().isEmpty()
                && vehicleId != null && !vehicleId.trim().isEmpty()){
            if(isValid()){
                try {
                    if(VehicleManitenanceRepository.isMIdAvailable(id)){
                        try {
                            boolean isSaved = VehicleManitenanceRepository.save(vm);
                            if(isSaved){
                                new Alert(Alert.AlertType.CONFIRMATION, "Maintenance saved!").show();
                                refreshTableView();
                                clearFields();
                                initializeMId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Maintenance already exist!!").show();
                        txtMId.requestFocus();
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
        String id = txtMId.getText();
        String description = txtDescription.getText();
        String cost = txtCost.getText();
        String vehicleId = null;
        String date = null;

        if (cmbVehicles.getValue() != null) {
            vehicleId = cmbVehicles.getValue();
        }

        LocalDate dateRaw = dpDate.getValue();

        if (dateRaw != null) {
            // Format the date as a SQL DATE string (yyyy-MM-dd)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = dateRaw.format(formatter);
            // Now you can use the sqlDate string as needed
        }

        VehicleMaintenance vm = new VehicleMaintenance(id, description, date , cost , vehicleId);

        if(id != null && !id.trim().isEmpty() && description != null && !description.trim().isEmpty()
                && cost != null && !cost.trim().isEmpty() && date != null && !date.trim().isEmpty()
                && vehicleId != null && !vehicleId.trim().isEmpty()){
            if(isValid()){
                try {
                    if(VehicleManitenanceRepository.isMIdExist(id)){
                        try {
                            boolean isUpdated = VehicleManitenanceRepository.update(vm);
                            if(isUpdated){
                                new Alert(Alert.AlertType.CONFIRMATION, "Maintenance updated!").show();
                                refreshTableView();
                                clearFields();
                                initializeMId();
                            }
                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Maintenance not found!!").show();
                        txtMId.requestFocus();
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
    void txtSearchByVIdOnAction(ActionEvent event) {
        String id = txtSearchByVId.getText();

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearchByVId)){
                try {
                    if(VehicleRepository.isVehicleIdExist(id)){
                        this.maintenancesDetailList = getAllMaintenanceDetails(id);
                        loadMaintenanceDetailsTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Vehicle not found!").show();
                        this.maintenancesDetailList = getAllMaintenanceDetails(id);
                        loadMaintenanceDetailsTable();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Vehicle Id!").show();
        }
    }

    private void loadMaintenanceDetailsTable() {
        mdTmList = FXCollections.observableArrayList();

        for (VehicleMaintenance vm : maintenancesDetailList) {
            VehicleMaintenanceTm vmTm = new VehicleMaintenanceTm(
                    vm.getMId(),
                    vm.getDescription(),
                    vm.getDate(),
                    vm.getCost(),
                    vm.getVId()
            );

            mdTmList.add(vmTm);
        }
        tableMDetails.setItems(mdTmList);
        tableMDetails.refresh();
    }

    private List<VehicleMaintenance> getAllMaintenanceDetails(String id) {
        List<VehicleMaintenance> list = null;
        try {
            list = VehicleManitenanceRepository.getAllMaintenanceDetails(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String id = txtSearch.getText();

        clearFields();
        txtMId.setText(id);

        if (id != null && !id.isEmpty()){
            if(Regex.setTextColor(TextField.ID,txtSearch)){
                try {
                    VehicleMaintenance vm = VehicleManitenanceRepository.searchById(id);

                    if (vm != null){
                        txtMId.setText(vm.getMId());
                        txtCost.setText(vm.getCost());
                        txtDescription.setText(vm.getDescription());
                        cmbVehicles.setValue(vm.getVId());

                        LocalDate dateRaw = LocalDate.parse(vm.getDate()); // Parse the SQL DATE string to LocalDate
                        dpDate.setValue(dateRaw);
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Maintenance not Found!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Incorrect value in fields!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a Maintenance Id!").show();
        }
    }

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtMId)) return false;
        if (!Regex.setTextColor(TextField.DOUBLE,txtCost)) return false;
        return true;
    }

    @FXML
    void txtCostOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.DOUBLE,txtCost);
    }

    @FXML
    void txtMIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(lk.ijse.lms.util.TextField.ID,txtMId);
    }

    @FXML
    void txtSearchByVIdOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearchByVId);
    }

    @FXML
    void txtSearchOnKeyRelesed(KeyEvent event) {
        Regex.setTextColor(TextField.ID,txtSearch);
    }
}
