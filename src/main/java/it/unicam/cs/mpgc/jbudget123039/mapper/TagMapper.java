package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper per la conversione tra TagEntity e Tag.
 * Gestisce conversioni bidirezionali, incluse le relazioni gerarchiche
 * tra tag (parent e children), evitando cicli infiniti tramite una cache temporanea.
 */
public class TagMapper {

    /**
     * Converte una TagEntity in un modello Tag, gestendo ricorsivamente
     * parent e children per mantenere la struttura gerarchica.
     *
     * @param entity l'entità TagEntity da convertire
     * @return il corrispondente modello Tag oppure null se l'entità è null
     */
    public static Tag toModel(TagEntity entity) {
        return toModel(entity, new HashMap<>());
    }

    /**
     * Conversione interna con supporto a cache per evitare ricorsioni infinite.
     *
     * @param entity l'entità TagEntity da convertire
     * @param seen mappa di Tag già convertiti per gestione cicli
     * @return il corrispondente modello Tag oppure null se l'entità è null
     */
    private static Tag toModel(TagEntity entity, Map<UUID, Tag> seen) {
        if (entity == null) return null;

        Tag existing = seen.get(entity.getId());
        if (existing != null) return existing;

        Tag tag = new Tag(entity.getName());
        tag.setId(entity.getId());

        seen.put(entity.getId(), tag);

        if (entity.getParent() != null) {
            Tag parent = toModel(entity.getParent(), seen);
            tag.setParent(parent);
        }

        List<TagEntity> childrenEntities = entity.getChildren();
        if (childrenEntities != null && !childrenEntities.isEmpty()) {
            for (TagEntity childEntity : childrenEntities) {
                Tag childTag = toModel(childEntity, seen);
                tag.addChild(childTag);
            }
        }

        return tag;
    }

    /**
     * Converte un modello Tag in un'entità TagEntity, mantenendo la struttura
     * gerarchica e utilizzando una cache per evitare cicli infiniti.
     *
     * @param tag il modello Tag da convertire
     * @return il corrispondente TagEntity
     * @throws IllegalArgumentException se l'id del tag o del parent è null
     */
    public static TagEntity toEntity(Tag tag) {
        return toEntity(tag, new HashMap<>());
    }

    /**
     * Conversione interna con cache per gestione ricorsiva e cicli.
     *
     * @param tag il modello Tag da convertire
     * @param seen mappa di TagEntity già convertiti per gestione cicli
     * @return il corrispondente TagEntity
     * @throws IllegalArgumentException se l'id del tag o del parent è null
     */
    private static TagEntity toEntity(Tag tag, Map<UUID, TagEntity> seen) {
        if (tag == null) return null;

        if (tag.getId() == null) {
            throw new IllegalArgumentException("Tag ID must not be null for entity mapping");
        }

        TagEntity existing = seen.get(tag.getId());
        if (existing != null) return existing;

        TagEntity entity = new TagEntity();
        entity.setId(tag.getId());
        entity.setName(tag.getName());

        seen.put(entity.getId(), entity);

        if (tag.getParent() != null) {
            if (tag.getParent().getId() == null) {
                throw new IllegalArgumentException("Parent tag ID must not be null");
            }
            TagEntity parentEntity = seen.get(tag.getParent().getId());
            if (parentEntity == null) {
                parentEntity = toEntity(tag.getParent(), seen);
            }
            entity.setParent(parentEntity);
        }

        if (!tag.getChildren().isEmpty()) {
            List<TagEntity> childEntities = tag.getChildren().stream()
                    .map(child -> {
                        TagEntity childEntity = toEntity(child, seen);
                        if (childEntity.getParent() == null || !childEntity.getParent().equals(entity)) {
                            childEntity.setParent(entity);
                        }
                        return childEntity;
                    })
                    .collect(Collectors.toList());

            entity.setChildren(childEntities);
        }

        return entity;
    }

    /**
     * Converte una lista di modelli Tag in una lista di TagEntity.
     *
     * @param tags lista di modelli Tag, può essere null
     * @return lista di TagEntity corrispondenti, lista vuota se input è null
     */
    public static List<TagEntity> toEntityTagList(List<Tag> tags) {
        return tags == null ? List.of() : tags.stream()
                .map(TagMapper::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converte una lista di entità TagEntity in una lista di modelli Tag.
     *
     * @param entities lista di TagEntity, può essere null
     * @return lista di modelli Tag corrispondenti, lista vuota se input è null
     */
    public static List<Tag> toModelTagList(List<TagEntity> entities) {
        if (entities == null) return List.of();

        Map<UUID, Tag> seen = new HashMap<>();
        return entities.stream()
                .map(e -> toModel(e, seen))
                .collect(Collectors.toList());
    }
}