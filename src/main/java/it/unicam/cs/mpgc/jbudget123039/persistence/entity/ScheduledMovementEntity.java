package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovementOrigin;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Entity JPA che rappresenta un movimento schedulato (programmato) nel database.
 */
@Entity
@Table(name = "scheduled_movements")
public class ScheduledMovementEntity {

    /**
     * ID univoco del movimento schedulato.
     */
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Descrizione del movimento schedulato.
     */
    private String description;

    /**
     * Data programmata del movimento.
     */
    private LocalDate scheduledDate;

    /**
     * Importo del movimento.
     */
    private BigDecimal amount;

    /**
     * Indica se il movimento è un'entrata (true) o uscita (false).
     */
    private boolean income;

    /**
     * Lista dei tag associati al movimento schedulato.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "scheduled_movement_tags",
            joinColumns = @JoinColumn(name = "scheduled_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagEntity> tags;

    /**
     * Origine del movimento schedulato.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false)
    private ScheduledMovementOrigin origin = ScheduledMovementOrigin.MANUAL;

    /**
     * Costruttore di default.
     */
    public ScheduledMovementEntity() {
    }

    /**
     * Restituisce l'ID univoco del movimento schedulato.
     * @return UUID del movimento schedulato
     */
    public UUID getId() {
        return id;
    }

    /**
     * Imposta l'ID del movimento schedulato.
     * @param id UUID da impostare
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Restituisce la descrizione del movimento schedulato.
     * @return descrizione
     */
    public String getDescription() {
        return description;
    }

    /**
     * Imposta la descrizione del movimento schedulato.
     * @param description testo descrittivo da impostare
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Restituisce la data programmata del movimento.
     * @return data schedulata
     */
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Imposta la data programmata del movimento.
     * @param scheduledDate data da impostare
     */
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    /**
     * Restituisce l'importo del movimento schedulato.
     * @return importo come BigDecimal
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Imposta l'importo del movimento schedulato.
     * @param amount importo da impostare
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Indica se il movimento schedulato è un'entrata (true) o uscita (false).
     * @return true se entrata, false altrimenti
     */
    public boolean isIncome() {
        return income;
    }

    /**
     * Imposta se il movimento schedulato è un'entrata o un'uscita.
     * @param income true per entrata, false per uscita
     */
    public void setIncome(boolean income) {
        this.income = income;
    }

    /**
     * Restituisce la lista dei tag associati al movimento schedulato.
     * @return lista di TagEntity
     */
    public List<TagEntity> getTags() {
        return tags;
    }

    /**
     * Imposta la lista dei tag associati al movimento schedulato.
     * @param tags lista di TagEntity da impostare
     */
    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    /**
     * Restituisce l'origine del movimento schedulato.
     * @return origine (MANUAL, AMORTIZATION, ecc.)
     */
    public ScheduledMovementOrigin getOrigin() {
        return origin;
    }

    /**
     * Imposta l'origine del movimento schedulato.
     * @param origin origine da impostare
     */
    public void setOrigin(ScheduledMovementOrigin origin) {
        this.origin = origin;
    }
}