package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.*;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovementMapper {

    public static MovementEntity scheduledToMovement(ScheduledMovementEntity scheduled) {
        MovementEntity entity = new MovementEntity();
        entity.setId(UUID.randomUUID());
        entity.setDescription(scheduled.getDescription());
        entity.setAmount(scheduled.getAmount());
        entity.setDate(scheduled.getScheduledDate());
        entity.setIncome(scheduled.isIncome());
        entity.setTags(scheduled.getTags());
        return entity;
    }

    public static MovementEntity toEntity(Movement m) {
        MovementEntity e = new MovementEntity();
        e.setId(m.getId() != null ? m.getId() : UUID.randomUUID());
        e.setDescription(m.getDescription());
        e.setDate(m.getDate());
        e.setAmount(m.getAmount());
        e.setIncome(m.isIncome());
        e.setTags(toEntityTagList(m.getTags()));
        return e;
    }

    public static Movement toModel(MovementEntity e) {
        if (e == null) return null;

        BasicMovement movement = new BasicMovement(
                e.getDescription(),
                e.getDate(),
                e.getAmount(),
                e.isIncome(),
                toModelTagList(e.getTags())
        );
        movement.setId(e.getId());
        return movement;
    }

    public static TagEntity tagToEntity(Tag t) {
        return TagMapper.toEntity(t);
    }

    public static Tag tagToModel(TagEntity e) {
        return TagMapper.toModel(e);
    }

    public static List<TagEntity> toEntityTagList(List<Tag> tags) {
        return TagMapper.toEntityTagList(tags);
    }

    public static List<Tag> toModelTagList(List<TagEntity> tagEntities) {
        return TagMapper.toModelTagList(tagEntities);
    }

}