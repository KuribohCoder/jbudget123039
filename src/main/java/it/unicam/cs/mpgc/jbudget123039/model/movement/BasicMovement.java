package it.unicam.cs.mpgc.jbudget123039.model.movement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BasicMovement implements Movement {
    private UUID id;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private boolean income;
    private List<Tag> tags;

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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}