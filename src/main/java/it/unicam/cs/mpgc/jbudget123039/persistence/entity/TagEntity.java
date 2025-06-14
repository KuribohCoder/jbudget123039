package it.unicam.cs.mpgc.jbudget123039.persistence.entity;

import jakarta.persistence.*;

import java.util.*;

/**
 * Entity JPA che rappresenta un Tag nel database.
 * Un Tag può essere associato a movimenti e movimenti schedulati.
 * Supporta gerarchie di tag tramite relazione padre-figlio.
 */
@Entity
@Table(name = "tags")
public class TagEntity {

    /**
     * ID univoco del tag.
     */
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Nome descrittivo del tag.
     */
    private String name;

    /**
     * Movimenti associati a questo tag.
     */
    @ManyToMany(mappedBy = "tags")
    private Set<MovementEntity> movements = new HashSet<>();

    /**
     * Movimenti schedulati associati a questo tag.
     */
    @ManyToMany(mappedBy = "tags")
    private Set<ScheduledMovementEntity> scheduledMovements = new HashSet<>();

    /**
     * Tag genitore in una gerarchia di tag (può essere null).
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TagEntity parent;

    /**
     * Lista di tag figli associati a questo tag.
     * La relazione è caricata eager e usa cascade e orphanRemoval.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TagEntity> children = new ArrayList<>();

    /**
     * Restituisce l'ID univoco del tag.
     * @return UUID del tag
     */
    public UUID getId() {
        return id;
    }

    /**
     * Imposta l'ID univoco del tag.
     * @param id UUID da impostare
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Restituisce il nome del tag.
     * @return nome del tag
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il nome del tag.
     * @param name nome da impostare
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce l'insieme dei movimenti associati a questo tag.
     * @return insieme di MovementEntity
     */
    public Set<MovementEntity> getMovements() {
        return movements;
    }

    /**
     * Imposta l'insieme dei movimenti associati a questo tag.
     * @param movements insieme di MovementEntity da associare
     */
    public void setMovements(Set<MovementEntity> movements) {
        this.movements = movements;
    }

    /**
     * Restituisce l'insieme dei movimenti schedulati associati a questo tag.
     * @return insieme di ScheduledMovementEntity
     */
    public Set<ScheduledMovementEntity> getScheduledMovements() {
        return scheduledMovements;
    }

    /**
     * Restituisce il tag genitore (può essere null).
     * @return tag genitore o null
     */
    public TagEntity getParent() {
        return parent;
    }

    /**
     * Imposta il tag genitore.
     * @param parent tag genitore da impostare
     */
    public void setParent(TagEntity parent) {
        this.parent = parent;
    }

    /**
     * Restituisce la lista di tag figli.
     * @return lista di tag figli
     */
    public List<TagEntity> getChildren() {
        return children;
    }

    /**
     * Imposta la lista di tag figli.
     * Aggiorna la relazione padre-figlio in modo coerente.
     * @param children lista di tag figli da impostare
     */
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