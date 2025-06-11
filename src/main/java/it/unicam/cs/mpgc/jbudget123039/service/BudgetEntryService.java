package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.BudgetEntryMapper;
import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetEntry;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntryEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.BudgetEntryRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class BudgetEntryService {

    private final BudgetEntryRepository budgetEntryRepository;

    public BudgetEntryService(BudgetEntryRepository budgetEntryRepository) {
        this.budgetEntryRepository = budgetEntryRepository;
    }

    public CompletionStage<Void> addOrUpdateBudgetEntry(BudgetEntry entry) {
        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID());
        }
        BudgetEntryEntity entity = BudgetEntryMapper.toEntity(entry);
        return budgetEntryRepository.saveOrUpdateBudgetEntryAsync(entity);
    }

    public CompletionStage<Void> deleteBudgetEntry(UUID id) {
        return budgetEntryRepository.deleteBudgetEntryAsync(id);
    }

    public CompletionStage<List<BudgetEntry>> loadAllBudgetEntries() {
        return budgetEntryRepository.loadAllBudgetEntriesAsync()
                .thenApply(list -> list.stream()
                        .map(BudgetEntryMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<BudgetEntry> findById(UUID id) {
        return budgetEntryRepository.findByIdAsync(id)
                .thenApply(BudgetEntryMapper::toModel);
    }
}