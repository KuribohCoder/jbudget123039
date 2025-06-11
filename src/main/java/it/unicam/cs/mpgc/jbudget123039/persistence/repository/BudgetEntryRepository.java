package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.BudgetEntryEntity;
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

public class BudgetEntryRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public CompletionStage<Void> saveOrUpdateBudgetEntryAsync(BudgetEntryEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                if (entity.getTag() != null && entity.getTag().getId() != null) {
                    TagEntity managedTag = em.getReference(TagEntity.class, entity.getTag().getId());
                    entity.setTag(managedTag);
                }

                if (entity.getId() == null || em.find(BudgetEntryEntity.class, entity.getId()) == null) {
                    if (entity.getId() == null) {
                        entity.setId(UUID.randomUUID());
                    }
                    em.persist(entity);
                } else {
                    em.merge(entity);
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

    public CompletionStage<List<BudgetEntryEntity>> loadAllBudgetEntriesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT be FROM BudgetEntryEntity be", BudgetEntryEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<BudgetEntryEntity> findByIdAsync(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.find(BudgetEntryEntity.class, id);
            } finally {
                em.close();
            }
        }, executor);
    }

    public CompletionStage<Void> deleteBudgetEntryAsync(UUID id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                BudgetEntryEntity entity = em.find(BudgetEntryEntity.class, id);
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