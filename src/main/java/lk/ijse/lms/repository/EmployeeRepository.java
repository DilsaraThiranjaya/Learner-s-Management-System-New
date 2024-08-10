package lk.ijse.lms.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Employee;

import javax.print.DocFlavor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    public static boolean save(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, employee.getId());
        pstm.setObject(2, employee.getFname());
        pstm.setObject(3, employee.getLname());
        pstm.setObject(4, employee.getDOb());
        pstm.setObject(5, employee.getGender());
        pstm.setObject(6, employee.getJoinDate());
        pstm.setObject(7, employee.getNIC());
        pstm.setObject(8, employee.getAddress());
        pstm.setObject(9, employee.getCNo());
        pstm.setObject(10, employee.getEmail());
        pstm.setObject(11, employee.getRole());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isEmployeeIdAvailable(String employeeId) throws SQLException {
        String sql = "SELECT employeeId FROM employee WHERE employeeId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, employeeId);

        return !pstm.executeQuery().next();
    }

    public static String getNextEmployeeId() throws SQLException {
        String sql = "SELECT employeeId FROM employee ORDER BY employeeId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 1); // Assuming format is "EXXX"
            int numericPart = Integer.parseInt(lastId.substring(1)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "EXXX" format
            return nextId;
        }

        return "E001";
    }

    public static boolean update(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET firstName=?, lastName=?, dateOfBirth=?, gender=?, joinDate=?, NIC=?, address=?, contactNo=?, email=?, role=? WHERE employeeId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, employee.getFname());
        pstm.setObject(2, employee.getLname());
        pstm.setObject(3, employee.getDOb());
        pstm.setObject(4, employee.getGender());
        pstm.setObject(5, employee.getJoinDate());
        pstm.setObject(6, employee.getNIC());
        pstm.setObject(7, employee.getAddress());
        pstm.setObject(8, employee.getCNo());
        pstm.setObject(9, employee.getEmail());
        pstm.setObject(10, employee.getRole());
        pstm.setObject(11, employee.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM employee WHERE employeeId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection()
                .prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static Employee searchById(String id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE employeeId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        Employee employee = null;

        if (resultSet.next()) {
            String employeeId = resultSet.getString(1);
            String fname = resultSet.getString(2);
            String lname = resultSet.getString(3);
            String dateOfBirth = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String joinDate = resultSet.getString(6);
            String nic = resultSet.getString(7);
            String address = resultSet.getString(8);
            String contactNo = resultSet.getString(9);
            String email = resultSet.getString(10);
            String role = resultSet.getString(11);

            employee = new Employee(employeeId, fname, lname, dateOfBirth, gender, joinDate, nic, address, contactNo, email, role);
        }
        return employee;
    }

    public static boolean EmployeeIdIsExist(String id) throws SQLException {
        String sql = "SELECT employeeId FROM employee WHERE employeeId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static List<Employee> getAll() throws SQLException {
        String sql = "SELECT * FROM employee";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Employee> employeeList = new ArrayList<>();

        while (resultSet.next()) {
            String employeeId = resultSet.getString(1);
            String fname = resultSet.getString(2);
            String lname = resultSet.getString(3);
            String dateOfBirth = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String joinDate = resultSet.getString(6);
            String nic = resultSet.getString(7);
            String address = resultSet.getString(8);
            String contactNo = resultSet.getString(9);
            String email = resultSet.getString(10);
            String role = resultSet.getString(11);

            Employee employee = new Employee(employeeId, fname, lname, dateOfBirth, gender, joinDate, nic, address, contactNo, email, role);

            employeeList.add(employee);
        }
        return employeeList;
    }

    public static String getEmName(String id) throws SQLException {
        String sql = "SELECT firstName, lastName FROM employee WHERE employeeId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String firstName = resultSet.getString(1);
            String lastName = resultSet.getString(2);

            return firstName + " " + lastName;
        }
        return null;
    }

    public static ObservableList<String> getAllEmployees() throws SQLException {
        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "select employeeId from employee";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            items.add(resultSet.getString("employeeId"));
        }
        return items;
    }

    public static ObservableList<String> getAllInstructors() throws SQLException {
        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "select employeeId from employee WHERE role = 'Instructor'";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            items.add(resultSet.getString("employeeId"));
        }
        return items;
    }

    public static Employee searchByCNo(String cNo) throws SQLException {
        String sql = "SELECT * FROM employee WHERE contactNo = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, cNo);
        ResultSet resultSet = pstm.executeQuery();

        Employee employee = null;

        if (resultSet.next()) {
            String employeeId = resultSet.getString(1);
            String fname = resultSet.getString(2);
            String lname = resultSet.getString(3);
            String dateOfBirth = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String joinDate = resultSet.getString(6);
            String nic = resultSet.getString(7);
            String address = resultSet.getString(8);
            String contactNo = resultSet.getString(9);
            String email = resultSet.getString(10);
            String role = resultSet.getString(11);

            employee = new Employee(employeeId, fname, lname, dateOfBirth, gender, joinDate, nic, address, contactNo, email, role);
        }
        return employee;
    }

    public static String getRole(String employeeId) throws SQLException {
        String sql = "SELECT role FROM employee WHERE employeeId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, employeeId);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String role = resultSet.getString("role");

            return role;
        }
        return null;
    }
}
