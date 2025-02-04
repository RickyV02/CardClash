package com.cardclash;

import java.time.LocalDate;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

public class CardClashTest {

    private static CardClash cardClash;

    @BeforeClass
    public static void initTest() {
        cardClash = CardClash.getInstance();
    }

    @After
    public void clearTest() {
        cardClash.setTorneoCorrente(null);
        cardClash.setGiocatoreCorrente(null);
        cardClash.getTornei().clear();
        cardClash.getGiocatori().clear();
    }

    @Test
    public void testSingletonInstance() {
        CardClash anotherInstance = CardClash.getInstance();
        assertSame(anotherInstance, cardClash);
    }

    @Test
    public void testLoadFormati() {
        Map<Integer, FormatoTorneo> formati = cardClash.getFormati();
        assertNotNull(formati);
        assertEquals(3, formati.size());
    }

    @Test
    public void testCreaTorneo() {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();
        assertNotNull(torneo);
    }

    @Test
    public void testSelezionaFormato() {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();

        cardClash.selezionaFormato(20); // formato inesistente
        assertNull(torneo.getFormato());

        cardClash.selezionaFormato(1); // Pauper
        assertNotNull(torneo.getFormato());
        assertEquals("Pauper", torneo.getFormato().getNome());
    }

    @Test
    public void testConfermaCreazione() {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        cardClash.confermaCreazione();
        Torneo torneo = cardClash.getTorneoCorrente();
        assertNotNull(torneo);
        assertTrue(cardClash.getTornei().contains(torneo));
    }

    @Test
    public void testRegistraGiocatore() throws GiocatoreGiaRegistratoException {
        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password456", "mario123");
        Giocatore g = cardClash.getGiocatoreCorrente();
        assertNotNull(g);
        cardClash.confermaRegistrazione();

        // Tenta di registrare lo stesso giocatore con la stessa email
        assertNotNull(assertThrows(GiocatoreGiaRegistratoException.class, () -> 
            cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password456", "mario123")
        ));
    }

    @Test
    public void testConfermaRegistrazione() throws GiocatoreGiaRegistratoException {
        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g = cardClash.getGiocatoreCorrente();
        assertNotNull(g);

        cardClash.confermaRegistrazione();

        Giocatore giocatoreRegistrato = cardClash.getGiocatori().get(g.getEmail());
        assertNotNull(giocatoreRegistrato);
        assertSame(g, giocatoreRegistrato);
    }

    @Test
    public void testMostraTorneiDisponibili() {
        // 1. Crea tornei con date passate
        cardClash.creaTorneo("Torneo 1", LocalDate.of(2025, 01, 12), "10:00", "Luogo 1");
        Torneo t1 = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();
        cardClash.creaTorneo("Torneo 2", LocalDate.of(2025, 01, 13), "14:00", "Luogo 2");
        Torneo t2 = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();

        assertFalse(cardClash.mostraTorneiDisponibili().contains(t1));
        assertFalse(cardClash.mostraTorneiDisponibili().contains(t2));

        // 2. Crea tornei con data futura
        cardClash.creaTorneo("Torneo 3", LocalDate.of(2026, 12, 12), "10:00", "Luogo 3");
        FormatoTorneo f = cardClash.getFormati().get(1);
        Torneo t3 = cardClash.getTorneoCorrente();
        t3.setFormato(f);
        cardClash.confermaCreazione();
        cardClash.creaTorneo("Torneo 4", LocalDate.of(2026, 12, 13), "14:00", "Luogo 4");
        Torneo t4 = cardClash.getTorneoCorrente();
        t4.setFormato(f);
        cardClash.confermaCreazione();

        assertTrue(cardClash.mostraTorneiDisponibili().contains(t3));
        assertTrue(cardClash.mostraTorneiDisponibili().contains(t4));

    }

    @Test
    public void testSelezionaTorneo() {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        Torneo t = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();
        cardClash.selezionaTorneo(t.getCodice());
        assertSame(t, cardClash.getTorneoCorrente());
    }

    @Test
    public void testInserimentoMazzo() throws GiocatoreGiaRegistratoException {
        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.confermaRegistrazione();
        cardClash.setGiocatoreCorrente(cardClash.getGiocatori().get("mario@mail.com"));

        cardClash.inserimentoMazzo("Mazzo Test");
        assertNotNull(cardClash.getGiocatori().get("mario@mail.com").getMazzoCorrente());
        Mazzo mazzo = cardClash.getGiocatori().get("mario@mail.com").getMazzoCorrente();
        mazzo.setCodice();
        assertNotNull(mazzo);
        assertNotNull(mazzo.getCodice());
    }

    @Test
    public void testSelezionaTipo() {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        cardClash.confermaCreazione();
        cardClash.selezionaFormato(1);
        Map<Integer, TipoMazzo> tipiMazziConsentiti = cardClash.getTorneoCorrente().getFormato().getTipiMazzo();

        Giocatore giocatore = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.setGiocatoreCorrente(giocatore);

        Mazzo mazzo = new Mazzo("Mazzo di Mario");
        giocatore.setMazzoCorrente(mazzo);

        //prendiamo il primo tipo di mazzo consentito, per semplicità
        Integer codiceTipo = tipiMazziConsentiti.keySet().stream().findFirst().get();
        cardClash.selezionaTipo(codiceTipo);

        assertNotNull(mazzo.getTipoMazzo());
        assertEquals(cardClash.getTorneoCorrente().getFormato().getTipiMazzo().get(codiceTipo), mazzo.getTipoMazzo());

    }

    @Test
    public void testConfermaIscrizione() throws GiocatoreGiaRegistratoException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();

        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.confermaRegistrazione();

        cardClash.inserimentoMazzo("Mazzo Test");
        cardClash.confermaIscrizione();
        Integer codiceMazzo = cardClash.getGiocatoreCorrente().getMazzoCorrente().getCodice();
        assertTrue(torneo.getGiocatori().containsKey("mario@mail.com"));
        assertTrue(torneo.getMazzi().containsKey(codiceMazzo));
        assertTrue(torneo.getGiocatore("mario@mail.com").getMazziGiocatore().containsKey(codiceMazzo));
    }

}
