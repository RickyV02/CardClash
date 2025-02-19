package com.cardclash;

import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class FormatoTorneoTest {

    private FormatoTorneo formatoPauper;
    private FormatoTorneo formatoMonotype;
    private FormatoTorneo formato1v1;
    private FormatoTorneo formatoPersonalizzato;

    @Before
    public void setUp() {
        formatoPauper = new FormatoTorneoPauper(1, "Pauper", Gioco.MAGIC, 16, 2.0f, 3.0f);
        formatoMonotype = new FormatoTorneoMonotype(2, "Monotype", Gioco.POKEMON, 16, 2.0f, 3.0f);
        formato1v1 = new FormatoTorneo1v1(3, "1v1", Gioco.YUGIOH, 16, 1.0f, 3.0f);
        formatoPersonalizzato = new FormatoTorneoPersonalizzato(4, "Personalizzato", Gioco.MAGIC, 16, 2.0f, 3.0f);
    }

    @Test
    public void testLoadTipiMazzoPauper() {
        Map<Integer, TipoMazzo> tipiMazzo = formatoPauper.getTipiMazzo();
        assertNotNull(tipiMazzo.values());
        assertEquals(1, tipiMazzo.size());
    }

    @Test
    public void testLoadTipiMazzoMonotype() {
        Map<Integer, TipoMazzo> tipiMazzo = formatoMonotype.getTipiMazzo();
        assertNotNull(tipiMazzo.values());
        assertEquals(1, tipiMazzo.size());
    }

    @Test
    public void testLoadTipiMazzo1v1() {
        Map<Integer, TipoMazzo> tipiMazzo = formato1v1.getTipiMazzo();
        assertNotNull(tipiMazzo.values());
        assertEquals(2, tipiMazzo.size());
    }

    @Test
    public void testLoadTipiMazzoPersonalizzato() {
        Map<Integer, TipoMazzo> tipiMazzo = formatoPersonalizzato.getTipiMazzo();
        assertNotNull(tipiMazzo);
        //il formato personalizzato non ha mazzi predefiniti da caricare
        assertEquals(0, tipiMazzo.size());
    }

    @Test
    public void testInserisciTipoMazzo() {
        try {
            formato1v1.inserisciTipoMazzo("Control Deck");
        } catch (TipoMazzoEsistenteException e) {
            System.err.println(e.getMessage());
        }
        assertNotNull(formato1v1.getTipoMazzoCorrente());
    }

    @Test
    public void testConfermaInserimento() {
        try {
            formato1v1.inserisciTipoMazzo("Control Deck");
        } catch (TipoMazzoEsistenteException e) {
            System.err.println(e.getMessage());
        }
        formato1v1.confermaInserimento();
        Integer codice = formato1v1.getTipoMazzoCorrente().getCodice();
        assertTrue(formato1v1.getTipiMazzo().containsKey(codice));
    }

}
