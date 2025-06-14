package it.unicam.cs.mpgc.jbudget123039.service;

import it.unicam.cs.mpgc.jbudget123039.mapper.MovementMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.MovementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Service per il calcolo e confronto delle statistiche fra due periodi.
 */
public class StatisticsService {

    private final MovementRepository movementRepository;

    public StatisticsService() {
        this.movementRepository = new MovementRepository();
    }

    /**
     * Confronta due periodi: per ogni categoria (tag), calcola la spesa/entrata totale in ciascuno.
     *
     * @param start1 inizio periodo 1
     * @param end1 fine periodo 1
     * @param start2 inizio periodo 2
     * @param end2 fine periodo 2
     * @return CompletionStage di map da tag-name a record contenente due BigDecimal
     */
    public CompletableFuture<Map<String, StatComparison>> comparePeriods(
            LocalDate start1, LocalDate end1,
            LocalDate start2, LocalDate end2) {

        CompletionStage<List<Movement>> p1 = movementRepository
                .findMovementsByTagAndDateRangeAsync(null, start1, end1)
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));

        CompletionStage<List<Movement>> p2 = movementRepository
                .findMovementsByTagAndDateRangeAsync(null, start2, end2)
                .thenApply(list -> list.stream()
                        .map(MovementMapper::toModel)
                        .collect(Collectors.toList()));

        return CompletableFuture.allOf(p1.toCompletableFuture(), p2.toCompletableFuture())
                .thenApply(v -> {
                    List<Movement> list1 = p1.toCompletableFuture().join();
                    List<Movement> list2 = p2.toCompletableFuture().join();

                    Map<String, BigDecimal> sum1 = sumByTag(list1);
                    Map<String, BigDecimal> sum2 = sumByTag(list2);

                    // Union di tutte le categorie
                    Set<String> allTags = new HashSet<>();
                    allTags.addAll(sum1.keySet());
                    allTags.addAll(sum2.keySet());

                    Map<String, StatComparison> result = new TreeMap<>();
                    for (String tag : allTags) {
                        result.put(tag,
                                new StatComparison(
                                        sum1.getOrDefault(tag, BigDecimal.ZERO),
                                        sum2.getOrDefault(tag, BigDecimal.ZERO)
                                ));
                    }

                    return result;
                }).toCompletableFuture();
    }

    private Map<String, BigDecimal> sumByTag(List<Movement> list) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Movement m : list) {
            List<Tag> tags = m.getTags();
            if (tags.isEmpty()) {
                map.merge("Senza categoria", m.getAmount(), BigDecimal::add);
            } else {
                for (Tag t : tags) {
                    map.merge(t.getName(), m.getAmount(), BigDecimal::add);
                }
            }
        }
        return map;
    }

    public record StatComparison(BigDecimal amount1, BigDecimal amount2) {}
}