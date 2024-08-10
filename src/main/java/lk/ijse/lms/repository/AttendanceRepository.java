package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Attendance;
import lk.ijse.lms.model.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {
    public static boolean isDateAvailable(Attendance attendance) throws SQLException {
        String columnName = "";
        if (attendance.getRole().equalsIgnoreCase("Employee")) {
            columnName = "employeeId";
        } else {
            columnName = "studentId";
        }

        String sql = "SELECT date FROM attendance WHERE date = ? AND " + columnName + " = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, attendance.getDate());
        pstm.setObject(2, attendance.getId());

        ResultSet resultSet = pstm.executeQuery();
        return !resultSet.next(); // Return true if date is available, false otherwise
    }


    public static boolean save(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (date, inTime, outTime, status, employeeId, studentId) VALUES (?, ?, ?, ?, "
                + "CASE WHEN ? = 'Employee' THEN ? ELSE NULL END, "
                + "CASE WHEN ? = 'Student' THEN ? ELSE NULL END)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, attendance.getDate());
        pstm.setObject(2, attendance.getInTime());
        pstm.setObject(3, attendance.getOutTime());
        pstm.setObject(4, attendance.getStatus());
        pstm.setObject(5, attendance.getRole());
        pstm.setObject(6, attendance.getId());
        pstm.setObject(7, attendance.getRole());
        pstm.setObject(8, attendance.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isEmployeeOrStudentExist(Attendance attendance) throws SQLException {
        if (attendance.getRole().equalsIgnoreCase("Employee")) {
            return EmployeeRepository.EmployeeIdIsExist(attendance.getId());
        } else if (attendance.getRole().equalsIgnoreCase("Student")) {
            return StudentRepository.StudentIdIsExist(attendance.getId());
        } else {
            // Handle invalid role value
            throw new IllegalArgumentException("Invalid role value: " + attendance.getRole());
        }
    }

    public static boolean isAttendanceExist(String id, String date, String role) throws SQLException {
        String columnName = "";
        if (role.equalsIgnoreCase("Employee")) {
            columnName = "employeeId";
        } else {
            columnName = "studentId";
        }

        String sql = "SELECT * FROM attendance WHERE " + columnName + " = ? AND date = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        pstm.setObject(2, date);

        ResultSet resultSet = pstm.executeQuery();
        return resultSet.next(); // If a row exists, attendance exists; otherwise, it does not.
    }


    public static boolean remove(String id, String date, String role) throws SQLException {
        String sql = "";
        if (role.equalsIgnoreCase("Employee")) {
            sql = "DELETE FROM attendance WHERE employeeId = ? AND date = ?";
        } else {
            sql = "DELETE FROM attendance WHERE studentId = ? AND date = ?";
        }

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        pstm.setObject(2, date);

        return pstm.executeUpdate() > 0;
    }


    public static boolean update(Attendance attendance) throws SQLException {
        String sql = "UPDATE attendance SET date = ?, inTime = ?, outTime = ?, status = ? WHERE ";

        // Determine which column to use based on role
        if (attendance.getRole().equalsIgnoreCase("Employee")) {
            sql += "employeeId = ?";
        } else {
            sql += "studentId = ?";
        }

        sql += " AND date = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, attendance.getDate());
        pstm.setObject(2, attendance.getInTime());
        pstm.setObject(3, attendance.getOutTime());
        pstm.setObject(4, attendance.getStatus());

        // Set the appropriate ID based on role
        pstm.setObject(5, attendance.getId());
        pstm.setObject(6, attendance.getDate());

        return pstm.executeUpdate() > 0;
    }


    public static Attendance searchById(String id, String date, String role) throws SQLException {
        String sql = "";
        if (role.equalsIgnoreCase("Employee")) {
            sql = "SELECT * FROM attendance WHERE employeeId = ? AND date = ?";
        } else {
            sql = "SELECT * FROM attendance WHERE studentId = ? AND date = ?";
        }

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        pstm.setObject(2, date);

        ResultSet resultSet = pstm.executeQuery();

        Attendance attendance = null;
        if (resultSet.next()) {
            String attendanceDate = resultSet.getString("date");
            String inTime = resultSet.getString("inTime");
            String outTime = resultSet.getString("outTime");
            String status = resultSet.getString("status");
            String Id;

            if (role.equalsIgnoreCase("Employee")) {
                Id = resultSet.getString("employeeId");
            } else {
                Id = resultSet.getString("studentId");
            }

            attendance = new Attendance(role, attendanceDate, inTime, outTime, status, Id);
        }
        return attendance;
    }

    public static List<Attendance> getAllEmAttendance() throws SQLException {
        String sql = "SELECT * FROM attendance WHERE studentId IS NULL ORDER BY date"; // Corrected SQL query

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Attendance> list = new ArrayList<>();

        while (resultSet.next()) {
            String date = resultSet.getString("date"); // Using column names
            String inTime = resultSet.getString("inTime");
            String outTime = resultSet.getString("outTime");
            String status = resultSet.getString("status");
            String id = resultSet.getString("employeeId"); // Corrected column name
            String role = "Employee";

            // Check if employeeId is not null
            if (id != null) {
                Attendance attendance = new Attendance(role, date, inTime, outTime, status, id);
                list.add(attendance);
            }
        }
        return list;
    }


    public static List<Attendance> getAllStAttendance() throws SQLException {
        String sql = "SELECT * FROM attendance WHERE employeeId IS NULL ORDER BY date"; // Corrected SQL query

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Attendance> list = new ArrayList<>();

        while (resultSet.next()) {
            String date = resultSet.getString("date"); // Using column names
            String inTime = resultSet.getString("inTime");
            String outTime = resultSet.getString("outTime");
            String status = resultSet.getString("status");
            String id = resultSet.getString("studentId"); // Corrected column name
            String role = "Student";

            // Check if studentId is not null
            if (id != null) {
                Attendance attendance = new Attendance(role, date, inTime, outTime, status, id);
                list.add(attendance);
            }
        }
        return list;
    }

}
