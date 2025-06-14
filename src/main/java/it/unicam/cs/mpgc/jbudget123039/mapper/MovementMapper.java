package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.*;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;

import java.util.UUID;

/**
 * Mapper per la conversione tra oggetti Movement, MovementEntity e ScheduledMovementEntity.
 * Fornisce metodi statici per la trasformazione tra modelli e entità.
 */
public class MovementMapper {

    /**
     * Converte un oggetto ScheduledMovementEntity in un MovementEntity.
     * Genera un nuovo UUID per l'entity.
     *
     * @param scheduled l'entità ScheduledMovementEntity da convertire
     * @return il corrispondente MovementEntity oppure null se l'input è null
     */
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

    /**
     * Converte un oggetto Movement in un MovementEntity.
     * Se l'id del modello è null, viene generato un nuovo UUID.
     *
     * @param model l'oggetto Movement da convertire
     * @return il corrispondente MovementEntity oppure null se il modello è null
     */
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

    /**
     * Converte un oggetto MovementEntity in un Movement.
     * Il tipo restituito è BasicMovement.
     *
     * @param entity l'entità MovementEntity da convertire
     * @return il corrispondente oggetto Movement oppure null se l'entità è null
     */
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