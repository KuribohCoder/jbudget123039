package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Entity JPA che rappresenta un Budget nel database.
 */
@Entity
@Table(name = "budgets")
public class BudgetEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "budget_movements",
            joinColumns = @JoinColumn(name = "budget_id"),
            inverseJoinColumns = @JoinColumn(name = "movement_id"))
    private List<MovementEntity> movements;

    /**
     * Costruttore di default che genera un UUID casuale per l'id.
     */
    public BudgetEntity() {
        this.id = UUID.randomUUID();
    }

    /**
     * Restituisce l'ID univoco del budget.
     * @return UUID del budget
     */
    public UUID getId() {
        return id;
    }

    /**
     * Imposta l'ID del budget.
     * @param id UUID da impostare
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Restituisce il nome del budget.
     * @return nome del budget
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il nome del budget.
     * @param name nome da impostare
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce la data di inizio del budget.
     * @return data di inizio
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Imposta la data di inizio del budget.
     * @param startDate data di inizio da impostare
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Restituisce la data di fine del budget.
     * @return data di fine
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Imposta la data di fine del budget.
     * @param endDate data di fine da impostare
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Restituisce la lista dei movimenti associati al budget.
     * @return lista dei movimenti
     */
    public List<MovementEntity> getMovements() {
        return movements;
    }

    /**
     * Imposta la lista dei movimenti associati al budget.
     * @param movements lista di movimenti da impostare
     */
    public void setMovements(List<MovementEntity> movements) {
        this.movements = movements;
    }
}