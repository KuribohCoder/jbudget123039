package it.unicam.cs.mpgc.jbudget123039.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcherUtils {
    /**
     * Cambia la scena corrente con quella caricata da fxmlPath.
     *
     * @param currentStage lo stage corrente
     * @param fxmlPath     percorso FXML relativo, es. "/ui/main.fxml"
     */

    public static void switchScene(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcherUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}