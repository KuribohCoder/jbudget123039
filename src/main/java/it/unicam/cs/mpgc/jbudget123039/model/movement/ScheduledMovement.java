package it.unicam.cs.mpgc.jbudget123039.model.movement;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Rappresenta un movimento programmato (scheduled) che implementa l'interfaccia Movement.
 * Include una data programmata separata dalla data standard e un'origine per identificare
 * come è stato creato il movimento programmato.
 */
public class ScheduledMovement implements Movement {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private boolean income;
    private LocalDate scheduledDate;
    private List<Tag> tags;

    private ScheduledMovementOrigin origin = ScheduledMovementOrigin.MANUAL;

    /**
     * Costruttore vuoto per creare un ScheduledMovement.
     */
    public ScheduledMovement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     * Nota: restituisce la data programmata associata al movimento schedulato.
     */
    @Override
    public LocalDate getDate() {
        return scheduledDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Metodo vuoto: non utilizza la data standard, usa solo la data programmata.
     *
     * @param date ignorata
     */
    @Override
    public void setDate(LocalDate date) {
        // intenzionalmente vuoto
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIncome() {
        return income;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncome(boolean income) {
        this.income = income;
    }

    /**
     * Restituisce la data programmata del movimento.
     *
     * @return data programmata
     */
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Imposta la data programmata del movimento.
     *
     * @param scheduledDate nuova data programmata
     */
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Restituisce l'origine del movimento schedulato.
     *
     * @return origine del movimento schedulato
     */
    public ScheduledMovementOrigin getOrigin() {
        return origin;
    }

    /**
     * Imposta l'origine del movimento schedulato.
     *
     * @param origin nuova origine
     */
    public void setOrigin(ScheduledMovementOrigin origin) {
        this.origin = origin;
    }
}