package com.c195.c195.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty location;
    private final StringProperty contact;
    private final StringProperty type;
    private final StringProperty date;
    private final StringProperty endDate;
    private final StringProperty customerId;
    private final StringProperty userId;
    private final StringProperty customerName;
    private final StringProperty userName;
    private final StringProperty contactName;
    private final StringProperty createDate;
    private final StringProperty createdBy;
    private final StringProperty lastUpdate;
    private final StringProperty lastUpdatedBy;

    public Appointment(String id, String title, String description, String location, String contact, String type,
                       String date, String endDate, String customerId, String userId, String customerName, String userName, String contactName, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.type = new SimpleStringProperty(type);
        this.date = new SimpleStringProperty(date);
        this.endDate = new SimpleStringProperty(endDate);
        this.customerId = new SimpleStringProperty(customerId);
        this.userId = new SimpleStringProperty(userId);
        this.customerName = new SimpleStringProperty(customerName);
        this.userName = new SimpleStringProperty(userName);
        this.contactName = new SimpleStringProperty(contactName);
        this.createDate = new SimpleStringProperty(createDate);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.lastUpdate = new SimpleStringProperty(lastUpdate);
        this.lastUpdatedBy = new SimpleStringProperty(lastUpdatedBy);
    }

    // Getters for the properties
    public StringProperty idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty endDateProperty() {
        return endDate;
    }

    public StringProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public StringProperty contactNameProperty() {
        return contactName;
    }
    public StringProperty createdByProperty() {
        return createdBy;
    }
    public StringProperty createDateProperty() {
        return createDate;
    }
    public StringProperty lastUpdateProperty() {
        return lastUpdate;
    }
    public StringProperty lastUpdatedByProperty() {
        return lastUpdatedBy;
    }

    // Additional methods for setting LocalDateTime properties
    public void setDate(LocalDateTime date) {
        this.date.set(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate.set(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    public StringProperty getId() {
        return id;
    }

    // Static method to get distinct appointment types from the database
    public static ObservableList<String> getAppointmentTypes() throws SQLException {
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = getConnection();

            // Query to get distinct appointment types
            String query = "SELECT DISTINCT Type FROM appointments";

            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Add types to the list
            while (resultSet.next()) {
                appointmentTypes.add(resultSet.getString("Type"));
            }
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }

        return appointmentTypes;
    }

    private static Connection getConnection() throws SQLException {
        // Define your database connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/client_schedule";
        String dbUsername = "sqlUser";
        String dbPassword = "Passw0rd!";

        // Establish the connection
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }
}
