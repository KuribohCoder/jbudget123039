package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea un tag con il nome specificato solo se non esiste già.
     * Utile principalmente per interfacce UI.
     */
    public CompletionStage<Tag> createTagIfNotExistsByName(String name) {
        return repository.findByNameAsync(name)
                .thenCompose(existing -> {
                    if (existing != null) {
                        return CompletableFuture.completedFuture(TagMapper.toModel(existing));
                    }
                    TagEntity newTag = new TagEntity();
                    newTag.setId(UUID.randomUUID());
                    newTag.setName(name);
                    System.out.println("[TagService] createTagIfNotExistsByName, nuovo tag ID: " + newTag.getId());
                    return repository.saveOrUpdateTagAsync(newTag)
                            .thenApply(TagMapper::toModel);
                });
    }

    /**
     * Salva o aggiorna un tag. Se è presente un parent, verifica che esista nel DB.
     */
    public CompletionStage<Tag> saveOrUpdateTag(Tag tag) {
        return ensureParentExists(tag)
                .thenCompose(v -> {
                    TagEntity entity = TagMapper.toEntity(tag);
                    System.out.println("[TagService] saveOrUpdateTag ID: " + tag.getId());
                    return repository.saveOrUpdateTagAsync(entity)
                            .thenApply(TagMapper::toModel);
                });
    }

    private CompletionStage<Void> ensureParentExists(Tag tag) {
        if (tag.getParent() == null) return CompletableFuture.completedFuture(null);

        UUID parentId = tag.getParent().getId();
        if (parentId == null) {
            throw new IllegalArgumentException("Parent tag must have a valid ID.");
        }
        System.out.println("[TagService] Verifica parent ID: " + parentId);

        return repository.findByIdAsync(parentId)
                .thenAccept(existing -> {
                    if (existing == null) {
                        throw new IllegalStateException("Parent tag not found: " + parentId);
                    }
                    System.out.println("[TagService] Parent trovato: " + existing.getName());
                });
    }

    /**
     * Elimina un tag rimuovendo prima tutte le relazioni con movimenti, scheduledMovements e figli.
     */
    public CompletionStage<Void> deleteTag(UUID id) {
        return repository.findByIdAsync(id)
                .thenCompose(entity -> {
                    if (entity == null) return CompletableFuture.completedFuture(null);

                    if (entity.getMovements() != null) {
                        entity.getMovements().forEach(m -> m.getTags().remove(entity));
                    }
                    if (entity.getScheduledMovements() != null) {
                        entity.getScheduledMovements().forEach(s -> s.getTags().remove(entity));
                    }
                    if (entity.getParent() != null) {
                        entity.getParent().getChildren().remove(entity);
                        entity.setParent(null);
                    }
                    if (entity.getChildren() != null) {
                        entity.getChildren().forEach(child -> child.setParent(null));
                        entity.getChildren().clear();
                    }

                    return repository.deleteTagAsync(id);
                });
    }

    /**
     * Carica tutti i tag, inclusa la gerarchia di figli.
     */
    public CompletionStage<List<Tag>> loadAllTagsWithHierarchy() {
        return repository.loadAllTagsWithChildrenAsync()
                .thenApply(TagMapper::toModelTagList);
    }

    public CompletionStage<List<Tag>> loadAllTags() {
        return repository.loadAllTagsWithChildrenAsync()
                .thenApply(TagMapper::toModelTagList);
    }
}