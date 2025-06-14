package it.unicam.cs.mpgc.jbudget123039.mapper;

import it.unicam.cs.mpgc.jbudget123039.model.budget.Budget;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper per la conversione tra oggetti BudgetEntity e Budget.
 * Fornisce metodi statici per la trasformazione bidirezionale tra modello e entity.
 */
public class BudgetMapper {

    /**
     * Converte un oggetto BudgetEntity in un oggetto Budget.
     * Effettua la conversione anche della lista di movimenti associati.
     *
     * @param entity l'entità BudgetEntity da convertire
     * @return l'oggetto Budget corrispondente oppure null se l'entità è null
     */
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

    /**
     * Converte un oggetto Budget in un oggetto BudgetEntity.
     * Se l'id del modello è null, viene generato un nuovo UUID.
     * Effettua la conversione anche della lista di movimenti associati.
     *
     * @param model l'oggetto Budget da convertire
     * @return l'entità BudgetEntity corrispondente oppure null se il modello è null
     */
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