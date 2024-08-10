package lk.ijse.lms.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Course;
import lk.ijse.lms.model.CourseDetails;
import lk.ijse.lms.model.Payment;
import lk.ijse.lms.model.tm.CoursePriceTm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    public static boolean saveCourseDetails(Payment payment) throws SQLException {
        String sql = "INSERT INTO courseDetails VALUES (?, ?, NULL)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        boolean isSaved = false;

        if(payment.getCp() != null){
            for(CoursePriceTm cpTm : payment.getCp()){
                String studentId = payment.getSId();
                if(studentId != null){
                    pstm.setObject(1, studentId);
                    pstm.setObject(2, getCourseId(cpTm.getCourse()));

                    isSaved = pstm.executeUpdate() > 0;
                }
            }
            return isSaved;
        }

        return false;
    }

    private static String getCourseID(String courseName) throws SQLException {
        String sql = "SELECT courseID FROM course WHERE name = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        
        pstm.setObject(1, courseName);
        ResultSet resultSet = pstm.executeQuery();
        
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return null;
    }

    public static boolean updateCourseDetails(Payment payment) throws SQLException {
        boolean isDeleted = searchAndDeleteCourses(payment);
        if(isDeleted){
            boolean isSaved = saveCourseDetails(payment);
            if(isSaved){
                return true;
            }
        }
        return false;
    }

    private static boolean searchAndDeleteCourses(Payment payment) throws SQLException {
        String sql = "SELECT courseId FROM courseDetails WHERE studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, payment.getSId());
        ResultSet resultSet = pstm.executeQuery();

        boolean isDeleted = false;

        while(resultSet.next()){
            isDeleted = DeleteCourse(resultSet.getString("courseId"), payment.getSId());
        }
        return isDeleted;
    }

    private static boolean DeleteCourse(String courseId, String id) throws SQLException {
        String sql = "DELETE FROM courseDetails WHERE courseId = ? AND studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, courseId);
        pstm.setObject(2, id);

        return pstm.executeUpdate() > 0;
    }

    public static boolean isCourseIdAvailable(String id) throws SQLException {
        String sql ="SELECT courseId FROM course WHERE courseId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return !pstm.executeQuery().next();
    }

    public static boolean save(Course course) throws SQLException {
        String sql = "INSERT INTO course VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,course.getId());
        pstm.setObject(2,course.getName());
        pstm.setObject(3,course.getDescription());
        pstm.setObject(4,course.getDuration());
        pstm.setObject(5,course.getPrice());

        return pstm.executeUpdate() > 0;
    }

    public static String getNextCourseId() throws SQLException {
        String sql ="SELECT courseId FROM course ORDER BY courseId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 1); // Assuming format is "CXXX"
            int numericPart = Integer.parseInt(lastId.substring(1)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "CXXX" format
            return nextId;
        }

        return "C001";
    }

    public static boolean isCourseIdExist(String id) throws SQLException {
        String sql ="SELECT courseId FROM course WHERE courseId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeQuery().next();
    }

    public static boolean update(Course course) throws SQLException {
        String sql = "UPDATE course SET name=?, description=?, duration=?, price=? WHERE courseId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,course.getName());
        pstm.setObject(2,course.getDescription());
        pstm.setObject(3,course.getDuration());
        pstm.setObject(4,course.getPrice());
        pstm.setObject(5,course.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean remove(String id) throws SQLException {
        String sql = "DELETE FROM course WHERE courseId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        return pstm.executeUpdate() > 0;
    }

    public static Course searchById(String id) throws SQLException {
        String sql = "SELECT * FROM course WHERE courseId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        Course course = null;

        if (resultSet.next()) {
            String courseId = resultSet.getString(1);
            String name = resultSet.getString(2);
            String description = resultSet.getString(3);
            String duration = resultSet.getString(4);
            String price = resultSet.getString(5);

            course = new Course(courseId, name, description, duration, price);
        }
        return course;
    }

    public static boolean isCourseDetailExist(CourseDetails courseDetails) throws SQLException {
        String sql ="SELECT * FROM courseDetails WHERE courseId = ? AND studentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, courseDetails.getCourseId());
        pstm.setObject(2, courseDetails.getStudentId());

        return pstm.executeQuery().next();
    }

    public static boolean updateCourseStatus(CourseDetails cd) throws SQLException {
        String sql = "UPDATE courseDetails SET status=? WHERE courseId=? AND studentId=?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,cd.getStatus());
        pstm.setObject(2,cd.getCourseId());
        pstm.setObject(3,cd.getStudentId());

        return pstm.executeUpdate() > 0;
    }

    public static List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT * FROM course";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Course> list = new ArrayList<>();

        while (resultSet.next()) {
            String courseId = resultSet.getString(1);
            String name = resultSet.getString(2);
            String description = resultSet.getString(3);
            String duration = resultSet.getString(4);
            String price = resultSet.getString(5);

            Course course = new Course(courseId, name, description, duration, price);
            list.add(course);
        }
        return list;
    }

    public static List<CourseDetails> getAllCourseDetails(String id) throws SQLException {
        String sql = "SELECT * FROM courseDetails WHERE courseId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);

        ResultSet resultSet = pstm.executeQuery();

        List<CourseDetails> list = new ArrayList<>();

        while (resultSet.next()) {
            String studentId = resultSet.getString(1);
            String status = resultSet.getString(3);

            CourseDetails cd = new CourseDetails(id, studentId, status);
            list.add(cd);
        }
        return list;
    }

    public static ObservableList<String> getCourses() throws SQLException {
        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "select name from course";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            items.add(resultSet.getString("name"));
        }
        return items;
    }

    public static String getCourseName(String courseId) throws SQLException {
        String sql = "select name from course WHERE courseId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, courseId);

        ResultSet resultSet = pstm.executeQuery();

        String name = null;

        if (resultSet.next()) {
            name = resultSet.getString("name");

            return name;
        }
        return name;
    }

    public static String getCourseId(String name) throws SQLException {
        String sql = "select courseId from course WHERE name = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, name);

        ResultSet resultSet = pstm.executeQuery();

        String id = null;

        if (resultSet.next()) {
            id = resultSet.getString("courseId");

            return id;
        }
        return id;
    }

    public static double getPrice(String selectedCourse) throws SQLException {
        String sql = "select price from course WHERE name = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, selectedCourse);

        ResultSet resultSet = pstm.executeQuery();

        double price = 0;

        if (resultSet.next()) {
            price = resultSet.getDouble("price");

            return price;
        }
        return price;
    }
}
