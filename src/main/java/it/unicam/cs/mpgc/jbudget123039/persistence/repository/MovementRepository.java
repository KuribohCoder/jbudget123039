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

/**
 * Repository per la gestione asincrona dei {@link MovementEntity} utilizzando JPA.
 * Fornisce operazioni CRUD e query personalizzate eseguite in modo asincrono tramite thread pool dedicato.
 */
public class MovementRepository {

    private final EntityManagerFactory emf;
    private final ExecutorService executor;

    /**
     * Costruttore che inizializza l'EntityManagerFactory e un thread pool per operazioni asincrone.
     */
    public MovementRepository() {
        this.emf = Persistence.createEntityManagerFactory("jbudgetPU");
        this.executor = Executors.newFixedThreadPool(4);
    }

    /**
     * Salva o aggiorna un movimento nel database.
     * I tag associati vengono gestiti esplicitamente per garantire che siano già presenti e gestiti da JPA.
     *
     * @param entity l'entità {@link MovementEntity} da salvare o aggiornare
     * @return {@link CompletionStage} che restituisce l'entità salvata
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
                                return em.createQuery(
                                                "SELECT t FROM TagEntity t WHERE t.id = :id", TagEntity.class)
                                        .setParameter("id", tag.getId())
                                        .getResultStream()
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("Tag with id " + tag.getId() + " not found."));
                            })
                            .toList();
                    entity.setTags(managedTags);
                }

                MovementEntity merged = em.merge(entity);
                em.getTransaction().commit();
                return merged;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Carica tutti i movimenti presenti nel database.
     *
     * @return {@link CompletionStage} contenente la lista dei {@link MovementEntity}
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
     * Elimina un movimento dal database dato il suo ID.
     *
     * @param id UUID del movimento da eliminare
     * @return {@link CompletionStage} che completa l'operazione
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

    /**
     * Trova i movimenti filtrati per tag (opzionale) e intervallo di date.
     *
     * @param tagId  ID del tag da filtrare (può essere null)
     * @param start  data di inizio inclusiva
     * @param end    data di fine inclusiva
     * @return {@link CompletionStage} contenente la lista dei movimenti filtrati
     */
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
     * Chiude l'EntityManagerFactory e il pool di thread.
     * Va chiamato in fase di shutdown dell'applicazione.
     */
    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}