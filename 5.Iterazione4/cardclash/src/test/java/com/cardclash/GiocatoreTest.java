package com.cardclash;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

public class GiocatoreTest {

    private Giocatore giocatore;
    private Mazzo mazzo;
    private Torneo torneo;

    @Before
    public void setUp() {
        torneo = new Torneo("Torneo di Prova", LocalDate.of(2026, 5, 20), LocalTime.of(10, 0), "Roma");
        giocatore = new Giocatore("Marco Rossi", "marco.rossi@example.com", "password123", "marco_99");
        mazzo = new Mazzo("Mazzo1");
        mazzo.setCodice();

    }

    @After
    public void clearTest() {
        giocatore.setMazzoCorrente(null);
    }

    @Test
    public void testAggiungiMazzo() {
        giocatore.aggiungiMazzo(mazzo.getCodice(), mazzo);
        assertEquals(1, giocatore.getMazziGiocatore().size());
        assertTrue(giocatore.getMazziGiocatore().containsKey(mazzo.getCodice()));
    }

    @Test
    public void testSetMazzoCorrente() {
        giocatore.setMazzoCorrente(mazzo);
        assertEquals(mazzo, giocatore.getMazzoCorrente());
    }

    @Test
    public void testSetPunteggio() {
        giocatore.setPunteggio(torneo.getCodice());
        assertEquals(0.0f, giocatore.getPunteggio(torneo.getCodice()), 0.01);
    }

    @Test
    public void testAddPunteggio() {
        giocatore.setPunteggio(torneo.getCodice());
        giocatore.addPunteggio(torneo.getCodice(), 10.0f);
        assertEquals(10.0f, giocatore.getPunteggio(torneo.getCodice()), 0.01);
    }

    @Test
    public void testAggiornaELO() {
        Integer codTorneo = torneo.getCodice();
        giocatore.setPunteggio(codTorneo);
        giocatore.addPunteggio(codTorneo, 12.0f);
        giocatore.aggiornaELO(codTorneo);
        assertEquals(12.0f, giocatore.getELO(), 0.01f);
    }

}
