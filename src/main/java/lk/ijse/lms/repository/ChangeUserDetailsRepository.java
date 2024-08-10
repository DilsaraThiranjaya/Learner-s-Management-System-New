package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangeUserDetailsRepository {
    public static boolean isUserIdAvailable(String userId) throws SQLException {
        String sql ="SELECT userId FROM user WHERE userId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, userId);

        return !pstm.executeQuery().next();
    }

    public static boolean update(User user, String userId) throws SQLException {
        String sql = "UPDATE user SET userName = ?, password = ?, userID = ? WHERE userId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, user.getUserName());
        pstm.setObject(2, user.getPassword());
        pstm.setObject(3, user.getUserId());
        pstm.setObject(4, userId);

        return pstm.executeUpdate() > 0;
    }
}
