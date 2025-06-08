package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "movements")
public class MovementEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private boolean income;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movement_tags",
            joinColumns = @JoinColumn(name = "movement_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags;

    public MovementEntity() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }
}