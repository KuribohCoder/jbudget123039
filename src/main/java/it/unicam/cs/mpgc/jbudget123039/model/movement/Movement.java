package it.unicam.cs.mpgc.jbudget123039.model.movement;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interfaccia che definisce un movimento finanziario con metodi per accedere e modificare
 * le proprietà base di un movimento come descrizione, data, importo, tipo e tag associati.
 */
public interface Movement {

    /**
     * Restituisce l'identificativo univoco del movimento.
     *
     * @return UUID del movimento
     */
    UUID getId();

    /**
     * Restituisce la descrizione del movimento.
     *
     * @return descrizione testuale
     */
    String getDescription();

    /**
     * Restituisce la data del movimento.
     *
     * @return data del movimento
     */
    LocalDate getDate();

    /**
     * Restituisce l'importo del movimento.
     *
     * @return importo come BigDecimal
     */
    BigDecimal getAmount();

    /**
     * Indica se il movimento è un'entrata (true) o un'uscita (false).
     *
     * @return true se è un'entrata, false altrimenti
     */
    boolean isIncome();

    /**
     * Restituisce la lista dei tag associati al movimento.
     *
     * @return lista di tag
     */
    List<Tag> getTags();

    /**
     * Imposta la descrizione del movimento.
     *
     * @param text nuova descrizione
     */
    void setDescription(String text);

    /**
     * Imposta l'importo del movimento.
     *
     * @param amount nuovo importo
     */
    void setAmount(BigDecimal amount);

    /**
     * Imposta la data del movimento.
     *
     * @param date nuova data
     */
    void setDate(LocalDate date);

    /**
     * Imposta se il movimento è un'entrata o un'uscita.
     *
     * @param income true se è un'entrata, false altrimenti
     */
    void setIncome(boolean income);

    /**
     * Imposta la lista di tag associati al movimento.
     *
     * @param tags nuova lista di tag
     */
    void setTags(List<Tag> tags);

    /**
     * Imposta l'identificativo univoco del movimento.
     *
     * @param id UUID da assegnare
     */
    void setId(UUID id);
}