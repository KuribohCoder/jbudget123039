package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.model.tag.Tag;
import it.unicam.cs.mpgc.jbudget123039.service.TagService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Controller per la gestione delle operazioni asincrone sui Tag.
 * Utilizza il TagService per eseguire operazioni di caricamento, salvataggio,
 * aggiornamento, cancellazione e creazione di tag.
 */
public class TagController {

    private final TagService tagService;

    /**
     * Costruttore che inietta il servizio TagService.
     * Questo favorisce la testabilità e modularità del codice.
     *
     * @param tagService il servizio per la gestione dei tag
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Carica tutti i tag con i loro eventuali figli in modo asincrono.
     *
     * @return un CompletionStage che restituisce la lista dei tag
     */
    public CompletionStage<List<Tag>> loadAllTagsWithChildrenAsync() {
        return tagService.loadAllTags();
    }

    /**
     * Salva o aggiorna un tag in modo asincrono.
     *
     * @param tag il tag da salvare o aggiornare
     * @return un CompletionStage che restituisce il tag salvato o aggiornato
     */
    public CompletionStage<Tag> saveOrUpdateTagAsync(Tag tag) {
        return tagService.saveOrUpdateTag(tag);
    }

    /**
     * Elimina un tag dato il suo identificatore in modo asincrono.
     *
     * @param id l'UUID del tag da eliminare
     * @return un CompletionStage che indica il completamento dell'operazione
     */
    public CompletionStage<Void> deleteTagAsync(UUID id) {
        return tagService.deleteTag(id);
    }

    /**
     * Crea un tag con un nome specificato se non esiste già, in modo asincrono.
     *
     * @param name il nome del tag da creare
     * @return un CompletionStage che restituisce il tag creato o esistente
     */
    public CompletionStage<Tag> createTagIfNotExistsByName(String name) {
        return tagService.createTagIfNotExistsByName(name);
    }
}