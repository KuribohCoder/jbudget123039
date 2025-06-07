package it.unicam.cs.mpgc.jbudget123039.persistence;

import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DataPersistenceManager {
    void saveMovements(List<Movement> movements, File file) throws IOException;
    List<Movement> loadMovements(File file) throws IOException;
}