package it.unicam.cs.mpgc.jbudget123039.model.movement;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementazione base dell'interfaccia Movement, rappresenta un movimento finanziario.
 */
public class BasicMovement implements Movement {
    private UUID id;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private boolean income;
    private List<Tag> tags;

    /**
     * Costruttore che crea un movimento con i dati forniti e un id generato automaticamente.
     *
     * @param description descrizione del movimento
     * @param date data del movimento
     * @param amount importo del movimento
     * @param income true se il movimento è un'entrata, false se un'uscita
     * @param tags lista di tag associati al movimento
     */
    public BasicMovement(String description, LocalDate date, BigDecimal amount, boolean income, List<Tag> tags) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.income = income;
        this.tags = tags;
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
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getDate() {
        return date;
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
    public boolean isIncome() {
        return income;
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
    public void setId(UUID id) {
        this.id = id;
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
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncome(boolean income) {
        this.income = income;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}