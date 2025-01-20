package com.cardclash;

public class Mazzo {

    private int codice;
    private String nome;
    private TipoMazzo tipoMazzo;

    // Costruttore
    public Mazzo(String nome) {
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter per il tipo di mazzo
    public TipoMazzo getTipoMazzo() {
        return tipoMazzo;
    }

    // Setter per il tipo di mazzo
    public void setTipo(TipoMazzo tm) {
        this.tipoMazzo = tm;
    }

    @Override
    public String toString() {
        return "Mazzo{" +
                "codice=" + codice +
                ", nome='" + nome + '\'' +
                ", tipoMazzo=" + (tipoMazzo != null ? tipoMazzo.getNome() : "N/A") +
                '}';
    }
}
