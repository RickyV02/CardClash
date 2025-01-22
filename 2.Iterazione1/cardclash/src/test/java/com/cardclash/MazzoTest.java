package com.cardclash;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class MazzoTest {

    Mazzo m;

    @Before
    public void initTest() {
        m = new Mazzo("Mazzo Test");
    }

    @After
    public void clearTest() {
        m = null;
    }

    @Test
    public void testGeneraCodice() {
        assertNull(m.getCodice());

        m.setCodice();
        assertNotNull(m.getCodice());
    }

}
