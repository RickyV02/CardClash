package com.cardclash;

import java.security.SecureRandom;

public class TipoMazzo {

    private Integer codice;
    private String nome;

    // Costruttor
    public TipoMazzo(String nome) {
        this.nome = nome;
    }

    // Getter per il codice
    public Integer getCodice() {
        return codice;
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    // Setter per il codice
    public void setCodice() {
        this.generaCodice();
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
