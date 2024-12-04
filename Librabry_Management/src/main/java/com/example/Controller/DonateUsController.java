package com.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.librabry_management.*;


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

    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    /**
     * Initialize donate us scene.
     */
    public void initialize() {
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }

    /**
     * Handle show notification event.
     */
    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }


    /**
     * Handle switch to my library event.
     */
    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    /**
     * Handle switch to my profile event.
     */
    @FXML
    public void profileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    /**
     * Handle switch to home event.
     */
    public void homeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    /**
     * Handle switch to books event.
     */
    public void booksButtonHandler() {
        MainStaticObjectControl.openBookStage(getCurrentStage());
    }

    /**
     * Handle exit event.
     */
    @FXML
    public void logOutButtonHandler() {
        MainStaticObjectControl.logOut(getCurrentStage());
    }

    /**
     * Handle clear all notification  event.
     */
    @FXML
    public void clearALlButtonHandler() {
        MainStaticObjectControl.clearAllNotificationsForUser();
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }
}
