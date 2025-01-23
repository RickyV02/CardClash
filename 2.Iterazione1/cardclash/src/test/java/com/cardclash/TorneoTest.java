package com.cardclash;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class TorneoTest {

    private Torneo torneo;
    private Giocatore giocatore;
    private Mazzo mazzo;

    @Before
    public void setUp() {
        torneo = new Torneo("Torneo di Prova", LocalDate.of(2025, 5, 20), LocalTime.of(10, 0), "Roma");
        giocatore = new Giocatore("Marco Rossi", "marco@esempio.com", "password123", "marcor");
        mazzo = new Mazzo("Mazzo di prova");
        mazzo.setCodice();
    }

    @After
    public void clearTest() {
        torneo = null;
        giocatore = null;
        mazzo = null;
    }

    @Test
    public void testAggiungiGiocatore() {
        assertEquals(0, torneo.getGiocatori().size());
        torneo.aggiungiGiocatore(giocatore.getEmail(), giocatore);
        assertEquals(1, torneo.getGiocatori().size());
        assertEquals(giocatore, torneo.getGiocatore(giocatore.getEmail()));
        assertNotNull(torneo.getGiocatori());
    }

    @Test
    public void testAggiungiMazzo() {
        assertEquals(0, torneo.getMazzi().size());
        torneo.aggiungiMazzo(mazzo.getCodice(), mazzo);
        assertEquals(1, torneo.getMazzi().size());
        assertEquals(mazzo, torneo.getMazzo(mazzo.getCodice()));
        assertNotNull(torneo.getMazzi());
    }

    @Test
    public void testIsAperto() {
        // Torneo con data futura
        assertTrue(torneo.isAperto());
        // Torneo con data passata
        Torneo t = new Torneo("Torneo passato", LocalDate.of(2020, 5, 20), LocalTime.of(10, 0), "Roma");
        assertFalse(t.isAperto());
    }

}
