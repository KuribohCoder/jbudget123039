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
        entity.setId(UUID.randomUUID()); // nuovo ID
        entity.setDescription(scheduled.getDescription());
        entity.setAmount(scheduled.getAmount());
        entity.setDate(scheduled.getScheduledDate());
        entity.setIncome(scheduled.isIncome());
        entity.setTags(scheduled.getTags()); // attenzione: stesso riferimento, ok se immutabile

        return entity;
    }
    public static MovementEntity toEntity(Movement m) {
        MovementEntity e = new MovementEntity();
        if (m.getId() != null) {
            e.setId(m.getId());
        } else {
            e.setId(UUID.randomUUID());
        }
        e.setDescription(m.getDescription());
        e.setDate(m.getDate());
        e.setAmount(m.getAmount());
        e.setIncome(m.isIncome());
        e.setTags(m.getTags().stream().map(MovementMapper::tagToEntity).collect(Collectors.toList()));
        return e;
    }

    public static TagEntity tagToEntity(Tag t) {
        TagEntity e = new TagEntity();
        e.setName(t.getName());
        // mappa gerarchia se serve
        return e;
    }

    public static Movement toModel(MovementEntity e) {
        if (e == null) return null;

        List<Tag> tags = e.getTags() == null ? List.of() :
                e.getTags().stream()
                        .map(MovementMapper::tagToModel)
                        .collect(Collectors.toList());

        BasicMovement movement = new BasicMovement(
                e.getDescription(),
                e.getDate(),
                e.getAmount(),
                e.isIncome(),
                tags
        );
        movement.setId(e.getId()); // 👈 fondamentale per l'eliminazione

        return movement;
    }

    public static Tag tagToModel(TagEntity e) {
        if (e == null) return null;

        Tag tag = new Tag(e.getName());
        // Se vuoi puoi mappare anche la gerarchia di parent/children
        return tag;
    }


}
