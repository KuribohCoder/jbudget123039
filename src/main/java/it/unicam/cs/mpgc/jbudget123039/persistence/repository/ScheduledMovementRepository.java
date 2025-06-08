package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;

public class ScheduledMovementRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public CompletionStage<Void> saveScheduledMovementAsync(ScheduledMovementEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(entity);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<List<ScheduledMovementEntity>> loadDueScheduledMovements() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT s FROM ScheduledMovementEntity s WHERE s.scheduledDate <= :today", ScheduledMovementEntity.class)
                        .setParameter("today", LocalDate.now())
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<Void> deleteScheduledMovement(Long id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                ScheduledMovementEntity entity = em.find(ScheduledMovementEntity.class, id);
                if (entity != null) em.remove(entity);
                em.getTransaction().commit();
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