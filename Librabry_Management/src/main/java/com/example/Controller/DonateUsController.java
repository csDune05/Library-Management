package com.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.librabry_management.*;
import com.example.Feature.*;


public class DonateUsController {
    @FXML
    private Button homeButton;

    @FXML
    private Button booksButton;

    @FXML
    private Button myLibraryButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button clearNotificationsButton;

    @FXML
    private ComboBox<String> optionsComboBox;

    @FXML
    private ImageView notificationImageView;

    @FXML
    private Button notificationButton;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private ScrollPane notificationScrollPane;

    @FXML
    private VBox notificationList;

    @FXML
    private TextArea notificationText;

    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    public void initialize() {
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }


    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    @FXML
    public void ProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    public void BooksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    @FXML
    public void LogOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    @FXML
    public void ClearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }
}
