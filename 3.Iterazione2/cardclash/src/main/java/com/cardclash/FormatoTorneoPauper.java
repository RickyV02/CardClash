package com.cardclash;

public class FormatoTorneoPauper extends FormatoTorneo {

    public FormatoTorneoPauper(Integer codice, String nome, Gioco gioco, Integer numGiocatori, Float victoryScore, Float penaltyScore) {
        super(codice, nome, gioco, numGiocatori, victoryScore, penaltyScore);
    }

    @Override
    protected void loadTipiMazzo() {
        switch (getGioco()) {
            case MAGIC:
                TipoMazzo tm = new TipoMazzo("Mazzo pauper");
                do {
                    tm.setCodice();
                } while (getTipiMazzo().containsKey(tm.getCodice()));
                getTipiMazzo().put(tm.getCodice(), tm);
                System.out.println("Tipi di mazzo caricati per Pauper (" + getGioco() + ").");
                break;
            default:
                System.out.println("Il formato Pauper è definito solo per Magic: The Gathering (" + getGioco() + " non supportato).");
                break;
        }
    }
}
