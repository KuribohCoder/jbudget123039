package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.util.DateUtil;

/**
 * Mapper per la conversione tra ScheduledMovementEntity e ScheduledMovement.
 * Fornisce metodi statici per la trasformazione bidirezionale tra entity e modello.
 */
public class ScheduledMovementMapper {

    /**
     * Converte un oggetto ScheduledMovementEntity in un ScheduledMovement.
     * Utilizza DateUtil per fornire un valore di default alla data schedulata, se necessario.
     *
     * @param entity l'entità ScheduledMovementEntity da convertire
     * @return il corrispondente ScheduledMovement oppure null se l'entità è null
     */
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

    /**
     * Converte un oggetto ScheduledMovement in un ScheduledMovementEntity.
     *
     * @param model l'oggetto ScheduledMovement da convertire
     * @return il corrispondente ScheduledMovementEntity oppure null se il modello è null
     */
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