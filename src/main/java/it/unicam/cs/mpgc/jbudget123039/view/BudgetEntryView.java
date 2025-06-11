package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.BudgetEntryController;
import it.unicam.cs.mpgc.jbudget123039.controller.TagController;
import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetEntry;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.BudgetEntryRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetEntryService;
import it.unicam.cs.mpgc.jbudget123039.service.TagService;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.*;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class BudgetEntryView {

    @FXML private TableView<BudgetEntry> budgetEntryTable;
    @FXML private TableColumn<BudgetEntry, String> colDescription;
    @FXML private TableColumn<BudgetEntry, BigDecimal> colAmount;
    @FXML private TableColumn<BudgetEntry, String> colTag;
    @FXML private TableColumn<BudgetEntry, String> colDate;

    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Tag> tagComboBox;

    private final BudgetEntryRepository budgetEntryRepository = new BudgetEntryRepository();
    private final BudgetEntryService budgetEntryService = new BudgetEntryService(budgetEntryRepository);
    private final BudgetEntryController controller = new BudgetEntryController(budgetEntryService);
    private final TagRepository tagRepository = new TagRepository();
    private final TagService tagService = new TagService(tagRepository);
    private final TagController tagController = new TagController(tagService);

    private final ObservableList<BudgetEntry> budgetEntries = FXCollections.observableArrayList();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colAmount.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        colTag.setCellValueFactory(data -> {
            Tag tag = data.getValue().getTag();
            return new SimpleStringProperty(tag != null ? tag.getName() : "");
        });

        budgetEntryTable.setItems(budgetEntries);
        tagComboBox.setItems(tags);

        loadTags();
        loadBudgetEntries();
    }

    private void loadTags() {
        tagController.loadAllTagsWithChildrenAsync()
                .thenAccept(list -> Platform.runLater(() -> {
                    tags.setAll(list);
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento delle categorie"));
                    return null;
                });
    }

    private void loadBudgetEntries() {
        controller.loadAllBudgetEntries()
                .thenAccept(list -> Platform.runLater(() -> {
                    budgetEntries.setAll(list);
                    budgetEntryTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento delle voci di budget"));
                    return null;
                });
    }

    @FXML
    private void handleAddBudgetEntry() {
        try {
            String description = descriptionField.getText();
            BigDecimal amount = new BigDecimal(amountField.getText());
            var date = datePicker.getValue();
            Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();

            if (description.isBlank() || amount == null || date == null || selectedTag == null) {
                showError("Completa tutti i campi.");
                return;
            }

            BudgetEntry entry = new BudgetEntry();
            entry.setDescription(description);
            entry.setAmount(amount);
            entry.setDate(date);
            entry.setTag(selectedTag);

            controller.addOrUpdateBudgetEntry(entry)
                    .thenRun(() -> Platform.runLater(() -> {
                        clearForm();
                        loadBudgetEntries();
                    }))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore nell'aggiunta della voce"));
                        return null;
                    });

        } catch (Exception e) {
            showError("Errore durante l'aggiunta: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateBudgetEntry() {
        BudgetEntry selected = budgetEntryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una voce da modificare.");
            return;
        }

        try {
            if (!descriptionField.getText().isBlank()) selected.setDescription(descriptionField.getText());
            if (!amountField.getText().isBlank()) selected.setAmount(new BigDecimal(amountField.getText()));
            if (datePicker.getValue() != null) selected.setDate(datePicker.getValue());
            Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();
            if (selectedTag != null) selected.setTag(selectedTag);

            controller.addOrUpdateBudgetEntry(selected)
                    .thenRun(() -> Platform.runLater(() -> {
                        clearForm();
                        loadBudgetEntries();
                    }))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore nella modifica della voce"));
                        return null;
                    });

        } catch (Exception e) {
            showError("Errore durante la modifica: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteBudgetEntry() {
        BudgetEntry selected = budgetEntryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una voce da eliminare.");
            return;
        }

        controller.deleteBudgetEntry(selected.getId())
                .thenRun(() -> Platform.runLater(this::loadBudgetEntries))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante l'eliminazione della voce"));
                    return null;
                });
    }

    @FXML
    private void handleBackToMain() {
        Stage stage = (Stage) budgetEntryTable.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }

    private void clearForm() {
        descriptionField.clear();
        amountField.clear();
        datePicker.setValue(null);
        tagComboBox.getSelectionModel().clearSelection();
    }
}