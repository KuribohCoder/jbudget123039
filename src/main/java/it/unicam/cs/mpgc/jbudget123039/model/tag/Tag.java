package it.unicam.cs.mpgc.jbudget123039.model.tag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tag {
    private UUID id;
    private String name;
    private Tag parent;
    private final List<Tag> children = new ArrayList<>();

    public Tag(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Tag getParent() { return parent; }

    public List<Tag> getChildren() { return children; }

    public void setParent(Tag parent) {
        if (this.parent == parent) return; // evita ricorsione infinita
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.addChild(this);
        }
    }

    public void addChild(Tag child) {
        if (children.contains(child)) return; // evita duplicazioni
        children.add(child);
        if (child.getParent() != this) {
            child.setParent(this);
        }
    }
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}