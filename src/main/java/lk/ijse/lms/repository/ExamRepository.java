package lk.ijse.lms.repository;

import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamRepository {
    public static String getNextExamId() throws SQLException {
        String sql ="SELECT examId FROM exam ORDER BY examId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 2); // Assuming format is "EXXXX"
            int numericPart = Integer.parseInt(lastId.substring(2)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "EXXXX" format
            return nextId;
        }

        return "EX001";
    }

    public static boolean isExamIdAvailable(String id) throws SQLException {
        String sql = "SELECT examId FROM exam WHERE examId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return !pstm.executeQuery().next();
    }

    public static boolean save(Exam exam) throws SQLException {
        String sql = "INSERT INTO exam VALUES (?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, exam.getId());
        pstm.setObject(2, exam.getDate());
        pstm.setObject(3, exam.getType());
        pstm.setObject(4, exam.getDescription());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isExamIdExist(String id) throws SQLException {
        String sql = "SELECT examId FROM exam WHERE examId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static boolean update(Exam exam) throws SQLException {
        String sql = "UPDATE exam SET date=?, type=?, description=? WHERE examId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, exam.getDate());
        pstm.setObject(2, exam.getType());
        pstm.setObject(3, exam.getDescription());
        pstm.setObject(4, exam.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM exam WHERE examId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static Exam searchById(String id) throws SQLException {
        String sql = "SELECT * FROM exam WHERE examId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        Exam exam = null;

        if (resultSet.next()) {
            String examId = resultSet.getString(1);
            String date = resultSet.getString(2);
            String type = resultSet.getString(3);
            String desc = resultSet.getString(4);

            exam = new Exam(id, date, type,desc);
        }
        return exam;
    }

    public static List<Exam> getAllExams() throws SQLException {
        String sql = "SELECT * FROM exam";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Exam> list = new ArrayList<>();

        while (resultSet.next()) {
            String id = resultSet.getString(1);
            String date = resultSet.getString(2);
            String type = resultSet.getString(3);
            String desc = resultSet.getString(4);

            Exam exam = new Exam(id, date, type,desc);
            list.add(exam);
        }
        return list;
    }

    public static boolean isExamDetailAlreadyExist(ExamDetail ed) throws SQLException {
        String sql = "SELECT * FROM examDetails WHERE examId = ? AND studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ed.getExamId());
        pstm.setObject(2, ed.getStudentId());

        return pstm.executeQuery().next();
    }

    public static boolean saveExamDetail(ExamDetail ed) throws SQLException {
        String sql = "INSERT INTO examDetails VALUES (?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ed.getExamId());
        pstm.setObject(2, ed.getResult());
        pstm.setObject(3, ed.getStudentId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean updateExamDetail(ExamDetail ed) throws SQLException {
        String sql = "UPDATE examDetails SET result=? WHERE examId = ? AND studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, ed.getResult());
        pstm.setObject(2, ed.getExamId());
        pstm.setObject(3, ed.getStudentId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isExamDetailExist(String examId, String studentId) throws SQLException {
        String sql = "SELECT * FROM examDetails WHERE examId = ? AND studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, examId);
        pstm.setObject(2, studentId);

        return pstm.executeQuery().next();
    }

    public static boolean removeExamDetail(String examId, String studentId) throws SQLException {
        String sql = "DELETE FROM examDetails WHERE examId = ? AND studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, examId);
        pstm.setObject(2, studentId);

        return pstm.executeUpdate() > 0;
    }

    public static List<ExamDetail> getAllExamDetails(String id) throws SQLException {
        String sql = "SELECT * FROM examDetails WHERE examId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        ResultSet resultSet = pstm.executeQuery();

        List<ExamDetail> list = new ArrayList<>();

        while (resultSet.next()) {
            String examId = resultSet.getString(1);
            String result = resultSet.getString(2);
            String studentId = resultSet.getString(3);

            ExamDetail ed = new ExamDetail(examId, studentId, result);
            list.add(ed);
        }
        return list;
    }
}
