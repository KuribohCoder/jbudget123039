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

/**
 * Controller class for managing the tag hierarchy UI.
 * <p>
 * Provides functionality for displaying, editing, adding, and deleting tags in a hierarchical
 * TreeView structure. Supports asynchronous operations through the TagController.
 * Tags can be renamed directly in the TreeView by editing their text.
 * Users can add root tags or child tags to the currently selected tag.
 * The UI updates automatically after each modification to reflect the current state of the tag hierarchy.
 * </p>
 */
public class TagView {

    @FXML
    private TreeView<Tag> tagTreeView;

    private final TagRepository tagRepository = new TagRepository();
    private final TagService tagService = new TagService(tagRepository);
    private final TagController tagController = new TagController(tagService);

    /**
     * Initializes the tag tree view.
     * Sets the tree view to editable and provides a custom cell factory
     * allowing tag names to be edited inline.
     * Loads the full tag hierarchy from the controller asynchronously.
     */
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

    /**
     * Loads the entire tag hierarchy from the controller asynchronously,
     * constructs the tree structure, and sets it as the root of the TreeView.
     * The root tag is a dummy "ROOT" node and is not shown in the UI.
     */
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

    /**
     * Recursively builds a TreeItem and its children from a Tag and its child tags.
     *
     * @param tag the tag to create a TreeItem for
     * @return the TreeItem representing the tag and its descendants
     */
    private TreeItem<Tag> buildTreeItem(Tag tag) {
        TreeItem<Tag> item = new TreeItem<>(tag);
        item.setExpanded(true);

        for (Tag child : tag.getChildren()) {
            item.getChildren().add(buildTreeItem(child));
        }

        return item;
    }

    /**
     * Adds a new root tag with a default name "Nuovo Tag".
     * Saves it asynchronously via the controller and reloads the tag hierarchy.
     */
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

    /**
     * Adds a new child tag under the currently selected tag with a default name "Nuovo Sottotag".
     * If no tag is selected, an error message is shown.
     * Saves the new tag asynchronously and reloads the hierarchy on success.
     */
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

    /**
     * Deletes the currently selected tag.
     * Shows an error if no tag is selected or the tag has no ID (likely a new unsaved tag).
     * Deletes asynchronously and reloads the tag hierarchy on success.
     */
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

    /**
     * Handles the back button action to return to the main screen.
     */
    @FXML
    private void handleBackToMain() {
        Stage stage = (Stage) tagTreeView.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }
}