package it.unicam.cs.mpgc.jbudget123039.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;

public class MainViewController {

    private Stage currentStage;

    public void setStage(Stage stage) {
        this.currentStage = stage;
    }

    @FXML
    private void showMovements() {
        switchScene("/ui/movement.fxml");
    }

    @FXML
    private void showBudget() {
        System.out.println("Funzione Budget non ancora implementata.");
    }

    @FXML
    private void showStatistics() {
        System.out.println("Funzione Statistiche non ancora implementata.");
    }

    private void switchScene(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = currentStage != null ? currentStage : new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestione Movimenti");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}