package com.example.Controller;

import com.example.librabry_management.MainStaticObjectControl;
import com.example.librabry_management.Note;
import com.example.librabry_management.NotesStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NotesController {

    private boolean isEditing = false;  // Biến để kiểm tra trạng thái hiện tại (đang ở chế độ Edit hay Save)

    @FXML
    private Button addButton;

    @FXML
    private Button editSaveButton;

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
    private ListView<Note> notesListView;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private TextField titleTextField;

    private ObservableList<Note> notesList;

    @FXML
    public void initialize() {
        // combo box options
        MainStaticObjectControl.configureOptionsComboBox(optionsComboBox);
        // notification
        MainStaticObjectControl.updateNotificationIcon(notificationImageView);
        MainStaticObjectControl.updateNotifications(notificationScrollPane, notificationList);

        // Đặt nút thành "Edit" lúc đầu và các trường ở trạng thái không thể chỉnh sửa
        editSaveButton.setText("Edit");
        titleTextField.setDisable(true);
        contentTextArea.setDisable(true);

        addButton.setOnAction(event -> addNewNote());

        notesList = FXCollections.observableArrayList(NotesStorage.loadNotes());
        notesListView.setItems(notesList);

        // Thiết lập cell factory để hiển thị title của từng Note
        notesListView.setCellFactory(param -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null || note.getTitle() == null) {
                    setText(null);
                } else {
                    setText(note.getTitle());
                }
            }
        });


        // Hiển thị nội dung ghi chú khi được chọn
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldNote, newNote) -> {
            if (newNote != null) {
                titleTextField.setText(newNote.getTitle());
                contentTextArea.setText(newNote.getContent());
            }
        });
    }

    @FXML
    private void addNewNote() {
        if (isEditing) {
            // Nếu đang trong chế độ chỉnh sửa, thông báo cho người dùng phải lưu ghi chú trước khi thêm mới
            System.out.println("Please save the current note before adding a new one.");
        } else {
            // Đặt các trường thành rỗng để thêm ghi chú mới
            titleTextField.setText("");
            contentTextArea.setText("");

            // Cho phép chỉnh sửa các trường
            titleTextField.setDisable(false);
            contentTextArea.setDisable(false);

            // Tạo ghi chú mới
            Note newNote = new Note();
            newNote.setTitle("New Note");
            notesListView.getItems().add(newNote);

            // Đặt ghi chú mới là đang chọn trong ListView
            notesListView.getSelectionModel().select(newNote);

            // Đặt trạng thái nút Edit/Save thành "Save"
            isEditing = true;
            editSaveButton.setText("Save");

            // Vô hiệu hóa nút Add trong khi đang chỉnh sửa
            addButton.setDisable(true);
        }
    }

    @FXML
    private void editSaveNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            if (!isEditing) {
                // Nếu đang ở chế độ Edit
                isEditing = true;

                // Đổi nhãn nút thành "Save"
                editSaveButton.setText("Save");

                // Cho phép chỉnh sửa các trường thông tin
                titleTextField.setDisable(false);
                contentTextArea.setDisable(false);

                // Điền thông tin của Note vào các trường để người dùng chỉnh sửa
                titleTextField.setText(selectedNote.getTitle());
                contentTextArea.setText(selectedNote.getContent());

                // Vô hiệu hóa nút Add để tránh việc thêm mới khi đang chỉnh sửa
                addButton.setDisable(true);
            } else {
                // Nếu đang ở chế độ Save
                isEditing = false;

                // Đổi nhãn nút lại thành "Edit"
                editSaveButton.setText("Edit");

                // Lưu lại thay đổi cho Note
                selectedNote.setTitle(titleTextField.getText());
                selectedNote.setContent(contentTextArea.getText());

                // Vô hiệu hóa các trường thông tin sau khi đã lưu
                titleTextField.setDisable(true);
                contentTextArea.setDisable(true);

                // Cập nhật lại ListView để hiển thị thay đổi
                notesListView.refresh();

                // Cho phép nút Add hoạt động trở lại
                addButton.setDisable(false);
            }
        }
    }

    @FXML
    private void deleteNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            notesList.remove(selectedNote);
            saveNotes();
        }
    }

    public void saveNotes() {
        NotesStorage.saveNotes(notesList);
    }

    @FXML
    public void passwordAndSecurityButtonHandler() {MainStaticObjectControl.openProfilePasswordAndSecurityStage(getCurrentStage());}

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
