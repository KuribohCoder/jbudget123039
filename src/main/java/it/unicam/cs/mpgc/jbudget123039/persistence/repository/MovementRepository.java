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

                // Gestione dei tag per evitare errore "detached entity"
                if (entity.getTags() != null && !entity.getTags().isEmpty()) {
                    List<TagEntity> managedTags = entity.getTags().stream()
                            .map(tag -> em.getReference(TagEntity.class, tag.getId()))
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

                System.out.println("Cerco entità con ID: " + id);
                MovementEntity entity = em.find(MovementEntity.class, id);

                if (entity == null) {
                    System.out.println("⚠️ Movimento non trovato in DB!");
                } else {
                    em.remove(entity);
                    System.out.println("✅ Movimento rimosso");
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