package com.cardclash;

import java.time.LocalDate;
import java.util.List;
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
        assertEquals(4, formati.size());
    }

    @Test
    public void testCreaTorneo() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();
        assertNotNull(torneo);
    }

    @Test
    public void testSelezionaFormato() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        Torneo torneo = cardClash.getTorneoCorrente();

        cardClash.selezionaFormato(20); // formato inesistente
        assertNull(torneo.getFormato());

        cardClash.selezionaFormato(1); // Pauper
        assertNotNull(torneo.getFormato());
        assertEquals("Pauper", torneo.getFormato().getNome());
    }

    @Test
    public void testConfermaCreazione() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        cardClash.confermaCreazione();
        Torneo torneo = cardClash.getTorneoCorrente();
        assertNotNull(torneo);
        assertTrue(cardClash.getTornei().containsKey(torneo.getCodice()));
    }

    @Test
    public void testRegistraGiocatore() throws GiocatoreGiaRegistratoException {
        cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password456", "mario123");
        Giocatore g = cardClash.getGiocatoreCorrente();
        assertNotNull(g);
        cardClash.confermaRegistrazione();

        // Tenta di registrare lo stesso giocatore con la stessa email
        assertNotNull(assertThrows(GiocatoreGiaRegistratoException.class,
                () -> cardClash.registraGiocatore("Mario Rossi", "mario@mail.com", "password456", "mario123")));
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
    public void testMostraTorneiDisponibili() throws DataGiaPresenteException {
        FormatoTorneo f = cardClash.getFormati().get(1);
        // 1. Crea tornei con date passate
        cardClash.creaTorneo("Torneo 1", LocalDate.of(2025, 01, 12), "10:00", "Luogo 1");
        Torneo t1 = cardClash.getTorneoCorrente();
        cardClash.selezionaFormato(1);
        cardClash.confermaCreazione();
        cardClash.creaTorneo("Torneo 2", LocalDate.of(2025, 01, 13), "14:00", "Luogo 2");
        Torneo t2 = cardClash.getTorneoCorrente();
        cardClash.selezionaFormato(1);
        cardClash.confermaCreazione();

        assertFalse(cardClash.mostraTorneiDisponibili().contains(t1));
        assertFalse(cardClash.mostraTorneiDisponibili().contains(t2));

        // 2. Crea tornei con data futura
        cardClash.creaTorneo("Torneo 3", LocalDate.of(2026, 12, 12), "10:00", "Luogo 3");
        Torneo t3 = cardClash.getTorneoCorrente();
        t3.setFormato(f);
        cardClash.confermaCreazione();
        cardClash.creaTorneo("Torneo 4", LocalDate.of(2026, 12, 13), "14:00", "Luogo 4");
        Torneo t4 = cardClash.getTorneoCorrente();
        t4.setFormato(f);
        cardClash.confermaCreazione();

        assertTrue(cardClash.mostraTorneiDisponibili().contains(t3));
        assertTrue(cardClash.mostraTorneiDisponibili().contains(t4));

        cardClash.creaTorneo("Torneo 5", LocalDate.of(2026, 12, 16), "14:00", "Luogo 4");
        Torneo t5 = cardClash.getTorneoCorrente();
        t5.setFormato(f);
        cardClash.confermaCreazione();
        t5.concludiTorneo();
        assertFalse(cardClash.mostraTorneiDisponibili().contains(t5));

    }

    @Test
    public void testSelezionaTorneo() throws DataGiaPresenteException {
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
    public void testSelezionaTipo() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "15:00", "Luogo Test");
        cardClash.confermaCreazione();
        cardClash.selezionaFormato(1);
        Map<Integer, TipoMazzo> tipiMazziConsentiti = cardClash.getTorneoCorrente().getFormato().getTipiMazzo();

        Giocatore giocatore = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        cardClash.setGiocatoreCorrente(giocatore);

        Mazzo mazzo = new Mazzo("Mazzo di Mario");
        giocatore.setMazzoCorrente(mazzo);

        // prendiamo il primo tipo di mazzo consentito, per semplicità
        Integer codiceTipo = tipiMazziConsentiti.keySet().stream().findFirst().get();
        cardClash.selezionaTipo(codiceTipo);

        assertNotNull(mazzo.getTipoMazzo());
        assertEquals(cardClash.getTorneoCorrente().getFormato().getTipiMazzo().get(codiceTipo), mazzo.getTipoMazzo());

    }

    @Test
    public void testConfermaIscrizione() throws GiocatoreGiaRegistratoException, DataGiaPresenteException {
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

    @Test
    public void testSelezioneFormato() {
        cardClash.selezioneFormato(1);
        FormatoTorneo formatoCorrente = cardClash.getFormatoCorrente();
        assertNotNull(formatoCorrente);
    }

    @Test
    public void testInserimentoTipoMazzo() {
        cardClash.selezioneFormato(1);
        cardClash.inserimentoTipoMazzo("Aggro");
        FormatoTorneo formato = cardClash.getFormati().get(1);
        assertNotNull(formato.getTipoMazzoCorrente());
        assertEquals("Aggro", formato.getTipoMazzoCorrente().getNome());
    }

    @Test
    public void testConfermaInserimentoTipo() {
        cardClash.selezioneFormato(1);
        Integer size = cardClash.getFormatoCorrente().getTipiMazzo().size();
        cardClash.inserimentoTipoMazzo("Control");
        cardClash.confermaInserimentoTipo();
        assertEquals(size + 1, cardClash.getFormatoCorrente().getTipiMazzo().size());
        assertTrue("Control", cardClash.getFormati().get(1).getTipiMazzo().values().stream()
                .anyMatch(tm -> tm.getNome().equals("Control")));
    }

    @Test
    public void testCreaTabellone() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "12:00", "Roma");
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456");
        Torneo t = cardClash.getTorneoCorrente();
        cardClash.getTorneoCorrente().getGiocatori().put(g1.getEmail(), g1);
        cardClash.getTorneoCorrente().getGiocatori().put(g2.getEmail(), g2);
        cardClash.confermaCreazione();
        Tabellone tabellone = cardClash.creaTabellone(cardClash.getTorneoCorrente().getCodice());
        assertNotNull(tabellone);
        assertEquals(1, tabellone.getPartite().size());
        t.concludiTorneo();
        assertNull(cardClash.creaTabellone(t.getCodice()));
    }

    @Test
    public void testConfermaTabellone() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "12:00", "Roma");
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456");
        cardClash.getTorneoCorrente().getGiocatori().put(g1.getEmail(), g1);
        cardClash.getTorneoCorrente().getGiocatori().put(g2.getEmail(), g2);
        cardClash.confermaCreazione();
        cardClash.creaTabellone(cardClash.getTorneoCorrente().getCodice());
        cardClash.confermaTabellone();
        assertNotNull(cardClash.getTorneoCorrente().getTabellone());
    }

    @Test
    public void testVisualizzaTabellone() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "12:00", "Roma");
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456");
        cardClash.getTorneoCorrente().getGiocatori().put(g1.getEmail(), g1);
        cardClash.getTorneoCorrente().getGiocatori().put(g2.getEmail(), g2);
        cardClash.confermaCreazione();
        cardClash.creaTabellone(cardClash.getTorneoCorrente().getCodice());
        cardClash.confermaTabellone();
        Tabellone tabellone = cardClash.visualizzaTabellone(cardClash.getTorneoCorrente().getCodice());
        assertNotNull(tabellone);
    }

    @Test
    public void testEliminaGiocatore() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "12:00", "Roma");
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456");
        cardClash.getTorneoCorrente().getGiocatori().put(g1.getEmail(), g1);
        cardClash.getTorneoCorrente().getGiocatori().put(g2.getEmail(), g2);
        cardClash.confermaCreazione();
        cardClash.creaTabellone(cardClash.getTorneoCorrente().getCodice());
        cardClash.confermaTabellone();
        cardClash.eliminaGiocatore("test@mail.com");
        assertFalse(cardClash.getTorneoCorrente().getTabellone().getGiocatori().stream()
                .anyMatch(g -> g.getEmail().equals("test@mail.com")));
    }

    @Test
    public void testAggiornaTabellone() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "12:00", "Roma");
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456");
        cardClash.getTorneoCorrente().getGiocatori().put(g1.getEmail(), g1);
        cardClash.getTorneoCorrente().getGiocatori().put(g2.getEmail(), g2);
        cardClash.confermaCreazione();
        cardClash.creaTabellone(cardClash.getTorneoCorrente().getCodice());
        cardClash.aggiornaTabellone();
        assertNotNull(cardClash.getTorneoCorrente().getTabellone());
    }

    @Test
    public void testAggiornaPunteggio() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Test", LocalDate.now(), "12:00", "Roma");
        Torneo t = cardClash.getTorneoCorrente();
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password123", "mario123");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password456", "luigi456");
        cardClash.getTorneoCorrente().getGiocatori().put(g1.getEmail(), g1);
        cardClash.getTorneoCorrente().getGiocatori().put(g2.getEmail(), g2);
        cardClash.selezionaFormato(1);
        cardClash.confermaCreazione();
        cardClash.creaTabellone(cardClash.getTorneoCorrente().getCodice());
        assertEquals(0.0f, g1.getPunteggio(t.getCodice()), 0.01);
        cardClash.aggiornaPunteggio();
        assertEquals(2.0f, g1.getPunteggio(t.getCodice()), 0.01);
    }

    @Test
    public void testVisualizzaClassifica() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Classifica", LocalDate.now(), "12:00", "Roma");
        Torneo t = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();
        int codTorneo = t.getCodice();

        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "password", "mario");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "password", "luigi");
        t.getGiocatori().put(g1.getEmail(), g1);
        t.getGiocatori().put(g2.getEmail(), g2);

        g1.setPunteggio(codTorneo);
        g2.setPunteggio(codTorneo);
        g1.addPunteggio(codTorneo, 5.0f);
        g2.addPunteggio(codTorneo, 10.0f);

        List<Giocatore> classifica = cardClash.visualizzaClassifica(codTorneo);
        assertEquals(g2, classifica.get(0));
        assertEquals(g1, classifica.get(1));
    }

    @Test
    public void testGetTorneiDaConcludere() throws DataGiaPresenteException {
        // Crea il torneo A (non terminato)
        cardClash.creaTorneo("Torneo A", LocalDate.of(2025, 01, 12), "12:00", "Roma");
        Torneo t1 = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();

        // Crea il torneo B e lo conclude
        cardClash.creaTorneo("Torneo B", LocalDate.of(2026, 01, 12), "12:00", "Roma");
        Torneo t2 = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();
        t2.concludiTorneo();

        Map<Integer, Torneo> torneiDaConcludere = cardClash.getTorneiDaConcludere();
        assertTrue(torneiDaConcludere.containsKey(t1.getCodice()));
        assertFalse(torneiDaConcludere.containsKey(t2.getCodice()));
    }

    @Test
    public void testAggiornaELO() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo ELO", LocalDate.now(), "12:00", "Roma");
        Torneo t = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();
        int codTorneo = t.getCodice();
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "pass", "mario");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "pass", "luigi");
        t.getGiocatori().put(g1.getEmail(), g1);
        t.getGiocatori().put(g2.getEmail(), g2);

        g1.setPunteggio(codTorneo);
        g2.setPunteggio(codTorneo);
        g1.addPunteggio(codTorneo, 10.0f);
        g2.addPunteggio(codTorneo, 20.0f);

        cardClash.aggiornaELO(codTorneo);

        assertEquals(10.0f, g1.getELO(), 0.01);
        assertEquals(20.0f, g2.getELO(), 0.01);
    }

    @Test
    public void testSetVincitore() throws DataGiaPresenteException {
        cardClash.creaTorneo("Torneo Vincitore", LocalDate.now(), "12:00", "Roma");
        Torneo t = cardClash.getTorneoCorrente();
        cardClash.confermaCreazione();
        Integer codTorneo = t.getCodice();
        Giocatore g1 = new Giocatore("Mario Rossi", "mario@mail.com", "pass", "mario");
        Giocatore g2 = new Giocatore("Luigi Bianchi", "luigi@mail.com", "pass", "luigi");
        t.getGiocatori().put(g1.getEmail(), g1);
        t.getGiocatori().put(g2.getEmail(), g2);
        g1.setPunteggio(codTorneo);
        g2.setPunteggio(codTorneo);
        g1.addPunteggio(codTorneo, 10.0f);
        g2.addPunteggio(codTorneo, 20.0f);

        assertFalse(t.isTerminato());

        cardClash.setVincitore();

        assertSame(g2, t.getVincitore());
        assertTrue(t.isTerminato());
    }

    @Test
    public void testCreaNuovoFormato() throws GiocoNonSupportatoException {
        // Caso valido: crea un nuovo formato con un gioco valido
        FormatoTorneo formato = cardClash.creaNuovoFormato(100, "Formato Test", "MAGIC", 16, 1.0f, -1.0f);
        assertNotNull(formato);
        assertEquals(100, formato.getCodice().intValue());
        assertEquals("Formato Test", formato.getNome());

        // Caso non valido: il gioco passato non è presente nell'enum, quindi deve
        // lanciare l'eccezione
        assertNotNull(assertThrows(GiocoNonSupportatoException.class, () -> {
            cardClash.creaNuovoFormato(101, "Formato Invalido", "NONESISTENTE", 16, 1.0f, -1.0f);
        }));
    }

    @Test
    public void testConfermaFormato() throws GiocoNonSupportatoException {
        FormatoTorneo formato = cardClash.creaNuovoFormato(102, "Formato Conferma", "MAGIC", 16, 1.0f, -1.0f);
        assertFalse(cardClash.getFormati().containsKey(102));

        cardClash.confermaFormato();
        assertTrue(cardClash.getFormati().containsKey(102));
        assertSame(formato, cardClash.getFormati().get(102));
    }

}
