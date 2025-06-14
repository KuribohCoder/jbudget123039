package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.BudgetMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.mapper.TagMapper;
import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.BudgetRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Servizio per la gestione logica dei budget, dei movimenti e dei tag associati.
 * Fornisce metodi asincroni per operazioni CRUD e per il caricamento di dati strutturati.
 */
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final MovementRepository movementRepository;
    private final TagRepository tagRepository;

    /**
     * Costruisce un nuovo {@code BudgetService} iniettando i repository necessari.
     *
     * @param budgetRepository     repository per la persistenza dei budget
     * @param movementRepository   repository per la persistenza dei movimenti
     * @param tagRepository        repository per la persistenza dei tag
     */
    public BudgetService(BudgetRepository budgetRepository,
                         MovementRepository movementRepository,
                         TagRepository tagRepository) {
        this.budgetRepository = budgetRepository;
        this.movementRepository = movementRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Aggiunge o aggiorna un budget nel database.
     * Se il budget non ha un ID, ne viene generato uno nuovo.
     *
     * @param budget il budget da aggiungere o aggiornare
     * @return un {@link CompletionStage} che completa l'operazione
     */
    public CompletionStage<Void> addOrUpdateBudget(Budget budget) {
        if (budget.getId() == null) {
            budget.setId(UUID.randomUUID());
        }
        BudgetEntity entity = BudgetMapper.toEntity(budget);
        return budgetRepository.saveOrUpdateBudgetAsync(entity);
    }

    /**
     * Elimina un budget esistente dato il suo ID.
     *
     * @param id l'UUID del budget da eliminare
     * @return un {@link CompletionStage} che completa l'operazione
     */
    public CompletionStage<Void> deleteBudget(UUID id) {
        return budgetRepository.deleteBudgetAsync(id);
    }

    /**
     * Carica tutti i budget presenti nel sistema.
     *
     * @return un {@link CompletionStage} contenente la lista di budget
     */
    public CompletionStage<List<Budget>> loadAllBudgets() {
        return budgetRepository.loadAllBudgetsAsync()
                .thenApply(list -> list.stream()
                        .map(BudgetMapper::toModel)
                        .collect(Collectors.toList()));
    }

    /**
     * Trova un budget dato il suo ID.
     *
     * @param id l'UUID del budget da cercare
     * @return un {@link CompletionStage} contenente il budget trovato, oppure null se non esiste
     */
    public CompletionStage<Budget> findById(UUID id) {
        return budgetRepository.findByIdAsync(id)
                .thenApply(BudgetMapper::toModel);
    }

    /**
     * Carica tutti i tag disponibili nel sistema.
     *
     * @return un {@link CompletionStage} contenente la lista di tag
     */
    public CompletionStage<List<Tag>> loadAllTags() {
        return tagRepository.loadAllTagsAsync()
                .thenApply(list -> list.stream()
                        .map(TagMapper::toModel)
                        .collect(Collectors.toList()));
    }

    /**
     * Carica i movimenti relativi a un budget, cercando un tag associato
     * al primo movimento del budget per filtrare i risultati.
     *
     * @param budget il budget di riferimento
     * @return un {@link CompletionStage} contenente la lista di movimenti trovati
     */
    public CompletionStage<List<Movement>> loadMovementsForBudget(Budget budget) {
        UUID tagId = budget.getMovements() != null && !budget.getMovements().isEmpty()
                ? budget.getMovements().get(0).getTags().stream().findFirst().map(Tag::getId).orElse(null)
                : null;

        return movementRepository.findMovementsByTagAndDateRangeAsync(tagId, budget.getStartDate(), budget.getEndDate())
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

    /**
     * Carica i movimenti relativi a un budget filtrando per uno specifico tag.
     *
     * @param budget il budget di riferimento
     * @param tag    il tag da usare come filtro
     * @return un {@link CompletionStage} contenente la lista dei movimenti trovati
     */
    public CompletionStage<List<Movement>> loadMovementsForBudgetWithTag(Budget budget, Tag tag) {
        return movementRepository.findMovementsByTagAndDateRangeAsync(tag.getId(), budget.getStartDate(), budget.getEndDate())
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));
    }
}