package it.unicam.cs.mpgc.jbudget123039.model.schedule;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;

import java.time.LocalDate;

public class ScheduledMovement {
    private final Movement movement;
    private final LocalDate scheduledDate;

    public ScheduledMovement(Movement movement, LocalDate scheduledDate) {
        this.movement = movement;
        this.scheduledDate = scheduledDate;
    }

    public Movement getMovement() {
        return movement;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
}