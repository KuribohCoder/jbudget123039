package it.unicam.cs.mpgc.jbudget123039.persistence;

import it.unicam.cs.mpgc.jbudget123039.model.movement.MovementEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovementDAO {

    private final EntityManagerFactory emf;
    private final ExecutorService executor;

    public MovementDAO() {
        this.emf = Persistence.createEntityManagerFactory("jbudgetPU");
        this.executor = Executors.newFixedThreadPool(4); // 4 thread per DB async
    }

    public CompletionStage<Void> saveMovementAsync(MovementEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
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
                throw e;
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