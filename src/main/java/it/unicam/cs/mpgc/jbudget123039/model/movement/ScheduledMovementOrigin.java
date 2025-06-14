package it.unicam.cs.mpgc.jbudget123039.model.movement;

/**
 * Enumerazione che rappresenta l'origine di un movimento schedulato.
 */
public enum ScheduledMovementOrigin {

    /**
     * Movimento inserito manualmente tramite lo scadenziario.
     */
    MANUAL,

    /**
     * Movimento generato automaticamente da un piano di ammortamento.
     */
    AMORTIZATION
}