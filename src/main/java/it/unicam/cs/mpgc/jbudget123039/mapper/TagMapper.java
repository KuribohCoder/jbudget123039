package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {

    // Converte da TagEntity a Tag (model), gestendo ricorsione per gerarchia
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

    // Converte da Tag (model) a TagEntity, ricorsivamente per gerarchia
    public static TagEntity toEntity(Tag tag) {
        if (tag == null) return null;

        TagEntity entity = new TagEntity();
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

        // Gestione parent se presente
        if (tag.getParent() != null) {
            entity.setParent(toEntity(tag.getParent()));
        }

        return entity;
    }
}