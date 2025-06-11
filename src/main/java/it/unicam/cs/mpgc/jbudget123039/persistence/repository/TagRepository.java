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

    public CompletionStage<TagEntity> saveOrUpdateTagAsync(TagEntity entity) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                TagEntity managed = em.merge(entity);
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

    public CompletionStage<TagEntity> findByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                List<TagEntity> results = em.createQuery("SELECT t FROM TagEntity t WHERE t.name = :name", TagEntity.class)
                        .setParameter("name", name)
                        .getResultList();
                return results.isEmpty() ? null : results.get(0);
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