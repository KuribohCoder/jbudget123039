package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.*;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;

import java.util.UUID;
public class MovementMapper {

    public static MovementEntity scheduledToMovement(ScheduledMovementEntity scheduled) {
        if (scheduled == null) return null;

        MovementEntity entity = new MovementEntity();
        entity.setId(UUID.randomUUID());
        entity.setDescription(scheduled.getDescription());
        entity.setAmount(scheduled.getAmount());
        entity.setDate(scheduled.getScheduledDate());
        entity.setIncome(scheduled.isIncome());
        entity.setTags(TagMapper.toEntityTagList(TagMapper.toModelTagList(scheduled.getTags())));
        return entity;
    }

    public static MovementEntity toEntity(Movement model) {
        if (model == null) return null;

        MovementEntity entity = new MovementEntity();
        entity.setId(model.getId() != null ? model.getId() : UUID.randomUUID());
        entity.setDescription(model.getDescription());
        entity.setDate(model.getDate());
        entity.setAmount(model.getAmount());
        entity.setIncome(model.isIncome());
        entity.setTags(TagMapper.toEntityTagList(model.getTags()));

        return entity;
    }

    public static Movement toModel(MovementEntity entity) {
        if (entity == null) return null;

        BasicMovement movement = new BasicMovement(
                entity.getDescription(),
                entity.getDate(),
                entity.getAmount(),
                entity.isIncome(),
                TagMapper.toModelTagList(entity.getTags())
        );
        movement.setId(entity.getId());

        return movement;
    }
}