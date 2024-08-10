package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Course;
import lk.ijse.lms.model.CourseDetails;
import lk.ijse.lms.model.VehicleMaintenance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleManitenanceRepository {
    public static String getNextMId() throws SQLException {
        String sql ="SELECT maintenanceId FROM vehicleMaintenance ORDER BY maintenanceId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 1); // Assuming format is "MXXX"
            int numericPart = Integer.parseInt(lastId.substring(1)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "MXXX" format
            return nextId;
        }

        return "M001";
    }

    public static boolean isMIdAvailable(String id) throws SQLException {
        String sql = "SELECT maintenanceId FROM vehicleMaintenance WHERE maintenanceId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return !pstm.executeQuery().next();
    }

    public static boolean save(VehicleMaintenance vm) throws SQLException {
        String sql = "INSERT INTO vehicleMaintenance VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, vm.getMId());
        pstm.setObject(2, vm.getDescription());
        pstm.setObject(3, vm.getDate());
        pstm.setObject(4, vm.getCost());
        pstm.setObject(5, vm.getVId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isMIdExist(String id) throws SQLException {
        String sql = "SELECT maintenanceId FROM vehicleMaintenance WHERE maintenanceId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static boolean update(VehicleMaintenance vm) throws SQLException {
        String sql = "UPDATE vehicleMaintenance SET description=?, date=?, cost=?, vehicleId=? WHERE maintenanceId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, vm.getDescription());
        pstm.setObject(2, vm.getDate());
        pstm.setObject(3, vm.getCost());
        pstm.setObject(4, vm.getVId());
        pstm.setObject(5, vm.getMId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM vehicleMaintenance WHERE maintenanceId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static VehicleMaintenance searchById(String id) throws SQLException {
        String sql = "SELECT * FROM vehicleMaintenance WHERE maintenanceId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        VehicleMaintenance vm = null;

        if (resultSet.next()) {
            String mId = resultSet.getString(1);
            String desc = resultSet.getString(2);
            String date = resultSet.getString(3);
            String cost = resultSet.getString(4);
            String vId = resultSet.getString(5);

            vm = new VehicleMaintenance(mId, desc, date, cost, vId);
        }
        return vm;
    }

    public static List<VehicleMaintenance> getAllMaintenances() throws SQLException {
        String sql = "SELECT * FROM vehicleMaintenance";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<VehicleMaintenance> list = new ArrayList<>();

        while (resultSet.next()) {
            String mId = resultSet.getString(1);
            String desc = resultSet.getString(2);
            String date = resultSet.getString(3);
            String cost = resultSet.getString(4);
            String vId = resultSet.getString(5);

            VehicleMaintenance vm = new VehicleMaintenance(mId, desc, date , cost , vId);
            list.add(vm);
        }
        return list;
    }

    public static List<VehicleMaintenance> getAllMaintenanceDetails(String id) throws SQLException {
        String sql = "SELECT * FROM vehicleMaintenance WHERE vehicleId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        ResultSet resultSet = pstm.executeQuery();

        List<VehicleMaintenance> list = new ArrayList<>();

        while (resultSet.next()) {
            String mId = resultSet.getString(1);
            String desc = resultSet.getString(2);
            String date = resultSet.getString(3);
            String cost = resultSet.getString(4);

            VehicleMaintenance vm = new VehicleMaintenance(mId, desc, date , cost , id);
            list.add(vm);
        }
        return list;
    }
}
