package com.example.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.librabry_management.*;
import com.example.Feature.*;
import java.io.*;


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
    private TextField searchField;

    @FXML
    private Button seeAllButton;

    @FXML
    private Button searchButton;

    private ObservableList<Book> bookData = FXCollections.observableArrayList();

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

    public Stage getCurrentStage() {
        return (Stage) booksButton.getScene().getWindow();
    }

    /**
     * Initialize the default dashboard scene.
     */
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

    /**
     * Handle view notifications event.
     */
    @FXML
    public void notificationButtonHandle() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) {
            MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
        }
    }

    /**
     * Handle switch to profile scene.
     */
    @FXML
    public void profileButtonHandle() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    /**
     * Handle switch to my library scene.
     */
    @FXML
    public void myLibraryButtonHandle() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    /**
     * Handle switch to books scene.
     */
    @FXML
    public void booksButtonHandle() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    /**
     * Handle switch to donate us scene.
     */
    @FXML
    public void donateUsButtonHandle() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    /**
     * Handle exit dashboard stage.
     */
    @FXML
    public void logOutButtonHandle() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    /**
     * Handle clear all notification event.
     */
    public void clearALlButtonHandle() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * update over view chart.
     */
    public void updateVisitorChart() {
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

    /**
     * @return visit times.
     * Get from file txt.
     */
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

    /**
     * Update statistics.
     */
    public void updateLabels() {
        int totalBorrowed = DatabaseHelper.getTotalBorrowedBooks();
        int totalOverdue = DatabaseHelper.getTotalOverdueBooks();
        int totalAccounts = DatabaseHelper.getTotalAccounts();
        int visitTimes = getVisitTimes();

        borrowedBooksLabel.setText(String.valueOf(totalBorrowed));
        overdueBooksLabel.setText(String.valueOf(totalOverdue));
        visitTimesLabel.setText(String.valueOf(visitTimes));
        membersLabel.setText(String.valueOf(totalAccounts));
    }

    /**
     * Update table view.
     */
    public void updateTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("member"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        overdueColumn.setCellValueFactory(new PropertyValueFactory<>("overdue"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        User currentUser = MainStaticObjectControl.getCurrentUser();

        if (currentUser == null) {
            System.out.println("Can not find current user");
            return;
        }

        String currentUserName = currentUser.getName();
        ObservableList<LoanRecord> allData = DatabaseHelper.getLoanRecords();
        ObservableList<LoanRecord> data = allData.filtered(record ->
                record.getMember().equals(currentUserName)
        );
        loanRecordTableView.setItems(data);
        if (data.isEmpty()) {
            System.out.println("No loan records found: " + currentUserName);
        }
    }

    /**
     * Search books in table view.
     */
    public void performSearch() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            updateTableView();
            return;
        }

        ObservableList<LoanRecord> allData = DatabaseHelper.getLoanRecords();
        User currentUser = MainStaticObjectControl.getCurrentUser();
        String currentUserName = currentUser.getName();

        ObservableList<LoanRecord> userData = allData.filtered(record -> record.getMember().equals(currentUserName));
        ObservableList<LoanRecord> filteredRecords = userData.filtered(record ->
                record.getTitle().toLowerCase().contains(query)
        );

        loanRecordTableView.setItems(filteredRecords);
    }

    /**
     * Handle see all data of table view event.
     */
    @FXML
    private void seeAllButtonHandle() {
        searchField.clear();
        updateTableView();
    }

    /**
     * Load card for book.
     */
    public void loadBookCards() {
        try {
            Book sampleBooks = DatabaseHelper.getTopView();
            VBox bookCard = createBookCard(sampleBooks);
            bookCardContainer.getChildren().add(bookCard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create card for book.
     */
    public VBox createBookCard(Book book) {
        try {
            VBox card = BookCard.createBookCard(book, this::viewBookDetails);
            card.setPrefHeight(260);
            return card;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handle view all information of book event.
     */
    public void viewBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librabry_management/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetail(book);
            detailController.setDashboardController(this);

            Stage stage = (Stage) booksButton.getScene().getWindow();
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
