package com.c195.c195;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    // JDBC URL, username, and password for the database connection
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/client_schedule";
    private static final String USERNAME = "sqlUser";
    private static final String PASSWORD = "Passw0rd!";

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            // Pass the connection to the login controller
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));
            fxmlLoader.getNamespace().put("connection", connection); // Add connection to FXML namespace
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (SQLException e) {
            // Handle connection error
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}