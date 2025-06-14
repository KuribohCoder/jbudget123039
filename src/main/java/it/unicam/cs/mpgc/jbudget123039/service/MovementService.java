package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class MovementService {

    private final MovementRepository movementRepository;
    private final TagRepository tagRepository;

    public MovementService(MovementRepository movementRepository, TagRepository tagRepository) {
        this.movementRepository = movementRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Carica i TagEntity associati, verificando che esistano.
     * Assicura che tutti i tag nel modello abbiano ID validi e corrispondano ad entità persistenti.
     */
    private CompletionStage<Void> verifyTagsExist(List<Tag> tags) {
        List<CompletableFuture<Void>> futures = tags.stream()
                .map(tag -> {
                    if (tag.getId() == null) {
                        return tagRepository.findByNameAsync(tag.getName())
                                .thenAccept(entity -> {
                                    if (entity == null) {
                                        throw new IllegalStateException("Tag '" + tag.getName() + "' not found.");
                                    }
                                    tag.setId(entity.getId());
                                }).toCompletableFuture();
                    } else {
                        // Cerca per ID
                        return tagRepository.findByIdAsync(tag.getId())
                                .thenAccept(entity -> {
                                    if (entity == null) {
                                        throw new IllegalStateException("Tag with ID " + tag.getId() + " not found.");
                                    }
                                }).toCompletableFuture();
                    }
                })
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletionStage<Void> addMovementAsync(Movement movement) {
        if (movement.getId() == null) {
            movement.setId(UUID.randomUUID());
        }
        return verifyTagsExist(movement.getTags())
                .thenCompose(v -> {
                    var entity = MovementMapper.toEntity(movement);
                    System.out.println("[MovementService] Aggiungo movement con tag IDs: " +
                            movement.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
                    return movementRepository.saveOrUpdateAsync(entity).thenApply(e -> null);
                });
    }

    public CompletionStage<Void> updateMovementAsync(Movement movement) {
        if (movement.getId() == null) {
            throw new IllegalArgumentException("Movement ID cannot be null for update");
        }
        return verifyTagsExist(movement.getTags())
                .thenCompose(v -> {
                    var entity = MovementMapper.toEntity(movement);
                    System.out.println("[MovementService] Aggiorno movement con tag IDs: " +
                            movement.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
                    return movementRepository.saveOrUpdateAsync(entity).thenApply(e -> null);
                });
    }

    public CompletionStage<Void> deleteMovementAsync(UUID id) {
        return movementRepository.deleteMovementAsync(id);
    }

    public CompletionStage<List<Movement>> loadAllMovementsAsync() {
        return movementRepository.loadAllMovementsAsync()
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return tagRepository.loadAllTagsAsync()
                .thenApply(list -> list.stream()
                        .map(TagMapper::toModel)
                        .collect(Collectors.toList()));
    }
}