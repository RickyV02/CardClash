package com.cardclash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tabellone {

    private Integer codice;
    private final List<Giocatore> giocatori;
    private final Map<Integer, Partita> partite;

    public Tabellone() {
        this.giocatori = new ArrayList<>();
        this.partite = new HashMap<>();
    }

    public void creaPartita(Giocatore g1, Giocatore g2) {
        Partita partita = new Partita(g1, g2);
        partite.put(partita.getCodice(), partita);
    }

    public void eliminaPartita(Partita partita) {
        partite.remove(partita);
    }

    private void eliminaGiocatore(Giocatore giocatore) {
        giocatori.remove(giocatore);
    }

    public void aggiornaPunteggio(Giocatore giocatore, float punteggio) {
        // Logica per aggiornare il punteggio del giocatore
    }

    public void aggiornaTabellone() {
        // Logica per aggiornare il tabellone
    }
}
