package com.cardclash;

import java.util.HashMap;
import java.util.Map;

public class FormatoTorneo {

    private Integer codice;
    private String nome;
    private String gioco;
    private final Map<Integer, TipoMazzo> tipiMazzo;
    private TipoMazzo tipoMazzoCorrente;
    private final Integer numMaxGiocatori;

    // Costruttore
    public FormatoTorneo(Integer codice, String nome, String gioco, Integer numGiocatori) {
        this.codice = codice;
        this.nome = nome;
        this.gioco = gioco;
        this.tipiMazzo = new HashMap<>();
        this.numMaxGiocatori = numGiocatori;
        loadTipiMazzo();
    }

    // da rifare, il costruttore di TipoMazzo è stato modificato
    public void loadTipiMazzo() {
        //placeholder
        TipoMazzo tm1 = new TipoMazzo("Mazzo1");
        TipoMazzo tm2 = new TipoMazzo("Mazzo2");
        TipoMazzo tm3 = new TipoMazzo("Mazzo3");
        tipiMazzo.put(tm1.getCodice(), tm1);
        tipiMazzo.put(tm2.getCodice(), tm2);
        tipiMazzo.put(tm3.getCodice(), tm3);
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
        do {
            tm.setCodice();
        } while (tipiMazzo.containsKey(tm.getCodice()));
        Integer code = tm.getCodice();
        tipiMazzo.put(code, tm);
    }

    // Getter per il codice
    public Integer getCodice() {
        return codice;
    }

    public TipoMazzo getTipoMazzoCorrente() {
        return tipoMazzoCorrente;
    }

    public Integer getNumMaxGiocatori() {
        return numMaxGiocatori;
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
        return "FormatoTorneo{"
                + "codice=" + codice
                + ", nome='" + nome + '\''
                + ", gioco='" + gioco + '\''
                + ", tipiMazzo=" + tipiMazzo
                + '}';
    }
}
