package com.c195.c195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

public class CustomerAdd {

    public Label errorMessageLabel;
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

    @FXML
    void initialize() {
        try {
            establishConnection();
            populateDivisionComboBox();
            populateCountryComboBox();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void establishConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String user = "sqlUser";
        String password = "Passw0rd!";
        connection = DriverManager.getConnection(url, user, password);
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

    @FXML
    void actionCancelButton(ActionEvent event) {
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

    @FXML
    void actionCountryFilter(ActionEvent event) {
        String selectedDivision = customerDivisionCombo.getValue();

        if (selectedDivision != null) {
            try {
                // Get the associated country for the selected division from the database
                String associatedCountry = getAssociatedCountry(selectedDivision);

                // Set the selected country in the country combo box
                if (associatedCountry != null) {
                    customerCountryCombo.setValue(associatedCountry);
                }
            } catch (SQLException e) {
                // Handle database error
                e.printStackTrace();
            }
        }
    }

    // Method to retrieve the associated country for a given division from the database
    private String getAssociatedCountry(String division) throws SQLException {
        // Initialize the associated country variable
        String associatedCountry = null;

        // Define the SQL query to retrieve the associated country
        String query = "SELECT c.Country FROM countries c " +
                "JOIN first_level_divisions f ON c.Country_ID = f.Country_ID " +
                "WHERE f.Division = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the division parameter in the prepared statement
            preparedStatement.setString(1, division);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if the query returned a result
                if (resultSet.next()) {
                    // Retrieve the associated country from the result set
                    associatedCountry = resultSet.getString("Country");
                }
            }
        }

        return associatedCountry;
    }
    @FXML
    void actionDivisionFilter(ActionEvent event) {
        String selectedCountry = customerCountryCombo.getValue();

        if (selectedCountry != null) {
            try {
                // Get the divisions associated with the selected country from the database
                List<String> divisions = getDivisionsByCountry(selectedCountry);

                // Clear existing items and add filtered divisions to the division combo box
                customerDivisionCombo.getItems().clear();
                customerDivisionCombo.getItems().addAll(divisions);
                // Print divisions to check if they are fetched correctly
                System.out.println("Divisions for country " + selectedCountry + ": " + divisions);
            } catch (SQLException e) {
                // Handle database error
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not get country");
        }
    }

    // Method to retrieve divisions associated with a given country from the database
    private List<String> getDivisionsByCountry(String country) throws SQLException {
        // Initialize a list to store divisions associated with the country
        List<String> divisions = new ArrayList<>();

        // Define the SQL query to retrieve divisions by country
        String query = "SELECT Division FROM first_level_divisions WHERE Country_ID = " +
                "(SELECT Country_ID FROM countries WHERE Country = ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the country parameter in the prepared statement
            preparedStatement.setString(1, country);

            // Print the executed SQL query for debugging
            System.out.println("Executing SQL query: " + preparedStatement.toString());


            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Iterate over the result set and add divisions to the list
                while (resultSet.next()) {
                    divisions.add(resultSet.getString("Division"));
                }
            }
        }

        return divisions;
    }

    @FXML
    void actionSaveButton(ActionEvent event) {
        String name = customerNameTextField.getText();
        String phone = customerPhoneTextField.getText();
        String address = customerAddressTextField.getText();
        String postalCode = customerPostalTextField.getText();
        String divisionName = customerDivisionCombo.getValue();
        String countryName = customerCountryCombo.getValue();



        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || postalCode.isEmpty() || divisionName == null || countryName == null) {
            errorMessageLabel.setText("Please fill in all required fields.");
            return;
        } else{

            try {
                Connection connection = getConnection();

                // Query to insert a new customer
                String insertQuery = "INSERT INTO Customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                // Generate current timestamp in UTC
                LocalDateTime currentTime = LocalDateTime.now();
                ZoneId utcZone = ZoneId.of("UTC");
                ZonedDateTime utcTime = ZonedDateTime.of(currentTime, utcZone);

                // Format the UTC time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = utcTime.format(formatter);

                // Get the logged-in username
                String createdBy = Login.getLoggedInUsername();

                // Get the division and country IDs from the names selected
                int divisionId = getDivisionIdByName(divisionName, connection);

                // Prepare the statement
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3, postalCode);
                preparedStatement.setString(4, phone);
                preparedStatement.setString(5, timestamp);
                preparedStatement.setString(6, createdBy);
                preparedStatement.setString(7, timestamp);
                preparedStatement.setString(8, createdBy);
                preparedStatement.setInt(9, divisionId);

                // Execute the query
                preparedStatement.executeUpdate();

                // Close resources
                preparedStatement.close();
                connection.close();
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

                // Optionally, display success message or navigate to another screen
            } catch (SQLException e) {
                // Handle SQL exception
                e.printStackTrace();
            }

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

    private int getCountryIdByName(String countryName, Connection connection) throws SQLException {
        // Query to get country ID by name
        String query = "SELECT Country_ID FROM countries WHERE Country = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, countryName);

        ResultSet resultSet = statement.executeQuery();

        int countryId = -1; // Default value if not found

        if (resultSet.next()) {
            countryId = resultSet.getInt("Country_ID");
        }

        resultSet.close();
        statement.close();

        return countryId;
    }

}
