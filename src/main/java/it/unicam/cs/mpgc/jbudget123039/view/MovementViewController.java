package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.MovementController;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MovementViewController {

    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private CheckBox incomeCheckBox;
    @FXML private TableView<Movement> movementTable;
    @FXML private TableColumn<Movement, String> colDescription;
    @FXML private TableColumn<Movement, LocalDate> colDate;
    @FXML private TableColumn<Movement, BigDecimal> colAmount;
    @FXML private TableColumn<Movement, String> colType;
    @FXML private Button deleteButton;


    private final MovementController controller = new MovementController();
    private final ObservableList<Movement> movements = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDate()));
        colAmount.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));
        colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isIncome() ? "Entrata" : "Uscita"));
        movementTable.setItems(movements);

        controller.loadMovementsAsync()
                .thenRun(() -> {
                    javafx.application.Platform.runLater(() -> {
                        movements.setAll(controller.getAllMovements());
                        movementTable.refresh();
                    });
                })
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
            LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
            boolean income = incomeCheckBox.isSelected();
            List<Tag> tags = List.of(new Tag("Generico")); // placeholder

            controller.addMovementAsync(description, date, amount, income, tags)
                    .thenCompose(v -> controller.loadMovementsAsync())
                    .thenRun(() -> {
                        javafx.application.Platform.runLater(() -> {
                            movements.setAll(controller.getAllMovements());
                            movementTable.refresh();
                        });
                    })
                    .exceptionally(ex -> {
                        javafx.application.Platform.runLater(() -> showError("Errore nel salvataggio: " + ex.getMessage()));
                        return null;
                    });

            descriptionField.clear();
            amountField.clear();
            datePicker.setValue(null);
            incomeCheckBox.setSelected(false);

        } catch (Exception e) {
            showError("Errore durante l'aggiunta del movimento: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteMovement() {
        Movement selected = movementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un movimento da eliminare");
            return;
        }

        controller.removeMovementAsync(selected.getId())
                .thenRun(() -> {
                    javafx.application.Platform.runLater(() -> {
                        movements.remove(selected);
                        movementTable.refresh();
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> showError("Errore nella cancellazione: " + ex.getMessage()));
                    return null;
                });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}