package com.cardclash;

import org.junit.*;
import java.util.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

class CardClashTest {

    private static CardClash cardClash;

    @Before
    public static void initTest() {
        cardClash = CardClash.getInstance();
    }

    @Test
    void testSingletonInstance() {
        CardClash anotherInstance = CardClash.getInstance();
        assertSame(anotherInstance, cardClash);
    }

    @Test
    void testLoadFormati() {
        Map<Integer, FormatoTorneo> formati = cardClash.getFormati();
        assertNotNull(formati);
        assertEquals(3, formati.size());
    }

    @Test
    void testCreaTorneo() {
        Date data = new Date();
        cardClash.creaTorneo("Torneo Test", data, "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();

        assertNotNull(torneo);
    }

    @Test
    void testSelezionaFormato() {
        cardClash.creaTorneo("Torneo Test", new Date(), "15:00", "Luogo Test");
        cardClash.selezionaFormato(1); // Commander

        Torneo torneo = cardClash.getTorneoCorrente();
        assertNotNull(torneo.getFormato());

        cardClash.selezionaFormato(20); // formato inesistente
        assertNull(torneo.getFormato());
    }

    @Test
    void testRegistraGiocatore() {
        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.confermaRegistrazione();

        Map<String, Giocatore> giocatori = cardClash.getGiocatori();
        assertTrue(giocatori.containsKey("mario@mail.com"), "Il giocatore non è stato registrato correttamente");
        Giocatore giocatore = giocatori.get("mario@mail.com");
        assertEquals("Mario Rossi", giocatore.getNome(), "Il nome del giocatore non corrisponde");
    }

    @Test
    void testInserimentoMazzo() {
        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.confermaRegistrazione();
        cardClash.setGiocatoreCorrente(cardClash.getGiocatori().get("mario@mail.com"));

        cardClash.inserimentoMazzo("Mazzo Test");
        Mazzo mazzo = cardClash.getGiocatori().get("mario@mail.com").getMazzoCorrente();

        assertNotNull(mazzo, "Il mazzo dovrebbe essere stato creato");
        assertEquals("Mazzo Test", mazzo.getNome(), "Il nome del mazzo non corrisponde");
    }

    @Test
    void testConfermaIscrizione() {
        cardClash.creaTorneo("Torneo Test", new Date(), "15:00", "Luogo Test");
        cardClash.selezionaFormato(1); // Commander

        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.confermaRegistrazione();
        cardClash.setGiocatoreCorrente(cardClash.getGiocatori().get("mario@mail.com"));

        cardClash.inserimentoMazzo("Mazzo Test");
        cardClash.confermaIscrizione();

        Torneo torneo = cardClash.getTornei().get(0);
        assertTrue(torneo.getGiocatori().containsKey("mario@mail.com"),
                "Il giocatore non è stato iscritto correttamente");
    }

    @Test
    void testMostraTorneiDisponibili() {
        cardClash.creaTorneo("Torneo Test 1", new Date(), "15:00", "Luogo Test");
        cardClash.creaTorneo("Torneo Test 2", new Date(), "16:00", "Altro Luogo");
        cardClash.getTornei().get(0).setAperto(true);

        List<Torneo> torneiDisponibili = cardClash.getTornei();
        assertTrue(torneiDisponibili.size() > 0, "Ci dovrebbero essere tornei disponibili");
    }
}
