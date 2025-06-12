package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class BudgetRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public CompletionStage<Void> saveOrUpdateBudgetAsync(BudgetEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.merge(entity);  // merge funziona sia su persist che update
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<List<BudgetEntity>> loadAllBudgetsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT DISTINCT b FROM BudgetEntity b LEFT JOIN FETCH b.movements", BudgetEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<BudgetEntity> findByIdAsync(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT b FROM BudgetEntity b LEFT JOIN FETCH b.movements WHERE b.id = :id", BudgetEntity.class)
                        .setParameter("id", id)
                        .getSingleResult();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<Void> deleteBudgetAsync(UUID id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                BudgetEntity entity = em.find(BudgetEntity.class, id);
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

    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}