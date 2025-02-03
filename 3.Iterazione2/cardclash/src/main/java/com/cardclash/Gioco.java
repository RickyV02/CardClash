package com.cardclash;

public enum Gioco {
    MAGIC("Magic: The Gathering"),
    POKEMON("Pokémon"),
    YUGIOH("Yu-Gi-Oh!");

    private final String nome;

    Gioco(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
