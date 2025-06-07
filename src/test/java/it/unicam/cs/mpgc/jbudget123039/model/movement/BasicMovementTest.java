package it.unicam.cs.mpgc.jbudget123039.model.movement;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasicMovementTest {
    @Test
    public void testMovementCreation() {
        Movement m = new BasicMovement("Test", LocalDate.now(), new BigDecimal("10.00"), false, List.of(new Tag("Test")));
        assertEquals("Test", m.getDescription());
    }
}