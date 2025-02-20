package com.cardclash;

import java.util.HashMap;
import java.util.Map;

public abstract class FormatoTorneo {

    private Integer codice;
    private String nome;
    private final Gioco gioco;
    private final Map<Integer, TipoMazzo> tipiMazzo;
    private TipoMazzo tipoMazzoCorrente;
    private final Integer numMaxGiocatori;
    private final Float victoryScore;
    private final Float penaltyScore;

    public FormatoTorneo(Integer codice, String nome, Gioco gioco, Integer numGiocatori, Float victoryScore,
            Float penaltyScore) {
        this.codice = codice;
        this.nome = nome;
        this.gioco = gioco;
        this.tipiMazzo = new HashMap<>();
        this.numMaxGiocatori = numGiocatori;
        this.victoryScore = victoryScore;
        this.penaltyScore = penaltyScore;
        loadTipiMazzo();
    }

    // Metodo astratto che deve essere implementato dalle sottoclassi
    protected abstract void loadTipiMazzo();

    public void inserisciTipoMazzo(String nome) throws TipoMazzoEsistenteException {
        if (tipiMazzo.values().stream().anyMatch(tm -> tm.getNome().equals(nome))) {
            throw new TipoMazzoEsistenteException(nome);
        } else {
            TipoMazzo tm = new TipoMazzo(nome);
            this.setTipoMazzoCorrente(tm);
        }
    }

    public void confermaInserimento() {
        TipoMazzo tm = getTipoMazzoCorrente();
        do {
            tm.setCodice();
        } while (tipiMazzo.containsKey(tm.getCodice()));
        Integer code = tm.getCodice();
        tipiMazzo.put(code, tm);
    }

    // Getter e Setter
    public Integer getCodice() {
        return codice;
    }

    public void setCodice(Integer codice) {
        this.codice = codice;
    }

    public Map<Integer, TipoMazzo> getTipiMazzo() {
        return tipiMazzo;
    }

    public TipoMazzo getTipoMazzoCorrente() {
        return tipoMazzoCorrente;
    }

    public Integer getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Gioco getGioco() {
        return gioco;
    }

    public void setTipoMazzoCorrente(TipoMazzo tipoMazzoCorrente) {
        this.tipoMazzoCorrente = tipoMazzoCorrente;
    }

    public Float getVictoryScore() {
        return victoryScore;
    }

    public Float getPenaltyScore() {
        return penaltyScore;
    }

    @Override
    public String toString() {
        return "FormatoTorneo{"
                + "codice=" + codice
                + ", nome='" + nome + '\''
                + ", gioco=" + gioco // Utilizza il metodo toString() dell'enum
                + ", tipiMazzo=" + tipiMazzo
                + '}';
    }
}
