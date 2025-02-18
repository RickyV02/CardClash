package com.cardclash;

public class FormatoTorneoPersonalizzato extends FormatoTorneo {

    public FormatoTorneoPersonalizzato(Integer codice, String nome, Gioco gioco, Integer numGiocatori,
            Float victoryScore,
            Float penaltyScore) {
        super(codice, nome, gioco, numGiocatori, victoryScore, penaltyScore);
    }

    @Override
    protected void loadTipiMazzo() {
        System.out.println("Formato torneo personalizzato" + this.getNome() + " creato con successo!");
    }
}
