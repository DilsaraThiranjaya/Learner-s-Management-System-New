package lk.ijse.lms.db;

import com.sun.scenario.effect.Color4f;

import java.sql.*;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lms",
                "root",
                "07614"
        );
    }

    public static DBConnection getInstance() throws SQLException {
        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
