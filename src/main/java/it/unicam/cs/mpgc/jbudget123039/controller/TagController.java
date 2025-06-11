package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.service.TagService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class TagController {

    private final TagService tagService;

    // Costruttore che riceve il service da iniettare (meglio per test e modularità)
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    public CompletionStage<List<Tag>> loadAllTagsWithChildrenAsync() {
        return tagService.loadAllTags();
    }

    public CompletionStage<Tag> saveOrUpdateTagAsync(Tag tag) {
        return tagService.saveOrUpdateTag(tag);
    }

    public CompletionStage<Void> deleteTagAsync(UUID id) {
        return tagService.deleteTag(id);
    }

    public CompletionStage<Tag> createTagIfNotExistsByName(String name) {
        return tagService.createTagIfNotExistsByName(name);
    }
}