package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.BudgetController;
import it.unicam.cs.mpgc.jbudget123039.controller.TagController;
import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.BudgetRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetService;
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

import java.time.LocalDate;
import java.util.List;

public class BudgetView {

    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> colName;
    @FXML private TableColumn<Budget, LocalDate> colStartDate;
    @FXML private TableColumn<Budget, LocalDate> colEndDate;

    @FXML private TextField nameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private final BudgetRepository budgetRepository = new BudgetRepository();
    private final BudgetService budgetService = new BudgetService(budgetRepository);
    private final BudgetController controller = new BudgetController(budgetService);
    private final ObservableList<Budget> budgets = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colStartDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStartDate()));
        colEndDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEndDate()));

        budgetTable.setItems(budgets);

        loadBudgets();
    }

    private void loadBudgets() {
        controller.loadAllBudgets()
                .thenAccept(list -> Platform.runLater(() -> {
                    budgets.setAll(list);
                    budgetTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento dei budget"));
                    return null;
                });
    }

    @FXML
    private void handleAddBudget() {
        try {
            String name = nameField.getText();
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            if (name.isBlank() || start == null || end == null) {
                showError("Compila tutti i campi.");
                return;
            }

            Budget budget = new Budget();
            budget.setName(name);
            budget.setStartDate(start);
            budget.setEndDate(end);

            controller.addOrUpdateBudget(budget)
                    .thenRun(() -> Platform.runLater(() -> {
                        clearForm();
                        loadBudgets();
                    }))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore nell'aggiunta del budget"));
                        return null;
                    });

        } catch (Exception e) {
            showError("Errore durante l'aggiunta: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateBudget() {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un budget da modificare.");
            return;
        }

        try {
            if (!nameField.getText().isBlank()) selected.setName(nameField.getText());
            if (startDatePicker.getValue() != null) selected.setStartDate(startDatePicker.getValue());
            if (endDatePicker.getValue() != null) selected.setEndDate(endDatePicker.getValue());

            controller.addOrUpdateBudget(selected)
                    .thenRun(() -> Platform.runLater(() -> {
                        clearForm();
                        loadBudgets();
                    }))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore nella modifica del budget"));
                        return null;
                    });

        } catch (Exception e) {
            showError("Errore durante la modifica: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteBudget() {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona un budget da eliminare.");
            return;
        }

        controller.deleteBudget(selected.getId())
                .thenRun(() -> Platform.runLater(this::loadBudgets))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante l'eliminazione del budget"));
                    return null;
                });
    }

    @FXML
    private void handleBackToMain() {
        Stage stage = (Stage) budgetTable.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }

    private void clearForm() {
        nameField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}