package com.cardclash;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Tabellone {

    private Integer codice;
    private final List<Giocatore> giocatori;
    private final Map<Integer, Partita> partite;

    public Tabellone(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
        this.partite = new HashMap<>();
        inizializzaPartite();
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    public void setCodice() {
        this.generaCodice();
    }

    public Integer getCodice() {
        return codice;
    }

    private void inizializzaPartite() {
        for (Iterator<Giocatore> iterator = giocatori.iterator(); iterator.hasNext();) {
            Giocatore g1 = iterator.next();
            Giocatore g2 = iterator.next();
            Partita p = new Partita(g1, g2);
            p.setCodice();
            partite.put(p.getCodice(), p);
        }
    }

    public void eliminaGiocatore(String email) {
        for (Iterator<Giocatore> iterator = giocatori.iterator(); iterator.hasNext();) {
            Giocatore g = iterator.next();
            if (g.getEmail().equals(email)) {
                iterator.remove();
            }
        }
    }

    public void aggiornaTabellone() {
        partite.clear();
        inizializzaPartite();
    }

    public void aggiornaPunteggi(Integer codTorneo, float punteggio) {
        for (Iterator<Giocatore> iterator = giocatori.iterator(); iterator.hasNext();) {
            Giocatore g = iterator.next();
            g.addPunteggio(codTorneo, punteggio);
        }
    }
}
