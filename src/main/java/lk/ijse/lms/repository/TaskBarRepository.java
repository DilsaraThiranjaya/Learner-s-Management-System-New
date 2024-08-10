package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Note;
import lk.ijse.lms.model.Student;
import lk.ijse.lms.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskBarRepository {
    public static List<Note> getAll(User user) throws SQLException {
        String sql = "SELECT description FROM note WHERE userId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, user.getUserId());

        ResultSet resultSet = pstm.executeQuery();

        List<Note> noteList = new ArrayList<>();

        while (resultSet.next()) {
            String description = resultSet.getString(1);

            Note note = new Note(description);

            noteList.add(note);
        }
        return noteList;
    }
}
