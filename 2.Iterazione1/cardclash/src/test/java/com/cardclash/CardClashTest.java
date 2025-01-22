package com.cardclash;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.After;
import static org.junit.Assert.assertEquals;
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
        Date data = new Date();
        cardClash.creaTorneo("Torneo Test", data, "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();
        assertNotNull(torneo);
    }

    @Test
    public void testSelezionaFormato() {
        cardClash.creaTorneo("Torneo Test", new Date(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();

        cardClash.selezionaFormato(20); // formato inesistente
        assertNull(torneo.getFormato());

        cardClash.selezionaFormato(1); // Commander
        assertNotNull(torneo.getFormato());
        assertEquals("Commander", torneo.getFormato().getNome());
    }

    @Test
    public void testConfermaCreazione() {
        Date data = new Date();
        cardClash.creaTorneo("Torneo Test", data, "15:00", "Luogo Test");
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
        GiocatoreGiaRegistratoException exception = assertThrows(GiocatoreGiaRegistratoException.class, () -> {
            cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password456", "mario123");
        });
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
        cardClash.creaTorneo("Torneo 1", LocalDate.of(2023, 12, 12), "10:00", "Luogo 1");
        cardClash.confermaCreazione();
        cardClash.creaTorneo("Torneo 2", LocalDate.of(2023, 12, 13), "14:00", "Luogo 2");
        cardClash.confermaCreazione();

        // 2. Aggiungi un torneo con data futura (aperto)
        cardClash.creaTorneo("Torneo 3", LocalDate.now().plusDays(1), "10:00", "Luogo 3");

        // 3. Mostra i tornei disponibili (il metodo rimuove i tornei chiusi)
        cardClash.mostraTorneiDisponibili();

        // 4. Verifica che solo il Torneo 3 (con data futura) sia disponibile
        assertTrue(cardClash.getTorneiDisponibili().contains("Torneo 3"));
        assertFalse(cardClash.getTorneiDisponibili().contains("Torneo 1"));
        assertFalse(cardClash.getTorneiDisponibili().contains("Torneo 2"));

        // 5. Crea tornei con data futura
        cardClash.creaTorneo("Torneo 4", LocalDate.of(2024, 12, 12), "10:00", "Luogo 4");
        cardClash.creaTorneo("Torneo 5", LocalDate.of(2024, 12, 13), "14:00", "Luogo 5");

        // 6. Mostra i tornei disponibili (verifica che siano visibili anche i tornei futuri)
        cardClash.mostraTorneiDisponibili();

        // 7. Verifica che i tornei futuri siano disponibili
        assertTrue(cardClash.getTorneiDisponibili().contains("Torneo 4"));
        assertTrue(cardClash.getTorneiDisponibili().contains("Torneo 5"));

        // 8. Crea tornei con data passata (già chiusi)
        cardClash.creaTorneo("Torneo 6", LocalDate.of(2023, 12, 12), "10:00", "Luogo 6");
        cardClash.creaTorneo("Torneo 7", LocalDate.of(2023, 12, 13), "14:00", "Luogo 7");

        // 9. Mostra i tornei disponibili (verifica che i tornei passati non siano visibili)
        cardClash.mostraTorneiDisponibili();

        // 10. Verifica che i tornei passati non siano disponibili
        assertFalse(cardClash.getTorneiDisponibili().contains("Torneo 6"));
        assertFalse(cardClash.getTorneiDisponibili().contains("Torneo 7"));

        // 11. Crea un torneo con data uguale a quella odierna (quindi ancora aperto)
        cardClash.creaTorneo("Torneo 8", LocalDate.now(), "10:00", "Luogo 8");

        // 12. Mostra i tornei disponibili (verifica che il torneo odierno sia disponibile)
        cardClash.mostraTorneiDisponibili();

        // 13. Verifica che il torneo con data odierna sia disponibile
        assertTrue(cardClash.getTorneiDisponibili().contains("Torneo 8"));
    }

    @Test
    public void testSelezionaTorneo() {
        Date data = new Date();
        cardClash.creaTorneo("Torneo Test", data, "15:00", "Luogo Test");
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
        assertNotNull(mazzo);
        assertNotNull(mazzo.getCodice());
    }

    @Test
    public void testSelezionaTipo() {
        Date data = new Date();
        cardClash.creaTorneo("Torneo Test", data, "15:00", "Luogo Test");
        cardClash.selezionaFormato(1);

        Giocatore giocatore = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.setGiocatoreCorrente(giocatore);

        Mazzo mazzo = new Mazzo("Mazzo di Mario");
        giocatore.setMazzoCorrente(mazzo);

        cardClash.selezionaTipo(1);

        assertNotNull(mazzo.getTipoMazzo());
        assertEquals(cardClash.getTorneoCorrente().getFormato().getTipiMazzo().get(1), mazzo.getTipoMazzo());

        cardClash.selezionaTipo(5);

        assertNull(mazzo.getTipoMazzo());
    }

    @Test
    public void testConfermaIscrizione() throws GiocatoreGiaRegistratoException {
        cardClash.creaTorneo("Torneo Test", new Date(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();

        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.confermaRegistrazione();

        cardClash.inserimentoMazzo("Mazzo Test");
        Integer codiceMazzo = cardClash.getGiocatoreCorrente().getMazzoCorrente().getCodice();
        cardClash.confermaIscrizione();

        assertTrue(torneo.getGiocatori().containsKey("mario@mail.com"));
        assertTrue(torneo.getMazzi().containsKey(codiceMazzo));
        assertTrue(torneo.getGiocatore("mario@mail.com").getMazziGiocatore().containsKey(codiceMazzo));
    }

}
