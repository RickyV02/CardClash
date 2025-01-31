package com.cardclash;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GiocatoreTest {

    private Giocatore giocatore;
    private Mazzo mazzo;

    @Before
    public void setUp() {
        giocatore = new Giocatore("Marco Rossi", "marco.rossi@example.com", "password123", "marco_99");
        mazzo = new Mazzo("Mazzo1");
        mazzo.setCodice();
    }

    @After
    public void clearTest() {
        giocatore.setMazzoCorrente(null);
    }

    @Test
    public void testAggiungiMazzo() {
        giocatore.aggiungiMazzo(mazzo.getCodice(), mazzo);
        assertEquals(1, giocatore.getMazziGiocatore().size());
        assertTrue(giocatore.getMazziGiocatore().containsKey(mazzo.getCodice()));
    }

    @Test
    public void testSetMazzoCorrente() {
        giocatore.setMazzoCorrente(mazzo);
        assertEquals(mazzo, giocatore.getMazzoCorrente());
    }

}
