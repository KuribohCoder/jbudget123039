package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;
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

public class MovementRepository {

    private final EntityManagerFactory emf;
    private final ExecutorService executor;

    public MovementRepository() {
        this.emf = Persistence.createEntityManagerFactory("jbudgetPU");
        this.executor = Executors.newFixedThreadPool(4); // 4 thread per DB async
    }

    public CompletionStage<Void> saveMovementAsync(MovementEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                if (entity.getTags() != null && !entity.getTags().isEmpty()) {
                    List<TagEntity> managedTags = entity.getTags().stream()
                            .map(tag -> {
                                if (tag.getId() == null) {
                                    throw new IllegalStateException("Tag without id found. Save tags before saving movement.");
                                }
                                return em.getReference(TagEntity.class, tag.getId());
                            })
                            .toList();
                    entity.setTags(managedTags);
                }

                em.persist(entity);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<List<MovementEntity>> loadAllMovementsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT m FROM MovementEntity m", MovementEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<Void> deleteMovementAsync(UUID id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                MovementEntity entity = em.find(MovementEntity.class, id);

                if (entity != null) {
                    em.remove(entity);
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

    public CompletionStage<Void> updateMovementAsync(MovementEntity movementEntity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                if (movementEntity.getTags() != null && !movementEntity.getTags().isEmpty()) {
                    List<TagEntity> managedTags = movementEntity.getTags().stream()
                            .map(tag -> {
                                if (tag.getId() == null) {
                                    throw new IllegalStateException("Tag without ID found.");
                                }
                                TagEntity managedTag = em.find(TagEntity.class, tag.getId());
                                if (managedTag == null) {
                                    em.persist(tag);
                                    managedTag = tag;
                                }
                                return managedTag;
                            })
                            .toList();
                    movementEntity.setTags(managedTags);
                }

                em.merge(movementEntity);
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