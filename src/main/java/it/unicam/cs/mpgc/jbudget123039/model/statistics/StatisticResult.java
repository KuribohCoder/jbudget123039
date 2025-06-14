package it.unicam.cs.mpgc.jbudget123039.model.statistics;

import java.math.BigDecimal;
import java.util.Map;

public class StatisticResult {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private Map<String, BigDecimal> expensesByTag; // nome tag -> totale
}