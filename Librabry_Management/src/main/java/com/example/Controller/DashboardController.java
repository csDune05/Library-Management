package com.example.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.example.librabry_management.*;

import java.io.BufferedReader;
import java.io.FileReader;

public class DashboardController {

    @FXML
    private BarChart<String, Number> visitorChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label titleLabel;

    @FXML
    private ComboBox<String> timeFilterComboBox;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private TableView<LoanRecord> overdueTableView;

    @FXML
    private TableColumn<LoanRecord, String> idColumn;

    @FXML
    private TableColumn<LoanRecord, String> memberColumn;

    @FXML
    private TableColumn<LoanRecord, String> titleColumn;

    @FXML
    private TableColumn<LoanRecord, String> authorColumn;

    @FXML
    private TableColumn<LoanRecord, String> overdueColumn;

    @FXML
    private TableColumn<LoanRecord, String> returnDateColumn;

    @FXML
    private Button booksButton;

    @FXML
    private Button donateUsButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button myLibraryButton;

    @FXML
    private Label searchLabel;

    @FXML
    private Label readLabel;

    @FXML
    private Label visitTimesLabel;

    @FXML
    private Label membersLabel;

    @FXML
    public void initialize() {

        titleLabel.setText("Dashboard");

        timeFilterComboBox.getItems().addAll("Weekly", "Monthly", "Yearly");
        optionsComboBox.getItems().addAll("My Profile", "Log out");

        // bắt sự kiện nếu Options là log out thì thoát.
        optionsComboBox.setOnAction(event -> {
            String selectedOption = optionsComboBox.getValue();
            if (selectedOption.equals("Log out")) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmation");
                confirmationAlert.setHeaderText("Are you sure you want to log out?");
                confirmationAlert.setContentText("Press OK to log out, or Cancel to stay.");

                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        optionsComboBox.getScene().getWindow().hide();
                        MainStaticObjectControl.openWelcomeStage();
                    } else {
                        optionsComboBox.setValue(null);
                    }
                });
            }
        });

        timeFilterComboBox.setOnAction(event -> {
            String selectedValue = timeFilterComboBox.getValue();
            System.out.println("Selected filter: " + selectedValue);
        });

        updateVisitorChart();
        updateLabels();
        configureTableView();
    }

    @FXML
    public void ProfileButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Profile.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) profileButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void myLibraryButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/MyLibrary.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) profileButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void BooksButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/Book.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) booksButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DonateUsButtonHandler() {
        try {
            Parent booksRoot = FXMLLoader.load(getClass().getResource("/com/example/librabry_management/DonateUs.fxml"));
            Scene booksScene = new Scene(booksRoot);

            Stage stage = (Stage) booksButton.getScene().getWindow();

            stage.setScene(booksScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVisitorChart() {
        int searchCount = readCountFromFile("countSearch.txt");
        int readCount = readCountFromFile("countRead.txt");
        int visitCount = readCountFromFile("countVisit.txt");
        int memberCount = readCountFromFile("countMembers.txt");

        visitorChart.getData().clear();

        XYChart.Series<String, Number> searchSeries = new XYChart.Series<>();
        searchSeries.setName("Search");
        searchSeries.getData().add(new XYChart.Data<>("Search", searchCount));

        XYChart.Series<String, Number> readSeries = new XYChart.Series<>();
        readSeries.setName("Read");
        readSeries.getData().add(new XYChart.Data<>("Read", readCount));

        XYChart.Series<String, Number> visitsSeries = new XYChart.Series<>();
        visitsSeries.setName("Visit times");
        visitsSeries.getData().add(new XYChart.Data<>("Visit times", visitCount));

        XYChart.Series<String, Number> membersSeries = new XYChart.Series<>();
        membersSeries.setName("Members");
        membersSeries.getData().add(new XYChart.Data<>("Members", memberCount));

        visitorChart.getData().addAll(searchSeries, readSeries, visitsSeries, membersSeries);
        xAxis.setTickLabelsVisible(false);
    }

    private void updateLabels() {
        searchLabel.setText(String.valueOf(readCountFromFile("countSearch.txt")));
        readLabel.setText(String.valueOf(readCountFromFile("countRead.txt")));
        visitTimesLabel.setText(String.valueOf(readCountFromFile("countVisit.txt")));
        membersLabel.setText(String.valueOf(readCountFromFile("countMembers.txt")));
    }

    private int readCountFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void configureTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("member"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        overdueColumn.setCellValueFactory(new PropertyValueFactory<>("overdue"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        ObservableList<LoanRecord> overdueList = FXCollections.observableArrayList(
                new LoanRecord("88934231", "Danielle Rusadi", "The Midnight Line", "Lee Child", "2 days", "Jun 14, 2022"),
                new LoanRecord("88934232", "Eleanor Amantis", "Henry V", "William Shakespeare", "2 days", "Jun 10, 2022"),
                new LoanRecord("88934233", "Michael Smith", "1984", "George Orwell", "3 days", "Jun 12, 2022"),
                new LoanRecord("88934234", "Anna Johnson", "Pride and Prejudice", "Jane Austen", "5 days", "Jun 8, 2022"),
                new LoanRecord("88934235", "John Doe", "To Kill a Mockingbird", "Harper Lee", "1 day", "Jun 15, 2022")
        );

        overdueTableView.setItems(overdueList);
    }
}
