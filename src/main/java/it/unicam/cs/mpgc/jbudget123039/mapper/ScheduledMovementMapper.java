package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ScheduledMovementMapper {

    public static ScheduledMovement toModel(ScheduledMovementEntity entity) {
        if (entity == null) return null;

        ScheduledMovement model = new ScheduledMovement();
        model.setId(entity.getId());
        model.setDescription(entity.getDescription());
        model.setAmount(entity.getAmount());
        model.setScheduledDate(entity.getScheduledDate());
        model.setIncome(entity.isIncome());

        List<Tag> tags = entity.getTags() == null ? List.of() :
                entity.getTags().stream()
                        .map(tagEntity -> {
                            Tag tag = new Tag(tagEntity.getName());
                            tag.setId(tagEntity.getId());  // <--- Impostare anche l'ID
                            return tag;
                        })
                        .collect(Collectors.toList());
        model.setTags(tags);

        return model;
    }

    public static ScheduledMovementEntity toEntity(ScheduledMovement model) {
        if (model == null) return null;

        ScheduledMovementEntity entity = new ScheduledMovementEntity();
        entity.setId(model.getId());
        entity.setDescription(model.getDescription());
        entity.setAmount(model.getAmount());
        entity.setScheduledDate(model.getScheduledDate());
        entity.setIncome(model.isIncome());

        List<TagEntity> tagEntities = model.getTags() == null ? List.of() :
                model.getTags().stream()
                        .map(tag -> {
                            TagEntity te = new TagEntity();
                            te.setId(tag.getId());   // <--- Impostare anche l'ID
                            te.setName(tag.getName());
                            return te;
                        }).collect(Collectors.toList());
        entity.setTags(tagEntities);

        return entity;
    }
}