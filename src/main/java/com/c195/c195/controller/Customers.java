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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class Customers {

    @FXML
    private TableView<Customer> custTable;

    @FXML
    private TableColumn<Customer, Integer> custIdCol;

    @FXML
    private TableColumn<Customer, String> custNameCol;

    @FXML
    private TableColumn<Customer, String> custPhoneCol;

    @FXML
    private TableColumn<Customer, String> custAddressCol;

    @FXML
    private TableColumn<Customer, String> custPostalCol;

    @FXML
    private TableColumn<Customer, String> custFirstCol;

    @FXML
    private TableColumn<Customer, String> custCountryCol;

    @FXML
    private TableColumn<Customer, String> custCreateDateCol;
    @FXML
    private TableColumn<Customer, String> custCreatedByCol;
    @FXML
    private TableColumn<Customer, String> custLastUpdateCol;
    @FXML
    private TableColumn<Customer, String> custLastUpdatedByCol;


    @FXML
    private Button custDeleteLabel;

    @FXML
    private Button custAddLabel;

    @FXML
    private Button custUpdateLabel;

    @FXML
    private Button backToMenu;

    private ObservableList<Customer> customersData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set cell value factories for each column
        custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        custAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        custPostalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        custFirstCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
        custCountryCol.setCellValueFactory(new PropertyValueFactory<>("countryId"));
        custCreateDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        custCreatedByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        custLastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        custLastUpdatedByCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        // Add cell value factory for custCountryCol if you have country data in the Customer class

        // Populate the table
        populateTable();
    }

    private void populateTable() {
        try {
            Connection connection = getConnection(); // Implement your own getConnection() method
            String query = "SELECT c.customer_id, c.customer_name, c.phone, c.address, c.postal_code, d.division, co.country, c.create_date, c.created_by, c.last_update, c.last_updated_by " +
                    "FROM customers c " +
                    "JOIN first_level_divisions d ON c.division_id = d.division_id " +
                    "JOIN countries co ON d.country_id = co.country_id";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        parseToLocalDateTime(resultSet.getString("Create_Date")),
                        resultSet.getString("Created_by"),
                        parseToLocalDateTime(resultSet.getString("Last_Update")),
                        resultSet.getString("last_Updated_By"),
                        resultSet.getString("division"),
                        resultSet.getString("country")
                        // Add additional parameters if needed for the Customer constructor
                );
                customersData.add(customer);
            }
            // Populate the table with data
            custTable.setItems(customersData);

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private LocalDateTime parseToLocalDateTime(String utcDateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime utcDateTime = LocalDateTime.parse(utcDateTimeStr, formatter);

        // Get the user's default time zone
        ZoneId userZone = ZoneId.systemDefault();

        // Convert UTC time to user's local time
        ZonedDateTime utcZonedDateTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"));
        ZonedDateTime userZonedDateTime = utcZonedDateTime.withZoneSameInstant(userZone);

        // Return the local date and time
        return userZonedDateTime.toLocalDateTime();
    }


    @FXML
    private void actionCustDelete(ActionEvent event) {
        Customer selectedCustomer = custTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            // Check for Existing Appointments
            boolean hasAppointments = hasCustomerAppointments(selectedCustomer.getId());

            if (hasAppointments) {
                // Display Error Message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Deletion Error");
                alert.setContentText("Customer " + selectedCustomer.getName() + " has existing appointments. Cannot be deleted.");
                alert.showAndWait();
            } else {
                // Confirmation Dialog
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setHeaderText("Delete Customer Confirmation");
                confirmationAlert.setContentText("Are you sure you want to delete " + selectedCustomer.getName() + "?");
                Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

                if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                    try {
                        Connection connection = getConnection();
                        String deleteQuery = "DELETE FROM customers WHERE customer_id = ?";
                        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                        deleteStatement.setInt(1, selectedCustomer.getId());
                        deleteStatement.executeUpdate();

                        // Remove from ObservableList
                        customersData.remove(selectedCustomer);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // Handle the case where no customer is selected
        }
    }

    // Helper method to check for existing appointments
// Helper method to check for existing appointments
    private boolean hasCustomerAppointments(int customerId) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM appointments WHERE customer_id = ?")) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int appointmentCount = resultSet.getInt(1);
                return appointmentCount > 0;
            } else {
                // Handle potential errors here (e.g., no data found)
                return false;
            }

        } catch (SQLException e) {
            // Handle SQL exceptions here
            e.printStackTrace(); // Log or display the error message
            return false; // Assume no appointments if an error occurs
        }
    }

    @FXML
    private void actionCustAdd(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/CustomerAdd.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("New Customer");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            custAddLabel.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actionCustUpdate(ActionEvent event) {
        Customer selectedCustomer = custTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/c195/c195/CustomerModify.fxml"));
                AnchorPane modifyRoot = (AnchorPane) loader.load();

                CustomerModify modifyController = loader.getController();
                modifyController.setCustomer(selectedCustomer);

                // Assuming you have a Stage or Parent for this current scene
                // Replace the current scene with the CustomerModify scene
                Stage stage = (Stage) custTable.getScene().getWindow();
                stage.setScene(new Scene(modifyRoot));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where no customer is selected
        }
    }

    @FXML
    private void backToMenu(ActionEvent event) {
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
    private Connection getConnection() throws SQLException {
        // Define your database connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/client_schedule";
        String dbUsername = "sqlUser";
        String dbPassword = "Passw0rd!";

        // Establish the connection
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }


}
