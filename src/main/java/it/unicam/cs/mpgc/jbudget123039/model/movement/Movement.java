package it.unicam.cs.mpgc.jbudget123039.model.movement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface Movement {
    UUID getId();
    String getDescription();
    LocalDate getDate();
    BigDecimal getAmount();
    boolean isIncome();
    List<Tag> getTags();
}