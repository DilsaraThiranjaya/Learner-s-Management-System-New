package lk.ijse.lms.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.LessonSchedule;
import lk.ijse.lms.model.Salary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaryRepository {
    public static ObservableList<String> getAllPaymentMonths() throws SQLException {
        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "select monthOfPay from salary";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            items.add(resultSet.getString("monthOfPay"));
        }
        return items;
    }

    public static boolean isSalaryAvailable(Salary salary) throws SQLException {
        String sql = "SELECT * FROM salary WHERE monthOfPay = ? AND employeeId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, salary.getMonthOfPay());
        pstm.setObject(2, salary.getEmployeeId());

        return !pstm.executeQuery().next();
    }

    public static boolean save(Salary salary) throws SQLException {
        String sql = "INSERT INTO salary (monthOfPay, date, basicPayment, OTPayment, employeeId) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, salary.getMonthOfPay());
        pstm.setObject(2, salary.getDate());
        pstm.setObject(3, salary.getBasicP());
        pstm.setObject(4, salary.getOtP());
        pstm.setObject(5, salary.getEmployeeId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isSalaryExist(String id, String mop) throws SQLException {
        String sql = "SELECT * FROM salary WHERE monthOfPay = ? AND employeeId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, mop);
        pstm.setObject(2, id);

        return pstm.executeQuery().next();
    }

    public static boolean update(Salary salary) throws SQLException {
        String sql = "UPDATE salary SET date=?, basicPayment=?, OTPayment=? WHERE employeeId=? AND monthOfPay=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, salary.getDate());
        pstm.setObject(2, salary.getBasicP());
        pstm.setObject(3, salary.getOtP());
        pstm.setObject(4, salary.getEmployeeId());
        pstm.setObject(5, salary.getMonthOfPay());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id, String mop) throws SQLException {
        String sql = "DELETE FROM salary WHERE employeeId=? AND monthOfPay=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        pstm.setObject(2, mop);

        return pstm.executeUpdate() > 0;
    }

    public static Salary searchById(String id, String mop) throws SQLException {
        String sql = "SELECT * FROM salary WHERE employeeId=? AND monthOfPay=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        pstm.setObject(2, mop);
        ResultSet resultSet = pstm.executeQuery();

        Salary salary = null;

        if (resultSet.next()) {
            String monthOfPay = resultSet.getString(2);
            String date = resultSet.getString(3);
            String basicP = resultSet.getString(4);
            String otP = resultSet.getString(5);
            String eId = resultSet.getString(6);

            salary = new Salary(monthOfPay, date, basicP, otP, eId);
        }
        return salary;
    }

    public static List<Salary> getAllSalaries() throws SQLException {
        String sql = "SELECT * FROM salary";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Salary> list = new ArrayList<>();

        while (resultSet.next()) {
            String monthOfPay = resultSet.getString(2);
            String date = resultSet.getString(3);
            String basicP = resultSet.getString(4);
            String otP = resultSet.getString(5);
            String eId = resultSet.getString(6);

            Salary salary = new Salary(monthOfPay, date, basicP, otP, eId);
            list.add(salary);
        }
        return list;
    }
}
