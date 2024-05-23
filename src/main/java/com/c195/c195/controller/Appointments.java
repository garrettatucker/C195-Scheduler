package com.c195.c195.controller;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Appointments {

    @FXML
    private TableView<Appointment> appointTable;

    @FXML
    private TableColumn<String, String> appointIdCol;

    @FXML
    private TableColumn<String, String> appointTitleCol;

    @FXML
    private TableColumn<String, String> appointDescriptionCol;

    @FXML
    private TableColumn<String, String> appointLocationCol;

    @FXML
    private TableColumn<String, String> appointContactCol;

    @FXML
    private TableColumn<String, String> appointTypeCol;

    @FXML
    private TableColumn<String, String> appointDateCol;

    @FXML
    private TableColumn<String, String> appointEndDateCol;
    @FXML
    private TableColumn<String, String> appointCustNameCol;

    @FXML
    private TableColumn<String, String> appointUserNameCol;


    @FXML
    private TableColumn<Customer, String> appointCreateDateCol;
    @FXML
    private TableColumn<Customer, String> appointCreatedByCol;
    @FXML
    private TableColumn<Customer, String> appointLastUpdateCol;
    @FXML
    private TableColumn<Customer, String> appointLastUpdatedByCol;

    @FXML
    private RadioButton allAppointments;

    @FXML
    private RadioButton monthlyAppointments;

    @FXML
    private RadioButton weeklyAppointments;

    @FXML
    private ToggleGroup appointmentView;

    @FXML
    private Button backToMenu;
    List<Appointment> appointmentsData = new ArrayList<>();

    /**
     * Initialize FXML fields
     */
    public void initialize() {
        // Initialize table columns
        appointIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        appointEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        appointCustNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        appointUserNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        appointCreateDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        appointCreatedByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        appointLastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        appointLastUpdatedByCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));

        // Load appointments from the database
        loadAppointments();
    }
    /**
     * Load table view with all appointments
     */
    private void loadAppointments() {

        // Clear the list of appointments data before loading new data
        appointmentsData.clear();

        try {
            Connection connection = Functions.getConnection(); // Implement your own getConnection() method
            String query = "SELECT a.appointment_id, a.title, a.description, a.location, a.contact_id, a.type, a.start, a.end, a.customer_id, a.user_id, a.create_Date, a.created_By, a.last_update, a.last_updated_by, " +
                    "c.customer_name AS customer_name, u.user_name AS user_name, con.contact_name AS contact_name " +
                    "FROM appointments a " +
                    "JOIN customers c ON a.customer_id = c.customer_id " +
                    "JOIN users u ON a.user_id = u.user_id " +
                    "JOIN contacts con ON a.contact_id = con.contact_id";


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                // Parse UTC datetime to local datetime
                LocalDateTime startLocalDateTime = parseToLocalDateTime(resultSet.getString("start"));
                LocalDateTime endLocalDateTime = parseToLocalDateTime(resultSet.getString("end"));
                LocalDateTime createDateLocalDateTime = parseToLocalDateTime(resultSet.getString("create_date"));
                LocalDateTime lastUpdateLocalDateTime = parseToLocalDateTime(resultSet.getString("last_update"));

                Appointment appointment = new Appointment(
                        resultSet.getString("appointment_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("location"),
                        resultSet.getString("contact_name"),
                        resultSet.getString("type"),
                        startLocalDateTime.toString(), // Convert LocalDateTime to String
                        endLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("customer_id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("user_name"),
                        resultSet.getString("contact_name"),
                        createDateLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("created_by"),
                        lastUpdateLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("last_updated_by")
                        // Add additional parameters if needed for the Appointment constructor
                );
                appointmentsData.add(appointment);
            }
            // Populate the table with data
            ObservableList<Appointment> observableAppointmentsData = FXCollections.observableArrayList(appointmentsData);
            appointTable.setItems(observableAppointmentsData);

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Convert utc to local user time
     */
    private LocalDateTime parseToLocalDateTime(String utcDateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime utcDateTime = ZonedDateTime.parse(utcDateTimeStr, formatter.withZone(ZoneId.of("UTC")));

        // Get the user's default time zone
        ZoneId userZone = ZoneId.systemDefault();

        // Convert UTC time to user's local time
        ZonedDateTime userZonedDateTime = utcDateTime.withZoneSameInstant(userZone);

        // Return the local date and time
        return userZonedDateTime.toLocalDateTime();
    }

    /**
     * action to delete row from db
     */
    @FXML
    void actionAppointDelete(ActionEvent event) {
        Appointment selectedAppointment = appointTable.getSelectionModel().getSelectedItem();
        StringProperty appointmentId = selectedAppointment.getId();
        String appointmentIdStr = appointmentId.get(); // Retrieve the value from StringProperty
        int appointmentIdInt = Integer.parseInt(appointmentIdStr);
        String appointmentType = selectedAppointment.typeProperty().get(); // Retrieve the value from StringProperty

        if (selectedAppointment != null) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setHeaderText("Delete Appointment Confirmation");
                confirmationAlert.setContentText("Are you sure you want to delete this appointment? ID: " +
                    appointmentId.get() + " Type: " + appointmentType + "?");
                Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

                if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                    try {
                        Connection connection = Functions.getConnection();
                        String deleteQuery = "DELETE FROM appointments WHERE appointment_id = ?";
                        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                        deleteStatement.setInt(1, appointmentIdInt);
                        deleteStatement.executeUpdate();

                        // Remove from ObservableList
                        appointmentsData.remove(selectedAppointment);
                        appointTable.refresh();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    /**
     * loads form to add new appoint to table
     */
    @FXML
    void actionAppointAdd(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/AppointmentsAdd.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("New Appointment");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            backToMenu.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * loads form to update selected appointment
     */
    @FXML
    void actionAppointUpdate(ActionEvent event) {
        // Get the selected appointment from the table view
        Appointment selectedAppointment = appointTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            try {
                // Load the AppointmentsModify.fxml file
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/AppointmentsModify.fxml"));
                Parent root = fxmlLoader.load();
                AppointmentsModify appointmentsModifyController = fxmlLoader.getController();

                // Populate appointment data into the form fields
                appointmentsModifyController.populateAppointmentData(selectedAppointment);

                // Display the AppointmentsModify window
                Stage stage = new Stage();
                stage.setTitle("Update Appointment");
                stage.setScene(new Scene(root));
                stage.show();

                // Close the login window
                backToMenu.getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // No appointment selected, display an error message or handle as appropriate
        }
    }

    /**
     * load all appointments when radio button is clicked
     */
    @FXML
    void allAppointments(ActionEvent event) {
        loadAppointments();
    }

    /**
     * load appointments within the next 30 days when radio button is clicked
     */
    @FXML
    void monthlyAppointments(ActionEvent event) {
        loadAppointmentsWithin30Days();
    }

    /**
     * load appointments within the next 30 days when radio button is clicked
     */
    private void loadAppointmentsWithin30Days() {
        try {
            Connection connection = Functions.getConnection(); // Implement your own getConnection() method
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime endDateTime = currentDateTime.plusDays(30);

            // Clear the list of appointments data before loading new data
            appointmentsData.clear();

            String query = "SELECT a.appointment_id, a.title, a.description, a.location, a.contact_id, a.type, a.start, a.end, a.customer_id, a.user_id, a.create_Date, a.created_By, a.last_update, a.last_updated_by, " +
                    "c.customer_name AS customer_name, u.user_name AS user_name, con.contact_name AS contact_name " +
                    "FROM appointments a " +
                    "JOIN customers c ON a.customer_id = c.customer_id " +
                    "JOIN users u ON a.user_id = u.user_id " +
                    "JOIN contacts con ON a.contact_id = con.contact_id " +
                    "WHERE a.start BETWEEN ? AND ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(currentDateTime));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDateTime));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                // Parse UTC datetime to local datetime
                LocalDateTime startLocalDateTime = parseToLocalDateTime(resultSet.getString("start"));
                LocalDateTime endLocalDateTime = parseToLocalDateTime(resultSet.getString("end"));
                LocalDateTime createDateLocalDateTime = parseToLocalDateTime(resultSet.getString("create_date"));
                LocalDateTime lastUpdateLocalDateTime = parseToLocalDateTime(resultSet.getString("last_update"));

                Appointment appointment = new Appointment(
                        resultSet.getString("appointment_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("location"),
                        resultSet.getString("contact_name"),
                        resultSet.getString("type"),
                        startLocalDateTime.toString(), // Convert LocalDateTime to String
                        endLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("customer_id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("contact_name"),
                        resultSet.getString("user_name"),
                        createDateLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("created_by"),
                        lastUpdateLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("last_updated_by")
                        // Add additional parameters if needed for the Appointment constructor
                );
                appointmentsData.add(appointment);
            }
            // Populate the table with data
            ObservableList<Appointment> observableAppointmentsData = FXCollections.observableArrayList(appointmentsData);
            appointTable.setItems(observableAppointmentsData);

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * load appointments within the next 7 days when radio button is clicked
     */
    @FXML
    void weeklyAppointments(ActionEvent event) {
        loadAppointmentsWithin7Days();
    }
    /**
     * load appointments within the next 7 days when radio button is clicked
     */
    private void loadAppointmentsWithin7Days() {
        try {
            Connection connection = Functions.getConnection(); // Implement your own getConnection() method
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime endDateTime = currentDateTime.plusDays(7);

            // Clear the list of appointments data before loading new data
            appointmentsData.clear();

            String query = "SELECT a.appointment_id, a.title, a.description, a.location, a.contact_id, a.type, a.start, a.end, a.customer_id, a.user_id, a.create_Date, a.created_By, a.last_update, a.last_updated_by, " +
                    "c.customer_name AS customer_name, u.user_name AS user_name, con.contact_name AS contact_name " +
                    "FROM appointments a " +
                    "JOIN customers c ON a.customer_id = c.customer_id " +
                    "JOIN users u ON a.user_id = u.user_id " +
                    "JOIN contacts con ON a.contact_id = con.contact_id " +
                    "WHERE a.start BETWEEN ? AND ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(currentDateTime));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDateTime));

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean foundAppointments = false;

            while (resultSet.next()) {
                foundAppointments = true;
                // Parse UTC datetime to local datetime
                LocalDateTime startLocalDateTime = parseToLocalDateTime(resultSet.getString("start"));
                LocalDateTime endLocalDateTime = parseToLocalDateTime(resultSet.getString("end"));
                LocalDateTime createDateLocalDateTime = parseToLocalDateTime(resultSet.getString("create_date"));
                LocalDateTime lastUpdateLocalDateTime = parseToLocalDateTime(resultSet.getString("last_update"));

                Appointment appointment = new Appointment(
                        resultSet.getString("appointment_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("location"),
                        resultSet.getString("contact_name"),
                        resultSet.getString("type"),
                        startLocalDateTime.toString(), // Convert LocalDateTime to String
                        endLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("customer_id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("contact_name"),
                        resultSet.getString("user_name"),
                        createDateLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("created_by"),
                        lastUpdateLocalDateTime.toString(),   // Convert LocalDateTime to String
                        resultSet.getString("last_updated_by")
                        // Add additional parameters if needed for the Appointment constructor
                );
                appointmentsData.add(appointment);
            }
            if (!foundAppointments) {
                // If no appointments fit the criteria, display a message in the table view
                Appointment noAppointmentMessage = new Appointment(
                        "-1",
                        "No appointments fit this criteria.",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                );
                appointmentsData.add(noAppointmentMessage);
            }
            // Populate the table with data
            ObservableList<Appointment> observableAppointmentsData = FXCollections.observableArrayList(appointmentsData);
            appointTable.setItems(observableAppointmentsData);

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * back to main menu
     */
    @FXML
    void backToMenu(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Menu.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Menu");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            backToMenu.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
