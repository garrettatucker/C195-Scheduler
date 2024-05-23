package com.c195.c195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Menu {

    @FXML
    private Button exitButton;
    @FXML
    private Button customerButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button appointButton;


    @FXML
    void menuCustomer(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Customers.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Customers");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            customerButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void menuAppoint(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Appointments.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Appointments");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            appointButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void menuReport(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/Reports.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Reports");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            reportButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void menuExit(ActionEvent event) {
        try {
            // Load the login.fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/c195/c195/login.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new stage for the login screen
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current menu window
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
