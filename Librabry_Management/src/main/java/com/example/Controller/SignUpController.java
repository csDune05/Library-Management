package com.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import com.example.librabry_management.*;
import com.example.Feature.*;

public class SignUpController implements Initializable {
    @FXML
    private TextField nameField;

    @FXML
    private TextField birthdateField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button createAccountButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label emailStatusLabel;

    @FXML
    private Label phoneStatusLabel;

    @FXML
    private Label birthDateStatusLabel;

    @FXML
    private Button cancelButton;

    /**
     * Handle cancel button event.
     */
    public void cancelButtonHandle() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handle create new account event.
     */
    public void createAccountButtonHandle() {
        boolean isValid = true;

        if (nameField.getText().isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (birthdateField.getText().isEmpty() || !isValidBirthdate(birthdateField.getText())) {
            birthdateField.setStyle("-fx-border-color: red;");
            birthDateStatusLabel.setText("Invalid date or incorrect dd/mm/yyyy format");
            isValid = false;
        }

        if (phoneNumberField.getText().isEmpty() || !isPhoneNumberValid(phoneNumberField.getText())) {
            phoneNumberField.setStyle("-fx-border-color: red;");
            phoneStatusLabel.setText("Phone number must have exactly 10 digits.");
            isValid = false;
        }

        if (locationField.getText().isEmpty()) {
            locationField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (DatabaseHelper.isEmailExists(emailField.getText())) {
            emailField.setStyle("-fx-border-color: red;");
            emailStatusLabel.setText("Email already exists.");
            isValid = false;
        }

        if (emailField.getText().isEmpty()) {
            emailField.setStyle("-fx-border-color: red;");
            emailStatusLabel.setText("Invalid email address.");
            isValid = false;
        }

        if (passwordField.getText().isEmpty()) {
            passwordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (isValid) {
            try {
                User newUser = new User(
                        nameField.getText(),
                        formatBirthdate(birthdateField.getText()),
                        phoneNumberField.getText(),
                        emailField.getText(),
                        locationField.getText(),
                        passwordField.getText()
                );

                DatabaseHelper.saveUser(newUser);

                resetAllFields();
                statusLabel.setText("Account created successfully!");
            } catch (Exception e) {
                statusLabel.setText("Failed to create account!");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Failed to create account!");
        }
    }

    /**
     * Reset all fields.
     */
    public void resetAllFields() {
        nameField.clear();
        birthdateField.clear();
        phoneNumberField.clear();
        locationField.clear();
        emailField.clear();
        passwordField.clear();

        nameField.setStyle(null);
        birthdateField.setStyle(null);
        phoneNumberField.setStyle(null);
        locationField.setStyle(null);
        emailField.setStyle(null);
        passwordField.setStyle(null);

        emailStatusLabel.setText("");
        phoneStatusLabel.setText("");
        birthDateStatusLabel.setText("");
        statusLabel.setText("");
    }

    /**
     * @return
     * Check valid phone number.
     */
    public boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    /**
     * @return
     * Check valid birthdate.
     */
    public boolean isValidBirthdate(String birthdate) {
        if (!birthdate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        String[] parts = birthdate.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        return isValidDate(day, month, year);
    }

    /**
     * @return
     * Check date.
     */
    private boolean isValidDate(int day, int month, int year) {
        if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > daysInMonth(month, year)) {
            return false;
        }
        return year >= 0 && year <= Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * return days in month.
     */
    private int daysInMonth(int month, int year) {
        return switch (month) {
            case 4, 6, 9, 11 -> 30;
            case 2 -> (isLeapYear(year)) ? 29 : 28;
            default -> 31;
        };
    }

    /**
     * @return
     * Check leap year.
     */
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * @throws IllegalArgumentException
     * Format birthdate input for birthdate field.
     */
    public String formatBirthdate(String birthdate) throws IllegalArgumentException {
        String[] parts = birthdate.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format");
        }
        String formattedDate = parts[2] + "-" + parts[1] + "-" + parts[0]; // Định dạng yyyy-MM-dd
        return formattedDate;
    }

    /**
     * Initialize the default sign up scene.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.createUsersTable();
        birthdateField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replaceAll("[^\\d/]", "");

            if (newValue.length() > 2 && !newValue.contains("/")) {
                newValue = newValue.substring(0, 2) + "/" + newValue.substring(2);
            }
            if (newValue.length() > 5 && newValue.charAt(5) != '/') {
                newValue = newValue.substring(0, 5) + "/" + newValue.substring(5);
            }

            if (newValue.length() > 10) {
                newValue = newValue.substring(0, 10);
            }
            birthdateField.setText(newValue);
        });

        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                newValue = newValue.substring(0, 10);
            }
            phoneNumberField.setText(newValue);
        });
    }
}