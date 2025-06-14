package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.BasicMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;
import it.unicam.cs.mpgc.jbudget123039.service.MovementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Controller per la gestione dei movimenti.
 * Espone metodi asincroni per aggiungere, aggiornare, eliminare e caricare movimenti,
 * delegando la logica al servizio {@link MovementService}.
 */
public class MovementController {

    private final MovementService movementService = new MovementService(
            new MovementRepository(),
            new TagRepository()
    );

    /**
     * Aggiunge un nuovo movimento asincronamente.
     *
     * @param description descrizione del movimento
     * @param date data del movimento
     * @param amount importo del movimento
     * @param income indica se è entrata o uscita
     * @param tags lista dei tag associati
     * @return CompletionStage completato quando l'operazione è terminata
     */
    public CompletionStage<Void> addMovementAsync(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        Movement movement = new BasicMovement(description, date, amount, income, tags);
        return movementService.addMovementAsync(movement);
    }

    /**
     * Aggiorna un movimento esistente asincronamente.
     *
     * @param movement movimento da aggiornare
     * @return CompletionStage completato quando l'operazione è terminata
     */
    public CompletionStage<Void> updateMovementAsync(Movement movement) {
        return movementService.updateMovementAsync(movement);
    }

    /**
     * Elimina un movimento dato il suo ID asincronamente.
     *
     * @param id UUID del movimento da eliminare
     * @return CompletionStage completato quando l'operazione è terminata
     */
    public CompletionStage<Void> deleteMovementAsync(UUID id) {
        return movementService.deleteMovementAsync(id);
    }

    /**
     * Carica tutti i movimenti asincronamente.
     *
     * @return CompletionStage con la lista di tutti i movimenti
     */
    public CompletionStage<List<Movement>> loadAllMovementsAsync() {
        return movementService.loadAllMovementsAsync();
    }

    /**
     * Converte una lista di {@link TagEntity} in una lista di modelli {@link Tag}.
     *
     * @param entities lista di entità tag
     * @return lista di modelli tag
     */
    public List<Tag> convertToModelTags(List<TagEntity> entities) {
        return TagMapper.toModelTagList(entities);
    }

    /**
     * Carica tutti i tag asincronamente.
     *
     * @return CompletionStage con la lista di tutti i tag
     */
    public CompletionStage<List<Tag>> loadAllTagsAsync() {
        return movementService.loadAllTagsAsync();
    }
}