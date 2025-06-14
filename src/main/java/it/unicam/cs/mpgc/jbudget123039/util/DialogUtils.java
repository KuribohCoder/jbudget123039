package it.unicam.cs.mpgc.jbudget123039.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Utility per la gestione di finestre di dialogo comuni in JavaFX.
 * Fornisce metodi per mostrare alert di errore, informazioni e conferme.
 */
public class DialogUtils {

    /**
     * Mostra un dialogo di errore con il messaggio specificato.
     *
     * @param message il messaggio di errore da visualizzare
     */
    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.showAndWait();
    }

    /**
     * Mostra un dialogo informativo con il messaggio specificato.
     *
     * @param message il messaggio informativo da visualizzare
     */
    public static void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Informazione");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Mostra un dialogo di conferma con il messaggio specificato e ritorna la risposta dell'utente.
     *
     * @param message il messaggio da mostrare nella finestra di conferma
     * @return {@code true} se l'utente ha confermato (premuto YES), {@code false} altrimenti
     */
    public static boolean showConfirmation(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Conferma");
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}