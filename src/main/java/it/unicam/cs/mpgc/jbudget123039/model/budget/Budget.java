package it.unicam.cs.mpgc.jbudget123039.model.budget;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Budget {

    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    // Lista di movimenti (entrate/uscite) associati al budget
    private List<Movement> movements = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }
}