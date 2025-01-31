package com.cardclash;

import java.util.HashMap;
import java.util.Map;

public class FormatoTorneo {

    private Integer codice;
    private String nome;
    private String gioco;
    private final Map<Integer, TipoMazzo> tipiMazzo;
    private TipoMazzo tipoMazzoCorrente;
    private Integer numMaxGiocatori;

    // Costruttore
    public FormatoTorneo(Integer codice, String nome, String gioco) { // Integer numGiocatori) {
        this.codice = codice;
        this.nome = nome;
        this.gioco = gioco;
        this.tipiMazzo = new HashMap<>();
        // this.numMaxGiocatori = numGiocatori;
        loadTipiMazzo();
    }

    // todo perchè il costruttore di TipoMazzo è stato cambiato
    public void loadTipiMazzo() {
        /*
         * String key = this.getGioco() + "-" + this.getNome();
         * 
         * switch (key) {
         * case "Magic: The Gathering-Pauper" -> {
         * this.tipiMazzo.put(1, new TipoMazzo(1, "Mazzo pauper"));
         * System.out.println("Tipi di mazzo caricati per Pauper (Magic).");
         * }
         * case "Pokémon-Monotype" -> {
         * this.tipiMazzo.put(1, new TipoMazzo(1, "Mazzo monotype"));
         * System.out.println("Tipi di mazzo caricati per Monotype (Pokémon).");
         * }
         * case "Yu-Gi-Oh!-1v1" -> {
         * this.tipiMazzo.put(1, new TipoMazzo(1, "Main deck"));
         * this.tipiMazzo.put(2, new TipoMazzo(2, "Extra deck"));
         * this.tipiMazzo.put(3, new TipoMazzo(3, "Side deck"));
         * System.out.println("Tipi di mazzo caricati per 1v1 (Yu-Gi-Oh!).");
         * }
         * default -> System.out.println("Gioco non riconosciuto.");
         * }
         */
    }

    public void inserisciTipoMazzo(String nome) {
        TipoMazzo tm = new TipoMazzo(nome);
        this.setTipoMazzoCorrente(tm);
    }

    // Getter per tipiMazzo
    public Map<Integer, TipoMazzo> getTipiMazzo() {
        return tipiMazzo;
    }

    public void confermaInserimento() {
        TipoMazzo tm = getTipoMazzoCorrente();
        tm.setCodice();
        Integer codice = tm.getCodice();
        tipiMazzo.put(codice, tm);
    }

    // Getter per il codice
    public Integer getCodice() {
        return codice;
    }

    public TipoMazzo getTipoMazzoCorrente() {
        return tipoMazzoCorrente;
    }

    // Setter per il codice
    public void setCodice(Integer codice) {
        this.codice = codice;
    }

    // Getter per il nome
    public String getNome() {
        return nome;
    }

    // Setter per il nome
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter per il gioco
    public String getGioco() {
        return gioco;
    }

    // Setter per il gioco
    public void setGioco(String gioco) {
        this.gioco = gioco;
    }

    public void setTipoMazzoCorrente(TipoMazzo tipoMazzoCorrente) {
        this.tipoMazzoCorrente = tipoMazzoCorrente;
    }

    @Override
    public String toString() {
        return "FormatoTorneo{" +
                "codice=" + codice +
                ", nome='" + nome + '\'' +
                ", gioco='" + gioco + '\'' +
                ", tipiMazzo=" + tipiMazzo +
                '}';
    }
}
