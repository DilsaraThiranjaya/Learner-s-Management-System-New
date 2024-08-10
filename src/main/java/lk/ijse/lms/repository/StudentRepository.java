package lk.ijse.lms.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public static String getNextStudentId() throws SQLException {
        String sql ="SELECT studentId FROM student ORDER BY studentId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 2); // Assuming format is "STXXX"
            int numericPart = Integer.parseInt(lastId.substring(2)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "STXXX" format
            return nextId;
        }

        return "ST001";
    }


    public static boolean isStudentIdAvailable(String studentId) throws SQLException {
        String sql ="SELECT studentId FROM student WHERE studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, studentId);

        return !pstm.executeQuery().next();
    }

    public static boolean save(Student student) throws SQLException {
        String sql = "INSERT INTO student VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,student.getId());
        pstm.setObject(2,student.getFname());
        pstm.setObject(3,student.getLname());
        pstm.setObject(4,student.getDOb());
        pstm.setObject(5,student.getGender());
        pstm.setObject(6,student.getAdmissionDate());
        pstm.setObject(7,student.getNIC());
        pstm.setObject(8,student.getAddress());
        pstm.setObject(9,student.getCNo());
        pstm.setObject(10,student.getEmail());

        return pstm.executeUpdate() > 0;
    }

    public static boolean update(Student student) throws SQLException {
        String sql = "UPDATE student SET firstName = ?, lastName = ?, dateOfBirth = ?, gender = ?, admissionDate = ?, NIC = ?, address = ?, contactNo = ?, email = ? WHERE studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,student.getFname());
        pstm.setObject(2,student.getLname());
        pstm.setObject(3,student.getDOb());
        pstm.setObject(4,student.getGender());
        pstm.setObject(5,student.getAdmissionDate());
        pstm.setObject(6,student.getNIC());
        pstm.setObject(7,student.getAddress());
        pstm.setObject(8,student.getCNo());
        pstm.setObject(9,student.getEmail());
        pstm.setObject(10,student.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean StudentIdIsExist(String id) throws SQLException {
        String sql ="SELECT studentId FROM student WHERE studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM student WHERE studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static Student searchById(String id) throws SQLException {
        String sql = "SELECT * FROM student WHERE studentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        Student student = null;

        if (resultSet.next()) {
            String studentId = resultSet.getString(1);
            String fname = resultSet.getString(2);
            String lname = resultSet.getString(3);
            String dateOfBirth = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String admissionDate = resultSet.getString(6);
            String nic = resultSet.getString(7);
            String address = resultSet.getString(8);
            String contactNo = resultSet.getString(9);
            String email = resultSet.getString(10);

            student = new Student(studentId, fname, lname, dateOfBirth, gender, admissionDate, nic, address, contactNo, email);
        }
        return student;
    }

    public static Student searchByCNo(String CNo) throws SQLException {
        String sql = "SELECT * FROM student WHERE contactNo = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, CNo);
        ResultSet resultSet = pstm.executeQuery();

        Student student = null;

        if (resultSet.next()) {
            String studentId = resultSet.getString(1);
            String fname = resultSet.getString(2);
            String lname = resultSet.getString(3);
            String dateOfBirth = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String admissionDate = resultSet.getString(6);
            String nic = resultSet.getString(7);
            String address = resultSet.getString(8);
            String contactNo = resultSet.getString(9);
            String email = resultSet.getString(10);

            student = new Student(studentId, fname, lname, dateOfBirth, gender, admissionDate, nic, address, contactNo, email);
        }
        return student;
    }


    private static String getCourseName(String id) throws SQLException {
        String sql = "SELECT name FROM course WHERE courseId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        ResultSet resultSet = pstm.executeQuery();

        if(resultSet.next()) {
            return resultSet.getString(1);
        } else {
            return "Course not found";
        }
    }

    public static List<Student> getAll() throws SQLException {
        String sql = "SELECT * FROM student";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Student> studentList = new ArrayList<>();

        while (resultSet.next()) {
            String studentId = resultSet.getString(1);
            String fname = resultSet.getString(2);
            String lname = resultSet.getString(3);
            String dateOfBirth = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String admissionDate = resultSet.getString(6);
            String nic = resultSet.getString(7);
            String address = resultSet.getString(8);
            String contactNo = resultSet.getString(9);
            String email = resultSet.getString(10);

            Student student = new Student(studentId, fname, lname, dateOfBirth, gender, admissionDate, nic, address, contactNo, email);

            studentList.add(student);
        }
        return studentList;
    }

    public static String getStName(String id) throws SQLException {
        String sql = "SELECT firstName, lastName FROM student WHERE studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        ResultSet resultSet = pstm.executeQuery();

        if(resultSet.next()) {
            String firstName = resultSet.getString(1);
            String lastName = resultSet.getString(2);

            return firstName + " " + lastName;
        }
        return null;
    }

    public static ObservableList<String> getAllStudents() throws SQLException {
        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "select studentId from student";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            items.add(resultSet.getString("studentId"));
        }
        return items;
    }
}
