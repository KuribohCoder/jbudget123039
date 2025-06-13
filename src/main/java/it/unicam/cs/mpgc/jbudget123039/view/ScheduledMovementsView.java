package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.ScheduledMovementController;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
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

    private void loadScheduledMovements() {
        controller.loadAllScheduledMovements()
                .thenAccept(movements ->
                        Platform.runLater(() -> tableScheduledMovements.setItems(FXCollections.observableArrayList(movements)))
                ).exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento delle scadenze."));
                    return null;
                });
    }

    private void loadTags() {
        controller.loadAllTagsAsync().thenAccept(tags ->
                Platform.runLater(() -> listTags.setItems(FXCollections.observableArrayList(tags)))
        );
    }

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

            controller.saveOrUpdateScheduledMovement(movement)
                    .thenRun(this::loadScheduledMovements)
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore nel salvataggio della scadenza."));
                        return null;
                    });
        } catch (NumberFormatException e) {
            showError("Importo non valido.");
        }
    }

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

    @FXML
    private void handleBack() {
        Stage stage = (Stage) tableScheduledMovements.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }
}