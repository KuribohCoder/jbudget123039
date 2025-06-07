package it.unicam.cs.mpgc.jbudget123039.model.movement;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "tags")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    private TagEntity parent;

    @OneToMany(mappedBy = "parent")
    private List<TagEntity> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
