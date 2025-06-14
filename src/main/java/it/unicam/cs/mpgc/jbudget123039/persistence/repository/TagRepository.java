package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository per la gestione asincrona delle operazioni sui {@link TagEntity} tramite JPA.
 * Include operazioni CRUD e query personalizzate con supporto alla gerarchia dei tag.
 */
public class TagRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * Salva o aggiorna un tag nel database.
     *
     * @param entity il {@link TagEntity} da salvare o aggiornare
     * @return {@link CompletionStage} che restituisce il tag gestito
     */
    public CompletionStage<TagEntity> saveOrUpdateTagAsync(TagEntity entity) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                TagEntity managed = em.merge(entity);
                em.flush();
                em.getTransaction().commit();
                return managed;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Carica tutti i tag presenti nel database.
     *
     * @return {@link CompletionStage} con lista di {@link TagEntity}
     */
    public CompletionStage<List<TagEntity>> loadAllTagsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT t FROM TagEntity t", TagEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Carica tutti i tag includendo i figli (children) con fetch eager.
     *
     * @return {@link CompletionStage} con lista di tag con figli pre-caricati
     */
    public CompletionStage<List<TagEntity>> loadAllTagsWithChildrenAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                                "SELECT DISTINCT t FROM TagEntity t LEFT JOIN FETCH t.children", TagEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Elimina un tag dal database gestendo anche la rimozione dai movimenti e dalla gerarchia.
     *
     * @param id UUID del tag da eliminare
     * @return {@link CompletionStage} che completa l'operazione
     */
    public CompletionStage<Void> deleteTagAsync(UUID id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                TagEntity tag = em.find(TagEntity.class, id);
                if (tag != null) {

                    if (tag.getMovements() != null) {
                        tag.getMovements().forEach(m -> m.getTags().remove(tag));
                    }

                    if (tag.getParent() != null) {
                        TagEntity parent = tag.getParent();
                        parent.getChildren().remove(tag);
                        tag.setParent(null);
                        em.merge(parent);
                    }

                    em.remove(tag);
                }

                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Trova un tag tramite il suo ID.
     * Include movimenti e movimenti schedulati nella query.
     *
     * @param id UUID del tag da cercare
     * @return {@link CompletionStage} con il tag trovato oppure null
     */
    public CompletionStage<TagEntity> findByIdAsync(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                List<TagEntity> results = em.createQuery(
                                "SELECT t FROM TagEntity t " +
                                        "LEFT JOIN FETCH t.movements " +
                                        "LEFT JOIN FETCH t.scheduledMovements " +
                                        "WHERE t.id = :id", TagEntity.class)
                        .setParameter("id", id)
                        .getResultList();
                return results.isEmpty() ? null : results.get(0);
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Trova un tag tramite il suo nome.
     * Include movimenti e movimenti schedulati nella query.
     *
     * @param name nome del tag da cercare
     * @return {@link CompletionStage} con il tag trovato oppure null
     */
    public CompletionStage<TagEntity> findByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                List<TagEntity> results = em.createQuery(
                                "SELECT t FROM TagEntity t " +
                                        "LEFT JOIN FETCH t.movements " +
                                        "LEFT JOIN FETCH t.scheduledMovements " +
                                        "WHERE t.name = :name", TagEntity.class)
                        .setParameter("name", name)
                        .getResultList();
                return results.isEmpty() ? null : results.get(0);
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Chiude il thread pool e l'EntityManagerFactory.
     * Deve essere invocato al termine dell'applicazione.
     */
    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}