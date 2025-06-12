package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.BudgetController;
import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.BudgetRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetService;
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

public class BudgetView {

    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> colName;
    @FXML private TableColumn<Budget, LocalDate> colStartDate;
    @FXML private TableColumn<Budget, LocalDate> colEndDate;

    @FXML private TextField nameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Tag> tagCombo;

    // Nuova TableView per i movimenti associati
    @FXML private TableView<Movement> movementTable;
    @FXML private TableColumn<Movement, String> colMovementDescription;
    @FXML private TableColumn<Movement, String> colMovementAmount;
    @FXML private TableColumn<Movement, LocalDate> colMovementDate;

    private final BudgetRepository budgetRepository = new BudgetRepository();
    private final MovementRepository movementRepository = new MovementRepository();
    private final TagRepository tagRepository = new TagRepository();
    private final BudgetService budgetService = new BudgetService(budgetRepository, movementRepository,tagRepository);
    private final BudgetController controller = new BudgetController(budgetService);
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();
    private final ObservableList<Budget> budgets = FXCollections.observableArrayList();
    private final ObservableList<Movement> movements = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colStartDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStartDate()));
        colEndDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEndDate()));

        budgetTable.setItems(budgets);

        // Setup colonne tabella movimenti
        colMovementDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colMovementAmount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAmount().toString()));
        colMovementDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));

        movementTable.setItems(movements);

        budgetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldBudget, newBudget) -> {
            if (newBudget != null) {
                populateForm(newBudget);
                Tag selectedTag = tagCombo.getSelectionModel().getSelectedItem();
                if (selectedTag != null) {
                    loadMovementsWithTag(newBudget, selectedTag);
                } else {
                    loadMovements(newBudget);
                }
            } else {
                clearForm();
                movements.clear();
            }
        });

        loadBudgets();
        controller.loadAllTags()
                .thenAccept(list -> Platform.runLater(() -> {
                    tags.setAll(list);
                    tagCombo.setItems(tags);
                    tagCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldTag, newTag) -> {
                        Budget selectedBudget = budgetTable.getSelectionModel().getSelectedItem();
                        if (selectedBudget != null) {
                            loadMovementsWithTag(selectedBudget, newTag);
                        }
                    });
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento dei tag"));
                    return null;
                });
    }

    private void loadBudgets() {
        controller.loadAllBudgets()
                .thenAccept(list -> Platform.runLater(() -> {
                    budgets.setAll(list);
                    budgetTable.refresh();
                    clearForm();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento dei budget"));
                    return null;
                });
    }

    private void loadMovements(Budget budget) {
        controller.loadMovementsForBudget(budget)
                .thenAccept(list -> Platform.runLater(() -> {
                    movements.setAll(list);
                    movementTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento dei movimenti"));
                    return null;
                });
    }

    private void loadMovementsWithTag(Budget budget, Tag tag) {
        controller.loadMovementsForBudgetWithTag(budget, tag)
                .thenAccept(list -> Platform.runLater(() -> {
                    movements.setAll(list);
                    movementTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento dei movimenti filtrati per tag"));
                    return null;
                });
    }

    private void populateForm(Budget budget) {
        nameField.setText(budget.getName());
        startDatePicker.setValue(budget.getStartDate());
        endDatePicker.setValue(budget.getEndDate());
    }

    @FXML
    private void handleAddBudget() {
        String name = nameField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (name == null || name.isBlank() || startDate == null || endDate == null) {
            showError("Compila tutti i campi.");
            return;
        }

        Budget newBudget = new Budget();
        newBudget.setName(name);
        newBudget.setStartDate(startDate);
        newBudget.setEndDate(endDate);

        controller.addOrUpdateBudget(newBudget)
                .thenRun(this::loadBudgets)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante la creazione del budget."));
                    return null;
                });
    }

    @FXML
    private void handleUpdateBudget() {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Seleziona un budget da modificare.");
            return;
        }

        String name = nameField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (name == null || name.isBlank() || startDate == null || endDate == null) {
            showError("Compila tutti i campi.");
            return;
        }

        selected.setName(name);
        selected.setStartDate(startDate);
        selected.setEndDate(endDate);

        controller.addOrUpdateBudget(selected)
                .thenRun(this::loadBudgets)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante l'aggiornamento del budget."));
                    return null;
                });
    }

    @FXML
    private void handleDeleteBudget() {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Seleziona un budget da eliminare.");
            return;
        }

        if (!showConfirmation("Confermi l'eliminazione del budget?")) {
            return;
        }

        controller.deleteBudget(selected.getId())
                .thenRun(this::loadBudgets)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante l'eliminazione del budget."));
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
        budgetTable.getSelectionModel().clearSelection();
        movements.clear();
    }
}