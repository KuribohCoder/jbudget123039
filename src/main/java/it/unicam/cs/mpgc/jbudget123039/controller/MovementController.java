package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.BasicMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MovementController {

    private final List<Movement> movements = new ArrayList<>();
    private final MovementRepository movementRepository = new MovementRepository();
    private final TagRepository tagRepository = new TagRepository();

    public List<Movement> getAllMovements() {
        return new ArrayList<>(movements);
    }

    public List<Movement> filterByTag(Tag tag) {
        return movements.stream()
                .filter(m -> m.getTags().contains(tag))
                .toList();
    }

    public BigDecimal calculateTotal(boolean incomeType) {
        return movements.stream()
                .filter(m -> m.isIncome() == incomeType)
                .map(Movement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CompletionStage<Void> addMovementAsync(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        List<CompletableFuture<Void>> saveTagFutures = new ArrayList<>();

        for (Tag tag : tags) {
            if (tag.getId() == null) {
                CompletableFuture<Void> future = tagRepository.findByNameAsync(tag.getName())
                        .thenCompose(existingTag -> {
                            if (existingTag != null) {
                                tag.setId(existingTag.getId());
                                return CompletableFuture.completedFuture(null);
                            } else {
                                TagEntity newEntity = TagMapper.toEntity(tag);
                                return tagRepository.saveOrUpdateTagAsync(newEntity)
                                        .thenAccept(savedEntity -> tag.setId(savedEntity.getId()));
                            }
                        }).toCompletableFuture();
                saveTagFutures.add(future);
            }
        }

        return CompletableFuture.allOf(saveTagFutures.toArray(new CompletableFuture[0]))
                .thenCompose(v -> {
                    Movement movement = new BasicMovement(description, date, amount, income, tags);
                    movement.setId(UUID.randomUUID());
                    MovementEntity entity = MovementMapper.toEntity(movement);
                    return movementRepository.saveMovementAsync(entity)
                            .thenCompose(x -> loadMovementsAsync());
                });
    }

    public CompletionStage<Void> loadMovementsAsync() {
        return movementRepository.loadAllMovementsAsync()
                .thenAccept(entities -> {
                    synchronized (movements) {
                        movements.clear();
                        for (MovementEntity e : entities) {
                            movements.add(MovementMapper.toModel(e));
                        }
                    }
                });
    }

    public CompletionStage<Void> removeMovementAsync(UUID id) {
        return movementRepository.deleteMovementAsync(id)
                .thenRun(() -> {
                    synchronized (movements) {
                        movements.removeIf(m -> m.getId().equals(id));
                    }
                });
    }


    public CompletionStage<Void> updateMovementAsync(Movement movement) {
        List<CompletableFuture<Void>> saveTagFutures = new ArrayList<>();

        for (Tag tag : movement.getTags()) {
            if (tag.getId() == null) {
                CompletableFuture<Void> future = tagRepository.findByNameAsync(tag.getName())
                        .thenCompose(existingTag -> {
                            if (existingTag != null) {
                                tag.setId(existingTag.getId());
                                return CompletableFuture.completedFuture(null);
                            } else {
                                TagEntity newEntity = TagMapper.toEntity(tag);
                                return tagRepository.saveOrUpdateTagAsync(newEntity)
                                        .thenAccept(savedEntity -> tag.setId(savedEntity.getId()));
                            }
                        }).toCompletableFuture();
                saveTagFutures.add(future);
            }
        }

        return CompletableFuture.allOf(saveTagFutures.toArray(new CompletableFuture[0]))
                .thenCompose(v -> {
                    MovementEntity entity = MovementMapper.toEntity(movement);
                    return movementRepository.updateMovementAsync(entity);
                });
    }

    public CompletionStage<List<Tag>> loadTagsAsync() {
        return tagRepository.loadAllTagsAsync()
                .thenApply(tagEntities ->
                        tagEntities.stream()
                                .map(MovementMapper::tagToModel)
                                .toList()
                );
    }
}