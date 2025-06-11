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

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean isIncome() {
        return income;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public void setIncome(boolean income) {
        this.income = income;
    }

    @Override
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}