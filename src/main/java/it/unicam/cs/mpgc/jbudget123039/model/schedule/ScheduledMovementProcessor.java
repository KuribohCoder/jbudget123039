package it.unicam.cs.mpgc.jbudget123039.model.schedule;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;

public class ScheduledMovementProcessor {
    private final ScheduledMovementRepository scheduledDAO = new ScheduledMovementRepository();
    private final MovementRepository movementRepository = new MovementRepository();

    public void executeDueMovements() {
        scheduledDAO.loadDueScheduledMovements()
                .thenAccept(dueMovements -> {
                    for (ScheduledMovementEntity scheduled : dueMovements) {
                        MovementEntity movement = MovementMapper.scheduledToMovement(scheduled);
                        movementRepository.saveMovementAsync(movement);

                        if (scheduled.getRecurrenceDays() > 0) {
                            scheduled.setScheduledDate(scheduled.getScheduledDate().plusDays(scheduled.getRecurrenceDays()));
                            scheduledDAO.saveScheduledMovementAsync(scheduled);
                        } else {
                            scheduledDAO.deleteScheduledMovement(scheduled.getId());
                        }
                    }
                });
    }
}