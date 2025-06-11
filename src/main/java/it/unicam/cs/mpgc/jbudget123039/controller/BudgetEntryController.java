package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetEntry;
import it.unicam.cs.mpgc.jbudget123039.service.BudgetEntryService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class BudgetEntryController {

    private final BudgetEntryService budgetEntryService;

    public BudgetEntryController(BudgetEntryService budgetEntryService) {
        this.budgetEntryService = budgetEntryService;
    }

    public CompletionStage<Void> addOrUpdateBudgetEntry(BudgetEntry entry) {
        return budgetEntryService.addOrUpdateBudgetEntry(entry);
    }

    public CompletionStage<Void> deleteBudgetEntry(UUID id) {
        return budgetEntryService.deleteBudgetEntry(id);
    }

    public CompletionStage<List<BudgetEntry>> loadAllBudgetEntries() {
        return budgetEntryService.loadAllBudgetEntries();
    }

    public CompletionStage<BudgetEntry> findById(UUID id) {
        return budgetEntryService.findById(id);
    }
}