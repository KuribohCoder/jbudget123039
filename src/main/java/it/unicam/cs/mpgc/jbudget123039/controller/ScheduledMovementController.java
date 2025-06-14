package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.ScheduledMovementService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Controller per la gestione dei ScheduledMovement.
 * Espone metodi asincroni per interagire con il livello di servizio.
 */
public class ScheduledMovementController {

    private final ScheduledMovementService scheduledMovementService;

    /**
     * Costruisce un nuovo ScheduledMovementController con repository predefiniti.
     */
    public ScheduledMovementController() {
        this.scheduledMovementService = new ScheduledMovementService(
                new ScheduledMovementRepository(),
                new TagRepository()
        );
    }

    /**
     * Salva o aggiorna un ScheduledMovement.
     * @param model ScheduledMovement da salvare o aggiornare.
     * @return CompletionStage che completa al termine dell'operazione.
     */
    public CompletionStage<Void> saveOrUpdateScheduledMovement(ScheduledMovement model) {
        return scheduledMovementService.saveOrUpdateScheduledMovement(model);
    }

    /**
     * Carica tutti i ScheduledMovement.
     * @return CompletionStage con la lista di ScheduledMovement caricati.
     */
    public CompletionStage<List<ScheduledMovement>> loadAllScheduledMovements() {
        return scheduledMovementService.loadAllScheduledMovements();
    }

    /**
     * Elimina un ScheduledMovement dato il suo ID.
     * @param id UUID dell'entità da eliminare.
     * @return CompletionStage che completa al termine dell'eliminazione.
     */
    public CompletionStage<Void> deleteScheduledMovement(UUID id) {
        return scheduledMovementService.deleteScheduledMovement(id);
    }

    /**
     * Esegue il processo di trasformazione dei ScheduledMovement scaduti in Movement concreti.
     * @return CompletionStage che completa al termine del processo.
     */
    public CompletionStage<Void> processDueScheduledMovements() {
        return scheduledMovementService.processDueScheduledMovements();
    }

    /**
     * Carica tutti i Tag disponibili.
     * @return CompletionStage con la lista di Tag caricati.
     */
    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return scheduledMovementService.loadAllTagsAsync();
    }

    /**
     * Carica tutti i ScheduledMovement di origine manuale (non ammortamenti).
     * @return CompletionStage con la lista di ScheduledMovement manuali.
     */
    public CompletionStage<List<ScheduledMovement>> loadAllManualScheduledMovements() {
        return scheduledMovementService.loadAllManualScheduledMovements();
    }
}