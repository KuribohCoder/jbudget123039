package it.unicam.cs.mpgc.jbudget123039.model.statistics;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Rappresenta il risultato di una statistica finanziaria con totali aggregati
 * per entrate, uscite e dettagliate per tag.
 */
public class StatisticResult {

    /**
     * Totale delle entrate calcolate.
     */
    private BigDecimal totalIncome;

    /**
     * Totale delle uscite calcolate.
     */
    private BigDecimal totalExpenses;

    /**
     * Mappa che associa il nome di un tag al totale delle spese corrispondenti.
     */
    private Map<String, BigDecimal> expensesByTag;

    /**
     * Restituisce il totale delle entrate.
     *
     * @return totale entrate
     */
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    /**
     * Imposta il totale delle entrate.
     *
     * @param totalIncome nuovo totale entrate
     */
    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    /**
     * Restituisce il totale delle uscite.
     *
     * @return totale uscite
     */
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    /**
     * Imposta il totale delle uscite.
     *
     * @param totalExpenses nuovo totale uscite
     */
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    /**
     * Restituisce la mappa delle spese aggregate per nome tag.
     *
     * @return mappa nome tag -> totale spese
     */
    public Map<String, BigDecimal> getExpensesByTag() {
        return expensesByTag;
    }

    /**
     * Imposta la mappa delle spese aggregate per nome tag.
     *
     * @param expensesByTag nuova mappa nome tag -> totale spese
     */
    public void setExpensesByTag(Map<String, BigDecimal> expensesByTag) {
        this.expensesByTag = expensesByTag;
    }
}