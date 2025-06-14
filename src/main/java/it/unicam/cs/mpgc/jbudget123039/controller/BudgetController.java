package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Controller per la gestione dei budget.
 * Fornisce operazioni asincrone per creare, modificare, eliminare e consultare budget e relativi movimenti.
 */
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Costruisce un nuovo {@code BudgetController} con il servizio fornito.
     *
     * @param budgetService il servizio per la gestione dei budget
     */
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Aggiunge o aggiorna un budget.
     *
     * @param budget il budget da salvare o aggiornare
     * @return uno {@code CompletionStage} che si completa quando l’operazione è conclusa
     */
    public CompletionStage<Void> addOrUpdateBudget(Budget budget) {
        if (budget == null) {
            return CompletableFuture.failedStage(new IllegalArgumentException("Budget cannot be null"));
        }
        return budgetService.addOrUpdateBudget(budget);
    }

    /**
     * Elimina un budget dato il suo ID.
     *
     * @param id l’ID del budget da eliminare
     * @return uno {@code CompletionStage} che si completa quando l’operazione è conclusa
     */
    public CompletionStage<Void> deleteBudget(UUID id) {
        if (id == null) {
            return CompletableFuture.failedStage(new IllegalArgumentException("Budget ID cannot be null"));
        }
        return budgetService.deleteBudget(id);
    }

    /**
     * Carica tutti i budget salvati.
     *
     * @return uno {@code CompletionStage} contenente la lista dei budget
     */
    public CompletionStage<List<Budget>> loadAllBudgets() {
        return budgetService.loadAllBudgets();
    }

    /**
     * Trova un budget dato il suo ID.
     *
     * @param id l’ID del budget da cercare
     * @return uno {@code CompletionStage} contenente il budget, se trovato
     */
    public CompletionStage<Budget> findById(UUID id) {
        if (id == null) {
            return CompletableFuture.failedStage(new IllegalArgumentException("Budget ID cannot be null"));
        }
        return budgetService.findById(id);
    }

    /**
     * Carica i movimenti associati a un determinato budget, usando i tag presenti nei movimenti.
     *
     * @param budget il budget di riferimento
     * @return uno {@code CompletionStage} contenente la lista dei movimenti associati
     */
    public CompletionStage<List<Movement>> loadMovementsForBudget(Budget budget) {
        if (budget == null) {
            return CompletableFuture.completedFuture(List.of());
        }
        return budgetService.loadMovementsForBudget(budget);
    }

    /**
     * Carica tutti i tag disponibili.
     *
     * @return uno {@code CompletionStage} contenente la lista dei tag
     */
    public CompletionStage<List<Tag>> loadAllTags() {
        return budgetService.loadAllTags();
    }

    /**
     * Carica i movimenti associati a un determinato budget, filtrati per uno specifico tag.
     *
     * @param budget il budget di riferimento
     * @param tag    il tag per cui filtrare i movimenti
     * @return uno {@code CompletionStage} contenente la lista dei movimenti filtrati
     */
    public CompletionStage<List<Movement>> loadMovementsForBudgetWithTag(Budget budget, Tag tag) {
        if (budget == null || tag == null) {
            return CompletableFuture.completedFuture(List.of());
        }
        return budgetService.loadMovementsForBudgetWithTag(budget, tag);
    }
}