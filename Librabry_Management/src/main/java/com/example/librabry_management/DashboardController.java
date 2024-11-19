package com.example.librabry_management;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

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
    public void initialize() {

        titleLabel.setText("Dashboard");

        timeFilterComboBox.getItems().addAll("Weekly", "Monthly", "Yearly");

        timeFilterComboBox.setOnAction(event -> {
            String selectedValue = timeFilterComboBox.getValue();
            System.out.println("Selected filter: " + selectedValue);
        });

        xAxis.setLabel("Days");
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));


        yAxis.setLabel("Count");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(200);
        yAxis.setTickUnit(50);

        XYChart.Series<String, Number> visitors = new XYChart.Series<>();
        visitors.setName("Visitors");
        visitors.getData().add(new XYChart.Data<>("Mon", 100));
        visitors.getData().add(new XYChart.Data<>("Tue", 120));
        visitors.getData().add(new XYChart.Data<>("Wed", 80));

        XYChart.Series<String, Number> borrowers = new XYChart.Series<>();
        borrowers.setName("Borrowers");
        borrowers.getData().add(new XYChart.Data<>("Mon", 50));
        borrowers.getData().add(new XYChart.Data<>("Tue", 60));
        borrowers.getData().add(new XYChart.Data<>("Wed", 40));

        visitorChart.getData().addAll(visitors, borrowers);
    }
}
