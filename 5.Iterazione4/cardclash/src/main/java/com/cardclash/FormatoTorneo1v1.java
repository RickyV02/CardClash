package com.cardclash;

import java.util.ArrayList;
import java.util.List;

public class FormatoTorneo1v1 extends FormatoTorneo {

    public FormatoTorneo1v1(Integer codice, String nome, Gioco gioco, Integer numGiocatori, Float victoryScore,
            Float penaltyScore) {
        super(codice, nome, gioco, numGiocatori, victoryScore, penaltyScore);
    }

    @Override
    protected void loadTipiMazzo() {
        List<TipoMazzo> tipi = new ArrayList<>();

        switch (getGioco()) {
            case YUGIOH:
                tipi.add(new TipoMazzo("Main deck"));
                tipi.add(new TipoMazzo("Extra deck"));
                tipi.add(new TipoMazzo("Side deck"));
                break;
            case POKEMON:
                tipi.add(new TipoMazzo("Mazzo Standard"));
                break;
            case MAGIC:
                tipi.add(new TipoMazzo("Library"));
                tipi.add(new TipoMazzo("Sideboard"));
                break;
            default:
                System.out.println("Formato 1v1 non supportato per " + getGioco());
                return;
        }

        for (TipoMazzo tm : tipi) {
            do {
                tm.setCodice();
            } while (getTipiMazzo().containsKey(tm.getCodice()));
            getTipiMazzo().put(tm.getCodice(), tm);
        }

        System.out.println("Tipi di mazzo caricati per " + getGioco() + ".");

    }
}
