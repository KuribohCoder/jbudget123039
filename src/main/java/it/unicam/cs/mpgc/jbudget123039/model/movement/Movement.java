package it.unicam.cs.mpgc.jbudget123039.model.movement;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;

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

    void setDescription(String text);
    void setAmount(BigDecimal amount);
    void setDate(LocalDate date);
    void setIncome(boolean income);
    void setTags(List<Tag> tags);

    void setId(UUID id);
}