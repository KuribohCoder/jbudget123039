package it.unicam.cs.mpgc.jbudget123039.model.movement;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ScheduledMovement implements Movement {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private boolean income;
    private LocalDate scheduledDate;
    private List<Tag> tags;

    public ScheduledMovement() {

    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public LocalDate getDate() {
        return null;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isIncome() {
        return income;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public void setDate(LocalDate date) {

    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}