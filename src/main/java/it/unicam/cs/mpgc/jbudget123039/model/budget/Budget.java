package it.unicam.cs.mpgc.jbudget123039.model.budget;

import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Budget {

    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    // Lista delle voci di budget (entrate/uscite previste)
    private List<BudgetEntry> entries;

    public Budget() {
        this.entries = new ArrayList<>();
    }

    // Getters e setters

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

    public List<BudgetEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<BudgetEntry> entries) {
        this.entries = entries;
    }

    // Aggiunge una voce di budget alla lista, evitando duplicati
    public void addEntry(BudgetEntry entry) {
        if (!entries.contains(entry)) {
            entries.add(entry);
        }
    }

    // Rimuove una voce
    public void removeEntry(BudgetEntry entry) {
        entries.remove(entry);
    }
}