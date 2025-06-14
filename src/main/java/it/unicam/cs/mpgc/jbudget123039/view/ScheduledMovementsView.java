package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.ScheduledMovementController;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovementOrigin;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.showError;

/**
 * Controller for the Scheduled Movements view.
 * <p>
 * Manages display and CRUD operations of scheduled financial movements that are set manually.
 * Supports asynchronous loading and updating of scheduled movements and associated tags.
 * Provides user interface interaction for adding, deleting, updating scheduled movements,
 * and navigation back to the main view.
 * </p>
 */
public class ScheduledMovementsView {

    private final ScheduledMovementController controller = new ScheduledMovementController();

    @FXML private TableView<ScheduledMovement> tableScheduledMovements;
    @FXML private TableColumn<ScheduledMovement, String> colDescription;
    @FXML private TableColumn<ScheduledMovement, LocalDate> colDate;
    @FXML private TableColumn<ScheduledMovement, BigDecimal> colAmount;
    @FXML private TableColumn<ScheduledMovement, Boolean> colIncome;
    @FXML private TableColumn<ScheduledMovement, String> colTags;

    @FXML private TextField txtDescription;
    @FXML private TextField txtAmount;
    @FXML private DatePicker datePicker;
    @FXML private CheckBox checkIncome;
    @FXML private ListView<Tag> listTags;

    /**
     * Initializes the controller by setting up table columns,
     * loading scheduled movements and tags asynchronously,
     * and configuring UI elements.
     */
    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getScheduledDate()));
        colAmount.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));
        colIncome.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().isIncome()));
        colTags.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTags().stream()
                        .map(Tag::getName).collect(Collectors.joining(", "))));
        loadScheduledMovements();
        loadTags();
    }

    /**
     * Loads all manual scheduled movements asynchronously and populates the table.
     * Displays an error dialog if loading fails.
     */
    private void loadScheduledMovements() {
        controller.loadAllManualScheduledMovements()
                .thenAccept(movements ->
                        Platform.runLater(() -> tableScheduledMovements.setItems(FXCollections.observableArrayList(movements)))
                ).exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento delle scadenze."));
                    return null;
                });
    }

    /**
     * Loads all tags asynchronously and populates the tag selection list.
     */
    private void loadTags() {
        controller.loadAllTagsAsync().thenAccept(tags -> {
            Platform.runLater(() -> {
                listTags.setItems(FXCollections.observableArrayList(tags));
                listTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            });
        });
    }

    /**
     * Handles adding a new scheduled movement with data from the input fields.
     * Validates inputs, creates a ScheduledMovement object,
     * and saves it asynchronously.
     * Clears form fields after successful save.
     * Shows error dialogs for validation failures or save errors.
     */
    @FXML
    private void handleAdd() {
        String description = txtDescription.getText().trim();
        String amountStr = txtAmount.getText().trim();
        LocalDate date = datePicker.getValue();
        boolean income = checkIncome.isSelected();
        List<Tag> selectedTags = listTags.getSelectionModel().getSelectedItems();

        if (description.isEmpty() || amountStr.isEmpty() || date == null) {
            showError("Compila tutti i campi.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            ScheduledMovement movement = new ScheduledMovement();
            movement.setId(UUID.randomUUID());
            movement.setDescription(description);
            movement.setAmount(amount);
            movement.setScheduledDate(date);
            movement.setIncome(income);
            movement.setTags(selectedTags);
            movement.setOrigin(ScheduledMovementOrigin.MANUAL);

            controller.saveOrUpdateScheduledMovement(movement)
                    .thenRun(() -> {
                        loadScheduledMovements();
                        Platform.runLater(this::clearFields);
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore nel salvataggio della scadenza."));
                        return null;
                    });
        } catch (NumberFormatException e) {
            showError("Importo non valido.");
        }
    }

    /**
     * Handles deletion of the selected scheduled movement.
     * Shows error if no movement is selected.
     * Reloads scheduled movements after deletion.
     */
    @FXML
    private void handleDelete() {
        ScheduledMovement selected = tableScheduledMovements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una scadenza da eliminare.");
            return;
        }

        controller.deleteScheduledMovement(selected.getId())
                .thenRun(this::loadScheduledMovements)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante l'eliminazione."));
                    return null;
                });
    }

    /**
     * Handles updating the selected scheduled movement with the data from the input fields.
     * Validates inputs and saves changes asynchronously.
     * Shows errors for validation failures or save errors.
     */
    @FXML
    private void handleUpdate() {
        ScheduledMovement selected = tableScheduledMovements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una rata da modificare.");
            return;
        }

        try {
            String amountStr = txtAmount.getText();
            if (amountStr != null && !amountStr.isBlank()) {
                BigDecimal nuovoImporto = new BigDecimal(amountStr);
                selected.setAmount(nuovoImporto);
            }

            String description = txtDescription.getText().trim();
            if (description != null && !description.isBlank()) {
                selected.setDescription(description);
            }

            LocalDate date = datePicker.getValue();
            if (date != null) {
                selected.setScheduledDate(date);
            }

            List<Tag> selectedTags = listTags.getSelectionModel().getSelectedItems();
            if (!selectedTags.isEmpty()) {
                selected.setTags(selectedTags);
            }

            controller.saveOrUpdateScheduledMovement(selected)
                    .thenRun(this::loadScheduledMovements)
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore durante la modifica"));
                        return null;
                    });

        } catch (Exception e) {
            showError("Errore nei dati inseriti: " + e.getMessage());
        }
    }

    /**
     * Clears all input fields and resets selection in the form.
     */
    private void clearFields() {
        txtDescription.clear();
        txtAmount.clear();
        checkIncome.setSelected(false);
        datePicker.setValue(null);
        listTags.getSelectionModel().clearSelection();
    }

    /**
     * Handles navigation back to the main view.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) tableScheduledMovements.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }
}