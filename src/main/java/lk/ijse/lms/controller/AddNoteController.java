package lk.ijse.lms.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.lms.model.Note;
import lk.ijse.lms.model.User;
import lk.ijse.lms.repository.NoteRepository;
import lk.ijse.lms.util.Regex;
import lk.ijse.lms.util.TextField;
import lombok.SneakyThrows;

public class AddNoteController {
    private User user;

    @FXML
    private AnchorPane addNotePane;

    @FXML
    private JFXButton btnRegistor;

    @FXML
    private JFXTextField txtNote;

    private TaskBarController taskBarController;


    public void setTaskBarController(TaskBarController taskBarController) {
        this.taskBarController = taskBarController;
    }

    @SneakyThrows
    @FXML
    void btnAddOnAction(ActionEvent event) {
        String note = txtNote.getText();

        if (note != null && !note.isEmpty()) {
            if(Regex.setTextColor(TextField.NOTE,txtNote)){
                if(!NoteRepository.isNoteExist(note, user.getUserId())){
                    Note noteObj = new Note(note);
                    boolean isAdded = NoteRepository.add(noteObj, user.getUserId());
                    if(isAdded){
                        new Alert(Alert.AlertType.CONFIRMATION, "Note added successfully!").show();
                        // Refresh the TableView in the TaskBarController
                        taskBarController.refreshTableView();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Note not added!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Note already exist!").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Input exceeds 32 characters!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Enter a note!").show();
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    void txtNoteOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.NOTE,txtNote);
    }
}
