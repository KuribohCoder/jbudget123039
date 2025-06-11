package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.util.*;
import java.util.stream.Collectors;

public class TagMapper {

    public static Tag toModel(TagEntity entity) {
        return toModel(entity, new HashMap<>());
    }

    private static Tag toModel(TagEntity entity, Map<UUID, Tag> seen) {
        if (entity == null) return null;

        Tag existing = seen.get(entity.getId());
        if (existing != null) return existing;

        Tag tag = new Tag(entity.getName());
        tag.setId(entity.getId());

        seen.put(entity.getId(), tag);

        // Imposta parent ricorsivamente se presente
        if (entity.getParent() != null) {
            Tag parent = toModel(entity.getParent(), seen);
            tag.setParent(parent);
        }

        // Aggiunge i figli ricorsivamente
        List<TagEntity> childrenEntities = entity.getChildren();
        if (childrenEntities != null && !childrenEntities.isEmpty()) {
            for (TagEntity childEntity : childrenEntities) {
                Tag childTag = toModel(childEntity, seen);
                tag.addChild(childTag); // addChild imposta anche il parent
            }
        }

        return tag;
    }

    public static TagEntity toEntity(Tag tag) {
        return toEntity(tag, new HashMap<>());
    }

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

    public static List<TagEntity> toEntityTagList(List<Tag> tags) {
        return tags == null ? List.of() : tags.stream()
                .map(TagMapper::toEntity)
                .collect(Collectors.toList());
    }

    public static List<Tag> toModelTagList(List<TagEntity> entities) {
        if (entities == null) return List.of();

        Map<UUID, Tag> seen = new HashMap<>();
        return entities.stream()
                .map(e -> toModel(e, seen))
                .collect(Collectors.toList());
    }
}