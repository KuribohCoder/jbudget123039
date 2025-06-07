package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.schedule.ScheduledMovement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleController {

    private final List<ScheduledMovement> scheduled = new ArrayList<>();

    public void scheduleMovement(ScheduledMovement movement) {
        scheduled.add(movement);
    }

    public List<ScheduledMovement> getUpcomingMovements(LocalDate untilDate) {
        return scheduled.stream()
                .filter(m -> !m.getScheduledDate().isAfter(untilDate))
                .toList();
    }
}