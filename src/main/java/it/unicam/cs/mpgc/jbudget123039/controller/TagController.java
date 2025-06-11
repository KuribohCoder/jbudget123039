package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class TagController {

    private final TagRepository repository = new TagRepository();

    public CompletionStage<List<TagEntity>> loadAllTagsWithChildrenAsync() {
        return repository.loadAllTagsWithChildrenAsync();
    }

    public CompletionStage<TagEntity> saveOrUpdateTagAsync(TagEntity tag) {
        return repository.saveOrUpdateTagAsync(tag);
    }


    public CompletionStage<Void> deleteTagAsync(UUID id) {
        return repository.deleteTagAsync(id);
    }

}