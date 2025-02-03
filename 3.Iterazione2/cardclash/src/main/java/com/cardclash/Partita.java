package com.cardclash;

import java.security.SecureRandom;

public class Partita {

    private Integer codice;
    private final Giocatore giocatore1;
    private final Giocatore giocatore2;

    public Partita(Giocatore giocatore1, Giocatore giocatore2) {
        this.giocatore1 = giocatore1;
        this.giocatore2 = giocatore2;
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    public void setCodice() {
        this.generaCodice();
    }

    public Integer getCodice() {
        return codice;
    }

    public Giocatore getGiocatore1() {
        return giocatore1;
    }

    public Giocatore getGiocatore2() {
        return giocatore2;
    }

}
