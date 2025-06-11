package it.unicam.cs.mpgc.jbudget123039.model.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tag {
    private UUID id;
    private final String name;
    private Tag parent;
    private final List<Tag> children = new ArrayList<>();

    public Tag(String name) {
        this.name = name;
    }

    public Tag(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void addChild(Tag child) {
        this.children.add(child);
    }

    public String getName() {
        return name;
    }

    public List<Tag> getChildren() {
        return children;
    }

    public Tag getParent() {
        return parent;
    }
}