package com.cardclash;

public class TipoMazzo {

    private int codice;
    private String nome;

    // Costruttore
    public TipoMazzo(int codice, String nome) {
        this.codice = codice;
        this.nome = nome;
    }

    // Getter per il codice
    public int getCodice() {
        return codice;
    }

    // Setter per il codice
    public void setCodice(int codice) {
        this.codice = codice;
    }

    // Getter per il nome
    public String getNome() {
        return nome;
    }

    // Setter per il nome
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "TipoMazzo{" +
                "codice=" + codice +
                ", nome='" + nome + '\'' +
                '}';
    }
}
