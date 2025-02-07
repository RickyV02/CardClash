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
        FormatoTorneo formatoPauper = new FormatoTorneoPauper(1, "Pauper", Gioco.MAGIC, 16, 2.0f, 3.0f);
        torneo.setFormato(formatoPauper);
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
        int iniziale = torneo.getGiocatori().size();
        torneo.aggiungiGiocatore(giocatore.getEmail(), giocatore);
        assertEquals(iniziale + 1, torneo.getGiocatori().size());
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

    @Test
    public void testIsAperto() {
        assertTrue(torneo.isAperto());
        Torneo t2 = new Torneo("Torneo Aperto", LocalDate.of(2026, 5, 21), LocalTime.of(11, 0), "Milano");
        t2.setFormato(torneo.getFormato());  // stessa configurazione, numMaxGiocatori=16
        for (int i = 0; i < 16; i++) {
            t2.aggiungiGiocatore("g" + i + "@mail.com", new Giocatore("Giocatore" + i, "g" + i + "@mail.com", "pass", "nick" + i));
        }
        assertFalse(t2.isAperto());
    }

    @Test
    public void testIsPotenzaDiDue() {
        // Testa alcuni valori
        assertTrue(torneo.isPotenzaDiDue(1));
        assertTrue(torneo.isPotenzaDiDue(2));
        assertTrue(torneo.isPotenzaDiDue(4));
        assertFalse(torneo.isPotenzaDiDue(3));
        assertFalse(torneo.isPotenzaDiDue(0));
    }

    @Test
    public void testEliminaGiocatore() throws GiocatoriNotPotenzaDiDueException {
        Tabellone tabellone = torneo.creaTabellone();
        assertNotNull(tabellone);
        int iniziale = tabellone.getGiocatori().size();
        torneo.eliminaGiocatore("paolo@mail.com");
        assertEquals(iniziale - 1, tabellone.getGiocatori().size());
        boolean presente = tabellone.getGiocatori().stream()
                .anyMatch(g -> g.getEmail().equals("paolo@mail.com"));
        assertFalse(presente);
    }

    @Test
    public void testGetClassifica() {
        Integer codTorneo = torneo.getCodice();
        int i = 0;
        for (Giocatore g : torneo.getGiocatori().values()) {
            g.setPunteggio(codTorneo);
            switch (i) {
                case 0 ->
                    g.addPunteggio(codTorneo, 10.0f);
                case 1 ->
                    g.addPunteggio(codTorneo, 20.0f);
                case 2 ->
                    g.addPunteggio(codTorneo, 15.0f);
                default ->
                    g.addPunteggio(codTorneo, 5.0f);
            }
            i++;
        }
        List<Giocatore> classifica = torneo.getClassifica();
        assertNotNull(classifica);
        assertEquals(20.0f, classifica.get(0).getPunteggio(codTorneo), 0.01);
    }

    @Test
    public void testAggiornaELO() {
        Integer codTorneo = torneo.getCodice();
        for (Giocatore g : torneo.getGiocatori().values()) {
            g.setPunteggio(codTorneo);
        }
        torneo.getGiocatore("mario@mail.com").addPunteggio(codTorneo, 12.0f);
        torneo.getGiocatore("luigi@mail.com").addPunteggio(codTorneo, 8.0f);
        torneo.getGiocatore("giovanni@mail.com").addPunteggio(codTorneo, 5.0f);
        torneo.getGiocatore("paolo@mail.com").addPunteggio(codTorneo, 20.0f);
        torneo.aggiornaELO();

        assertEquals(12.0f, torneo.getGiocatore("mario@mail.com").getELO(), 0.01);
        assertEquals(8.0f, torneo.getGiocatore("luigi@mail.com").getELO(), 0.01);
        assertEquals(5.0f, torneo.getGiocatore("giovanni@mail.com").getELO(), 0.01);
        assertEquals(20.0f, torneo.getGiocatore("paolo@mail.com").getELO(), 0.01);
    }

    @Test
    public void testConcludiTorneo() {
        Integer codTorneo = torneo.getCodice();
        for (Giocatore g : torneo.getGiocatori().values()) {
            g.setPunteggio(codTorneo);
        }
        torneo.getGiocatore("mario@mail.com").addPunteggio(codTorneo, 10.0f);
        torneo.getGiocatore("luigi@mail.com").addPunteggio(codTorneo, 20.0f);
        torneo.getGiocatore("giovanni@mail.com").addPunteggio(codTorneo, 30.0f);
        torneo.getGiocatore("paolo@mail.com").addPunteggio(codTorneo, 5.0f);
        assertFalse(torneo.isTerminato());

        torneo.concludiTorneo();
        assertTrue(torneo.isTerminato());
        assertNotNull(torneo.getVincitore());
        assertEquals("giovanni@mail.com", torneo.getVincitore().getEmail());
    }
}
