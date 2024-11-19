package com.example.librabry_management;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
    public void initialize() {

        titleLabel.setText("Dashboard");

        timeFilterComboBox.getItems().addAll("Weekly", "Monthly", "Yearly");
        optionsComboBox.getItems().addAll("Dashboard", "My Profile", "Log out");

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

        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon 12", "Tue 13", "Wed 14", "Thu 15", "Fri 16", "Sat 17", "Sun 18"
        ));

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(120);
        yAxis.setTickUnit(20);


        XYChart.Series<String, Number> visitors = new XYChart.Series<>();
        visitors.setName("Visitors");
        visitors.getData().add(new XYChart.Data<>("Mon 12", 60));
        visitors.getData().add(new XYChart.Data<>("Tue 13", 40));
        visitors.getData().add(new XYChart.Data<>("Wed 14", 50));
        visitors.getData().add(new XYChart.Data<>("Thu 15", 70));
        visitors.getData().add(new XYChart.Data<>("Fri 16", 100));
        visitors.getData().add(new XYChart.Data<>("Sat 17", 90));
        visitors.getData().add(new XYChart.Data<>("Sun 18", 85));

        XYChart.Series<String, Number> borrowers = new XYChart.Series<>();
        borrowers.setName("Borrowers");
        borrowers.getData().add(new XYChart.Data<>("Mon 12", 20));
        borrowers.getData().add(new XYChart.Data<>("Tue 13", 10));
        borrowers.getData().add(new XYChart.Data<>("Wed 14", 15));
        borrowers.getData().add(new XYChart.Data<>("Thu 15", 30));
        borrowers.getData().add(new XYChart.Data<>("Fri 16", 70));
        borrowers.getData().add(new XYChart.Data<>("Sat 17", 60));
        borrowers.getData().add(new XYChart.Data<>("Sun 18", 55));

        visitorChart.getData().addAll(visitors, borrowers);

        // Cấu hình các cột của TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("member"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        overdueColumn.setCellValueFactory(new PropertyValueFactory<>("overdue"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        // Thêm dữ liệu vào TableView
        ObservableList<LoanRecord> overdueList = FXCollections.observableArrayList(
                new LoanRecord("88934231", "Danielle Rusadi", "The Midnight Line", "Lee Child", "2 days", "Jun 14, 2022"),
                new LoanRecord("88934231", "Eleanor Amantis", "Henry V", "William Shakespeare", "2 days", "Jun 10, 2022"),
                new LoanRecord("88934233", "Michael Smith", "1984", "George Orwell", "3 days", "Jun 12, 2022"),
                new LoanRecord("88934234", "Anna Johnson", "Pride and Prejudice", "Jane Austen", "5 days", "Jun 8, 2022"),
                new LoanRecord("88934235", "John Doe", "To Kill a Mockingbird", "Harper Lee", "1 day", "Jun 15, 2022")
        );

        overdueTableView.setItems(overdueList);
    }
}
