package com.cardclash;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

public class FormatoTorneoTest {

    private FormatoTorneo formatoMagic;
    private FormatoTorneo formatoPokemon;
    private FormatoTorneo formatoYuGiOh;

    @Before
    public void setUp() {
        formatoMagic = new FormatoTorneo(1, "Commander", "Magic: The Gathering");
        formatoPokemon = new FormatoTorneo(2, "Monotype", "Pokémon");
        formatoYuGiOh = new FormatoTorneo(3, "Standard", "Yu-Gi-Oh!");
    }

    @Test
    public void testLoadTipiMazzoMagic() {
        formatoMagic.loadTipiMazzo();
        Map<Integer, TipoMazzo> tipiMazzo = formatoMagic.getTipiMazzo();
        assertEquals(1, tipiMazzo.size());
        assertTrue(tipiMazzo.containsKey(1));
        assertEquals("Mazzo commander", tipiMazzo.get(1).getNome());
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
