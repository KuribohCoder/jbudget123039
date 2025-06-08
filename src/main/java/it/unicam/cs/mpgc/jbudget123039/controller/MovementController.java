package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.movement.*;
import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class MovementController {

    private final List<Movement> movements = new ArrayList<>();
    MovementRepository movementRepository = new MovementRepository();
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

    private List<Tag> convertToModelTags(List<TagEntity> tagEntities) {
        if (tagEntities == null) return List.of();
        return tagEntities.stream()
                .map(e -> new Tag(e.getName()))
                .toList();
    }

    public CompletionStage<Void> addMovementAsync(String description, LocalDate date, BigDecimal amount, boolean income, List<TagEntity> tags) {
        Movement m = new BasicMovement(description, date, amount, income, convertToModelTags(tags));
        MovementEntity entity = MovementMapper.toEntity(m);
        entity.setTags(tags);
        return movementRepository.saveMovementAsync(entity)
                .thenCompose(v -> loadMovementsAsync());
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

    public CompletionStage<List<TagEntity>> loadTagsAsync() {
        return tagRepository.loadAllTagsAsync();
    }

}