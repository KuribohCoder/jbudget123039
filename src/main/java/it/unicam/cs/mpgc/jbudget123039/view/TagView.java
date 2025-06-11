package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.TagController;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.*;
import java.util.stream.Collectors;

public class TagView {

    @FXML
    private TreeView<TagEntity> tagTreeView;

    private final TagController tagController = new TagController();

    @FXML
    public void initialize() {
        tagTreeView.setEditable(true);

        // Imposta la cell factory con il converter
        tagTreeView.setCellFactory(tv -> new TextFieldTreeCell<>(new StringConverter<>() {
            @Override
            public String toString(TagEntity tag) {
                return tag == null ? "" : tag.getName();
            }

            @Override
            public TagEntity fromString(String string) {
                // non usato qui, può restare null o come vuoi
                return null;
            }
        }));

        // Gestisci commit editing a livello di TreeView (non sulla cella)
        tagTreeView.setCellFactory(tv -> new TextFieldTreeCell<>(new StringConverter<TagEntity>() {
            @Override
            public String toString(TagEntity tag) {
                return tag == null ? "" : tag.getName();
            }

            @Override
            public TagEntity fromString(String newName) {
                TreeItem<TagEntity> selectedItem = tagTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    TagEntity tag = selectedItem.getValue();
                    tag.setName(newName);

                    // Salva la modifica
                    tagController.saveOrUpdateTagAsync(tag).exceptionally(ex -> {
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
                    // Costruisci mappa ID->TagEntity
                    Map<UUID, TagEntity> tagMap = tags.stream()
                            .collect(Collectors.toMap(TagEntity::getId, t -> t));

                    // Costruisci radici (tag senza parent)
                    List<TagEntity> roots = new ArrayList<>();
                    for (TagEntity tag : tags) {
                        if (tag.getParent() == null) {
                            roots.add(tag);
                        }
                    }

                    // Costruisci albero
                    TreeItem<TagEntity> rootItem = new TreeItem<>(new TagEntity());
                    rootItem.setExpanded(true);

                    for (TagEntity rootTag : roots) {
                        rootItem.getChildren().add(buildTreeItem(rootTag, tagMap));
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

    private TreeItem<TagEntity> buildTreeItem(TagEntity tag, Map<UUID, TagEntity> tagMap) {
        TreeItem<TagEntity> item = new TreeItem<>(tag);
        item.setExpanded(true);

        // Aggiungi figli
        for (TagEntity child : tag.getChildren()) {
            item.getChildren().add(buildTreeItem(child, tagMap));
        }
        return item;
    }

    @FXML
    private void handleAddRootTag() {
        TagEntity newTag = new TagEntity();
        newTag.setName("Nuovo Tag");
        newTag.setParent(null);

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
        TreeItem<TagEntity> selected = tagTreeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un tag a cui aggiungere un figlio");
            return;
        }
        TagEntity parent = selected.getValue();

        TagEntity newTag = new TagEntity();
        newTag.setName("Nuovo Sottotag");
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
        TreeItem<TagEntity> selected = tagTreeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue().getId() == null) {
            showError("Seleziona un tag da eliminare");
            return;
        }

        TagEntity tag = selected.getValue();
        tagController.deleteTagAsync(tag.getId())
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