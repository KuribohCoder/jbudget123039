package it.unicam.cs.mpgc.jbudget123039.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility per gestire il cambio di scena in applicazioni JavaFX.
 */
public class SceneSwitcherUtils {

    /**
     * Cambia la scena corrente dello stage con quella caricata dal file FXML specificato.
     *
     * @param stage    lo stage corrente in cui cambiare la scena
     * @param fxmlPath percorso relativo del file FXML da caricare (es. "/ui/main.fxml")
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