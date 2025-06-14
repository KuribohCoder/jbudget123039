package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.service.StatisticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Controller per la gestione delle statistiche relative ai movimenti.
 * Fa da intermediario tra la vista e il service dedicato.
 */
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Costruttore che inizializza il service per le statistiche.
     */
    public StatisticsController() {
        this.statisticsService = new StatisticsService();
    }

    /**
     * Confronta due periodi di tempo calcolando gli importi per categoria.
     *
     * @param start1 data di inizio del primo periodo
     * @param end1 data di fine del primo periodo
     * @param start2 data di inizio del secondo periodo
     * @param end2 data di fine del secondo periodo
     * @return una CompletableFuture con la mappa categoria -> confronto importi (periodo1, periodo2)
     */
    public CompletableFuture<Map<String, StatComparison>> comparePeriods(LocalDate start1, LocalDate end1,
                                                                         LocalDate start2, LocalDate end2) {
        return statisticsService.comparePeriods(start1, end1, start2, end2)
                .thenApply(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new StatComparison(e.getValue().amount1(), e.getValue().amount2())
                        )));
    }

    /**
     * Record che rappresenta il confronto degli importi per una categoria tra due periodi.
     */
    public record StatComparison(BigDecimal amount1, BigDecimal amount2) {}
}