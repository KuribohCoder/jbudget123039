package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public CompletionStage<Void> addOrUpdateBudget(Budget budget) {
        return budgetService.addOrUpdateBudget(budget);
    }

    public CompletionStage<Void> deleteBudget(UUID id) {
        return budgetService.deleteBudget(id);
    }

    public CompletionStage<List<Budget>> loadAllBudgets() {
        return budgetService.loadAllBudgets();
    }

    public CompletionStage<Budget> findById(UUID id) {
        return budgetService.findById(id);
    }
}