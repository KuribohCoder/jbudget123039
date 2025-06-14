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

/**
 * Repository per la gestione delle operazioni asincrone su {@link BudgetEntity}.
 * Utilizza {@link EntityManager} e JPA per operazioni CRUD su un database relazionale.
 */
public class BudgetRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * Salva o aggiorna un budget in modo asincrono.
     * Utilizza {@code merge}, che gestisce sia l'inserimento che l'aggiornamento.
     *
     * @param entity il budget da salvare o aggiornare
     * @return {@link CompletionStage} che completa l'operazione
     */
    public CompletionStage<Void> saveOrUpdateBudgetAsync(BudgetEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.merge(entity);
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
     * Carica tutti i budget presenti nel database con i relativi movimenti associati.
     *
     * @return {@link CompletionStage} con la lista dei {@link BudgetEntity}
     */
    public CompletionStage<List<BudgetEntity>> loadAllBudgetsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                        "SELECT DISTINCT b FROM BudgetEntity b LEFT JOIN FETCH b.movements", BudgetEntity.class
                ).getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Trova un budget per ID, inclusi i movimenti associati.
     *
     * @param id UUID del budget da cercare
     * @return {@link CompletionStage} con il {@link BudgetEntity} corrispondente
     */
    public CompletionStage<BudgetEntity> findByIdAsync(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                        "SELECT b FROM BudgetEntity b LEFT JOIN FETCH b.movements WHERE b.id = :id", BudgetEntity.class
                ).setParameter("id", id).getSingleResult();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Elimina un budget per ID, se esiste.
     *
     * @param id UUID del budget da eliminare
     * @return {@link CompletionStage} che completa l'operazione
     */
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

    /**
     * Arresta il pool di thread e chiude il {@link EntityManagerFactory}.
     * Va chiamato alla chiusura dell'applicazione.
     */
    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}