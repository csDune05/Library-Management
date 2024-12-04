package com.example.Controller;

import com.example.librabry_management.*;
import com.example.Feature.*;
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
import java.io.*;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Button signInButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button signUpButton;

    @FXML
    private Button forgotPasswordButton;

    @FXML
    private CheckBox staySignedIn;

    @FXML
    private ListView<String> accountListView;

    private Preferences preferences;
    private Stage signUpStage;
    private Stage dashboardStage;
    private Stage forgotPasswordStage;
    private ObservableList<String> accountList;

    /**
     * @throws JSONException
     * Initialize the initial login scene.
     */
    @FXML
    public void initialize() throws JSONException {
        preferences = Preferences.userNodeForPackage(LoginController.class);
        accountList = FXCollections.observableArrayList();
        loadSavedAccounts();

        // Recommended saved accounts.
        accountListView.setVisible(false);
        accountListView.setOnMouseEntered(event -> accountListView.setVisible(true));
        accountListView.setOnMouseExited(event -> accountListView.setVisible(false));
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

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                filterAccounts(newValue);
            } else {
                accountListView.setItems(accountList);
                accountListView.setVisible(!accountList.isEmpty());
            }
        });

        togglePasswordButton.setOnAction(event -> {
            if (passwordField.isVisible()) {
                passwordTextField.setText(passwordField.getText());
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                togglePasswordButton.setText("🙈");
            } else {
                passwordField.setText(passwordTextField.getText());
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                togglePasswordButton.setText("👁");
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordField.isVisible()) {
                passwordTextField.setText(newValue);
            }
        });
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordTextField.isVisible()) {
                passwordField.setText(newValue);
            }
        });

        emailField.setOnMouseEntered(event -> {
            if (!emailField.getText().isEmpty() && !accountListView.getItems().isEmpty()) {
                accountListView.setVisible(true);
            }
        });

        emailField.setOnMouseExited(event -> {
            if (!accountListView.isHover()) {
                accountListView.setVisible(false);
            }
        });

        forgotPasswordButton.setOnAction(event -> forgotPasswordButtonHandle());
    }

    /**
     * @throws JSONException
     * Load saved accounts.
     */
    public void loadSavedAccounts() throws JSONException {
        String accountsJson = preferences.get("accounts", "[]");
        JSONArray accounts = new JSONArray(accountsJson);

        accountList.clear();
        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            accountList.add(account.getString("username"));
        }

        accountListView.setItems(accountList);

        if (!accountList.isEmpty()) {
            accountListView.getSelectionModel().select(0);
            populateAccountDetails(accountListView.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * @throws JSONException
     * Information of saved accounts.
     */
    public void populateAccountDetails(String username) throws JSONException {
        String accountsJson = preferences.get("accounts", "[]");
        JSONArray accounts = new JSONArray(accountsJson);

        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            if (account.getString("username").equals(username)) {
                emailField.setText(account.getString("username"));
                passwordField.setText(account.optString("password", ""));
                staySignedIn.setSelected(account.optBoolean("rememberMe", false));
                break;
            }
        }
    }

    /**
     * @param searchText
     * Account filtering.
     */
    public void filterAccounts(String searchText) {
        ObservableList<String> filteredList = FXCollections.observableArrayList();
        for (String account : accountList) {
            if (account.toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(account);
            }
        }
        accountListView.setItems(filteredList);
        accountListView.setVisible(!filteredList.isEmpty());
    }

    /**
     * Handle sign in event.
     */
    @FXML
    public void signInButtonHandle() {
        String email = emailField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        try {
            User user = DatabaseHelper.getUserByEmail(email);
            if (user != null && user.getPassword().equals(password)) {
                statusLabel.setText("Login Successful!");
                MainStaticObjectControl.setCurrentUser(user);
                if (staySignedIn.isSelected()) {
                    saveAccount(user.getEmail(), user.getPassword(), true);
                } else {
                    saveAccount(user.getEmail(), "", false);
                }
                MainStaticObjectControl.closeWelcomeStage();
                updateVisitCount();
                openDashboard();
            } else {
                statusLabel.setText("Email or Password is incorrect!");
            }
        } catch (Exception e) {
            statusLabel.setText("An error occurred while logging in.");
            e.printStackTrace();
        }
    }

    /**
     * @throws JSONException
     * Save account.
     */
    public void saveAccount(String username, String password, boolean rememberMe) throws JSONException {
        String accountsJson = preferences.get("accounts", "[]");
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

        preferences.put("accounts", accounts.toString());
        loadSavedAccounts();
    }

    /**
     * @throws IOException
     * Update visit times in countVisit.
     */
    public void updateVisitCount() throws IOException {
        int currentCount = readVisitCount();
        currentCount++;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("countVisit.txt"))) {
            writer.write(String.valueOf(currentCount));
        }
    }

    /**
     * @throws IOException
     * Read visit times from file.
     */
    public int readVisitCount() throws IOException {
        File countFile = new File("countVisit.txt");
        if (!countFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(countFile))) {
                writer.write("0");
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(countFile))) {
            String line = reader.readLine(); // Đọc dòng đầu tiên
            if (line == null || line.trim().isEmpty()) {
                return 0;
            }
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Handle cancel event.
     */
    @FXML
    public void cancelButtonHandle() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handle switch to sign up stage event.
     */
    @FXML
    public void signUpButtonHandle() {
        try {
            if (signUpStage == null) {
                Parent signupRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/SignUp.fxml"));

                signUpStage = new Stage();
                signUpStage.initModality(Modality.WINDOW_MODAL);
                signUpStage.initOwner(signInButton.getScene().getWindow());
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

    /**
     * Handle forgot password event.
     */
    @FXML
    public void forgotPasswordButtonHandle() {
        try {
            if (forgotPasswordStage == null) {
                Parent forgotPasswordRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/ForgotPasswordForm.fxml"));

                forgotPasswordStage = new Stage();
                forgotPasswordStage.initModality(Modality.WINDOW_MODAL);
                forgotPasswordStage.initOwner(signInButton.getScene().getWindow());
                forgotPasswordStage.initStyle(StageStyle.UTILITY);
                forgotPasswordStage.setTitle("Forgot Password");

                Scene forgotPasswordScene = new Scene(forgotPasswordRoot);
                forgotPasswordStage.setScene(forgotPasswordScene);

                forgotPasswordStage.setOnHidden(event -> forgotPasswordStage = null);
            }

            forgotPasswordStage.centerOnScreen();
            forgotPasswordStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open dashboard stage.
     */
    public void openDashboard() {
        try {
            if (dashboardStage == null) {
                Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Dashboard.fxml"));
                Scene dashboardScene = new Scene(dashboardRoot);
                SceneHelper.applyTheme(dashboardScene);
                Stage currentStage = (Stage) signInButton.getScene().getWindow();
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
