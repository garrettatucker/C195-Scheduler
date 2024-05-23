package com.c195.c195.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Functions {

    public Connection connection; // Database connection
    /** General Functions */

    /**
     * get connection to database
     */
    public static Connection getConnection() throws SQLException {
        // Define your database connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/client_schedule";
        String dbUsername = "sqlUser";
        String dbPassword = "Passw0rd!";

        // Establish the connection
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }
    /**
     * Alternative connection to database
     */
    public static void establishConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String user = "sqlUser";
        String password = "Passw0rd!";
        // Database connection
        Connection connection = DriverManager.getConnection(url, user, password);
    }

    /** Appointment Functions */



}
