package com.cardclash;

public class GiocatoriNotPotenzaDiDueException extends Exception {
    public GiocatoriNotPotenzaDiDueException(int numGiocatori) {
        super("Il numero di giocatori (" + numGiocatori + ") non è una potenza di due.");
    }
}
