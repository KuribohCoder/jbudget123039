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
 * Service per il calcolo e confronto delle statistiche finanziarie
 * fra due periodi temporali distinti.
 * <p>
 * Permette di confrontare l'importo totale delle spese o entrate
 * categorizzate per tag in due intervalli di date.
 */
public class StatisticsService {

    private final MovementRepository movementRepository;

    /**
     * Costruttore che inizializza il repository dei movimenti.
     */
    public StatisticsService() {
        this.movementRepository = new MovementRepository();
    }

    /**
     * Confronta due periodi temporali calcolando la somma degli importi per ciascuna categoria (tag).
     * Se un movimento non ha tag, viene raggruppato sotto la categoria "Senza categoria".
     *
     * @param start1 inizio del primo periodo
     * @param end1 fine del primo periodo
     * @param start2 inizio del secondo periodo
     * @param end2 fine del secondo periodo
     * @return {@link CompletableFuture} che restituisce una mappa da nome del tag
     *         a {@link StatComparison} contenente le somme per ciascun periodo
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

    /**
     * Calcola la somma degli importi dei movimenti raggruppandoli per nome tag.
     * I movimenti senza tag sono raggruppati sotto "Senza categoria".
     *
     * @param list lista di movimenti da aggregare
     * @return mappa da nome tag a somma degli importi
     */
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

    /**
     * Record che rappresenta il confronto tra due valori statistici.
     * Contiene la somma degli importi per due periodi distinti.
     *
     * @param amount1 somma del primo periodo
     * @param amount2 somma del secondo periodo
     */
    public record StatComparison(BigDecimal amount1, BigDecimal amount2) {}
}