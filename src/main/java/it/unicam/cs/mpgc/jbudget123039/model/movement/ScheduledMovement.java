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

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public LocalDate getDate() {
        return scheduledDate;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public void setDate(LocalDate date) {

    }

    @Override
    public boolean isIncome() {
        return income;
    }

    @Override
    public void setIncome(boolean income) {
        this.income = income;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}