package it.unicam.cs.mpgc.jbudget123039.util;

import java.time.LocalDate;

/**
 * Classe di utilità per operazioni con {@link LocalDate}.
 * Contiene metodi statici per gestire date in modo sicuro ed efficiente.
 * Questa classe non è istanziabile.
 */
public final class DateUtil {

    private DateUtil() {
        // Classe di utilità statica, non istanziabile
    }

    /**
     * Restituisce la data passata come parametro, oppure la data odierna
     * nel caso in cui la data passata sia {@code null}.
     *
     * @param date la data da verificare, può essere null
     * @return la data passata o {@link LocalDate#now()} se la data è null
     */
    public static LocalDate getOrDefault(LocalDate date) {
        return (date == null) ? LocalDate.now() : date;
    }
}