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
                tipi.add(new TipoMazzo("Synchro deck"));
                tipi.add(new TipoMazzo("Xyz deck"));
                break;
            case POKEMON:
                tipi.add(new TipoMazzo("Mazzo Standard"));
                break;
            case MAGIC:
                tipi.add(new TipoMazzo("Library"));
                tipi.add(new TipoMazzo("Aggro"));
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
