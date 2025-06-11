package it.unicam.cs.mpgc.jbudget123039.util;

import java.time.LocalDate;

public final class DateUtil {

    private DateUtil() {
        // Classe di utilità statica, non istanziabile
    }

    /**
     * Restituisce la data passata oppure la data odierna se null.
     *
     * @param date la data da controllare
     * @return la data oppure LocalDate.now() se la data è null
     */
    public static LocalDate getOrDefault(LocalDate date) {
        return (date == null) ? LocalDate.now() : date;
    }
}