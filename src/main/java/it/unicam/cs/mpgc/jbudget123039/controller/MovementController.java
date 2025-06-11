package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.BasicMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.MovementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class MovementController {

    private final MovementService movementService = new MovementService(
            new MovementRepository(),
            new TagRepository()
    );

    public CompletionStage<Void> addMovementAsync(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        Movement movement = new BasicMovement(description, date, amount, income, tags);
        return movementService.addMovementAsync(movement);
    }

    public CompletionStage<Void> updateMovementAsync(Movement movement) {
        return movementService.updateMovementAsync(movement);
    }

    public CompletionStage<Void> deleteMovementAsync(UUID id) {
        return movementService.deleteMovementAsync(id);
    }

    public CompletionStage<List<Movement>> loadAllMovementsAsync() {
        return movementService.loadAllMovementsAsync();
    }

    public List<Tag> convertToModelTags(List<TagEntity> entities) {
        return TagMapper.toModelTagList(entities);
    }
    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return movementService.loadAllTagsAsync();
    }

}