package it.unicam.cs.mpgc.jbudget123039.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the main view of the application.
 * <p>
 * Provides navigation methods to switch between different views/scenes
 * such as Movements, Tags, Budget, Statistics, Amortization, and Scheduled Movements.
 * </p>
 */
public class MainView {

    @FXML
    private BorderPane mainRoot;

    /**
     * Navigates to the Movements management view.
     * Triggered by a UI event.
     */
    @FXML
    private void showMovements() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/movement.fxml", "Gestione Movimenti", stage);
    }

    /**
     * Navigates to the Tags management view.
     * Triggered by a UI event.
     */
    @FXML
    private void showTags() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/tag.fxml", "Gestione Tag", stage);
    }

    /**
     * Navigates to the Budget management view.
     * Triggered by a UI event.
     */
    @FXML
    private void showBudget() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/budget.fxml", "Gestione Budget", stage);
    }

    /**
     * Navigates to the Statistics view.
     * Triggered by a UI event.
     */
    @FXML
    private void showStatistics() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/StatisticsView.fxml", "Statistiche", stage);
    }

    /**
     * Navigates to the Amortization plan view.
     * Triggered by a UI event.
     */
    @FXML
    private void showAmortization() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/amortization.fxml", "Piano di Ammortamento", stage);
    }

    /**
     * Navigates to the Scheduled Movements view.
     * Triggered by a UI event.
     */
    @FXML
    private void showScheduledMovements() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        switchScene("/ui/scheduledMovements.fxml", "Scadenziario", stage);
    }

    /**
     * Switches the current scene to a new one.
     *
     * @param path  the path to the FXML resource to load
     * @param title the title to set for the stage window
     * @param stage the stage on which to set the new scene
     */
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