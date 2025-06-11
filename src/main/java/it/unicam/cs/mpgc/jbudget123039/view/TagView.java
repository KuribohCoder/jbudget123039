package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.TagController;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.TagService;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TagView {

    @FXML
    private TreeView<Tag> tagTreeView;

    private final TagRepository tagRepository = new TagRepository();
    private final TagService tagService = new TagService(tagRepository);
    private final TagController tagController = new TagController(tagService);


    @FXML
    public void initialize() {
        tagTreeView.setEditable(true);

        tagTreeView.setCellFactory(tv -> new TextFieldTreeCell<>(new StringConverter<>() {
            @Override
            public String toString(Tag tag) {
                return tag == null ? "" : tag.getName();
            }

            @Override
            public Tag fromString(String newName) {
                TreeItem<Tag> selectedItem = tagTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Tag tag = selectedItem.getValue();
                    tag.setName(newName);

                    tagController.saveOrUpdateTagAsync(tag)
                            .exceptionally(ex -> {
                                ex.printStackTrace();
                                Platform.runLater(() -> showError("Errore nel salvataggio del tag"));
                                return null;
                            });

                    return tag;
                }
                return null;
            }
        }));

        loadTagHierarchy();
    }

    private void loadTagHierarchy() {
        tagController.loadAllTagsWithChildrenAsync()
                .thenAccept(tags -> Platform.runLater(() -> {
                    List<Tag> rootTags = tags.stream()
                            .filter(tag -> tag.getParent() == null)
                            .collect(Collectors.toList());

                    TreeItem<Tag> rootItem = new TreeItem<>(new Tag("ROOT"));
                    rootItem.setExpanded(true);

                    for (Tag root : rootTags) {
                        rootItem.getChildren().add(buildTreeItem(root));
                    }

                    tagTreeView.setRoot(rootItem);
                    tagTreeView.setShowRoot(false);
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento dei tag"));
                    return null;
                });
    }

    private TreeItem<Tag> buildTreeItem(Tag tag) {
        TreeItem<Tag> item = new TreeItem<>(tag);
        item.setExpanded(true);

        for (Tag child : tag.getChildren()) {
            item.getChildren().add(buildTreeItem(child));
        }

        return item;
    }

    @FXML
    private void handleAddRootTag() {
        Tag newTag = new Tag("Nuovo Tag");

        tagController.saveOrUpdateTagAsync(newTag)
                .thenRun(this::loadTagHierarchy)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nell'aggiunta del tag"));
                    return null;
                });
    }

    @FXML
    private void handleAddChildTag() {
        TreeItem<Tag> selected = tagTreeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un tag a cui aggiungere un figlio");
            return;
        }

        Tag parent = selected.getValue();
        Tag newTag = new Tag("Nuovo Sottotag");
        newTag.setParent(parent);

        tagController.saveOrUpdateTagAsync(newTag)
                .thenRun(this::loadTagHierarchy)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nell'aggiunta del sottotag"));
                    return null;
                });
    }

    @FXML
    private void handleDeleteTag() {
        TreeItem<Tag> selected = tagTreeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue().getId() == null) {
            showError("Seleziona un tag da eliminare");
            return;
        }

        UUID id = selected.getValue().getId();
        tagController.deleteTagAsync(id)
                .thenRun(this::loadTagHierarchy)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nella cancellazione del tag"));
                    return null;
                });
    }

    @FXML
    private void handleBackToMain() {
        Stage stage = (Stage) tagTreeView.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }
}