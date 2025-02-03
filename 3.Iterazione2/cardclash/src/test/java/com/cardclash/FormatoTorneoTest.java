package com.cardclash;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class FormatoTorneoTest {

    private FormatoTorneo formatoMagic;
    private FormatoTorneo formatoPokemon;
    private FormatoTorneo formatoYuGiOh;

    @Before
    public void setUp() {
        formatoMagic = new FormatoTorneoPauper(1, "Pauper", Gioco.MAGIC, 16, 2.0f, 3.0f);
        formatoPokemon = new FormatoTorneoMonotype(2, "Monotype", Gioco.POKEMON, 16, 2.0f, 3.0f);
        formatoYuGiOh = new FormatoTorneo1v1(3, "1v1", Gioco.YUGIOH, 16, 1.0f, 3.0f);
    }

    @Test
    public void testLoadTipiMazzoMagic() {
        formatoMagic.loadTipiMazzo();
        Map<Integer, TipoMazzo> tipiMazzo = formatoMagic.getTipiMazzo();
        assertEquals(1, tipiMazzo.size());
        assertTrue(tipiMazzo.containsKey(1));
        assertEquals("Mazzo pauper", tipiMazzo.get(1).getNome());
    }

    @Test
    public void testLoadTipiMazzoPokemon() {
        formatoPokemon.loadTipiMazzo();
        Map<Integer, TipoMazzo> tipiMazzo = formatoPokemon.getTipiMazzo();
        assertEquals(1, tipiMazzo.size());
        assertTrue(tipiMazzo.containsKey(1));
        assertEquals("Mazzo monotype", tipiMazzo.get(1).getNome());
    }

    @Test
    public void testLoadTipiMazzoYuGiOh() {
        formatoYuGiOh.loadTipiMazzo();
        Map<Integer, TipoMazzo> tipiMazzo = formatoYuGiOh.getTipiMazzo();
        assertEquals(3, tipiMazzo.size());
        assertTrue(tipiMazzo.containsKey(1));
        assertTrue(tipiMazzo.containsKey(2));
        assertTrue(tipiMazzo.containsKey(3));
        assertEquals("Main deck", tipiMazzo.get(1).getNome());
        assertEquals("Extra deck", tipiMazzo.get(2).getNome());
        assertEquals("Side deck", tipiMazzo.get(3).getNome());
    }
}
