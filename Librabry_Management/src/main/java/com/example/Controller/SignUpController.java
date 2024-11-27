package com.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;
import com.example.librabry_management.*;

public class SignUpController implements Initializable {
    @FXML
    private TextField NameField;

    @FXML
    private TextField BirthdateField;

    @FXML
    private TextField PhoneNumberField;

    @FXML
    private TextField LocationField;

    @FXML
    private TextField EmailField;

    @FXML
    private TextField PasswordField;

    @FXML
    private Button CreateAccountButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label EmailStatusLabel;

    @FXML
    private Label PhoneStatusLabel;

    @FXML
    private Label BirthdateStatusLabel;

    @FXML
    private Button CancelButton;

    public void CancelActionHandle() {
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }

    public void CreateAccountButtonHandle() {
        boolean isValid = true;

        if (NameField.getText().isEmpty()) {
            NameField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (BirthdateField.getText().isEmpty() || !isValidBirthdate(BirthdateField.getText())) {
            BirthdateField.setStyle("-fx-border-color: red;");
            BirthdateStatusLabel.setText("Invalid date or incorrect dd/mm/yyyy format");
            isValid = false;
        }

        if (PhoneNumberField.getText().isEmpty() || !isPhoneNumberValid(PhoneNumberField.getText())) {
            PhoneNumberField.setStyle("-fx-border-color: red;");
            PhoneStatusLabel.setText("Phone number must have exactly 10 digits.");
            isValid = false;
        }

        if (LocationField.getText().isEmpty()) {
            LocationField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (DatabaseHelper.isEmailExists(EmailField.getText())) {
            EmailField.setStyle("-fx-border-color: red;");
            EmailStatusLabel.setText("Email already exists.");
            isValid = false;
        }

        if (EmailField.getText().isEmpty()) {
            EmailField.setStyle("-fx-border-color: red;");
            EmailStatusLabel.setText("Invalid email address.");
            isValid = false;
        }

        if (PasswordField.getText().isEmpty()) {
            PasswordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (isValid) {
            try {
                // Tạo một đối tượng User
                User newUser = new User(
                        NameField.getText(),
                        formatBirthdate(BirthdateField.getText()),
                        PhoneNumberField.getText(),
                        EmailField.getText(),
                        LocationField.getText(),
                        PasswordField.getText()
                );

                // Lưu đối tượng User vào cơ sở dữ liệu
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

    private void resetAllFields() {
        NameField.clear();
        BirthdateField.clear();
        PhoneNumberField.clear();
        LocationField.clear();
        EmailField.clear();
        PasswordField.clear();

        NameField.setStyle(null);
        BirthdateField.setStyle(null);
        PhoneNumberField.setStyle(null);
        LocationField.setStyle(null);
        EmailField.setStyle(null);
        PasswordField.setStyle(null);

        EmailStatusLabel.setText("");
        PhoneStatusLabel.setText("");
        BirthdateStatusLabel.setText("");
        statusLabel.setText("");
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    private boolean isValidBirthdate(String birthdate) {
        if (!birthdate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        String[] parts = birthdate.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        return isValidDate(day, month, year);
    }

    private boolean isValidDate(int day, int month, int year) {
        if (month < 1 || month > 12) {
            return false;
        }

        if (day < 1 || day > daysInMonth(month, year)) {
            return false;
        }

        if (year < 0 || year > Calendar.getInstance().get(Calendar.YEAR)) {
            return false;
        }

        return true;
    }

    private int daysInMonth(int month, int year) {
        switch (month) {
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return (isLeapYear(year)) ? 29 : 28;
            default:
                return 31;
        }
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private String formatBirthdate(String birthdate) throws IllegalArgumentException {
        String[] parts = birthdate.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format");
        }
        String formattedDate = parts[2] + "-" + parts[1] + "-" + parts[0]; // Định dạng yyyy-MM-dd
        return formattedDate;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.createUsersTable();
        BirthdateField.textProperty().addListener((observable, oldValue, newValue) -> {
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
            BirthdateField.setText(newValue);
        });

        PhoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                newValue = newValue.substring(0, 10);
            }
            PhoneNumberField.setText(newValue);
        });
    }
}