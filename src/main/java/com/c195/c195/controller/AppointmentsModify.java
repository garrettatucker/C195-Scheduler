package com.c195.c195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class AppointmentsModify {

    @FXML
    public Label errorMessageLabel;

    @FXML
    private TextField appointmentIDTextField;

    @FXML
    private TextField appointmentTitleTextField;

    @FXML
    private TextField appointmentDescriptionTextField;

    @FXML
    private TextField appointmentLocationTextField;

    @FXML
    private TextField appointmentTypeTextField;

    @FXML
    private ComboBox<String> contactCombo;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> startTimeCombo;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> endTimeCombo;

    @FXML
    private ComboBox<String> userCombo;

    @FXML
    private ComboBox<String> customerCombo;

    private Connection connection; // Database connection

    @FXML
    public void initialize() {
        try {
            establishConnection();
            populateUserComboBox();
            populateCustomerComboBox();
            populateContactComboBox();
            populateStartTimeComboBox();
            populateEndTimeComboBox();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Method to populate appointment information into form fields*/
    public void populateAppointmentData(Appointment appointment) {
        appointmentIDTextField.setText(appointment.idProperty().get());
        appointmentTitleTextField.setText(appointment.titleProperty().get());
        appointmentDescriptionTextField.setText(appointment.descriptionProperty().get());
        appointmentLocationTextField.setText(appointment.locationProperty().get());
        appointmentTypeTextField.setText(appointment.typeProperty().get());

        // Date and time fields
        LocalDateTime startDateTime = LocalDateTime.parse(appointment.dateProperty().get());
        startDatePicker.setValue(startDateTime.toLocalDate());
        startTimeCombo.setValue(startDateTime.toLocalTime().toString());

        LocalDateTime endDateTime = LocalDateTime.parse(appointment.endDateProperty().get());
        endDatePicker.setValue(endDateTime.toLocalDate());
        endTimeCombo.setValue(endDateTime.toLocalTime().toString());

        // ComboBoxes
        contactCombo.setValue(appointment.contactProperty().get());
        userCombo.setValue(appointment.userNameProperty().get());
        customerCombo.setValue(appointment.customerNameProperty().get());

        // Other fields...
    }
    /**
     * update appointment row
     */
    @FXML
    private void actionSaveButton(ActionEvent event) throws SQLException {
        // Retrieve data from UI elements
        String title = appointmentTitleTextField.getText();
        String description = appointmentDescriptionTextField.getText();
        String location = appointmentLocationTextField.getText();
        String type = appointmentTypeTextField.getText();
        String contact = contactCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = LocalTime.parse(startTimeCombo.getValue());
        LocalDate endDate = endDatePicker.getValue();
        LocalTime endTime = LocalTime.parse(endTimeCombo.getValue());
        String user = userCombo.getValue();
        String customer = customerCombo.getValue();
        int appointment_id = Integer.parseInt(appointmentIDTextField.getText());


        int userId = getUserIdByName(user, connection);
        System.out.println("User ID: " + userId);
        int contactId = getContactIdByName(contact, connection);
        System.out.println("Contact ID: " + contactId);
        int customerId = getCustomerIdByName(customer, connection);
        System.out.println("Customer ID: " + customerId);


        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || type.isEmpty() || startTime == null || startDate == null || endDate == null || endTime == null || user.isEmpty() || customer.isEmpty() || contact == null) {
            errorMessageLabel.setText("Please fill in all required fields.");
            return;
        } else{

            // Convert local times to UTC Instant objects
            Instant startInstant = ZonedDateTime.of(startDate, startTime, ZoneId.systemDefault()).toInstant();
            Instant endInstant = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault()).toInstant();

            // Format Instant objects as Strings in the expected format in UTC time zone
            String startDateTimeString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startInstant.atZone(ZoneOffset.UTC));
            String endDateTimeString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endInstant.atZone(ZoneOffset.UTC));

            System.out.println("Formatted Current Time (UTC): " + startDateTimeString);
            System.out.println("Formatted Fifteen Minutes Later (UTC): " + endDateTimeString);

            // Generate current timestamp in UTC
            LocalDateTime currentTime = LocalDateTime.now();
            ZoneId utcZone = ZoneId.of("UTC");
            ZonedDateTime utcTime = ZonedDateTime.of(currentTime, utcZone);

            // Format the UTC time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = utcTime.format(formatter);


            // Check if appointment falls within business hours (8:00 a.m. to 10:00 p.m. ET, including weekends)
            if (!isWithinBusinessHours(startDate, startTime, endDate, endTime)) {
                errorMessageLabel.setText("Appointments must be scheduled within business hours (8:00 a.m. to 10:00 p.m. ET, including weekends).");
                return;
            }

            try {
                Connection connection = getConnection();

                // Query to update appointment
                String updateQuery = "UPDATE Appointments " +
                        "SET Title = ?, " +
                        "Description = ?, " +
                        "Location = ?, " +
                        "Type = ?, " +
                        "Start = ?, " +
                        "End = ?, " +
                        "Last_Update = ?, " +
                        "Last_Updated_By = ?, " +
                        "Customer_ID = ?, " +
                        "User_ID = ? , " +
                        "Contact_ID = ? " +
                        "WHERE Appointment_ID = ?";

                // Get the logged-in username
                String createdBy = Login.getLoggedInUsername();

                // Prepare the statement
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, type);
                preparedStatement.setString(5, startDateTimeString);
                preparedStatement.setString(6, endDateTimeString);
                preparedStatement.setTimestamp(7, Timestamp.valueOf(timestamp));
                preparedStatement.setString(8, createdBy);
                preparedStatement.setString(9, String.valueOf(customerId));
                preparedStatement.setString(10, String.valueOf(userId));
                preparedStatement.setString(11, String.valueOf(contactId));
                preparedStatement.setInt(12, appointment_id);

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

    /** Method to check if appointment falls within business hours*/
    private boolean isWithinBusinessHours(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        ZonedDateTime startDateTime = ZonedDateTime.of(startDate, startTime, ZoneId.systemDefault());
        ZonedDateTime endDateTime = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault());

        /** Check if day falls within Monday to Friday */
        if (startDateTime.getDayOfWeek().getValue() >= DayOfWeek.MONDAY.getValue() &&
                startDateTime.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
            /** Check if appointment start and end time are within business hours (8:00 a.m. to 10:00 p.m. ET)*/
            if (startDateTime.getHour() >= 8 && startDateTime.getHour() < 22 &&
                    endDateTime.getHour() >= 8 && endDateTime.getHour() < 22) {
                return true;
            }
        } else {
            /** Check if appointment start and end time are within business hours (8:00 a.m. to 10:00 p.m. ET) on weekends */
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
    private boolean isAppointmentOverlapping(int customerId, Instant startInstant, Instant endInstant) {
        try {
            Connection connection = getConnection();

            // Query to retrieve existing appointments for the customer that overlap with the new appointment
            String query = "SELECT COUNT(*) FROM Appointments WHERE Customer_ID = ? " +
                    "AND ((Start <= ? AND End >= ?) OR (Start <= ? AND End >= ?))";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customerId);
            preparedStatement.setTimestamp(2, Timestamp.from(startInstant));
            preparedStatement.setTimestamp(3, Timestamp.from(startInstant));
            preparedStatement.setTimestamp(4, Timestamp.from(endInstant));
            preparedStatement.setTimestamp(5, Timestamp.from(endInstant));

            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if there are overlapping appointments
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Overlapping appointments found
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return true;
            }

            // No overlapping appointments found
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return false;
        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
            return true; // Consider it as overlapping in case of an error
        }
    }
    /**
     * get user id by name
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
     * get contact id by name
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
     * get customer id by name
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
     * return to appointments page
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
    /**
     * get times and populate box
     */
    private void populateEndTimeComboBox() {
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour);
                String minuteStr = String.format("%02d", minute);
                endTimeCombo.getItems().add(hourStr + ":" + minuteStr);
            }
        }
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour + 12); // Convert to PM
                String minuteStr = String.format("%02d", minute);
                endTimeCombo.getItems().add(hourStr + ":" + minuteStr);
            }
        }

    }
    /**
     * get times and populate box
     */
    private void populateStartTimeComboBox() {
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour);
                String minuteStr = String.format("%02d", minute);
                startTimeCombo.getItems().add(hourStr + ":" + minuteStr);
            }
        }
        for (int hour = 0; hour <= 11; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour + 12); // Convert to PM
                String minuteStr = String.format("%02d", minute);
                startTimeCombo.getItems().add(hourStr + ":" + minuteStr);
            }
        }
    }
    /**
     * get contacts and populate box
     */
    private void populateContactComboBox() throws SQLException {
        String query = "SELECT contact_name FROM contacts";
        List<String> contacts = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                contacts.add(resultSet.getString("Contact_Name"));
            }
        }

        contactCombo.getItems().addAll(contacts);
    }
    /**
     * get users and populate box
     */
    private void populateUserComboBox() throws SQLException {
        String query = "SELECT User_Name FROM users";
        List<String> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(resultSet.getString("User_Name"));
            }
        }

        userCombo.getItems().addAll(users);
    }
    /**
     * get customers and populate box
     */
    private void populateCustomerComboBox() throws SQLException {
        String query = "SELECT Customer_Name FROM customers";
        List<String> customers = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(resultSet.getString("Customer_Name"));
            }
        }

        customerCombo.getItems().addAll(customers);
    }
    /**
     * connect to db
     */
    private void establishConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String user = "sqlUser";
        String password = "Passw0rd!";
        connection = DriverManager.getConnection(url, user, password);
    }
    /**
     * connect to db
     */
    private Connection getConnection() throws SQLException {
        // Define your database connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/client_schedule";
        String dbUsername = "sqlUser";
        String dbPassword = "Passw0rd!";

        // Establish the connection
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }

}
