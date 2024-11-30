package com.example.Controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.librabry_management.*;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DashboardController {

    @FXML
    private Label newMemberLabel;

    @FXML
    private Label totalBookLabel;

    @FXML
    private BarChart<String, Number> visitorChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label titleLabel;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private ImageView notificationImageView;

    @FXML
    private Button notificationButton;

    @FXML
    private TableView<LoanRecord> loanRecordTableView;

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
    private Button logoutButton;

    @FXML
    private Button clearNotificationsButton;

    @FXML
    private Label borrowedBooksLabel;

    @FXML
    private Label overdueBooksLabel;

    @FXML
    private Label visitTimesLabel;

    @FXML
    private Label membersLabel;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private ScrollPane notificationScrollPane;

    @FXML
    private VBox notificationList;

    @FXML
    private VBox bookCardContainer;

    @FXML
    private Scene dashboardScene;

    @FXML
    private TextArea notificationText;

    private Stage getCurrentStage() {
        return (Stage) booksButton.getScene().getWindow();
    }

    @FXML
    public void initialize() {

        totalBookLabel.setText(String.valueOf(DatabaseHelper.getTotalBook()));
        newMemberLabel.setText(String.valueOf(DatabaseHelper.getNewMember()));
        totalBookLabel.setStyle("-fx-font-size: 25;" + "-fx-font-weight: bold;");
        newMemberLabel.setStyle("-fx-font-size: 25;" + "-fx-font-weight: bold;");

        loadBookCards();

        Platform.runLater(() -> {
            dashboardScene = booksButton.getScene();
        });

        titleLabel.setText("Dashboard");

        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);

        updateVisitorChart();
        updateLabels();
        updateTableView();
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void ProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    @FXML
    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    private void updateVisitorChart() {
        visitorChart.getData().clear();

        int totalBorrowed = DatabaseHelper.getTotalBorrowedBooks();
        int totalOverdue = DatabaseHelper.getTotalOverdueBooks();
        int totalAccounts = DatabaseHelper.getTotalAccounts();
        int visitTimes = getVisitTimes();

        XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
        borrowedSeries.getData().add(new XYChart.Data<>("Visit times", totalBorrowed));

        XYChart.Series<String, Number> overdueSeries = new XYChart.Series<>();
        overdueSeries.getData().add(new XYChart.Data<>("Visit times", totalOverdue));

        XYChart.Series<String, Number> visitsSeries = new XYChart.Series<>();
        visitsSeries.getData().add(new XYChart.Data<>("Visit times", visitTimes));

        XYChart.Series<String, Number> accountsSeries = new XYChart.Series<>();
        accountsSeries.getData().add(new XYChart.Data<>("Visit times", totalAccounts));

        visitorChart.setBarGap(80);
        visitorChart.setCategoryGap(30);

        visitorChart.getData().addAll(borrowedSeries, overdueSeries, visitsSeries, accountsSeries);
        xAxis.setTickLabelsVisible(false);
    }

    private int getVisitTimes() {
        File file = new File("countVisit.txt");
        int visitTimes = 0;

        try {
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line = reader.readLine();
                    if (line != null) {
                        visitTimes = Integer.parseInt(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return visitTimes;
    }

    private void updateLabels() {
        int totalBorrowed = DatabaseHelper.getTotalBorrowedBooks();
        int totalOverdue = DatabaseHelper.getTotalOverdueBooks();
        int totalAccounts = DatabaseHelper.getTotalAccounts();
        int visitTimes = getVisitTimes();

        borrowedBooksLabel.setText(String.valueOf(totalBorrowed));
        overdueBooksLabel.setText(String.valueOf(totalOverdue));
        visitTimesLabel.setText(String.valueOf(visitTimes));
        membersLabel.setText(String.valueOf(totalAccounts));
    }

    private void updateTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("member"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        overdueColumn.setCellValueFactory(new PropertyValueFactory<>("overdue"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        // Lấy dữ liệu từ DatabaseHelper và cập nhật TableView
        ObservableList<LoanRecord> data = DatabaseHelper.getLoanRecords();
        loanRecordTableView.setItems(data);
    }

    private void loadBookCards() {
        Book sampleBooks = DatabaseHelper.getTopView();
        VBox bookCard = createBookCard(sampleBooks); // Tạo BookCard cho từng sách
        bookCardContainer.getChildren().add(bookCard); // Thêm GridPane vào VBox
    }


    private VBox createBookCard(Book book) {
        VBox card = BookCard.createBookCard(book, this::viewBookDetails);
        card.setPrefHeight(260);
        return card;
    }

    private void viewBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetail(book);
            detailController.setDashboardController(this);

            Stage stage = (Stage) booksButton.getScene().getWindow();
            stage.setTitle("Book Details - " + book.getTitle());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return dashboardScene;
    }
}
