package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.ScheduledMovementController;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AmortizationView {

    @FXML private TextField importoField;
    @FXML private TextField rateField;
    @FXML private TextField interesseField;
    @FXML private DatePicker dataInizioPicker;

    @FXML private TableView<ScheduledMovement> rataTable;
    @FXML private TableColumn<ScheduledMovement, String> colDescrizione;
    @FXML private TableColumn<ScheduledMovement, LocalDate> colData;
    @FXML private TableColumn<ScheduledMovement, BigDecimal> colImporto;
    @FXML private TableColumn<ScheduledMovement, Boolean> colTipo;
    @FXML private TableColumn<ScheduledMovement, String> colTags;

    @FXML private TextField nomePianoField;
    @FXML private ListView<Tag> tagListView;

    @FXML private Button eliminaButton;

    private final ObservableList<ScheduledMovement> rateList = FXCollections.observableArrayList();
    private final ScheduledMovementController controller = new ScheduledMovementController();

    private final ObservableList<Tag> tagList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        rataTable.setItems(rateList);

        colDescrizione.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colData.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getScheduledDate()));
        colImporto.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));
        colTipo.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().isIncome()));

        colTags.setCellValueFactory(data -> {
            List<String> tagNames = data.getValue().getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            return new SimpleStringProperty(String.join(", ", tagNames));
        });

        tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        refreshRateTable();

        controller.processDueScheduledMovements()
                .thenRun(this::refreshRateTable)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel processo di ammortamento"));
                    return null;
                });

        controller.loadAllTagsAsync()
                .thenAccept(loadedTags -> Platform.runLater(() -> {
                    tagList.setAll(loadedTags);
                    tagListView.setItems(tagList);
                    tagListView.setCellFactory(param -> new ListCell<>() {
                        @Override
                        protected void updateItem(Tag item, boolean empty) {
                            super.updateItem(item, empty);
                            setText((item == null || empty) ? "" : item.getName());
                        }
                    });
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    @FXML
    private void handleCreaAmmortamento() {
        try {
            String nomePiano = nomePianoField.getText().trim();
            if (nomePiano.isEmpty()) {
                showError("Inserisci un nome per il piano di ammortamento.");
                return;
            }

            BigDecimal importoTotale = new BigDecimal(importoField.getText());
            int numeroRate = Integer.parseInt(rateField.getText());
            BigDecimal interessePercentuale = new BigDecimal(interesseField.getText());
            LocalDate dataInizio = DateUtil.getOrDefault(dataInizioPicker.getValue());

            BigDecimal interesse = interessePercentuale.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal quotaInteresse = importoTotale.multiply(interesse)
                    .divide(BigDecimal.valueOf(numeroRate), 2, RoundingMode.HALF_UP);
            BigDecimal quotaRimborso = importoTotale
                    .divide(BigDecimal.valueOf(numeroRate), 2, RoundingMode.HALF_UP);

            for (int i = 0; i < numeroRate; i++) {
                BigDecimal importoRata = quotaRimborso.add(quotaInteresse);
                LocalDate dataRata = dataInizio.plusMonths(i);

                ScheduledMovement rata = new ScheduledMovement();
                rata.setDescription(nomePiano + " - Rata #" + (i + 1));
                rata.setAmount(importoRata.negate());
                rata.setIncome(false);
                rata.setScheduledDate(dataRata);

                List<Tag> selectedTags = tagListView.getSelectionModel()
                        .getSelectedItems();

                rata.setTags(selectedTags);

                final int rataIndex = i;
                controller.saveOrUpdateScheduledMovement(rata)
                        .exceptionally(ex -> {
                            ex.printStackTrace();
                            Platform.runLater(() -> showError("Errore nel salvataggio della rata " + (rataIndex + 1)));
                            return null;
                        });
            }

            showInfo("Piano di ammortamento creato!");
            clearFields();
            refreshRateTable();

        } catch (Exception e) {
            showError("Errore nei dati inseriti: " + e.getMessage());
        }
    }

    private void refreshRateTable() {
        controller.loadAllScheduledMovements()
                .thenAccept(list -> Platform.runLater(() -> {
                    rateList.setAll(list);
                    rataTable.refresh();
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore nel caricamento delle rate"));
                    return null;
                });
    }

    @FXML
    private void handleEliminaRata() {
        ScheduledMovement selected = rataTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una rata da eliminare.");
            return;
        }

        controller.deleteScheduledMovement(selected.getId())
                .thenRun(this::refreshRateTable)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante l'eliminazione"));
                    return null;
                });
    }

    @FXML
    private void handleModificaRata() {
        ScheduledMovement selected = rataTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una rata da modificare.");
            return;
        }

        try {
            String importoTesto = importoField.getText();
            if (importoTesto != null && !importoTesto.isBlank()) {
                BigDecimal nuovoImporto = new BigDecimal(importoTesto);
                selected.setAmount(nuovoImporto);
            }

            String nomePiano = nomePianoField.getText().trim();
            if (nomePiano != null && !nomePiano.isBlank()) {
                selected.setDescription(nomePiano);
            }

            LocalDate nuovaData = dataInizioPicker.getValue();
            if (nuovaData != null) {
                selected.setScheduledDate(nuovaData);
            }

            List<Tag> selectedTags = tagListView.getSelectionModel()
                    .getSelectedItems();
            if (!selectedTags.isEmpty()) {
                selected.setTags(selectedTags);
            }

            controller.saveOrUpdateScheduledMovement(selected)
                    .thenRun(this::refreshRateTable)
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> showError("Errore durante la modifica"));
                        return null;
                    });

        } catch (Exception e) {
            showError("Errore nei dati inseriti: " + e.getMessage());
        }
    }

    private void clearFields() {
        nomePianoField.clear();
        importoField.clear();
        rateField.clear();
        interesseField.clear();
        dataInizioPicker.setValue(null);
        tagListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBackToMain() {
        javafx.stage.Stage stage = (javafx.stage.Stage) importoField.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }
}