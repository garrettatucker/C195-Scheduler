package com.c195.c195.controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class Login {
    @FXML
    private Label loginTitle;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private Label labelLocation;

    @FXML
    private TextField txtFieldUserName;

    @FXML
    private PasswordField txtFieldUserPassword;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;
    private ResourceBundle bundle;

    private static String loggedInUsername; // Static variable to store the logged-in username

    // Getter method to retrieve the logged-in username
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    @FXML
    void actionLoginButton(ActionEvent event) {
        // Add cancel functionality here
        System.out.println("Cancel button clicked");

        // Get the entered username and password
        String username = txtFieldUserName.getText();
        String password = txtFieldUserPassword.getText();

        // Query to check if the user exists and the password is correct
        String loginQuery = "SELECT COUNT(*) FROM USERS WHERE User_Name = ? AND Password = ?";

        boolean loginSuccessful = false; // Declaring the loginSuccessful variable here

        try {
            // Get the database connection
            System.out.println("Connecting to the database...");
            Connection connection = getConnection();
            System.out.println("Connected to the database.");

            // Prepare the statement
            System.out.println("Preparing SQL statement...");
            PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            System.out.println("Executing SQL query...");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a user with the provided username and password exists
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Login successful
                loginSuccessful = true;
                // Store the logged-in username in the session
                loggedInUsername = username;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Menu.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Menu");
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Close the login window
                    loginButton.getScene().getWindow().hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Login failed
                System.out.println("Invalid username or password");
                ResourceBundle errorBundle;
                Locale locale = Locale.getDefault();
                if (locale.getLanguage().equals("fr")) {
                    errorBundle = ResourceBundle.getBundle("Bundle_fr");
                } else {
                    errorBundle = ResourceBundle.getBundle("Bundle_en");
                }

                // Retrieve error message from the resource bundle
                String errorMessage = errorBundle.getString("loginError");

                // Display error message
                errorMessageLabel.setText(errorMessage);
            }

            // Close the resources
            System.out.println("Closing database resources...");
            resultSet.close();
            preparedStatement.close();
            connection.close();
            System.out.println("Database resources closed.");

        } catch (SQLException e) {
            // Handle SQL exception
            System.err.println("Error: Failed to execute SQL query.");
            e.printStackTrace();
        } finally {
            // Record login attempt
            recordLoginAttempt(username, loginSuccessful);
        }
        // Check for appointments within 15 minutes of log-in
        try {
            Connection connection = getConnection();

            // Calculate 15 minutes from the current time in UTC
            ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("UTC"));
            ZonedDateTime fifteenMinutesLater = currentTime.plusMinutes(15);

            // Convert ZonedDateTime to Instant
            Instant currentTimeInstant = currentTime.toInstant();
            Instant fifteenMinutesLaterInstant = fifteenMinutesLater.toInstant();

            // Convert Instant to Timestamp
            Timestamp currentTimeTimestamp = Timestamp.from(currentTimeInstant);
            Timestamp fifteenMinutesLaterTimestamp = Timestamp.from(fifteenMinutesLaterInstant);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
            String formattedCurrentTime = formatter.format(currentTimeTimestamp.toInstant());
            String formattedFifteenMinutesLater = formatter.format(fifteenMinutesLaterTimestamp.toInstant());

            System.out.println("Formatted Current Time (UTC): " + formattedCurrentTime);
            System.out.println("Formatted Fifteen Minutes Later (UTC): " + formattedFifteenMinutesLater);




            // Query to check for appointments within 15 minutes of log-in
            String appointmentTimeQuery = "SELECT * FROM Appointments WHERE User_ID = ? AND Start BETWEEN ? AND ?";

            PreparedStatement preparedStatement = connection.prepareStatement(appointmentTimeQuery);
            preparedStatement.setInt(1, getUserIdByName(username, connection));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(formattedCurrentTime));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(formattedFifteenMinutesLater));


            ResultSet resultSet = preparedStatement.executeQuery();

            // Construct the SQL query string with parameter values replaced
            String queryString = appointmentTimeQuery.replaceFirst("\\?", String.valueOf(getUserIdByName(username, connection)))
                    .replaceFirst("\\?", "'" + formattedCurrentTime + "'")
                    .replaceFirst("\\?", "'" + formattedFifteenMinutesLater + "'");

            System.out.println("SQL Query: " + queryString);

            if (resultSet.next()) {
                // Get the appointment start time in UTC
                Timestamp startTimestamp = resultSet.getTimestamp("Start");
                LocalDateTime startDateTime = startTimestamp.toLocalDateTime();

                // Convert the start time to ZonedDateTime in UTC
                ZonedDateTime utcStartDateTime = startDateTime.atZone(ZoneOffset.UTC);

                // Convert the UTC start time to the user's local time zone
                ZoneId userZone = ZoneId.systemDefault();
                ZonedDateTime userLocalStartDateTime = utcStartDateTime.withZoneSameInstant(userZone);

                // Format the user's local start time
                String appointmentDateTime = userLocalStartDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                // Display the appointment details
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming Appointment");
                alert.setHeaderText("Upcoming Appointment");
                String appointmentId = resultSet.getString("Appointment_ID");
                alert.setContentText("You have an upcoming appointment within 15 minutes.\nAppointment ID: " + appointmentId + "\nDate and Time: " + appointmentDateTime);
                alert.showAndWait();
            } else {
                // If there are no appointments within 15 minutes, display custom message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Upcoming Appointments");
                alert.setHeaderText("No Upcoming Appointments");
                alert.setContentText("You do not have any upcoming appointments within the next 15 minutes.");
                alert.showAndWait();
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
        }
    }

    // Method to record login attempt
    private void recordLoginAttempt(String username, boolean loginSuccessful) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String status = loginSuccessful ? "Successful" : "Failed";
        String record = String.format("Date/Time: %s | Username: %s | Status: %s%n", timestamp, username, status);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("login_activity.txt", true))) {
            writer.write(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void actionCancelButton(ActionEvent event) {
        // Close the application
        Platform.exit();
    }
    @FXML
    public void initialize() {
        // Get the system default timezone
        ZoneId zoneId = ZoneId.systemDefault();
        //Locale.setDefault(Locale.FRENCH);
        // Get the system default locale
        Locale locale = Locale.getDefault();

        // Load appropriate resource bundle based on locale
        if (locale.getLanguage().equals("fr")) {
            bundle = ResourceBundle.getBundle("Bundle_fr");
        } else {
            bundle = ResourceBundle.getBundle("Bundle_en");
        }

        // Set text for labels and buttons
        loginTitle.setText(bundle.getString("loginTitle"));
        labelLocation.setText(bundle.getString("labelLocation"));
        txtFieldUserName.setPromptText(bundle.getString("txtFieldUserName"));
        txtFieldUserPassword.setPromptText(bundle.getString("txtFieldUserPassword"));
        loginButton.setText(bundle.getString("loginButton"));
        cancelButton.setText(bundle.getString("cancelButton"));

        // Set the user's timezone to the label
        labelLocation.setText(zoneId.toString());
    }
    // Method to get the database connection
    private Connection getConnection() throws SQLException {
        // Define your database connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/client_schedule";
        String dbUsername = "sqlUser";
        String dbPassword = "Passw0rd!";

        // Establish the connection
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }

    private int getUserIdByName(String username, Connection connection) throws SQLException {
        int userId = -1; // Default value if user ID is not found

        // Query to retrieve user ID by username
        String query = "SELECT User_ID FROM USERS WHERE User_Name = ?";

        // Prepare the statement
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);

        // Execute the query
        ResultSet resultSet = preparedStatement.executeQuery();

        // Check if user ID exists
        if (resultSet.next()) {
            userId = resultSet.getInt("User_ID");
        }

        // Close resources
        resultSet.close();
        preparedStatement.close();

        return userId;
    }


}