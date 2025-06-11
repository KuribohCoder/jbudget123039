package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TagMapper {

    public static Tag toModel(TagEntity entity) {
        if (entity == null) return null;

        Tag tag = new Tag(entity.getName());

        if (entity.getChildren() != null) {
            for (TagEntity childEntity : entity.getChildren()) {
                Tag childTag = toModel(childEntity);
                childTag.setParent(tag);
            }
        }

        return tag;
    }

    public static TagEntity toEntity(Tag tag) {
        if (tag == null) return null;

        TagEntity entity = new TagEntity();

        if (tag.getId() != null) {
            entity.setId(tag.getId());
        } else {
            entity.setId(UUID.randomUUID());
        }

        entity.setName(tag.getName());

        if (tag.getChildren() != null && !tag.getChildren().isEmpty()) {
            List<TagEntity> childrenEntities = new ArrayList<>();
            for (Tag childTag : tag.getChildren()) {
                TagEntity childEntity = toEntity(childTag);
                childEntity.setParent(entity);
                childrenEntities.add(childEntity);
            }
            entity.setChildren(childrenEntities);
        }

        if (tag.getParent() != null) {
            entity.setParent(toEntity(tag.getParent()));
        }

        return entity;
    }

    public static List<TagEntity> toEntityTagList(List<Tag> tags) {
        return (tags == null) ? List.of() : tags.stream()
                .map(MovementMapper::tagToEntity)
                .collect(Collectors.toList());
    }

    public static List<Tag> toModelTagList(List<TagEntity> tagEntities) {
        return (tagEntities == null) ? List.of() : tagEntities.stream()
                .map(MovementMapper::tagToModel)
                .collect(Collectors.toList());
    }
}