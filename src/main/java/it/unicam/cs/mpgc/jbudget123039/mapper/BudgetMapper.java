package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetEntry;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntryEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BudgetMapper {

    public static Budget toModel(BudgetEntity entity) {
        if (entity == null) return null;

        Budget model = new Budget();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setStartDate(entity.getStartDate());
        model.setEndDate(entity.getEndDate());

        List<BudgetEntry> entries = entity.getEntries() == null ? List.of() :
                entity.getEntries().stream()
                        .map(BudgetEntryMapper::toModel)
                        .collect(Collectors.toList());
        model.setEntries(entries);

        return model;
    }

    public static BudgetEntity toEntity(Budget model) {
        if (model == null) return null;

        BudgetEntity entity = new BudgetEntity();
        entity.setId(model.getId() != null ? model.getId() : UUID.randomUUID());
        entity.setName(model.getName());
        entity.setStartDate(model.getStartDate());
        entity.setEndDate(model.getEndDate());

        List<BudgetEntryEntity> entryEntities = model.getEntries() == null ? List.of() :
                model.getEntries().stream()
                        .map(entry -> BudgetEntryMapper.toEntity(entry))
                        .collect(Collectors.toList());
        entity.setEntries(entryEntities);

        return entity;
    }
}