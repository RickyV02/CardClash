package com.cardclash;

public class TipoMazzo {

    private Integer codice;
    private String nome;

    // Costruttore
    public TipoMazzo(Integer codice, String nome) {
        this.codice = codice;
        this.nome = nome;
    }

    // Getter per il codice
    public Integer getCodice() {
        return codice;
    }

    // Setter per il codice
    public void setCodice(Integer codice) {
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
