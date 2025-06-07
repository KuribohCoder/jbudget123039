package it.unicam.cs.mpgc.jbudget123039.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String format(LocalDate date) {
        return formatter.format(date);
    }
}