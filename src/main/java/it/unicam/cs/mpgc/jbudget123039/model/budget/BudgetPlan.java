package it.unicam.cs.mpgc.jbudget123039.model.budget;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class BudgetPlan {
    private final YearMonth period;
    private final Map<Tag, BigDecimal> expectedExpenses = new HashMap<>();

    public BudgetPlan(YearMonth period) {
        this.period = period;
    }

    public void setExpectedExpense(Tag tag, BigDecimal amount) {
        expectedExpenses.put(tag, amount);
    }

    public BigDecimal getExpectedExpense(Tag tag) {
        return expectedExpenses.getOrDefault(tag, BigDecimal.ZERO);
    }

    public YearMonth getPeriod() {
        return period;
    }
}