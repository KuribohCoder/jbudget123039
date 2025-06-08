package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tags")
public class TagEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<MovementEntity> movements;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TagEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TagEntity> children;

    public TagEntity() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public TagEntity(String name) {
        this();
        this.name = name;
    }

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

    public List<MovementEntity> getMovements() {
        return movements;
    }

    public void setMovements(List<MovementEntity> movements) {
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
        this.children = children;
    }

    @Override
    public String toString() {
        return name;
    }
}