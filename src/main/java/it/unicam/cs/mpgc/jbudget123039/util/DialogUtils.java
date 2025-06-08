package it.unicam.cs.mpgc.jbudget123039.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class DialogUtils {

    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Informazione");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Conferma");
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}