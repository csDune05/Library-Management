package com.example.Controller;

import com.example.librabry_management.*;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField; // Trường để hiển thị mật khẩu

    @FXML
    private Button togglePasswordButton; // Nút để ẩn/hiện mật khẩu

    @FXML
    private Button signinButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button signupButton;

    @FXML
    private Button forgotPasswordButton; // Nút để quên mật khẩu

    @FXML
    private CheckBox staysignedin;

    @FXML
    private ListView<String> accountListView;

    private Preferences prefs;

    private Stage signUpStage;
    private Stage DashboardStage;
    private Stage forgotPasswordStage;

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

        // Sự kiện hiển thị hoặc ẩn mật khẩu
        togglePasswordButton.setOnAction(event -> {
            if (passwordField.isVisible()) {
                // Đổi sang hiển thị mật khẩu
                passwordTextField.setText(passwordField.getText());
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                togglePasswordButton.setText("🙈"); // Đổi biểu tượng sang "ẩn mật khẩu"
            } else {
                // Đổi sang ẩn mật khẩu
                passwordField.setText(passwordTextField.getText());
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                togglePasswordButton.setText("👁"); // Đổi biểu tượng sang "hiển mật khẩu"
            }
        });

        // Đồng bộ hóa nội dung giữa passwordField và passwordTextField
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

        // Sự kiện khi nhấn nút 'Forgot Password?'
        forgotPasswordButton.setOnAction(event -> handleForgotPassword());
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
    public void SignInButtonHandle() {
        String email = emailField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        try {
            User user = DatabaseHelper.getUserByEmail(email); // Tìm người dùng theo email
            if (user != null && user.getPassword().equals(password)) {
                statusLabel.setText("Login Successful!");
                MainStaticObjectControl.setCurrentUser(user); // Lưu thông tin User hiện tại

                // Ghi nhớ tài khoản nếu người dùng chọn "Stay signed in"
                if (staysignedin.isSelected()) {
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

    // cap nhat visit times
    private void updateVisitCount() throws IOException {
        int currentCount = readVisitCount();
        currentCount++;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("countVisit.txt"))) {
            writer.write(String.valueOf(currentCount));
        }
    }

    // dem so lan vao app
    private int readVisitCount() throws IOException {
        File countFile = new File("countVisit.txt");

        if (!countFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(countFile))) {
                writer.write("0");
            }
        }

        // Đọc giá trị từ file
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

    @FXML
    public void handleForgotPassword() {
        try {
            if (forgotPasswordStage == null) {
                Parent forgotPasswordRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/ForgotPasswordForm.fxml"));

                forgotPasswordStage = new Stage();
                forgotPasswordStage.initModality(Modality.WINDOW_MODAL);
                forgotPasswordStage.initOwner(signinButton.getScene().getWindow());
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

    private void openDashboard() {
        try {
            if (DashboardStage == null) {
                Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Dashboard.fxml"));
                Scene dashboardScene = new Scene(dashboardRoot);

                // Áp dụng theme từ SceneHelper
                SceneHelper.applyTheme(dashboardScene);

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
