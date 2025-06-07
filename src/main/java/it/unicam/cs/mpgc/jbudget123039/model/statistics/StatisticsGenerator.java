package it.unicam.cs.mpgc.jbudget123039.model.statistics;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class StatisticsGenerator {

    public Map<Tag, BigDecimal> calculateExpensesByTag(List<Movement> movements, YearMonth period) {
        Map<Tag, BigDecimal> result = new HashMap<>();

        for (Movement m : movements) {
            if (!m.isIncome() && YearMonth.from(m.getDate()).equals(period)) {
                for (Tag tag : m.getTags()) {
                    result.merge(tag, m.getAmount(), BigDecimal::add);
                }
            }
        }

        return result;
    }

    public BigDecimal calculateBalance(List<Movement> movements, YearMonth period) {
        return movements.stream()
                .filter(m -> YearMonth.from(m.getDate()).equals(period))
                .map(m -> m.isIncome() ? m.getAmount() : m.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}