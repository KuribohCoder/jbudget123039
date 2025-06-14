package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Entity JPA che rappresenta un movimento (entrata/uscita) nel database.
 */
@Entity
@Table(name = "movements")
public class MovementEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String description;

    private LocalDate date;

    private BigDecimal amount;

    private boolean income;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "movement_tags",
            joinColumns = @JoinColumn(name = "movement_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagEntity> tags;

    /**
     * Costruttore di default che inizializza l'id con un UUID casuale se nullo.
     */
    public MovementEntity() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    /**
     * Restituisce l'ID univoco del movimento.
     * @return UUID del movimento
     */
    public UUID getId() { return id; }

    /**
     * Imposta l'ID del movimento.
     * @param id UUID da impostare
     */
    public void setId(UUID id) { this.id = id; }

    /**
     * Restituisce la descrizione del movimento.
     * @return descrizione
     */
    public String getDescription() { return description; }

    /**
     * Imposta la descrizione del movimento.
     * @param description testo descrittivo da impostare
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Restituisce la data del movimento.
     * @return data
     */
    public LocalDate getDate() { return date; }

    /**
     * Imposta la data del movimento.
     * @param date data da impostare
     */
    public void setDate(LocalDate date) { this.date = date; }

    /**
     * Restituisce l'importo del movimento.
     * @return importo come BigDecimal
     */
    public BigDecimal getAmount() { return amount; }

    /**
     * Imposta l'importo del movimento.
     * @param amount importo da impostare
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /**
     * Indica se il movimento è un'entrata (true) o un'uscita (false).
     * @return true se entrata, false altrimenti
     */
    public boolean isIncome() { return income; }

    /**
     * Imposta se il movimento è un'entrata o un'uscita.
     * @param income true per entrata, false per uscita
     */
    public void setIncome(boolean income) { this.income = income; }

    /**
     * Restituisce la lista dei tag associati al movimento.
     * @return lista di TagEntity
     */
    public List<TagEntity> getTags() { return tags; }

    /**
     * Imposta la lista dei tag associati al movimento.
     * @param tags lista di TagEntity da impostare
     */
    public void setTags(List<TagEntity> tags) { this.tags = tags; }
}