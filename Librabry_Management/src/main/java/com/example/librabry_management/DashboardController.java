package com.example.librabry_management;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label titleLabel;

    @FXML
    private ComboBox<String> timeFilterComboBox;

    @FXML
    public void initialize() {
        // Gán tiêu đề mặc định
        titleLabel.setText("Dashboard");

        // Thêm giá trị cho ComboBox nếu không làm trong Scene Builder
        timeFilterComboBox.getItems().addAll("Weekly", "Monthly", "Yearly");

        // Xử lý sự kiện chọn giá trị trong ComboBox
        timeFilterComboBox.setOnAction(event -> {
            String selectedValue = timeFilterComboBox.getValue();
            System.out.println("Selected filter: " + selectedValue);
        });
    }
}
