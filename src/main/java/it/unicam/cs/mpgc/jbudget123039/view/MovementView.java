package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.MovementController;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.util.DateUtil;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.*;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MovementView {

    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox incomeCheckBox;
    @FXML
    private TableView<Movement> movementTable;
    @FXML
    private TableColumn<Movement, String> colDescription;
    @FXML
    private TableColumn<Movement, LocalDate> colDate;
    @FXML
    private TableColumn<Movement, BigDecimal> colAmount;
    @FXML
    private TableColumn<Movement, String> colType;
    @FXML
    private TableColumn<Movement, String> colTags;
    @FXML
    private ListView<Tag> tagListView;

    private final MovementController controller = new MovementController();
    private final ObservableList<Movement> movements = FXCollections.observableArrayList();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));
        colAmount.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isIncome() ? "Entrata" : "Uscita"));
        colTags.setCellValueFactory(data -> {
            List<String> tagNames = data.getValue().getTags().stream().map(Tag::getName).toList();
            return new SimpleStringProperty(String.join(", ", tagNames));
        });

        movementTable.setItems(movements);

        controller.loadAllMovementsAsync()
                .thenAccept(list -> Platform.runLater(() -> {
                    movements.setAll(list);
                    movementTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });

        controller.loadAllTagsAsync()
                .thenAccept(loadedTags -> Platform.runLater(() -> {
                    tags.setAll(loadedTags);
                    tagListView.setItems(tags);
                    tagListView.setCellFactory(param -> new ListCell<>() {
                        @Override
                        protected void updateItem(Tag item, boolean empty) {
                            super.updateItem(item, empty);
                            setText((item == null || empty) ? "" : item.getName());
                        }
                    });
                    tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    @FXML
    private void handleAddMovement() {
        try {
            String description = descriptionField.getText();
            BigDecimal amount = new BigDecimal(amountField.getText());
            LocalDate date = DateUtil.getOrDefault(datePicker.getValue());
            boolean income = incomeCheckBox.isSelected();
            List<Tag> selectedTags = tagListView.getSelectionModel().getSelectedItems();

            controller.addMovementAsync(description, date, amount, income, selectedTags)
                    .thenCompose(v -> controller.loadAllMovementsAsync())
                    .thenAccept(list -> Platform.runLater(() -> {
                        movements.setAll(list);
                        movementTable.refresh();
                        clearForm();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> showError("Errore nel salvataggio: " + ex.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showError("Errore durante l'aggiunta del movimento: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteMovement() {
        Movement selected = movementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un movimento da eliminare.");
            return;
        }

        controller.deleteMovementAsync(selected.getId())
                .thenCompose(v -> controller.loadAllMovementsAsync())
                .thenAccept(list -> Platform.runLater(() -> {
                    movements.setAll(list);
                    movementTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nella cancellazione: " + ex.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleUpdateMovement() {
        Movement selected = movementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un movimento da modificare.");
            return;
        }

        try {
            if (!descriptionField.getText().isBlank()) {
                selected.setDescription(descriptionField.getText());
            }
            if (!amountField.getText().isBlank()) {
                selected.setAmount(new BigDecimal(amountField.getText()));
            }
            if (datePicker.getValue() != null) {
                selected.setDate(datePicker.getValue());
            }
            selected.setIncome(incomeCheckBox.isSelected());

            List<Tag> selectedTags = tagListView.getSelectionModel().getSelectedItems();
            if (!selectedTags.isEmpty()) {
                selected.setTags(selectedTags);
            }

            controller.updateMovementAsync(selected)
                    .thenCompose(v -> controller.loadAllMovementsAsync())
                    .thenAccept(list -> Platform.runLater(() -> {
                        movements.setAll(list);
                        movementTable.refresh();
                        clearForm();
                    }))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore durante la modifica: " + ex.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showError("Errore nei dati inseriti: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToMain() {
        Stage stage = (Stage) movementTable.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }

    private void clearForm() {
        descriptionField.clear();
        amountField.clear();
        datePicker.setValue(null);
        incomeCheckBox.setSelected(false);
        tagListView.getSelectionModel().clearSelection();
    }
}