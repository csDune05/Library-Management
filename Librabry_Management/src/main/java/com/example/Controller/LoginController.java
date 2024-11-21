package com.example.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signinButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button signupButton;

    @FXML
    private CheckBox staysignedin;

    @FXML
    private ListView<String> accountListView;

    private Preferences prefs;

    private Stage signUpStage;
    private Stage DashboardStage;

    private ObservableList<String> accountList;

    @FXML
    public void initialize() throws JSONException {
        prefs = Preferences.userNodeForPackage(LoginController.class);
        accountList = FXCollections.observableArrayList();
        loadSavedAccounts();

        // Ẩn ListView khi khởi tạo
        accountListView.setVisible(false);

        // Sự kiện di chuột vào ListView để hiển thị
        accountListView.setOnMouseEntered(event -> accountListView.setVisible(true));

        // Sự kiện di chuột ra khỏi ListView để ẩn
        accountListView.setOnMouseExited(event -> accountListView.setVisible(false));

        // Sự kiện khi chọn một tài khoản trong ListView
        accountListView.setOnMouseClicked(event -> {
            String selectedAccount = accountListView.getSelectionModel().getSelectedItem();
            if (selectedAccount != null) {
                try {
                    populateAccountDetails(selectedAccount);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Lắng nghe thay đổi trong emailField để lọc tài khoản
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                filterAccounts(newValue);
            } else {
                accountListView.setItems(accountList);
                accountListView.setVisible(accountList.size() > 0);
            }
        });

        // Sự kiện di chuột vào emailField để hiển thị ListView
        emailField.setOnMouseEntered(event -> {
            if (!emailField.getText().isEmpty() && accountListView.getItems().size() > 0) {
                accountListView.setVisible(true);
            }
        });

        // Sự kiện di chuột ra khỏi emailField để ẩn ListView (nếu không di chuột vào ListView)
        emailField.setOnMouseExited(event -> {
            if (!accountListView.isHover()) {
                accountListView.setVisible(false);
            }
        });
    }

    private void loadSavedAccounts() throws JSONException {
        String accountsJson = prefs.get("accounts", "[]");
        JSONArray accounts = new JSONArray(accountsJson);

        accountList.clear();
        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            accountList.add(account.getString("username"));
        }

        accountListView.setItems(accountList);

        // Chọn tài khoản cuối cùng nếu có
        if (!accountList.isEmpty()) {
            accountListView.getSelectionModel().select(0);
            populateAccountDetails(accountListView.getSelectionModel().getSelectedItem());
        }
    }

    private void populateAccountDetails(String username) throws JSONException {
        String accountsJson = prefs.get("accounts", "[]");
        JSONArray accounts = new JSONArray(accountsJson);

        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            if (account.getString("username").equals(username)) {
                emailField.setText(account.getString("username"));
                passwordField.setText(account.optString("password", ""));
                staysignedin.setSelected(account.optBoolean("rememberMe", false));
                break;
            }
        }
    }

    private void filterAccounts(String searchText) {
        ObservableList<String> filteredList = FXCollections.observableArrayList();

        for (String account : accountList) {
            if (account.toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(account);
            }
        }

        accountListView.setItems(filteredList);
        accountListView.setVisible(!filteredList.isEmpty());
    }

    @FXML
    public void SignInButtonHandle() throws JSONException {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (isLoginValid(username, password)) {
            statusLabel.setText("Login Successful!");
            saveAccount(username, password, staysignedin.isSelected());
            openDashboard();
        } else {
            statusLabel.setText("Email or Password is incorrect!");
        }
    }

    private boolean isLoginValid(String email, String password) {
        File accountsList = new File("accounts.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsList))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] accountDetails = line.split(",");
                if (accountDetails.length >= 2) {
                    String storedEmail = accountDetails[0];
                    String storedPassword = accountDetails[1];

                    if (storedEmail.equals(email) && storedPassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveAccount(String username, String password, boolean rememberMe) throws JSONException {
        String accountsJson = prefs.get("accounts", "[]");
        JSONArray accounts = new JSONArray(accountsJson);

        boolean accountExists = false;
        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            if (account.getString("username").equals(username)) {
                account.put("password", rememberMe ? password : "");
                account.put("rememberMe", rememberMe);
                accountExists = true;
                break;
            }
        }

        if (!accountExists) {
            JSONObject newAccount = new JSONObject();
            newAccount.put("username", username);
            newAccount.put("password", rememberMe ? password : "");
            newAccount.put("rememberMe", rememberMe);
            accounts.put(newAccount);
        }

        prefs.put("accounts", accounts.toString());
        loadSavedAccounts();
    }

    @FXML
    public void handleCancelAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void SignUpButtonHandle() {
        try {
            if (signUpStage == null) {
                Parent signupRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/SignUp.fxml"));

                signUpStage = new Stage();
                signUpStage.initModality(Modality.WINDOW_MODAL);
                signUpStage.initOwner(signinButton.getScene().getWindow());
                signUpStage.initStyle(StageStyle.UTILITY);
                signUpStage.setTitle("Sign Up");

                Scene loginScene = new Scene(signupRoot);
                signUpStage.setScene(loginScene);

                signUpStage.setOnHidden(event -> signUpStage = null);
            }

            signUpStage.centerOnScreen();
            signUpStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDashboard() {
        try {
            if (DashboardStage == null) {
                Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Dashboard.fxml"));
                Scene dashboardScene = new Scene(dashboardRoot);

                Stage currentStage = (Stage) signinButton.getScene().getWindow();
                currentStage.close();

                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Dashboard");
                dashboardStage.setScene(dashboardScene);

                dashboardStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
