package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public CompletionStage<Void> addOrUpdateBudget(Budget budget) {
        if (budget == null) {
            return CompletableFuture.failedStage(new IllegalArgumentException("Budget cannot be null"));
        }
        return budgetService.addOrUpdateBudget(budget);
    }

    public CompletionStage<Void> deleteBudget(UUID id) {
        if (id == null) {
            return CompletableFuture.failedStage(new IllegalArgumentException("Budget ID cannot be null"));
        }
        return budgetService.deleteBudget(id);
    }

    public CompletionStage<List<Budget>> loadAllBudgets() {
        return budgetService.loadAllBudgets();
    }

    public CompletionStage<Budget> findById(UUID id) {
        if (id == null) {
            return CompletableFuture.failedStage(new IllegalArgumentException("Budget ID cannot be null"));
        }
        return budgetService.findById(id);
    }

    /**
     * Carica i movimenti associati a un budget, usando i tag già presenti nei movimenti.
     */
    public CompletionStage<List<Movement>> loadMovementsForBudget(Budget budget) {
        if (budget == null) {
            return CompletableFuture.completedFuture(List.of());
        }
        return budgetService.loadMovementsForBudget(budget);
    }

    /**
     * Carica tutti i tag disponibili.
     */
    public CompletionStage<List<Tag>> loadAllTags() {
        return budgetService.loadAllTags();
    }

    /**
     * Carica i movimenti filtrati per un solo tag selezionato dall’utente e l’intervallo temporale del budget.
     */
    public CompletionStage<List<Movement>> loadMovementsForBudgetWithTag(Budget budget, Tag tag) {
        if (budget == null || tag == null) {
            return CompletableFuture.completedFuture(List.of());
        }
        return budgetService.loadMovementsForBudgetWithTag(budget, tag);
    }
}