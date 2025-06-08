package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.TagController;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.*;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.List;


public class TagView {

    @FXML
    private TableView<TagEntity> tagTable;
    @FXML
    private TableColumn<TagEntity, String> colName;
    @FXML
    private TextField newTagName;

    private final TagController tagController = new TagController();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            TagEntity tag = event.getRowValue();
            tag.setName(event.getNewValue());
        });
        tagTable.setEditable(true);

        loadTags();
    }

    private void loadTags() {
        tagController.loadAllTagsWithChildrenAsync()
                .thenAccept(tags -> Platform.runLater(() -> tagTable.getItems().setAll(tags)))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    @FXML
    private void handleAddTag() {
        String name = newTagName.getText().trim();
        if (name.isEmpty()) return;

        TagEntity newTag = new TagEntity();
        newTag.setName(name);
        tagController.saveOrUpdateTagAsync(newTag)
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        newTagName.clear();
                        loadTags();
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    @FXML
    private void handleDeleteTag() {
        TagEntity selected = tagTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un tag da eliminare.");
            return;
        }

        tagController.deleteTagAsync(selected.getId())
                .thenRun(this::loadTags)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nell'eliminazione del tag."));
                    return null;
                });
    }

    @FXML
    private void handleSaveChanges() {
        List<TagEntity> tags = tagTable.getItems();
        for (TagEntity tag : tags) {
            tagController.saveOrUpdateTagAsync(tag).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
    }

    @FXML
    private void handleBackToMain() {
        Stage stage = (Stage) tagTable.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }
}