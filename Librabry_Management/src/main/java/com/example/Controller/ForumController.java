package com.example.Controller;

import com.example.Feature.CommentStorage;
import com.example.librabry_management.MainStaticObjectControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ForumController {

    @FXML
    private Button donateUsButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button booksButton;

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

    @FXML
    private Button myLibraryButton;


    private Stage getCurrentStage() {
        return (Stage) homeButton.getScene().getWindow();
    }

    @FXML
    private VBox commentList;

    @FXML
    private TextArea commentInput;

    private List<String> comments = new ArrayList<>();

    @FXML
    public void initialize() {

        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        // notification
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);

        // Tải bình luận từ file
        comments = CommentStorage.loadComments();
        for (String comment : comments) {
            addCommentToUI(comment);
        }
    }

    @FXML
    public void addComment() {
        String commentText = commentInput.getText().trim();
        if (commentText.isEmpty()) {
            return;
        }

        // Thêm bình luận vào danh sách
        comments.add(commentText);
        addCommentToUI(commentText);

        // Lưu bình luận vào file
        CommentStorage.saveComments(comments);

        // Xóa nội dung TextArea
        commentInput.clear();
    }

    private void addCommentToUI(String commentText) {
        Text comment = new Text(commentText);
        commentList.getChildren().add(comment);
    }

    @FXML
    public void notesProfileButtonHandler() {MainStaticObjectControl.openProfileNotesStage(getCurrentStage());}

    @FXML
    public void passwordAndSecurityButtonHandler() {
        MainStaticObjectControl.openProfilePasswordAndSecurityStage(getCurrentStage());}

    @FXML
    public void editProfileButtonHandler() {
        MainStaticObjectControl.openProfileStage(getCurrentStage());
    }

    @FXML
    public void myLibraryButtonHandler() {
        MainStaticObjectControl.openLibraryStage(getCurrentStage());
    }

    @FXML
    public void HomeButtonHandler() {
        MainStaticObjectControl.openDashboardStage(getCurrentStage());
    }

    @FXML
    public void DonateUsButtonHandler() {
        MainStaticObjectControl.openDonateStage(getCurrentStage());
    }

    @FXML
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

    @FXML
    public void notificationButtonHandler() {
        MainStaticObjectControl.showAnchorPane(notificationPane, notificationButton);
        if(!notificationPane.isVisible()) MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);
    }
}
