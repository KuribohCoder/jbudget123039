package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.BudgetMapper;
import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.BudgetRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public CompletionStage<Void> addOrUpdateBudget(Budget budget) {
        if (budget.getId() == null) {
            budget.setId(UUID.randomUUID());
        }
        BudgetEntity entity = BudgetMapper.toEntity(budget);
        return budgetRepository.saveOrUpdateBudgetAsync(entity);
    }

    public CompletionStage<Void> deleteBudget(UUID id) {
        return budgetRepository.deleteBudgetAsync(id);
    }

    public CompletionStage<List<Budget>> loadAllBudgets() {
        return budgetRepository.loadAllBudgetsAsync()
                .thenApply(list -> list.stream()
                        .map(BudgetMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<Budget> findById(UUID id) {
        return budgetRepository.findByIdAsync(id)
                .thenApply(BudgetMapper::toModel);
    }
}