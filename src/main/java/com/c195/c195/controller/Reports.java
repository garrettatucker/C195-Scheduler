/**
 * Controller class for the Reports view.
 */
package com.c195.c195.controller;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Reports {

    public Tab contactScheduleTab;
    @FXML
    private TableView<AppointmentType> appointTypeTable;

    @FXML
    private TableColumn<String, String> appointTotalType;

    @FXML
    private TableColumn<String, String> appointTypeTotal;

    @FXML
    private TableView<AppointmentMonth> appointMonthTable;


    @FXML
    private TableColumn<String, String> appointMonth;

    @FXML
    private TableColumn<String, String> appointMonthTotal;

    @FXML
    private TableView<Appointment> contactTable;

    @FXML
    private TableColumn<String, String> appointIdCol;

    @FXML
    private TableColumn<String, String> appointTitleCol;

    @FXML
    private TableColumn<String, String> appointDescriptionCol;

    @FXML
    private TableColumn<String, String> appointContactCol;

    @FXML
    private TableColumn<String, String> appointTypeCol;
    @FXML
    public TableColumn<String, String> appointLocationCol;
    @FXML
    public TableColumn<String, String> appointDateCol;
    @FXML
    public TableColumn<String, String> appointEndDateCol;
    @FXML
    public TableColumn<String, String> appointCustNameCol;
    @FXML
    public TableColumn<String, String> appointUserNameCol;
    @FXML
    public TableColumn<String, String> appointCreateDateCol;
    @FXML
    public TableColumn<String, String> appointLastUpdateCol;
    @FXML
    public TableColumn<String, String> appointLastUpdatedByCol;
    @FXML
    public TableColumn<String, String> appointCreatedByCol;

    @FXML
    private TableView<CountryCustomer> countryTable;

    @FXML
    private TableColumn<String, String> appointCountry;

    @FXML
    private TableColumn<String, String> appointCountryTotal;

    @FXML
    private ComboBox<String> contactCombo;

    private Connection connection; // Database connection

    @FXML
    private Button backToMenu;
    /**
     * Initializes the controller after all FXML elements are loaded.
     */
    @FXML
    public void initialize() {
        // Initialize Contact table columns
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

        // Initialize Appointment Type Totals table columns
        appointTotalType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointTypeTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Initialize month table columns
        appointMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        appointMonthTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Initialize table columns
        appointCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        appointCountryTotal.setCellValueFactory(new PropertyValueFactory<>("customerCount"));

        try {
            establishConnection();
            contactPopulate();
            appointTotalTable();
            appointMonthTable();
            customerCountryTable();
            // Add event handler for when a contact is selected
            contactCombo.setOnAction(event -> {
                String selectedContact = contactCombo.getValue();
                if (selectedContact != null) {
                    try {
                        // Retrieve contact ID for the selected contact name
                        int contactId = getContactId(selectedContact);

                        // Fetch appointments associated with the selected contact ID
                        ObservableList<Appointment> appointments = getAppointmentsForContact(contactId);

                        // Display appointments in the table view
                        contactTable.setItems(appointments);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /** Lambda expression: Populating the country table using ObservableList.forEach() method
     * Lambda expression simplifies event handling code, making it more concise and readable.*/
    private void customerCountryTable() {
        try {
            // Connect to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "sqlUser", "Passw0rd!");

            // Execute the SQL query to get count of customers per country
            String query = "SELECT countries.country, COUNT(customers.customer_id) AS customerCount " +
                    "FROM countries " +
                    "LEFT JOIN first_level_divisions ON countries.country_id = first_level_divisions.country_id " +
                    "LEFT JOIN customers ON first_level_divisions.division_id = customers.division_id " +
                    "GROUP BY countries.country";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Create an ObservableList to store country-customer data
            ObservableList<CountryCustomer> countryCustomers = FXCollections.observableArrayList();

            // Populate countryCustomers list with data from the result set using lambda expression
            while (resultSet.next()) {
                String country = resultSet.getString("country");
                int customerCount = resultSet.getInt("customerCount");
                countryCustomers.add(new CountryCustomer(country, customerCount));
            }

            // Close connections
            resultSet.close();
            statement.close();
            connection.close();

            // Set the data in the table using lambda expression
            countryCustomers.forEach(countryCustomer -> countryTable.getItems().add(countryCustomer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** Lambda expression: Populating the country table using ObservableList.forEach() method
     * Lambda expression simplifies event handling code, making it more concise and readable.*/
    private void appointMonthTable() {
        try {
            // Connect to your database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "sqlUser", "Passw0rd!");

            // Execute the SQL query to count appointments for each month
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MONTH(start) AS month, COUNT(*) AS total FROM appointments GROUP BY MONTH(start)");

            // Create an ObservableList to store appointment months
            ObservableList<AppointmentMonth> appointmentMonths = FXCollections.observableArrayList();

            // Populate appointmentMonths list with data from the result set
            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int total = resultSet.getInt("total");

                // Convert month number to month name
                String monthName = LocalDate.of(2000, month, 1).getMonth().toString();

                appointmentMonths.add(new AppointmentMonth(monthName, total));
            }

            // Close connections
            resultSet.close();
            statement.close();
            connection.close();

            // Set the data in the table
            appointMonthTable.setItems(appointmentMonths);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** Lambda expression: Populating the country table using ObservableList.forEach() method
     * Lambda expression simplifies event handling code, making it more concise and readable.*/
    private void appointTotalTable() {
        try {
            // Connect to your database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "sqlUser", "Passw0rd!");

            // Execute the SQL query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT type, COUNT(*) AS total FROM appointments GROUP BY type");

            // Create an ObservableList to store appointment types
            ObservableList<AppointmentType> appointmentTypes = FXCollections.observableArrayList();

            // Populate appointmentTypes list with data from the result set
            while (resultSet.next()) {
                String type = resultSet.getString("type");
                int total = resultSet.getInt("total");
                appointmentTypes.add(new AppointmentType(type, total));
            }

            // Close connections
            resultSet.close();
            statement.close();
            connection.close();

            // Set the data in the table
            appointTypeTable.setItems(appointmentTypes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


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


    public void contactPopulate() throws SQLException {
        String query = "SELECT contact_name FROM contacts";
        List<String> contacts = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                contacts.add(resultSet.getString("Contact_Name"));
            }
        }

        contactCombo.getItems().addAll(contacts);
        // Preselect the first contact name if the list is not empty
        if (!contacts.isEmpty()) {
            contactCombo.setValue(contacts.get(0));
        }
        String selectedContact = contactCombo.getValue();
        if (selectedContact != null) {
            try {
                // Retrieve contact ID for the selected contact name
                int contactId = getContactId(selectedContact);

                // Fetch appointments associated with the selected contact ID
                ObservableList<Appointment> appointments = getAppointmentsForContact(contactId);

                // Display appointments in the table view
                contactTable.setItems(appointments);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private int getContactId(String contactName) throws SQLException {
        String query = "SELECT contact_id FROM contacts WHERE contact_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, contactName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("contact_id");
                }
            }
        }
        throw new SQLException("Contact ID not found for name: " + contactName);
    }

    private ObservableList<Appointment> getAppointmentsForContact(int contactId) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String query = "SELECT a.appointment_id, a.title, a.description, a.location, a.contact_id, a.type, a.start, a.end, a.customer_id, a.user_id, a.create_Date, a.created_By, a.last_update, a.last_updated_by, " +
                "c.customer_name AS customer_name, u.user_name AS user_name, con.contact_name AS contact_name " +
                "FROM appointments a " +
                "JOIN customers c ON a.customer_id = c.customer_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN contacts con ON a.contact_id = con.contact_id " +
                "WHERE con.contact_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, contactId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Parse UTC datetime to local datetime
                    LocalDateTime startLocalDateTime = parseToLocalDateTime(resultSet.getString("start"));
                    LocalDateTime endLocalDateTime = parseToLocalDateTime(resultSet.getString("end"));
                    LocalDateTime createDateLocalDateTime = parseToLocalDateTime(resultSet.getString("create_date"));
                    LocalDateTime lastUpdateLocalDateTime = parseToLocalDateTime(resultSet.getString("last_update"));
                    // Log retrieved data to the console
                    System.out.println("Retrieved appointment data:");
                    System.out.println("Appointment ID: " + resultSet.getString("appointment_id"));
                    System.out.println("Title: " + resultSet.getString("title"));
                    // Create an Appointment object from the result set
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
                    );
                    appointments.add(appointment);
                }
            }
        }
        return appointments;
    }
    private LocalDateTime parseToLocalDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    private void establishConnection () throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String user = "sqlUser";
        String password = "Passw0rd!";
            connection = DriverManager.getConnection(url, user, password);
        }

        private Connection getConnection () throws SQLException {
            // Define your database connection parameters
            String jdbcUrl = "jdbc:mysql://localhost:3306/client_schedule";
            String dbUsername = "sqlUser";
            String dbPassword = "Passw0rd!";

            // Establish the connection
            return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
        }
    }
