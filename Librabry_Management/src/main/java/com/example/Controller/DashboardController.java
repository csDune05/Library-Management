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
        GridPane gridPane = new GridPane(); // Tạo GridPane mới
        gridPane.setVgap(10); // Khoảng cách dọc giữa các hàng

        List<Book> sampleBooks = DatabaseHelper.getTopRateBooks(); // Lấy 3 sách mẫu

        for (int i = 0; i < sampleBooks.size(); i++) {
            Book book = sampleBooks.get(i);
            VBox bookCard = createBookCard(book); // Tạo BookCard cho từng sách
            gridPane.add(bookCard, 0, i); // Thêm vào cột 0, hàng i
        }

        bookCardContainer.getChildren().add(gridPane); // Thêm GridPane vào VBox
    }


    private VBox createBookCard(Book book) {
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(100);
        thumbnail.setFitHeight(150);

        CompletableFuture.runAsync(() -> {
            Image image = new Image(book.getThumbnailUrl(), true);
            Platform.runLater(() -> thumbnail.setImage(image));
        });

        Label title = new Label(book.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label author = new Label("By: " + book.getAuthor());
        author.setStyle("-fx-font-size: 12px;");

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setOnAction(e -> viewBookDetails(book));
        viewDetailsButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        viewDetailsButton.setOnMouseEntered(event -> {
            viewDetailsButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-border-radius: 5px;");
        });
        viewDetailsButton.setOnMouseExited(event -> {
            viewDetailsButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5px;");
        });

        VBox card = new VBox(10, thumbnail, title, author, viewDetailsButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-background-color: #f5fcff; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
        );

        // Thêm hiệu ứng hover cho card
        card.setOnMouseEntered(event -> {
            card.setStyle(
                    "-fx-border-color: #007bff; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #e6f7ff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 0, 4);"
            );
        });

        card.setOnMouseExited(event -> {
            card.setStyle(
                    "-fx-border-color: lightgray; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-background-color: #f5fcff; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
            );
        });

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
