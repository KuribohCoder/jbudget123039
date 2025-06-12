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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final MovementRepository movementRepository;
    private final TagRepository tagRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         MovementRepository movementRepository,
                         TagRepository tagRepository) {
        this.budgetRepository = budgetRepository;
        this.movementRepository = movementRepository;
        this.tagRepository = tagRepository;
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

    public CompletionStage<List<Tag>> loadAllTags() {
        return tagRepository.loadAllTagsAsync()
                .thenApply(list -> list.stream()
                        .map(TagMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<List<Movement>> loadMovementsForBudget(Budget budget) {
        UUID tagId = budget.getMovements() != null && !budget.getMovements().isEmpty()
                ? budget.getMovements().get(0).getTags().stream().findFirst().map(Tag::getId).orElse(null)
                : null;

        return movementRepository.findMovementsByTagAndDateRangeAsync(tagId, budget.getStartDate(), budget.getEndDate())
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<List<Movement>> loadMovementsForBudgetWithTag(Budget budget, Tag tag) {
        return movementRepository.findMovementsByTagAndDateRangeAsync(tag.getId(), budget.getStartDate(), budget.getEndDate())
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));
    }
}