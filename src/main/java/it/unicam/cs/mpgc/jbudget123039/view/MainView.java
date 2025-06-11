package it.unicam.cs.mpgc.jbudget123039.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView {

    @FXML
    private BorderPane mainRoot;

    @FXML
    private void showMovements() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/MovementView.fxml", "Gestione Movimenti", stage);
    }

    @FXML
    private void showTags() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/TagView.fxml", "Gestione Tag", stage);
    }

    @FXML
    private void showBudget() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/BudgetView.fxml", "Gestione Budget", stage);
    }

    @FXML
    private void showBudgetEntry() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/BudgetEntryView.fxml", "Gestione Budget Entry", stage);
    }

    @FXML
    private void showStatistics() {
        System.out.println("Funzione Statistiche non ancora implementata.");
    }

    @FXML
    private void showAmortization() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/AmortizationView.fxml", "Piano di Ammortamento", stage);
    }

    private void switchScene(String path, String title, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}