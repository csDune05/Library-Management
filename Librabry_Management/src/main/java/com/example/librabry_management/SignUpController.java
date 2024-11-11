package com.example.librabry_management;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.*;
import java.util.Calendar;

public class SignUpController {
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
            BirthdateStatusLabel.setStyle("-fx-border-color: red;");
            BirthdateStatusLabel.setText("Invalid date or uncorrected dd/mm/yyyy format");
            isValid = false;
        }

        if (PhoneNumberField.getText().isEmpty() || !isPhoneNumberValid(PhoneNumberField.getText())) {
            PhoneNumberField.setStyle("-fx-border-color: red;");
            PhoneStatusLabel.setStyle("-fx-border-color: red;");
            PhoneStatusLabel.setText("Phone number must has enough 10 digits only");
            isValid = false;
        }

        if (LocationField.getText().isEmpty()) {
            LocationField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (isEmailDuplicate(EmailField.getText())) {
            EmailField.setStyle("-fx-border-color: red;");
            EmailStatusLabel.setText("Email already exists");
            isValid = false;
        }

        if (EmailStatusLabel.getText().isEmpty()) {
            EmailField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (PasswordField.getText().isEmpty()) {
            PasswordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (isValid) {
            try {
                saveToFile();
                resetAllFields();
                statusLabel.setText("Create account successful");
            } catch (IOException e) {
                statusLabel.setText("Create account failed");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Create account failed");
        }
    }

    private void resetAllFields() {
        NameField.setStyle(null);
        BirthdateField.setStyle(null);
        PhoneNumberField.setStyle(null);
        LocationField.setStyle(null);
        EmailField.setStyle(null);
        PasswordField.setStyle(null);
    }

    private boolean isEmailDuplicate(String email) {
        File accountsList = new File("accounts.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsList))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split(",")[0].equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    private void saveToFile() throws IOException {
        File accountsList = new File("accounts.txt");

        try (FileWriter writer = new FileWriter(accountsList, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            String accountData = EmailField.getText() + "," + PasswordField.getText() + "," +
                    LocationField.getText() + "," + PhoneNumberField.getText() + "," +
                    BirthdateField.getText() + "," + NameField.getText() + "\n";

            bufferedWriter.write(accountData);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        readAllDataInFile("accounts.txt");
    }

    public static void readAllDataInFile(String fileName) {
        File file = new File(fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] accountDetails = line.split(",");

                System.out.println("Email: " + accountDetails[0]);
                System.out.println("Password: " + accountDetails[1]);
                System.out.println("Location: " + accountDetails[2]);
                System.out.println("Phone Number: " + accountDetails[3]);
                System.out.println("Birthdate: " + accountDetails[4]);
                System.out.println("Name: " + accountDetails[5]);
                System.out.println("--------------------------------------");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllDataInFile(String fileName) {
        File file = new File(fileName);

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("");
            System.out.println("Delete all data in file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}