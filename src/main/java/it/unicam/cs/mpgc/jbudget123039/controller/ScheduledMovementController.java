package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.ScheduledMovementMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ScheduledMovementController {

    private final ScheduledMovementRepository scheduledRepo = new ScheduledMovementRepository();
    private final MovementRepository movementRepo = new MovementRepository();

    public CompletionStage<Void> saveOrUpdateScheduledMovement(ScheduledMovement model) {
        return scheduledRepo.saveOrUpdateScheduledMovementAsync(
                ScheduledMovementMapper.toEntity(model)
        );
    }

    public CompletionStage<List<ScheduledMovement>> loadAllScheduledMovements() {
        return scheduledRepo.loadAllScheduledMovementsAsync()
                .thenApply(entities ->
                        entities.stream()
                                .map(ScheduledMovementMapper::toModel)
                                .toList()
                );
    }

    public CompletionStage<Void> deleteScheduledMovement(UUID id) {
        return scheduledRepo.deleteScheduledMovementAsync(id);
    }

    public CompletionStage<Void> processDueScheduledMovements() {
        return scheduledRepo.loadDueScheduledMovementsAsync()
                .thenCompose(dueList -> {
                    CompletionStage<Void> chain = CompletableFuture.completedFuture(null);

                    for (var scheduled : dueList) {
                        var movement = MovementMapper.scheduledToMovement(scheduled);
                        chain = chain.thenCompose(v ->
                                movementRepo.saveMovementAsync(movement)
                                        .thenCompose(v2 -> scheduledRepo.deleteScheduledMovementAsync(scheduled.getId()))
                        );
                    }

                    return chain;
                });
    }
}