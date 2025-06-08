// LoanAmortizationController.java (UI per piano di ammortamento)
package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.controller.LoadAmortizationController;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoadAmortizationView {
    @FXML
    private TextField totalAmountField;
    @FXML
    private TextField interestRateField;
    @FXML
    private TextField numInstallmentsField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Button generateButton;

    private final LoadAmortizationController controller = new LoadAmortizationController();

    @FXML
    public void handleGenerateSchedule() {
        try {
            BigDecimal totalAmount = new BigDecimal(totalAmountField.getText());
            BigDecimal interestRate = new BigDecimal(interestRateField.getText());
            int installments = Integer.parseInt(numInstallmentsField.getText());
            LocalDate startDate = startDatePicker.getValue();
            List<TagEntity> tags = new ArrayList<>(); // aggiungere selezione da UI se necessario

            controller.generateSchedule(totalAmount, interestRate, installments, startDate, tags);

            showInfo("Piano di ammortamento generato con successo.");
        } catch (Exception e) {
            showError("Errore nella generazione del piano: " + e.getMessage());
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
