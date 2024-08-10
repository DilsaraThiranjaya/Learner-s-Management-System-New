package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistorRepository {
    public static boolean registor(User user) throws SQLException {
        String sql = "INSERT INTO user VALUES(?, ?, ?, ?)";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, user.getUserId());
        pstm.setObject(2, user.getUserName());
        pstm.setObject(3, user.getPassword());
        pstm.setObject(4, user.getEmployeeId());

        return pstm.executeUpdate() > 0;
    }


    public static boolean isUserIdAvailable(String userId) throws SQLException {
        String sql ="SELECT userId FROM user WHERE userId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, userId);

        return !pstm.executeQuery().next();
    }

    public static boolean isEmployeeIdAvailable(String employeeId) throws SQLException {
        String sql ="SELECT userId FROM user WHERE employeeId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, employeeId);

        return !pstm.executeQuery().next();
    }
}
