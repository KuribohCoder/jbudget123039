package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "scheduled_movements")
public class ScheduledMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;
    private BigDecimal amount;
    private boolean income;
    private LocalDate scheduledDate;
    private int recurrenceDays; // 0 = no recurrence

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TagEntity> tags;

    public ScheduledMovementEntity() {
    }
    public ScheduledMovementEntity(String description, BigDecimal amount, boolean income, LocalDate scheduledDate, int recurrenceDays, List<TagEntity> tags) {
        this.description = description;
        this.amount = amount;
        this.income = income;
        this.scheduledDate = scheduledDate;
        this.recurrenceDays = recurrenceDays;
        this.tags = tags;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isIncome() { return income; }
    public void setIncome(boolean income) { this.income = income; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public int getRecurrenceDays() { return recurrenceDays; }
    public void setRecurrenceDays(int recurrenceDays) { this.recurrenceDays = recurrenceDays; }

    public List<TagEntity> getTags() { return tags; }
    public void setTags(List<TagEntity> tags) { this.tags = tags; }
}