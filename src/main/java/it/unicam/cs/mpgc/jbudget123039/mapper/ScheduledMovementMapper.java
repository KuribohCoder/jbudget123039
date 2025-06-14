package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.util.DateUtil;

public class ScheduledMovementMapper {

    public static ScheduledMovement toModel(ScheduledMovementEntity entity) {
        if (entity == null) return null;

        ScheduledMovement model = new ScheduledMovement();
        model.setId(entity.getId());
        model.setDescription(entity.getDescription());
        model.setAmount(entity.getAmount());
        model.setScheduledDate(DateUtil.getOrDefault(entity.getScheduledDate()));
        model.setIncome(entity.isIncome());
        model.setTags(TagMapper.toModelTagList(entity.getTags()));
        model.setOrigin(entity.getOrigin());

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
        entity.setTags(TagMapper.toEntityTagList(model.getTags()));
        entity.setOrigin(model.getOrigin());

        return entity;
    }
}