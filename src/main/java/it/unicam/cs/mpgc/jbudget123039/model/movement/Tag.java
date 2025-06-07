package it.unicam.cs.mpgc.jbudget123039.model.movement;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private final String name;
    private Tag parent;
    private final List<Tag> children = new ArrayList<>();

    public Tag(String name) {
        this.name = name;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
        parent.addChild(this);
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