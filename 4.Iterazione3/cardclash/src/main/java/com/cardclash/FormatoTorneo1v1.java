package com.cardclash;

import java.util.ArrayList;
import java.util.List;

public class FormatoTorneo1v1 extends FormatoTorneo {

    public FormatoTorneo1v1(Integer codice, String nome, Gioco gioco, Integer numGiocatori, Float victoryScore, Float penaltyScore) {
        super(codice, nome, gioco, numGiocatori, victoryScore, penaltyScore);
    }

    @Override
    protected void loadTipiMazzo() {
        switch (getGioco()) {
            case YUGIOH:
                List<TipoMazzo> tipi = new ArrayList<>();
                tipi.add(new TipoMazzo("Main deck"));
                tipi.add(new TipoMazzo("Extra deck"));
                tipi.add(new TipoMazzo("Side deck"));

                for (TipoMazzo tm : tipi) {
                    do {
                        tm.setCodice();
                    } while (getTipiMazzo().containsKey(tm.getCodice()));
                    getTipiMazzo().put(tm.getCodice(), tm);
                }
                System.out.println("Tipi di mazzo caricati per 1v1 (" + getGioco() + ").");
                break;
            default:
                System.out.println("Il formato 1v1 è definito solo per Yu-Gi-Oh! (" + getGioco() + " non supportato).");
                break;
        }
    }
}
