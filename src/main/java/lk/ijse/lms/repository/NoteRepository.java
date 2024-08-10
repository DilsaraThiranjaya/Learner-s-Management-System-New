package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Note;
import lk.ijse.lms.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NoteRepository {
    public static boolean add(Note noteObj, String userId) throws SQLException {
        String sql = "INSERT INTO note (description, userId) VALUES (?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, noteObj.getDescription());
        pstm.setObject(2, userId);

        return pstm.executeUpdate() > 0;
    }

    public static boolean deleteNoteByDescription(User user, String description) throws SQLException {
        String sql = "DELETE FROM note WHERE userId = ? AND description = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, user.getUserId());
        pstm.setString(2, description);

        return pstm.executeUpdate() > 0;
    }

    public static boolean isNoteExist(String note, String userId) throws SQLException {
        String sql = "SELECT * FROM note WHERE description = ? AND userId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, note);
        pstm.setObject(2, userId);

        return pstm.executeQuery().next();
    }
}
