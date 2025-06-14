package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.service.StatisticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController() {
        this.statisticsService = new StatisticsService();
    }

    public CompletableFuture<Map<String, StatComparison>> comparePeriods(LocalDate start1, LocalDate end1,
                                                                         LocalDate start2, LocalDate end2) {
        return statisticsService.comparePeriods(start1, end1, start2, end2)
                .thenApply(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new StatComparison(e.getValue().amount1(), e.getValue().amount2())
                        )));
    }

    public record StatComparison(BigDecimal amount1, BigDecimal amount2) {}
}