package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetEntry;
import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntryEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;

public class BudgetEntryMapper {

    public static BudgetEntry toModel(BudgetEntryEntity entity) {
        if (entity == null) return null;

        BudgetEntry model = new BudgetEntry();
        model.setId(entity.getId());
        model.setDescription(entity.getDescription());
        model.setAmount(entity.getAmount());
        model.setDate(entity.getDate());

        // Usa TagMapper per la conversione completa
        if (entity.getTag() != null) {
            model.setTag(TagMapper.toModel(entity.getTag()));
        }

        // Budget shallow mapping (solo dati base per evitare cicli)
        if (entity.getBudget() != null) {
            Budget budgetModel = new Budget();
            budgetModel.setId(entity.getBudget().getId());
            budgetModel.setName(entity.getBudget().getName());
            budgetModel.setStartDate(entity.getBudget().getStartDate());
            budgetModel.setEndDate(entity.getBudget().getEndDate());
            model.setBudget(budgetModel);
        }

        return model;
    }

    public static BudgetEntryEntity toEntity(BudgetEntry model) {
        if (model == null) return null;

        BudgetEntryEntity entity = new BudgetEntryEntity();
        entity.setId(model.getId());
        entity.setDescription(model.getDescription());
        entity.setAmount(model.getAmount());
        entity.setDate(model.getDate());

        if (model.getTag() != null) {
            entity.setTag(TagMapper.toEntity(model.getTag()));
        }

        if (model.getBudget() != null) {
            BudgetEntity budgetEntity = new BudgetEntity();
            budgetEntity.setId(model.getBudget().getId());
            budgetEntity.setName(model.getBudget().getName());
            budgetEntity.setStartDate(model.getBudget().getStartDate());
            budgetEntity.setEndDate(model.getBudget().getEndDate());
            entity.setBudget(budgetEntity);
        }

        return entity;
    }
}