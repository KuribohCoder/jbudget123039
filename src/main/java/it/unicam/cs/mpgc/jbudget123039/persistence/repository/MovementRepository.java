package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.MovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
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

    /**
     * Salva o aggiorna un MovementEntity, gestendo i tag associati tramite query diretta per evitare proxy lazy.
     * Ritorna l'entità salvata.
     */
    public CompletionStage<MovementEntity> saveOrUpdateAsync(MovementEntity entity) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                if (entity.getTags() != null && !entity.getTags().isEmpty()) {
                    List<TagEntity> managedTags = entity.getTags().stream()
                            .map(tag -> {
                                if (tag.getId() == null) {
                                    throw new IllegalStateException("Tag without ID found.");
                                }
                                System.out.println("[MovementRepository] Verifico tag con ID: " + tag.getId());
                                TagEntity managedTag = em.createQuery(
                                                "SELECT t FROM TagEntity t WHERE t.id = :id", TagEntity.class)
                                        .setParameter("id", tag.getId())
                                        .getResultStream()
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("Tag with id " + tag.getId() + " not found."));
                                System.out.println("[MovementRepository] Tag trovato: " + managedTag.getId());
                                return managedTag;
                            })
                            .toList();
                    entity.setTags(managedTags);
                }

                MovementEntity merged = em.merge(entity);
                em.getTransaction().commit();
                return merged;
            } catch (Exception e) {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Carica tutti i movimenti.
     */
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

    /**
     * Elimina un movimento dato il suo ID.
     */
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

    public CompletionStage<List<MovementEntity>> findMovementsByTagAndDateRangeAsync(UUID tagId, LocalDate start, LocalDate end) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                String jpql = (tagId != null)
                        ? "SELECT DISTINCT m FROM MovementEntity m JOIN m.tags t WHERE t.id = :tagId AND m.date BETWEEN :start AND :end"
                        : "SELECT m FROM MovementEntity m WHERE m.date BETWEEN :start AND :end";
                var query = em.createQuery(jpql, MovementEntity.class)
                        .setParameter("start", start)
                        .setParameter("end", end);
                if (tagId != null) {
                    query.setParameter("tagId", tagId);
                }
                return query.getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Chiude risorse.
     */
    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}