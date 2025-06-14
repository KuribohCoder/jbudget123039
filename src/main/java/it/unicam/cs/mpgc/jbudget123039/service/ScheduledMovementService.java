package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.ScheduledMovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovementOrigin;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ScheduledMovementService {

    private final ScheduledMovementRepository scheduledRepo;
    private final MovementRepository movementRepo = new MovementRepository();
    private final TagRepository tagRepository;

    public ScheduledMovementService(ScheduledMovementRepository scheduledRepo, TagRepository tagRepository) {
        this.scheduledRepo = scheduledRepo;
        this.tagRepository = tagRepository;
    }

    /**
     * Verifica che tutti i tag nel modello abbiano ID validi e corrispondano ad entità persistenti.
     * Se un tag non ha ID, cerca per nome e assegna l'ID.
     */
    private CompletionStage<Void> verifyTagsExist(List<Tag> tags) {
        List<CompletableFuture<Void>> futures = tags.stream()
                .map(tag -> {
                    if (tag.getId() == null) {
                        return tagRepository.findByNameAsync(tag.getName())
                                .thenAccept(entity -> {
                                    if (entity == null) {
                                        throw new IllegalStateException("Tag '" + tag.getName() + "' not found.");
                                    }
                                    tag.setId(entity.getId());
                                }).toCompletableFuture();
                    } else {
                        return tagRepository.findByIdAsync(tag.getId())
                                .thenAccept(entity -> {
                                    if (entity == null) {
                                        throw new IllegalStateException("Tag with ID " + tag.getId() + " not found.");
                                    }
                                }).toCompletableFuture();
                    }
                })
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletionStage<Void> saveOrUpdateScheduledMovement(ScheduledMovement model) {
        if (model.getId() == null) {
            model.setId(UUID.randomUUID());
        }
        if (model.getOrigin() == null) {
            model.setOrigin(ScheduledMovementOrigin.MANUAL);
        }

        return verifyTagsExist(model.getTags())
                .thenCompose(v -> {
                    ScheduledMovementEntity entity = ScheduledMovementMapper.toEntity(model);
                    return scheduledRepo.saveOrUpdateScheduledMovementAsync(entity);
                });
    }

    public CompletionStage<Void> deleteScheduledMovement(UUID id) {
        return scheduledRepo.deleteScheduledMovementAsync(id);
    }

    public CompletionStage<List<ScheduledMovement>> loadAllScheduledMovements() {
        return scheduledRepo.loadAllScheduledMovementsAsync()
                .thenApply(list -> list.stream()
                        .map(ScheduledMovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return tagRepository.loadAllTagsAsync()
                .thenApply(list -> list.stream()
                        .map(TagMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<Void> processDueScheduledMovements() {
        return scheduledRepo.loadDueScheduledMovementsAsync()
                .thenCompose(dueList -> {
                    CompletionStage<Void> chain = CompletableFuture.completedFuture(null);

                    for (ScheduledMovementEntity scheduledEntity : dueList) {
                        MovementEntity movementEntity = MovementMapper.scheduledToMovement(scheduledEntity);

                        chain = chain.thenCompose(v ->
                                movementRepo.saveOrUpdateAsync(movementEntity)
                                        .thenCompose(v2 -> scheduledRepo.deleteScheduledMovementAsync(scheduledEntity.getId()))
                        );
                    }

                    return chain;
                });
    }

    public CompletionStage<List<ScheduledMovement>> loadAllManualScheduledMovements() {
        return scheduledRepo.loadAllManualScheduledMovementsAsync()
                .thenApply(list -> list.stream()
                        .map(ScheduledMovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

}