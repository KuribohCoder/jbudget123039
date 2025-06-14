package it.unicam.cs.mpgc.jbudget123039;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione JavaFX per la gestione del budget familiare.
 * <p>
 * Estende {@link javafx.application.Application} e configura la scena principale
 * caricando l'interfaccia da un file FXML.
 * </p>
 */
public class MainApp extends Application {

    /**
     * Metodo invocato all'avvio dell'applicazione.
     * Configura la finestra principale caricando il layout da "main.fxml",
     * imposta il titolo e mostra la finestra.
     *
     * @param stage lo stage principale fornito da JavaFX
     * @throws Exception se si verifica un errore nel caricamento del file FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestione Budget Familiare");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metodo main che avvia l'applicazione JavaFX.
     *
     * @param args argomenti della linea di comando (non usati)
     */
    public static void main(String[] args) {
        launch(args);
    }
}