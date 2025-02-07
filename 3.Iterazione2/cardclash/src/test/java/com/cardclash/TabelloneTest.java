package com.cardclash;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class TabelloneTest {

    private Tabellone tabellone;
    private List<Giocatore> giocatori;

    private Torneo torneo;

    @Before
    public void setUp() {
        giocatori = new ArrayList<>();
        giocatori.add(new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123"));
        giocatori.add(new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456"));
        giocatori.add(new Giocatore("Giovanni Verdi", "giovanni@mail.com", "password789", "giovanni789"));
        giocatori.add(new Giocatore("Paolo Neri", "paolo@mail.com", "password000", "paolo000"));

        torneo = new Torneo("Torneo di Test", LocalDate.now(), LocalTime.now(), "Online");
        torneo.setCodice();
        tabellone = new Tabellone(giocatori);
        torneo.setTabelloneCorrente(tabellone);
        torneo.inizializzaPunteggi(giocatori);
        torneo.confermaTabellone();

    }

    @Test
    public void testCreazioneTabellone() {
        assertNotNull(tabellone);
        assertEquals(4, giocatori.size());
    }

    @Test
    public void testInizializzaPartite() {
        Map<Integer, Partita> partite = tabellone.getPartite();
        assertNotNull(partite);
        assertEquals(2, partite.size());
        for (Partita p : partite.values()) {
            assertNotNull(p.getGiocatore1());
            assertNotNull(p.getGiocatore2());
        }
    }

    @Test
    public void testEliminaGiocatore() {
        tabellone.eliminaGiocatore("mario@mail.com");
        tabellone.eliminaGiocatore("paolo@mail.com");
        List<Giocatore> giocatoriRimasti = tabellone.getGiocatori();
        assertEquals(2, giocatoriRimasti.size());
    }

    @Test
    public void testAggiornaTabellone() {
        tabellone.eliminaGiocatore("mario@mail.com");
        tabellone.eliminaGiocatore("paolo@mail.com");
        List<Giocatore> giocatoriRimasti = tabellone.getGiocatori();
        assertEquals(2, giocatoriRimasti.size());
        tabellone.aggiornaTabellone();

        Map<Integer, Partita> partiteAggiornate = tabellone.getPartite();
        assertEquals(1, partiteAggiornate.size());
    }

    @Test
    public void testAggiornaPunteggi() {
        tabellone.aggiornaPunteggi(torneo.getCodice(), 10.0f);

        for (Giocatore g : giocatori) {
            assertEquals(10.0f, g.getPunteggio(torneo.getCodice()), 1);
        }
    }
}
