package it.unicam.cs.mpgc.jbudget123039.model.movement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BasicMovement implements Movement {
    private final UUID id;
    private final String description;
    private final LocalDate date;
    private final BigDecimal amount;
    private final boolean income;
    private final List<Tag> tags;

    public BasicMovement(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.income = income;
        this.tags = tags;
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public BigDecimal getAmount() { return amount; }
    public boolean isIncome() { return income; }
    public List<Tag> getTags() { return tags; }
}