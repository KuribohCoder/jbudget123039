package it.unicam.cs.mpgc.jbudget123039.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicam.cs.mpgc.jbudget123039.model.movement.Movement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonPersistenceManager implements DataPersistenceManager {
    private final ObjectMapper mapper = new ObjectMapper();

    public void saveMovements(List<Movement> movements, File file) throws IOException {
        mapper.writeValue(file, movements);
    }

    public List<Movement> loadMovements(File file) throws IOException {
        return List.of();
        // Da implementare
    }
}