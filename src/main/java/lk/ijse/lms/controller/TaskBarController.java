package lk.ijse.lms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.lms.model.Note;
import lk.ijse.lms.model.User;
import lk.ijse.lms.model.tm.NoteTm;
import lk.ijse.lms.repository.NoteRepository;
import lk.ijse.lms.repository.TaskBarRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TaskBarController {
    @FXML
    private TableColumn<?, ?> columnNote;

    @FXML
    private TableView<NoteTm> tableNote;


    private AnchorPane dashboardRoot;

    private User user;

    private AddNoteController addNoteController;

    private List<Note> noteList;

    private ObservableList<NoteTm> tmList;



    public void initialize() {
    }

    private void loadNoteTable() {
        tmList = FXCollections.observableArrayList();

        for (Note note : noteList) {

            NoteTm noteTm = new NoteTm(
                   note.getDescription()
            );

            tmList.add(noteTm);
        }
        tableNote.setItems(tmList);
        tableNote.refresh();
    }

    private void setCellValueFactory() {
        columnNote.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private List<Note> getAllNotes() {
        List<Note> noteList = null;
        try {
            noteList = TaskBarRepository.getAll(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return noteList;
    }

    public void refreshTableView() {
        this.noteList = getAllNotes();
        loadNoteTable();

    }

    public void setUser(User user) {
        this.user = user;
        this.noteList = getAllNotes();
        setCellValueFactory();
        loadNoteTable();
    }

    @FXML
    void btnAddNoteOnAction(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNote.fxml"));
        Parent rootNode = null;
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addNoteController = loader.getController();
        addNoteController.setUser(user);
        addNoteController.setTaskBarController(this);

        Scene scene = new Scene(rootNode);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Note Form");
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL); // Set as modal

        stage.showAndWait();

        refreshTableView();
    }

    @FXML
    void btnRemoveNoteOnAction(ActionEvent event) {
        // Get the selected item from the TableView
        NoteTm selectedNote = tableNote.getSelectionModel().getSelectedItem();

        if (selectedNote != null) {
            // Remove the item from the UI
            tmList.remove(selectedNote);
            tableNote.refresh(); // Refresh the TableView

            // Remove the item from the database
            removeNoteFromDatabase(selectedNote.getDescription());
        } else {
            // Handle if no item is selected
            new Alert(Alert.AlertType.WARNING, "Nothing selected!").show();
        }


    }

    private void removeNoteFromDatabase(String description) {
        try {
            boolean isRemoved = NoteRepository.deleteNoteByDescription(user, description);
            if (isRemoved){
                new Alert(Alert.AlertType.CONFIRMATION, "Note removed successfully!!").show();
                refreshTableView();
            } else {
                new Alert(Alert.AlertType.ERROR, "Note not removed!!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void setRootNode(AnchorPane rootNode) {
        dashboardRoot = rootNode;
    }
}
