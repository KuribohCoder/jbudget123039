package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;

import java.util.ArrayList;
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

        List<Movement> movements = entity.getMovements() == null ? List.of() :
                entity.getMovements().stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList());
        model.setMovements(movements);

        return model;
    }

    public static BudgetEntity toEntity(Budget model) {
        if (model == null) return null;

        BudgetEntity entity = new BudgetEntity();
        entity.setId(model.getId() != null ? model.getId() : UUID.randomUUID());
        entity.setName(model.getName());
        entity.setStartDate(model.getStartDate());
        entity.setEndDate(model.getEndDate());

        List<MovementEntity> movementEntities = model.getMovements() == null ? List.of() :
                model.getMovements().stream()
                        .map(MovementMapper::toEntity)
                        .collect(Collectors.toList());
        entity.setMovements(movementEntities);

        return entity;
    }
}