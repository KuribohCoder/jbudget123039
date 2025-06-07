package it.unicam.cs.mpgc.jbudget123039.persistence;

import it.unicam.cs.mpgc.jbudget123039.model.movement.MovementEntity;
import org.junit.jupiter.api.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MovementDAOTest {

    private EntityManagerFactory emf;
    private MovementDAO dao;

    @BeforeAll
    public void setup() {
        emf = Persistence.createEntityManagerFactory("jbudgetPU");
        dao = new MovementDAO();
    }

    @AfterAll
    public void cleanup() {
        dao.shutdown();
        emf.close();
    }

    @Test
    public void testSaveAndDeleteMovement() throws Exception {
        MovementEntity movement = new MovementEntity();
        movement.setDescription("Test movimento");
        movement.setDate(LocalDate.now());
        movement.setAmount(new BigDecimal("123.45"));
        movement.setIncome(false);

        // Assicura id unico
        movement.setId(UUID.randomUUID());

        // Salva
        dao.saveMovementAsync(movement).toCompletableFuture().get();

        // Verifica esistenza nel DB
        EntityManager em = emf.createEntityManager();
        MovementEntity loaded = em.find(MovementEntity.class, movement.getId());
        Assertions.assertNotNull(loaded, "Movimento salvato non trovato nel DB");
        em.close();

        // Cancella
        dao.deleteMovementAsync(movement.getId()).toCompletableFuture().get();

        // Verifica eliminazione
        em = emf.createEntityManager();
        MovementEntity deleted = em.find(MovementEntity.class, movement.getId());
        Assertions.assertNull(deleted, "Movimento non eliminato dal DB");
        em.close();
    }
}