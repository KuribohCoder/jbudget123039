package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.ScheduledMovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovementOrigin;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Service per la gestione dei movimenti programmati (ScheduledMovement).
 * Fornisce operazioni asincrone per creare, aggiornare, cancellare, caricare movimenti programmati,
 * verificare i tag associati e processare movimenti dovuti.
 */
public class ScheduledMovementService {

    private final ScheduledMovementRepository scheduledRepo;
    private final MovementRepository movementRepo = new MovementRepository();
    private final TagRepository tagRepository;

    /**
     * Costruttore che inizializza i repository necessari per gestire movimenti programmati e tag.
     *
     * @param scheduledRepo repository per i movimenti programmati
     * @param tagRepository repository per i tag
     */
    public ScheduledMovementService(ScheduledMovementRepository scheduledRepo, TagRepository tagRepository) {
        this.scheduledRepo = scheduledRepo;
        this.tagRepository = tagRepository;
    }

    /**
     * Verifica che tutti i tag nella lista esistano nel database.
     * Se un tag non ha ID, cerca per nome e assegna l'ID trovato.
     * Genera un errore se un tag non esiste o non è trovato.
     *
     * @param tags lista di tag da verificare
     * @return CompletionStage che completa al termine della verifica
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

    /**
     * Salva o aggiorna un movimento programmato.
     * Se l'ID non è presente, ne viene generato uno nuovo.
     * Se l'origine non è specificata, viene impostata manuale (MANUAL).
     * Verifica i tag associati prima del salvataggio.
     *
     * @param model movimento programmato da salvare o aggiornare
     * @return CompletionStage che completa quando l'operazione è terminata
     */
    public CompletionStage<Void> saveOrUpdateScheduledMovement(ScheduledMovement model) {
        if (model.getId() == null) {
            model.setId(UUID.randomUUID());
        }
        if (model.getOrigin() == null) {
            model.setOrigin(ScheduledMovementOrigin.MANUAL);
        }

        return verifyTagsExist(model.getTags())
                .thenCompose(v -> {
                    ScheduledMovementEntity entity = ScheduledMovementMapper.toEntity(model);
                    return scheduledRepo.saveOrUpdateScheduledMovementAsync(entity);
                });
    }

    /**
     * Elimina un movimento programmato dato il suo ID.
     *
     * @param id ID del movimento programmato da eliminare
     * @return CompletionStage che completa al termine della cancellazione
     */
    public CompletionStage<Void> deleteScheduledMovement(UUID id) {
        return scheduledRepo.deleteScheduledMovementAsync(id);
    }

    /**
     * Carica tutti i movimenti programmati presenti nel database.
     *
     * @return CompletionStage contenente la lista di movimenti programmati
     */
    public CompletionStage<List<ScheduledMovement>> loadAllScheduledMovements() {
        return scheduledRepo.loadAllScheduledMovementsAsync()
                .thenApply(list -> list.stream()
                        .map(ScheduledMovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

    /**
     * Carica tutti i tag disponibili.
     *
     * @return CompletionStage contenente la lista dei tag
     */
    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return tagRepository.loadAllTagsAsync()
                .thenApply(list -> list.stream()
                        .map(TagMapper::toModel)
                        .collect(Collectors.toList()));
    }

    /**
     * Processa i movimenti programmati dovuti (ad esempio quelli con data prevista già trascorsa).
     * Per ogni movimento dovuto, crea un movimento reale e poi elimina il movimento programmato corrispondente.
     *
     * @return CompletionStage che completa al termine del processo di tutti i movimenti dovuti
     */
    public CompletionStage<Void> processDueScheduledMovements() {
        return scheduledRepo.loadDueScheduledMovementsAsync()
                .thenCompose(dueList -> {
                    CompletionStage<Void> chain = CompletableFuture.completedFuture(null);

                    for (ScheduledMovementEntity scheduledEntity : dueList) {
                        MovementEntity movementEntity = MovementMapper.scheduledToMovement(scheduledEntity);

                        chain = chain.thenCompose(v ->
                                movementRepo.saveOrUpdateAsync(movementEntity)
                                        .thenCompose(v2 -> scheduledRepo.deleteScheduledMovementAsync(scheduledEntity.getId()))
                        );
                    }

                    return chain;
                });
    }

    /**
     * Carica tutti i movimenti programmati di origine manuale.
     *
     * @return CompletionStage contenente la lista di movimenti programmati manuali
     */
    public CompletionStage<List<ScheduledMovement>> loadAllManualScheduledMovements() {
        return scheduledRepo.loadAllManualScheduledMovementsAsync()
                .thenApply(list -> list.stream()
                        .map(ScheduledMovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

}