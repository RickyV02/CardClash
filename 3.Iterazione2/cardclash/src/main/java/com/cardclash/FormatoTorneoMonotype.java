package com.cardclash;

public class FormatoTorneoMonotype extends FormatoTorneo {

    public FormatoTorneoMonotype(Integer codice, String nome, Gioco gioco, Integer numGiocatori, Float victoryScore, Float penaltyScore) {
        super(codice, nome, gioco, numGiocatori, victoryScore, penaltyScore);
    }

    @Override
    public void loadTipiMazzo() {
        switch (getGioco()) {
            case POKEMON:
                TipoMazzo tm = new TipoMazzo("Mazzo monotype");
                do {
                    tm.setCodice();
                } while (getTipiMazzo().containsKey(tm.getCodice()));
                getTipiMazzo().put(tm.getCodice(), tm);
                System.out.println("Tipi di mazzo caricati per Monotype (" + getGioco() + ").");
                break;
            default:
                System.out.println("Il formato Monotype è definito solo per Pokémon (" + getGioco() + " non supportato).");
                break;
        }
    }
}
