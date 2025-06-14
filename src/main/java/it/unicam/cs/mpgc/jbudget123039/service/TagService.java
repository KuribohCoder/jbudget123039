package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Service per la gestione dei tag.
 * Fornisce metodi per creare, aggiornare, eliminare e caricare tag,
 * gestendo anche la verifica dell'esistenza del parent e la gerarchia dei figli.
 */
public class TagService {

    private final TagRepository repository;

    /**
     * Costruttore che riceve un repository di tag.
     *
     * @param repository repository per l'accesso e manipolazione dei dati TagEntity
     */
    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuovo tag con il nome specificato solo se non esiste già un tag con quel nome.
     * Utile principalmente per interfacce utente per evitare duplicati.
     *
     * @param name nome del tag da creare
     * @return {@link CompletionStage} contenente il tag creato o esistente
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
     * Salva o aggiorna un tag nel database.
     * Se il tag ha un parent, verifica che il parent esista nel database.
     *
     * @param tag modello Tag da salvare o aggiornare
     * @return {@link CompletionStage} contenente il tag salvato/aggiornato
     * @throws IllegalArgumentException se il parent ha ID nullo
     * @throws IllegalStateException se il parent non esiste nel database
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

    /**
     * Verifica che il parent del tag esista nel database.
     * Se il parent non è presente o il suo ID è nullo, lancia eccezioni.
     *
     * @param tag tag di cui verificare il parent
     * @return {@link CompletionStage} completato se la verifica ha successo
     */
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
     * Elimina un tag dal database.
     * Prima della cancellazione rimuove tutte le relazioni del tag con movimenti,
     * scheduled movements, il parent e i figli per mantenere l'integrità referenziale.
     *
     * @param id UUID del tag da eliminare
     * @return {@link CompletionStage} che completa quando la cancellazione è terminata
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
     * Carica tutti i tag dal database, inclusa la gerarchia dei figli.
     *
     * @return {@link CompletionStage} contenente la lista di tag con gerarchia
     */
    public CompletionStage<List<Tag>> loadAllTagsWithHierarchy() {
        return repository.loadAllTagsWithChildrenAsync()
                .thenApply(TagMapper::toModelTagList);
    }

    /**
     * Carica tutti i tag, equivalente a {@link #loadAllTagsWithHierarchy()}.
     *
     * @return {@link CompletionStage} contenente la lista di tag
     */
    public CompletionStage<List<Tag>> loadAllTags() {
        return repository.loadAllTagsWithChildrenAsync()
                .thenApply(TagMapper::toModelTagList);
    }
}