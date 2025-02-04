package com.cardclash;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TorneoTest {

    private Torneo torneo;
    private Giocatore giocatore;
    private Mazzo mazzo;
    private List<Giocatore> giocatori;

    @Before
    public void setUp() {
        torneo = new Torneo("Torneo di Prova", LocalDate.of(2026, 5, 20), LocalTime.of(10, 0), "Roma");
        giocatore = new Giocatore("Lorenzo Bianchi", "lorenzo@mail.com", "password123", "lore25");
        mazzo = new Mazzo("Mazzo di prova");
        mazzo.setCodice();

        giocatori = new ArrayList<>();
        giocatori.add(new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123"));
        giocatori.add(new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456"));
        giocatori.add(new Giocatore("Giovanni Verdi", "giovanni@mail.com", "password789", "giovanni789"));
        giocatori.add(new Giocatore("Paolo Neri", "paolo@mail.com", "password000", "paolo000"));

        for (Giocatore g : giocatori) {
            torneo.aggiungiGiocatore(g.getEmail(), g);
        }
    }

    @After
    public void clearTest() {
        torneo = null;
        giocatore = null;
        mazzo = null;
    }

    @Test
    public void testAggiungiGiocatore() {
        assertEquals(4, torneo.getGiocatori().size());
        torneo.aggiungiGiocatore(giocatore.getEmail(), giocatore);
        assertEquals(5, torneo.getGiocatori().size());
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
    public void testCreaTabellone() throws GiocatoriNotPotenzaDiDueException {
        Tabellone tabellone = torneo.creaTabellone();
        assertNotNull(tabellone);
        assertEquals(2, tabellone.getPartite().size());
    }

    @Test
    public void testCreaTabelloneGiocatoriNonPotenzaDiDue() throws GiocatoriNotPotenzaDiDueException {
        torneo.aggiungiGiocatore("extra@mail.com", new Giocatore("Extra", "extra@mail.com", "pass", "extra"));
        assertNotNull(assertThrows(GiocatoriNotPotenzaDiDueException.class, () -> torneo.creaTabellone()));
    }

    @Test
    public void testConfermaTabellone() throws GiocatoriNotPotenzaDiDueException {
        torneo.creaTabellone();
        torneo.confermaTabellone();
        assertNotNull(torneo.getTabellone());
    }

}
