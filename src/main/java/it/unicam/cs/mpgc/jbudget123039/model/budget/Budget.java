package it.unicam.cs.mpgc.jbudget123039.model.budget;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Rappresenta un budget con un intervallo temporale e una lista di movimenti finanziari associati.
 */
public class Budget {

    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<Movement> movements = new ArrayList<>();

    /**
     * Restituisce l'identificativo univoco del budget.
     *
     * @return UUID del budget
     */
    public UUID getId() {
        return id;
    }

    /**
     * Imposta l'identificativo univoco del budget.
     *
     * @param id UUID da assegnare
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Restituisce il nome del budget.
     *
     * @return nome del budget
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il nome del budget.
     *
     * @param name nome da assegnare
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce la data di inizio del budget.
     *
     * @return data di inizio
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Imposta la data di inizio del budget.
     *
     * @param startDate data di inizio da assegnare
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Restituisce la data di fine del budget.
     *
     * @return data di fine
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Imposta la data di fine del budget.
     *
     * @param endDate data di fine da assegnare
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Restituisce la lista dei movimenti associati al budget.
     *
     * @return lista di movimenti
     */
    public List<Movement> getMovements() {
        return movements;
    }

    /**
     * Imposta la lista dei movimenti associati al budget.
     *
     * @param movements lista di movimenti da assegnare
     */
    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }
}