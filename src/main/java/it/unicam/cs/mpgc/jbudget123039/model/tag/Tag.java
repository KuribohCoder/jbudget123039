package it.unicam.cs.mpgc.jbudget123039.model.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Rappresenta un'etichetta (tag) utilizzabile per categorizzare movimenti o altri elementi.
 * Può avere una struttura gerarchica con genitore e figli.
 */
public class Tag {

    private UUID id;
    private String name;
    private Tag parent;
    private final List<Tag> children = new ArrayList<>();

    /**
     * Costruisce un nuovo tag con il nome specificato e un ID generato casualmente.
     *
     * @param name nome del tag
     */
    public Tag(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    /**
     * Restituisce l'ID univoco del tag.
     *
     * @return UUID del tag
     */
    public UUID getId() {
        return id;
    }

    /**
     * Imposta l'ID del tag.
     *
     * @param id nuovo UUID del tag
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Restituisce il nome del tag.
     *
     * @return nome del tag
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il nome del tag.
     *
     * @param name nuovo nome del tag
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce il tag genitore, se presente.
     *
     * @return tag genitore oppure null se non presente
     */
    public Tag getParent() {
        return parent;
    }

    /**
     * Imposta il tag genitore.
     * Se il genitore non contiene già questo tag come figlio, lo aggiunge automaticamente.
     *
     * @param parent tag genitore
     */
    public void setParent(Tag parent) {
        if (this.parent == parent) return;
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.addChild(this);
        }
    }

    /**
     * Restituisce la lista dei tag figli.
     *
     * @return lista dei figli
     */
    public List<Tag> getChildren() {
        return children;
    }

    /**
     * Aggiunge un tag figlio.
     * Se il figlio non ha questo tag come genitore, lo imposta automaticamente.
     *
     * @param child tag figlio da aggiungere
     */
    public void addChild(Tag child) {
        if (children.contains(child)) return;
        children.add(child);
        if (child.getParent() != this) {
            child.setParent(this);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}