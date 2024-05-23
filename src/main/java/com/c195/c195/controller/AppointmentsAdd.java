package com.c195.c195.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentsAdd {

    @FXML
    public Label errorMessageLabel;

    @FXML
    private TextField appointmentIDTextField;

    @FXML
    private TextField appointTitleTextField;

    @FXML
    private TextField appointDescriptionTextField;

    @FXML
    private TextField appointLocationTextField;

    @FXML
    private TextField appointTypeTextField;

    @FXML
    private ComboBox<String> contactComboAdd;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private DatePicker startDatePickerAdd;

    @FXML
    private ComboBox<String> startTimeComboAdd;

    @FXML
    private DatePicker endDatePickerAdd;

    @FXML
    private ComboBox<String> endTimeComboAdd;

    @FXML
    private ComboBox<String> userComboAdd;

    @FXML
    private ComboBox<String> customerComboAdd;
    /**
     * initialize all fxml fields
     */
    @FXML
    public void initialize() {
        try {
            Functions.establishConnection();
            populateUserComboBox();
            populateCustomerComboBox();
            populateContactComboBox();
            populateStartTimeComboBox();
            populateEndTimeComboBox();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /**
     * populate options for the end time combo box
     */
    private void populateEndTimeComboBox() {
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour);
                String minuteStr = String.format("%02d", minute);
                endTimeComboAdd.getItems().add(hourStr + ":" + minuteStr);
            }
        }
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour + 12); // Convert to PM
                String minuteStr = String.format("%02d", minute);
                endTimeComboAdd.getItems().add(hourStr + ":" + minuteStr);
            }
        }

    }
    /**
     * populate all options for start times in the start time combo boxes
     */
    private void populateStartTimeComboBox() {
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour);
                String minuteStr = String.format("%02d", minute);
                startTimeComboAdd.getItems().add(hourStr + ":" + minuteStr);
            }
        }
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour + 12); // Convert to PM
                String minuteStr = String.format("%02d", minute);
                startTimeComboAdd.getItems().add(hourStr + ":" + minuteStr);
            }
        }
    }
    /**
     * get contacts from table and populate
     */
    private void populateContactComboBox() throws SQLException {
        String query = "SELECT contact_name FROM contacts";
        List<String> contacts = new ArrayList<>();
        // Obtain connection from Functions
        Connection connection = Functions.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                contacts.add(resultSet.getString("Contact_Name"));
            }
        }

        contactComboAdd.getItems().addAll(contacts);
    }
    /**
     * get all users from table and populate
     */
    private void populateUserComboBox() throws SQLException {
        String query = "SELECT User_Name FROM users";
        List<String> users = new ArrayList<>();
        // Obtain connection from Functions
        Connection connection = Functions.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(resultSet.getString("User_Name"));
            }
        }

        userComboAdd.getItems().addAll(users);
    }
    /**
     * get all customers from table and populate
     */
    private void populateCustomerComboBox() throws SQLException {
        String query = "SELECT Customer_Name FROM customers";
        List<String> customers = new ArrayList<>();
        // Obtain connection from Functions
        Connection connection = Functions.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(resultSet.getString("Customer_Name"));
            }
        }

        customerComboAdd.getItems().addAll(customers);
    }
    /**
     * save new appointment to table
     */
    @FXML
    private void actionSaveButton(ActionEvent event) throws SQLException {
        // Obtain connection from Functions
        Connection connection = Functions.getConnection();
        // Retrieve data from UI elements
        String title = appointTitleTextField.getText();
        String description = appointDescriptionTextField.getText();
        String location = appointLocationTextField.getText();
        String type = appointTypeTextField.getText();
        String contact = contactComboAdd.getValue();
        LocalDate startDate = startDatePickerAdd.getValue();
        LocalTime startTime = LocalTime.parse(startTimeComboAdd.getValue());
        LocalDate endDate = endDatePickerAdd.getValue();
        LocalTime endTime = LocalTime.parse(endTimeComboAdd.getValue());
        String user = userComboAdd.getValue();
        String customer = customerComboAdd.getValue();

        int userId = getUserIdByName(user, connection);
        int contactId = getContactIdByName(contact, connection);
        int customerId = getCustomerIdByName(customer, connection);

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || type.isEmpty() || startTime == null || startDate == null || endDate == null || endTime == null || user.isEmpty() || customer.isEmpty() || contact == null) {
            errorMessageLabel.setText("Please fill in all required fields.");
            return;
        } else{

            // Convert local times to UTC Instant objects
            Instant startDateTimeInstant = ZonedDateTime.of(startDate, startTime, ZoneId.systemDefault()).toInstant();
            Instant endDateTimeInstant = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault()).toInstant();

            // Convert Instant objects to strings without "T" and "Z"
            String startDateTimeUTCString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startDateTimeInstant.atZone(ZoneOffset.UTC));
            String endDateTimeUTCString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endDateTimeInstant.atZone(ZoneOffset.UTC));

            // Parse the strings back to Instant objects (optional)
            Instant startDateTimeUTC = LocalDateTime.parse(startDateTimeUTCString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneOffset.UTC).toInstant();
            Instant endDateTimeUTC = LocalDateTime.parse(endDateTimeUTCString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneOffset.UTC).toInstant();

            // Convert to UTC and format as strings
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
            String startDateTimeString = formatter.format(startDateTimeInstant).replace("T", " ").replace("Z", "");
            String endDateTimeString = formatter.format(endDateTimeInstant).replace("T", " ").replace("Z", "");


            System.out.println("Customer ID: " + customerId);
            System.out.println("Formatted startTime (UTC): " + startDateTimeUTCString);
            System.out.println("Formatted endTime (UTC): " + endDateTimeUTCString);

                // Generate current timestamp in UTC
                LocalDateTime currentTime = LocalDateTime.now();
                ZoneId utcZone = ZoneId.of("UTC");
                ZonedDateTime utcTime = ZonedDateTime.of(currentTime, utcZone);

                // Format the UTC time
                String timestamp = utcTime.format(formatter);

            // Check if appointment falls within business hours (8:00 a.m. to 10:00 p.m. ET, including weekends)
            if (!isWithinBusinessHours(startDate, startTime, endDate, endTime)) {
                errorMessageLabel.setText("Appointments must be scheduled within business hours (8:00 a.m. to 10:00 p.m. ET, including weekends).");
                return;
            }

            // Check if appointment overlaps with existing appointments for the customer
            if (isAppointmentOverlapping(customerId, startDateTimeUTCString, endDateTimeUTCString)) {
                errorMessageLabel.setText("Appointment overlaps with existing appointments for the customer.");
                return;
            }

            try {

                // Query to insert a new customer
                String insertQuery = "INSERT INTO Appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                // Get the logged-in username
                String createdBy = Login.getLoggedInUsername();

                // Prepare the statement
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, type);
                preparedStatement.setString(5, startDateTimeString);
                preparedStatement.setString(6, endDateTimeString);
                preparedStatement.setTimestamp(7, Timestamp.valueOf(timestamp));
                preparedStatement.setString(8, createdBy);
                preparedStatement.setTimestamp(9, Timestamp.valueOf(timestamp));
                preparedStatement.setString(10, createdBy);
                preparedStatement.setString(11, String.valueOf(customerId));
                preparedStatement.setString(12, String.valueOf(userId));
                preparedStatement.setString(13, String.valueOf(contactId));

                // Execute the query
                preparedStatement.executeUpdate();

                // Close resources
                preparedStatement.close();
                connection.close();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Appointments.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Appointments");
                    stage.setScene(new Scene(root));
                    stage.show();


                    // Close the login window
                    cancelButton.getScene().getWindow().hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Optionally, display success message or navigate to another screen
            } catch (SQLException e) {
                // Handle SQL exception
                e.printStackTrace();
            }

        }


    }

    /**
     * check if start and end times are with in business hours
     */
    private boolean isWithinBusinessHours(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        ZonedDateTime startDateTime = ZonedDateTime.of(startDate, startTime, ZoneId.systemDefault());
        ZonedDateTime endDateTime = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault());

        // Check if day falls within Monday to Friday
        if (startDateTime.getDayOfWeek().getValue() >= DayOfWeek.MONDAY.getValue() &&
                startDateTime.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
            // Check if appointment start and end time are within business hours (8:00 a.m. to 10:00 p.m. ET)
            if (startDateTime.getHour() >= 8 && startDateTime.getHour() < 22 &&
                    endDateTime.getHour() >= 8 && endDateTime.getHour() < 22) {
                return true;
            }
        } else {
            // Check if appointment start and end time are within business hours (8:00 a.m. to 10:00 p.m. ET) on weekends
            if ((startDateTime.getDayOfWeek().getValue() == DayOfWeek.SATURDAY.getValue() ||
                    startDateTime.getDayOfWeek().getValue() == DayOfWeek.SUNDAY.getValue()) &&
                    startDateTime.getHour() >= 8 && startDateTime.getHour() < 22 &&
                    endDateTime.getHour() >= 8 && endDateTime.getHour() < 22) {
                return true;
            }
        }
        return false;
    }

    /** Method to check if appointment overlaps with existing appointments for the customer*/
    private boolean isAppointmentOverlapping(int customerId, String startString, String endString) {
        try {
            System.out.println("Checking for overlapping appointments");
            Connection connection = Functions.getConnection();

            System.out.println("isAppointmentOverLapping - customerId: " + customerId);
            System.out.println("isAppointmentOverLapping - start: " + startString);
            System.out.println("isAppointmentOverLapping - end: " + endString);

            // Query to retrieve existing appointments for the customer that overlap with the new appointment
            String query = "SELECT COUNT(*) FROM Appointments WHERE Customer_ID = ? " +
                    "AND ((Start <= ? AND End >= ?) OR (Start <= ? AND End >= ?) OR (Start < ? AND End > ?))";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, startString);
            preparedStatement.setString(3, startString);
            preparedStatement.setString(4, endString);
            preparedStatement.setString(5, endString);
            preparedStatement.setString(6, startString);
            preparedStatement.setString(7, endString);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if there are overlapping appointments
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Overlapping appointments found
                resultSet.close();
                preparedStatement.close();
                connection.close();
                System.out.println("Found an overlapping appointment");
                return true;
            }

            // No overlapping appointments found
            resultSet.close();
            preparedStatement.close();
            connection.close();
            System.out.println("Found no overlapping appointment");
            return false;
        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
            System.out.println("error occurred while looking for overlapping appointments");
            return true; // Consider it as overlapping in case of an error
        }
    }


    private void printAppointmentTimesFromDB() {
        try {
            Connection connection = Functions.getConnection();

            // Query to retrieve start and end times from the database table
            String query = "SELECT Start, End FROM Appointments";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Print out the start and end times
            while (resultSet.next()) {
                Timestamp startTimestamp = resultSet.getTimestamp("Start");
                Timestamp endTimestamp = resultSet.getTimestamp("End");
                Instant startTime = startTimestamp.toInstant();
                Instant endTime = endTimestamp.toInstant();

                System.out.println("Start Time: " + startTime);
                System.out.println("End Time: " + endTime);
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


    /**
     * get user id using user name
     */
    private int getUserIdByName(String userName, Connection connection) throws SQLException {
        // Query to get division ID by name
        String query = "SELECT User_ID FROM users WHERE user_name = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, userName);

        ResultSet resultSet = statement.executeQuery();

        int userId = -1; // Default value if not found

        if (resultSet.next()) {
            userId = resultSet.getInt("User_ID");
        }

        resultSet.close();
        statement.close();

        return userId;
    }
    /**
     * get contact id using contact name
     */
    private int getContactIdByName(String contactName, Connection connection) throws SQLException {
        // Query to get division ID by name
        String query = "SELECT Contact_ID FROM Contacts WHERE Contact_name = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, contactName);

        ResultSet resultSet = statement.executeQuery();

        int contactId = -1; // Default value if not found

        if (resultSet.next()) {
            contactId = resultSet.getInt("contact_ID");
        }

        resultSet.close();
        statement.close();

        return contactId;
    }

    /**
     * get customer id using customer name
     */
    private int getCustomerIdByName(String customerName, Connection connection) throws SQLException {
        // Query to get division ID by name
        String query = "SELECT Customer_ID FROM customers WHERE customer_name = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, customerName);

        ResultSet resultSet = statement.executeQuery();

        int customerId = -1; // Default value if not found

        if (resultSet.next()) {
            customerId = resultSet.getInt("Customer_ID");
        }

        resultSet.close();
        statement.close();

        return customerId;
    }
    /**
     * Cancel and go back to appointments screen
     */
    @FXML
    private void actionCancelButton(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Appointments.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Appointments");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            cancelButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
