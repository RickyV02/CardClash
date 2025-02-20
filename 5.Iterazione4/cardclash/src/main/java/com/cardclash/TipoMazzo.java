package com.cardclash;

import java.security.SecureRandom;

public class TipoMazzo {

    private Integer codice;
    private String nome;

    public TipoMazzo(String nome) {
        this.nome = nome;
    }

    public Integer getCodice() {
        return codice;
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    public void setCodice() {
        this.generaCodice();
    }

    public String getNome() {
        return nome;
    }

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
