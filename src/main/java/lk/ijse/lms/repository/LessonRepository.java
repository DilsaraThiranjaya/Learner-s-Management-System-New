package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.LessonSchedule;
import lk.ijse.lms.model.LessonScheduleDetails;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LessonRepository {
    public static String getNextLId() throws SQLException {
        String sql ="SELECT lessonId FROM lessonSchedule ORDER BY lessonId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 1); // Assuming format is "LXXX"
            int numericPart = Integer.parseInt(lastId.substring(1)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "LXXX" format
            return nextId;
        }

        return "L001";
    }

    public static boolean isLIdAvailable(String id) throws SQLException {
        String sql = "SELECT lessonId FROM lessonSchedule WHERE lessonId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return !pstm.executeQuery().next();
    }

    public static boolean save(LessonSchedule ls) throws SQLException {
        String sql = "INSERT INTO lessonSchedule VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ls.getLessonId());
        pstm.setObject(2, ls.getDate());
        pstm.setObject(3, ls.getTime());
        pstm.setObject(4, ls.getEmployeeId());
        pstm.setObject(5, ls.getCourseId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isLIdExist(String id) throws SQLException {
        String sql = "SELECT lessonId FROM lessonSchedule WHERE lessonId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static boolean update(LessonSchedule ls) throws SQLException {
        String sql = "UPDATE lessonSchedule SET date=?, time=?, employeeId=?, courseId=? WHERE lessonId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ls.getDate());
        pstm.setObject(2, ls.getTime());
        pstm.setObject(3, ls.getEmployeeId());
        pstm.setObject(4, ls.getCourseId());
        pstm.setObject(5, ls.getLessonId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM lessonSchedule WHERE lessonId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static LessonSchedule searchById(String courseId, String dateS, String timeS) throws SQLException {
        String sql = "SELECT * FROM lessonSchedule WHERE courseId = ? AND date = ? AND time = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, courseId);
        pstm.setObject(2, dateS);
        pstm.setObject(3, timeS);

        ResultSet resultSet = pstm.executeQuery();

        LessonSchedule ls = null;

        if (resultSet.next()) {
            String lId = resultSet.getString(1);
            String date = resultSet.getString(2);
            String time = resultSet.getString(3);
            String eId = resultSet.getString(4);
            String cId = resultSet.getString(5);

            ls = new LessonSchedule(lId, date, time, eId, cId);
        }
        return ls;
    }

    public static boolean saveLessonDetails(LessonScheduleDetails ld) throws SQLException {
        String sql = "INSERT INTO lessonDetails VALUES (?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ld.getLessonId());
        pstm.setObject(2, ld.getStudentId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean removeLessonDetail(LessonScheduleDetails ld) throws SQLException {
        String sql = "DELETE FROM lessonDetails WHERE lessonId = ? AND studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ld.getLessonId());
        pstm.setObject(2, ld.getStudentId());

        return pstm.executeUpdate() > 0;
    }

    public static List<LessonSchedule> getAllLessons() throws SQLException {
        String sql = "SELECT * FROM lessonSchedule";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<LessonSchedule> list = new ArrayList<>();

        while (resultSet.next()) {
            String lId = resultSet.getString(1);
            String date = resultSet.getString(2);
            String time = resultSet.getString(3);
            String eId = resultSet.getString(4);
            String cId = resultSet.getString(5);

            LessonSchedule ls = new LessonSchedule(lId, date, time, eId, cId);
            list.add(ls);
        }
        return list;
    }

    public static List<LessonScheduleDetails> getAllLessonDetails(String lessonId) throws SQLException {
        String sql = "SELECT * FROM lessonDetails WHERE lessonId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, lessonId);

        ResultSet resultSet = pstm.executeQuery();

        List<LessonScheduleDetails> list = new ArrayList<>();

        while (resultSet.next()) {
            String lId = resultSet.getString(1);
            String sId = resultSet.getString(2);

            LessonScheduleDetails ld = new LessonScheduleDetails(lId, sId);
            list.add(ld);
        }
        return list;
    }

    public static boolean isSidAvailable(String lessonId, String studentId) throws SQLException {
        String sql = "SELECT * FROM lessonDetails WHERE studentId = ? AND lessonId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, studentId);
        pstm.setObject(2, lessonId);

        return !pstm.executeQuery().next();
    }

    public static boolean isScheduleAvailable(LessonSchedule ls) throws SQLException {
        String sql = "SELECT * FROM lessonSchedule WHERE courseId = ? AND date = ? AND time = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ls.getCourseId());
        pstm.setObject(2, ls.getDate());
        pstm.setObject(3, ls.getTime());

        return !pstm.executeQuery().next();
    }

    public static LessonSchedule searchById(String lessonId) throws SQLException {
        String sql = "SELECT * FROM lessonSchedule WHERE lessonId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, lessonId);

        ResultSet resultSet = pstm.executeQuery();

        LessonSchedule ls = null;

        if (resultSet.next()) {
            String lId = resultSet.getString(1);
            String date = resultSet.getString(2);
            String time = resultSet.getString(3);
            String eId = resultSet.getString(4);
            String cId = resultSet.getString(5);

            ls = new LessonSchedule(lId, date, time, eId, cId);
        }
        return ls;
    }

    public static boolean isLessonDetailExist(String lessonId, String studentId) throws SQLException {
        String sql = "SELECT * FROM lessonDetails WHERE lessonId = ? AND studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, lessonId);
        pstm.setObject(2, studentId);

        return pstm.executeQuery().next();
    }
}
