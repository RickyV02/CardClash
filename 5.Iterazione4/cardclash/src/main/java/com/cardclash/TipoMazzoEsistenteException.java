package com.cardclash;

public class TipoMazzoEsistenteException extends Exception {

    public TipoMazzoEsistenteException(String nome) {
        super("Tipo mazzo" + nome + "già esistente!");
    }
}
