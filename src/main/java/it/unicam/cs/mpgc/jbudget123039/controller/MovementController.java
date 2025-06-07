package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.BasicMovement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;
import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.persistence.MovementDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class MovementController {

    private final List<Movement> movements = new ArrayList<>();
    MovementDAO dao = new MovementDAO();
    public void addMovement(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        Movement m = new BasicMovement(description, date, amount, income, tags);
        movements.add(m);

        MovementEntity movementEntity = MovementMapper.toEntity(m);

        dao.saveMovementAsync(movementEntity)
                .thenRun(() -> System.out.println("Salvataggio completato"))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    public List<Movement> getAllMovements() {
        return new ArrayList<>(movements);
    }

    public List<Movement> filterByTag(Tag tag) {
        return movements.stream()
                .filter(m -> m.getTags().contains(tag))
                .toList();
    }

    public BigDecimal calculateTotal(boolean incomeType) {
        return movements.stream()
                .filter(m -> m.isIncome() == incomeType)
                .map(Movement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CompletionStage<Void> addMovementAsync(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        Movement m = new BasicMovement(description, date, amount, income, tags);
        MovementEntity entity = MovementMapper.toEntity(m);
        return dao.saveMovementAsync(entity)
                .thenCompose(v -> loadMovementsAsync());
    }

    public CompletionStage<Void> loadMovementsAsync() {
        return dao.loadAllMovementsAsync()
                .thenAccept(entities -> {
                    synchronized (movements) {
                        movements.clear();
                        for (MovementEntity e : entities) {
                            movements.add(MovementMapper.toModel(e));
                        }
                    }
                });
    }

    public CompletionStage<Void> removeMovementAsync(UUID id) {
        return dao.deleteMovementAsync(id)
                .thenRun(() -> {
                    synchronized (movements) {
                        movements.removeIf(m -> m.getId().equals(id));
                    }
                });
    }

}