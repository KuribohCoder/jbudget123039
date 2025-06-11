package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "tags")
public class TagEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<MovementEntity> movements = new HashSet<>();

    @ManyToMany(mappedBy = "tags")
    private Set<ScheduledMovementEntity> scheduledMovements = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TagEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TagEntity> children = new ArrayList<>();


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MovementEntity> getMovements() {
        return movements;
    }

    public Set<ScheduledMovementEntity> getScheduledMovements() {
        return scheduledMovements;
    }

    public void setMovements(Set<MovementEntity> movements) {
        this.movements = movements;
    }

    public TagEntity getParent() {
        return parent;
    }

    public void setParent(TagEntity parent) {
        this.parent = parent;
    }

    public List<TagEntity> getChildren() {
        return children;
    }

    public void setChildren(List<TagEntity> children) {
        this.children.clear();
        if (children != null) {
            for (TagEntity child : children) {
                child.setParent(this);
                this.children.add(child);
            }
        }
    }
}