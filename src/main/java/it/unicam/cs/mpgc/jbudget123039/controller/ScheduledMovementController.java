package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.ScheduledMovementService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class ScheduledMovementController {

    private final ScheduledMovementService scheduledMovementService;

    public ScheduledMovementController() {
        this.scheduledMovementService = new ScheduledMovementService(
                new ScheduledMovementRepository(),
                new TagRepository()
        );
    }

    public CompletionStage<Void> saveOrUpdateScheduledMovement(ScheduledMovement model) {
        return scheduledMovementService.saveOrUpdateScheduledMovement(model);
    }

    public CompletionStage<List<ScheduledMovement>> loadAllScheduledMovements() {
        return scheduledMovementService.loadAllScheduledMovements();
    }

    public CompletionStage<Void> deleteScheduledMovement(UUID id) {
        return scheduledMovementService.deleteScheduledMovement(id);
    }

    public CompletionStage<Void> processDueScheduledMovements() {
        // Se vuoi mantenere questa funzionalità, assicurati che il service la implementi
        return scheduledMovementService.processDueScheduledMovements();
    }

    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return scheduledMovementService.loadAllTagsAsync();
    }

    public CompletionStage<List<ScheduledMovement>> loadAllManualScheduledMovements() {
        return scheduledMovementService.loadAllManualScheduledMovements();
    }
}