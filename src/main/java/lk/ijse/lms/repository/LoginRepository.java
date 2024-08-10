package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepository {

    public static boolean isUserExist(String userId) throws SQLException {
        String sql = "SELECT * FROM user WHERE userId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, userId);

        return pstm.executeQuery().next();
    }

    public static boolean changePassword(String userId, String pass) throws SQLException {
        String sql = "UPDATE user SET password =? WHERE userId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, pass);
        pstm.setObject(2, userId);

        return pstm.executeUpdate() > 0;
    }

    public static String getEmployeeId(String userId) throws SQLException {
        String sql = "SELECT employeeId FROM user WHERE userId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1, userId);
        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String employeeId = resultSet.getString("employeeId");
            return employeeId;
        } else {
            // Handle the case where the user ID is not found
            return null;
        }
    }
}
