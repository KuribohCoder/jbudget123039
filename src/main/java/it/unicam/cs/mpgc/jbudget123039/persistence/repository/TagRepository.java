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

public class TagRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public CompletionStage<Void> saveOrUpdateTagAsync(TagEntity tag) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                if (tag.getId() == null) {
                    em.persist(tag);
                } else {
                    em.merge(tag);
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

    public CompletionStage<List<TagEntity>> loadAllTagsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT t FROM TagEntity t", TagEntity.class).getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<List<TagEntity>> loadAllTagsWithChildrenAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                        "SELECT DISTINCT t FROM TagEntity t LEFT JOIN FETCH t.children", TagEntity.class
                ).getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<Void> deleteTagAsync(UUID id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                TagEntity tag = em.find(TagEntity.class, id);
                if (tag != null) {
                    // Dissocia il tag da tutti i movimenti
                    tag.getMovements().forEach(movement -> movement.getTags().remove(tag));

                    // Optional: rimuovi anche eventuali figli se presenti
                    if (tag.getChildren() != null) {
                        tag.getChildren().forEach(child -> child.setParent(null));
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

    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}