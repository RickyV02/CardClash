package com.cardclash;

import java.security.SecureRandom;

public class Mazzo {

    private Integer codice;
    private String nome;
    private TipoMazzo tipoMazzo;

    // Costruttore
    public Mazzo(String nome) {
        this.nome = nome;
    }

    // Getter per il codice
    public Integer getCodice() {
        return codice;
    }

    public void setCodice() {
        this.generaCodice();
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
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
