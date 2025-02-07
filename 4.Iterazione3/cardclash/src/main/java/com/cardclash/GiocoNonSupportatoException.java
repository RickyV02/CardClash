package com.cardclash;

public class GiocoNonSupportatoException extends Exception {

    public GiocoNonSupportatoException(String gioco) {
        super("Il gioco " + gioco + " non è supportato.");
    }
}
