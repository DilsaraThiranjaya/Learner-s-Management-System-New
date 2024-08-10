package lk.ijse.lms.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    public static String getNextVehicleId() throws SQLException {
        String sql ="SELECT vehicleId FROM vehicle ORDER BY vehicleId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 1); // Assuming format is "VXXX"
            int numericPart = Integer.parseInt(lastId.substring(1)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "VXXX" format
            return nextId;
        }

        return "V001";
    }

    public static boolean isVehicleIdAvailable(String id) throws SQLException {
        String sql ="SELECT vehicleId FROM vehicle WHERE vehicleId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return !pstm.executeQuery().next();
    }

    public static boolean save(Vehicle vehicle) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isVehicleSaved = saveVehicle(vehicle);
            if (isVehicleSaved) {
                boolean isVehicleDetailsSaved = saveVehicleDetails(vehicle);
                if (isVehicleDetailsSaved) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static boolean saveVehicleDetails(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicleDetails VALUES (?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        boolean isSaved = false;

        if(vehicle.getEmployeeList() != null){
            for(String employeeId : vehicle.getEmployeeList()){
                pstm.setObject(1, vehicle.getId());
                pstm.setObject(2, employeeId);

                isSaved = pstm.executeUpdate() > 0;
            }
            return isSaved;
        }

        return false;
    }

    private static boolean saveVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicle VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,vehicle.getId());
        pstm.setObject(2,vehicle.getType());
        pstm.setObject(3,vehicle.getModel());
        pstm.setObject(4,vehicle.getFType());
        pstm.setObject(5,vehicle.getTransmission());
        pstm.setObject(6,vehicle.getRNumber());
        pstm.setObject(7,vehicle.getStatus());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isVehicleIdExist(String id) throws SQLException {
        String sql ="SELECT vehicleId FROM vehicle WHERE vehicleId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static boolean update(Vehicle vehicle) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isVehicleUpdated = updateVehicle(vehicle);
            if (isVehicleUpdated) {
                boolean isVehicleDetailsUpdated = updateVehicleDetails(vehicle);
                if (isVehicleDetailsUpdated) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static boolean updateVehicleDetails(Vehicle vehicle) throws SQLException {
        boolean isDeleted = searchAndDeleteVehicleDetails(vehicle);
        if(isDeleted){
            boolean isSaved = saveVehicleDetails(vehicle);
            if(isSaved){
                return true;
            }
        }
        return false;
    }

    private static boolean searchAndDeleteVehicleDetails(Vehicle vehicle) throws SQLException {
        String sql = "SELECT employeeId FROM vehicleDetails WHERE vehicleId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, vehicle.getId());
        ResultSet resultSet = pstm.executeQuery();

        boolean isDeleted = false;

        while(resultSet.next()){
            isDeleted = DeleteVehicleDetail(resultSet.getString("employeeId"), vehicle.getId());
        }
        return isDeleted;
    }

    private static boolean DeleteVehicleDetail(String employeeId, String id) throws SQLException {
        String sql = "DELETE FROM vehicleDetails WHERE employeeId = ? AND vehicleId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, employeeId);
        pstm.setObject(2, id);

        return pstm.executeUpdate() > 0;
    }

    private static boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicle SET type = ?, model = ?, fuelType = ?, transmission = ?, registrationNumber = ?, status = ? WHERE vehicleId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,vehicle.getType());
        pstm.setObject(2,vehicle.getModel());
        pstm.setObject(3,vehicle.getFType());
        pstm.setObject(4,vehicle.getTransmission());
        pstm.setObject(5,vehicle.getRNumber());
        pstm.setObject(6,vehicle.getStatus());
        pstm.setObject(7,vehicle.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM vehicle WHERE vehicleId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static Vehicle searchById(String id) throws SQLException {
        String sql = "SELECT * FROM vehicle WHERE vehicleId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        Vehicle vehicle = null;

        if (resultSet.next()) {
            String vehicleId = resultSet.getString(1);
            String type = resultSet.getString(2);
            String model = resultSet.getString(3);
            String fType = resultSet.getString(4);
            String trasmission = resultSet.getString(5);
            String rNumber = resultSet.getString(6);
            String status = resultSet.getString(7);

            vehicle = new Vehicle(vehicleId, type, model, fType, trasmission, rNumber, status, getEmployeeList(vehicleId));
        }
        return vehicle;
    }

    private static ObservableList<String> getEmployeeList(String vehicleId) throws SQLException {
        ObservableList<String> employeeLsit = FXCollections.observableArrayList();

        String sql = "SELECT employeeId FROM vehicleDetails WHERE vehicleId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, vehicleId);

        ResultSet resultSet = pstm.executeQuery();
        while (resultSet.next()) {
            employeeLsit.add(resultSet.getString("employeeId"));
        }
        return employeeLsit;
    }

    public static List<Vehicle> getAll() throws SQLException {
        String sql = "SELECT * FROM vehicle";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Vehicle> vehicleList = new ArrayList<>();

        while (resultSet.next()) {
            String id = resultSet.getString(1);
            String type = resultSet.getString(2);
            String model = resultSet.getString(3);
            String fType = resultSet.getString(4);
            String transmission = resultSet.getString(5);
            String rNumber = resultSet.getString(6);
            String status = resultSet.getString(7);

            Vehicle vehicle = new Vehicle(id, type, model, fType, transmission, rNumber, status, getEmployeeList(id));

            vehicleList.add(vehicle);
        }
        return vehicleList;
    }

    public static ObservableList<String> getAllVehicles() throws SQLException {
        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "select vehicleId from vehicle";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            items.add(resultSet.getString("vehicleId"));
        }
        return items;
    }
}
