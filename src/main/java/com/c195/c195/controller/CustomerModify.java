package com.c195.c195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class CustomerModify {

    @FXML
    private TextField customerIDTextField;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField customerPhoneTextField;
    @FXML
    private TextField customerAddressTextField;
    @FXML
    private TextField customerPostalTextField;
    @FXML
    private ComboBox<String> customerDivisionCombo;
    @FXML
    private ComboBox<String> customerCountryCombo;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Connection connection; // Database connection

    // Initialize method (called when the FXML is loaded)
    @FXML
    public void initialize() throws SQLException {
        establishConnection();
        populateDivisionComboBox();
        populateCountryComboBox();
    }
    public void setCustomer(Customer customer) {
        customerIDTextField.setText(String.valueOf(customer.getId())); // Assuming customer has an getId() method
        customerNameTextField.setText(customer.getName());
        customerPhoneTextField.setText(customer.getPhone());
        customerAddressTextField.setText(customer.getAddress());
        customerPostalTextField.setText(customer.getPostalCode());

// Select the division associated with the customer
        String divisionName = customer.getDivisionId();
        if (divisionName != null && !divisionName.isEmpty()) {
            customerDivisionCombo.getSelectionModel().select(divisionName);
        }

// Select the country associated with the customer
        String countryName = customer.getCountryId();
        if (countryName != null && !countryName.isEmpty()) {
                customerCountryCombo.getSelectionModel().select(countryName);
            }
        }

    // Event handler for the "Save" button
    @FXML
    private void actionSaveButton(ActionEvent event) {
        // Retrieve data from UI elements
        int customerId = Integer.parseInt(customerIDTextField.getText());
        String name = customerNameTextField.getText();
        String phone = customerPhoneTextField.getText();
        String address = customerAddressTextField.getText();
        String postalCode = customerPostalTextField.getText();
        String division = customerDivisionCombo.getValue();

        // Update customer information in the database
        try {
            // Get the logged-in username
            String username = Login.getLoggedInUsername();
            // Get the division and country IDs from the names selected
            int divisionId = getDivisionIdByName(division, connection);
            updateCustomer(customerId, name, phone, address, postalCode, String.valueOf(divisionId), username);
            // Show a success message
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database error
        }

        // Return to the main screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Customers.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Customers");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace(); // Handle error loading fxml file
        }
    }

    private void updateCustomer(int customerId, String name, String phone, String address, String postalCode, String divisionId, String username) throws SQLException {
        // Perform the update operation in the database
        String query = "UPDATE customers SET Customer_Name = ?, Phone = ?, Address = ?, Postal_Code = ?, Division_ID = ?, Last_Updated_By = ?, Last_Update = ? WHERE Customer_ID = ?";
        // Get the current date and time
        // Generate current timestamp in UTC
        LocalDateTime currentTime = LocalDateTime.now();
        ZoneId utcZone = ZoneId.of("UTC");
        ZonedDateTime utcTime = ZonedDateTime.of(currentTime, utcZone);

        // Format the UTC time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = utcTime.format(formatter);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, address);
            statement.setString(4, postalCode);
            statement.setString(5, divisionId);
            statement.setString(6, username);
            statement.setString(7, String.valueOf(utcTime));
            statement.setInt(8, customerId);
            statement.executeUpdate();
        }
    }

    // Event handler for the "Cancel" button
    @FXML
    private void actionCancelButton(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Customers.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Customers");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            cancelButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Event handler for the customerCountryCombo
    @FXML
    private void actionCountryLoad(ActionEvent event) {
        // Load divisions based on the selected country
    }
    private void populateDivisionComboBox() throws SQLException {
        String query = "SELECT Division FROM first_level_divisions";
        List<String> divisions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                divisions.add(resultSet.getString("Division"));
            }
        }

        customerDivisionCombo.getItems().addAll(divisions);
    }

    private void populateCountryComboBox() throws SQLException {
        String query = "SELECT Country FROM countries";
        List<String> countries = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                countries.add(resultSet.getString("Country"));
            }
        }

        customerCountryCombo.getItems().addAll(countries);
    }

    private String getCountryNameById(int countryId) {
        String query = "SELECT Country FROM countries WHERE Country_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, countryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Country");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return null; // Return null if no country with the given ID is found
    }

    private int getDivisionIdByName(String divisionName, Connection connection) throws SQLException {
        // Query to get division ID by name
        String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, divisionName);

        ResultSet resultSet = statement.executeQuery();

        int divisionId = -1; // Default value if not found

        if (resultSet.next()) {
            divisionId = resultSet.getInt("Division_ID");
        }

        resultSet.close();
        statement.close();

        return divisionId;
    }

    private void establishConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String user = "sqlUser";
        String password = "Passw0rd!";
        connection = DriverManager.getConnection(url, user, password);
    }
}
