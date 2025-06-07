package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.budget.BudgetPlan;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Tag;

import java.math.BigDecimal;
import java.time.YearMonth;

public class BudgetController {
    private BudgetPlan currentPlan;

    public void createPlan(YearMonth period) {
        currentPlan = new BudgetPlan(period);
    }

    public void setExpectedExpense(Tag tag, BigDecimal amount) {
        currentPlan.setExpectedExpense(tag, amount);
    }

    public BudgetPlan getCurrentPlan() {
        return currentPlan;
    }
}