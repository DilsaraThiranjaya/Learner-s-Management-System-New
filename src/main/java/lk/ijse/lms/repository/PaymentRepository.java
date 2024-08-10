package lk.ijse.lms.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.lms.db.DBConnection;
import lk.ijse.lms.model.Payment;
import lk.ijse.lms.model.Student;
import lk.ijse.lms.model.tm.CoursePriceTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    public static String getNextPaymentId() throws SQLException {
        String sql ="SELECT paymentId FROM payment ORDER BY paymentId DESC LIMIT 1;";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String prefix = lastId.substring(0, 1); // Assuming format is "PXXX"
            int numericPart = Integer.parseInt(lastId.substring(1)); // Extract numeric part
            int nextNumericPart = numericPart + 1;
            String nextId = prefix + String.format("%03d", nextNumericPart); // Format back to "PXXX" format
            return nextId;
        }

        return "P001";
    }

    public static boolean isPaymentIdAvailable(String paymnetId) throws SQLException {
        String sql ="SELECT paymentId FROM payment WHERE paymentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, paymnetId);

        return !pstm.executeQuery().next();
    }

    public static boolean save(Payment payment) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isPaymentSaved = savePaymnet(payment);
            if (isPaymentSaved) {
                boolean isPaymentDetailsSaved = savePaymentDetails(payment);
                if (isPaymentDetailsSaved) {
                    boolean isCourseDetailsSaved = CourseRepository.saveCourseDetails(payment);
                    if (isCourseDetailsSaved) {
                        connection.commit();
                        return true;
                    }
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static boolean savePaymentDetails(Payment payment) throws SQLException {
        String sql = "INSERT INTO paymentDetails VALUES (?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        boolean isSaved = false;

        if(payment.getCp() != null){
            for(CoursePriceTm cpTm : payment.getCp()){
                String paymentId = payment.getPId();
                if(paymentId != null){
                    pstm.setObject(1, paymentId);
                    pstm.setObject(2, CourseRepository.getCourseId(cpTm.getCourse()));

                    isSaved = pstm.executeUpdate() > 0;
                }
            }
            return isSaved;
        }

        return false;
    }

    private static boolean savePaymnet(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,payment.getPId());
        pstm.setObject(2,payment.getDesc());
        pstm.setObject(3,payment.getDate());
        pstm.setObject(4,payment.getMethod());
        pstm.setObject(5,payment.getAmount());
        pstm.setObject(6,payment.getSId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean isPaymentIdExist(String paymnetId) throws SQLException {
        String sql ="SELECT paymentId FROM payment WHERE paymentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, paymnetId);

        return pstm.executeQuery().next();
    }

    public static boolean update(Payment payment) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isPaymentUpdated = updatePaymnet(payment);
            if (isPaymentUpdated) {
                boolean isPaymentDetailsUpdated = updatePaymentDetails(payment);
                if (isPaymentDetailsUpdated) {
                    boolean isCourseDetailsUpdated = CourseRepository.updateCourseDetails(payment);
                    if (isCourseDetailsUpdated) {
                        connection.commit();
                        return true;
                    }
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static boolean updatePaymentDetails(Payment payment) throws SQLException {
        boolean isDeleted = searchAndDeletePaymentDetails(payment);
        if(isDeleted){
            boolean isSaved = savePaymentDetails(payment);
            if(isSaved){
                return true;
            }
        }
        return false;
    }

    private static boolean searchAndDeletePaymentDetails(Payment payment) throws SQLException {
        String sql = "SELECT courseId FROM paymentDetails WHERE paymentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, payment.getPId());
        ResultSet resultSet = pstm.executeQuery();

        boolean isDeleted = false;

        while(resultSet.next()){
            isDeleted = DeletePaymentDetail(resultSet.getString("courseId"), payment.getPId());
        }
        return isDeleted;
    }

    private static boolean DeletePaymentDetail(String courseId, String pId) throws SQLException {
        String sql = "DELETE FROM paymentDetails WHERE courseId = ? AND paymentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, courseId);
        pstm.setObject(2, pId);

        return pstm.executeUpdate() > 0;
    }

    private static boolean updatePaymnet(Payment payment) throws SQLException {
        String sql = "UPDATE payment SET description = ?, method = ?, amount = ?, studentId = ? WHERE paymentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,payment.getDesc());
        pstm.setObject(2,payment.getMethod());
        pstm.setObject(3,payment.getAmount());
        pstm.setObject(4,payment.getSId());
        pstm.setObject(5,payment.getPId());

        return pstm.executeUpdate() > 0;
    }

    public static Payment searchById(String id) throws SQLException {
        String sql = "SELECT * FROM payment WHERE paymentId = ?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, id);
        ResultSet resultSet = pstm.executeQuery();

        Payment payment = null;

        if (resultSet.next()) {
            String paymnetId = resultSet.getString(1);
            String desc = resultSet.getString(2);
            String date = resultSet.getString(3);
            String pMethod = resultSet.getString(4);
            String amount = resultSet.getString(5);
            String studentId = resultSet.getString(6);

            payment = new Payment(paymnetId, desc, date, pMethod, amount, studentId, getPaymentDetails(paymnetId));
        }
        return payment;
    }

    private static ObservableList<CoursePriceTm> getPaymentDetails(String pId) throws SQLException {
        String sql ="SELECT courseId FROM paymentDetails WHERE paymentId = ?";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1, pId);

        ObservableList<CoursePriceTm> list = FXCollections.observableArrayList();

        ResultSet resultSet = pstm.executeQuery();

        while(resultSet.next()){
            CoursePriceTm coursePriceTm = new CoursePriceTm(
                    CourseRepository.getCourseName(resultSet.getString("courseId")),
                    CourseRepository.getPrice(CourseRepository.getCourseName(resultSet.getString("courseId")))
            );
            list.add(coursePriceTm);
        }
        return list;
    }

    public static List<Payment> getAllPayments() throws SQLException {
        String sql = "SELECT * FROM payment";

        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Payment> paymentList = new ArrayList<>();

        while (resultSet.next()) {
            String paymnetId = resultSet.getString(1);
            String desc = resultSet.getString(2);
            String date = resultSet.getString(3);
            String pMethod = resultSet.getString(4);
            String amount = resultSet.getString(5);
            String studentId = resultSet.getString(6);

            Payment payment = new Payment(paymnetId, desc, date, pMethod, amount, studentId, getPaymentDetails(paymnetId));

            paymentList.add(payment);
        }
        return paymentList;
    }
}
