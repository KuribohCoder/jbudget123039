package it.unicam.cs.mpgc.jbudget123039.persistence.repository;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
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
 * Repository per la gestione asincrona dei {@link ScheduledMovementEntity} tramite JPA.
 * Include operazioni CRUD e query personalizzate, eseguite su un thread pool dedicato.
 */
public class ScheduledMovementRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jbudgetPU");
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * Salva o aggiorna un {@link ScheduledMovementEntity} nel database.
     * I tag associati vengono convertiti in entità gestite per evitare errori di stato.
     *
     * @param entity l'entità da salvare o aggiornare
     * @return un {@link CompletionStage} che completa l'operazione
     */
    public CompletionStage<Void> saveOrUpdateScheduledMovementAsync(ScheduledMovementEntity entity) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                if (entity.getTags() != null && !entity.getTags().isEmpty()) {
                    List<TagEntity> managedTags = entity.getTags().stream()
                            .map(tag -> {
                                if (tag.getId() == null) {
                                    throw new IllegalStateException("Tag without id found.");
                                }
                                return em.getReference(TagEntity.class, tag.getId());
                            })
                            .toList();
                    entity.setTags(managedTags);
                }

                ScheduledMovementEntity existing = entity.getId() != null
                        ? em.find(ScheduledMovementEntity.class, entity.getId())
                        : null;

                if (existing == null) {
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

    /**
     * Carica tutti i movimenti schedulati dal database.
     *
     * @return {@link CompletionStage} contenente una lista di {@link ScheduledMovementEntity}
     */
    public CompletionStage<List<ScheduledMovementEntity>> loadAllScheduledMovementsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT s FROM ScheduledMovementEntity s", ScheduledMovementEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Elimina un movimento schedulato dato il suo ID.
     *
     * @param id UUID del movimento da eliminare
     * @return {@link CompletionStage} che completa l'operazione
     */
    public CompletionStage<Void> deleteScheduledMovementAsync(UUID id) {
        return CompletableFuture.runAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                ScheduledMovementEntity entity = em.find(ScheduledMovementEntity.class, id);
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
     * Carica tutti i movimenti schedulati che risultano scaduti alla data odierna o precedente.
     *
     * @return {@link CompletionStage} con i movimenti scaduti
     */
    public CompletionStage<List<ScheduledMovementEntity>> loadDueScheduledMovementsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                                "SELECT s FROM ScheduledMovementEntity s WHERE s.scheduledDate <= :today",
                                ScheduledMovementEntity.class)
                        .setParameter("today", LocalDate.now())
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Carica tutti i movimenti schedulati di origine manuale.
     *
     * @return {@link CompletionStage} con i movimenti manuali
     */
    public CompletionStage<List<ScheduledMovementEntity>> loadAllManualScheduledMovementsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                                "SELECT s FROM ScheduledMovementEntity s WHERE s.origin = it.unicam.cs.mpgc.jbudget123039.model.movement.ScheduledMovementOrigin.MANUAL",
                                ScheduledMovementEntity.class)
                        .getResultList();
            } finally {
                em.close();
            }
        }, executor);
    }

    /**
     * Chiude il {@link EntityManagerFactory} e il thread pool associato.
     * Va invocato in fase di chiusura dell'applicazione.
     */
    public void shutdown() {
        executor.shutdown();
        emf.close();
    }
}