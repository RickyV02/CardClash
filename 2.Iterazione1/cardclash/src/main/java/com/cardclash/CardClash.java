package com.cardclash;

import java.util.*;

public class CardClash {

    private static CardClash cardClash;

    private final Map<Integer, Torneo> tornei;
    private final Map<String, Giocatore> giocatori;
    private final Map<Integer, FormatoTorneo> formati;

    private Torneo torneoCorrente;
    private Giocatore giocatoreCorrente;

    private CardClash() {
        this.formati = new HashMap<>();
        loadFormati();
        this.tornei = new HashMap<>();
        this.giocatori = new HashMap<>();
    }

    private void loadFormati() {
        FormatoTorneo formato1 = new FormatoTorneo(1, "Commander", "Magic: The Gathering");
        FormatoTorneo formato2 = new FormatoTorneo(2, "Monotype", "Pokémon");
        FormatoTorneo formato3 = new FormatoTorneo(3, "Standard", "Yu-Gi-Oh!");

        formati.put(formato1.getCodice(), formato1);
        formati.put(formato2.getCodice(), formato2);
        formati.put(formato3.getCodice(), formato3);

        System.out.println("Formati di torneo caricati con successo!");
    }

    public static CardClash getInstance() {
        if (cardClash == null) {
            cardClash = new CardClash();
        }
        return cardClash;
    }

    public void creaTorneo(String nome, Date data,String orario, String luogo) {
        this.torneoCorrente = new Torneo(nome, data, orario, luogo);
        System.out.println("Torneo creato: " + torneoCorrente.getNome());
    }

    public void selezionaFormato(Integer codice) {
        FormatoTorneo f = formati.get(codice);
        System.out.println("Formato selezionato: " + f.toString());
        if (f != null) {
            torneoCorrente.setFormato(f);
        }
    }

    public void confermaCreazione() {
        this.torneoCorrente.setCodice();
        Integer code=this.torneoCorrente.getCodice();
        this.tornei.put(code, torneoCorrente);
    }

    public void registraGiocatore(String nome, String mail, String password, String nickname) {
        if (giocatori.containsKey(mail)) {
            throw new IllegalArgumentException("Giocatore già registrato con questa email.");
        }
        Giocatore nuovoGiocatore = new Giocatore(nome, mail, password, nickname);
        giocatori.put(mail, nuovoGiocatore);
        this.giocatoreCorrente = nuovoGiocatore;
    }

    // Metodo per confermare la registrazione di un giocatore
    public void confermaRegistrazione() {
        if (giocatoreCorrente != null) {
            System.out.println("Registrazione confermata per: " + giocatoreCorrente.getNome());
        } else {
            System.out.println("Nessun giocatore da confermare.");
        }
    }

    // Metodo per mostrare tornei disponibili
    public void mostraTorneiDisponibili() {
        tornei.values().stream()
            .filter(Torneo::isAperto)
            .forEach(torneo -> System.out.println("Torneo disponibile: " + torneo.getNome()));
    }

    // Metodo per selezionare un torneo
    public void selezionaTorneo(int codTorneo) {
        if (!tornei.containsKey(codTorneo)) {
            throw new IllegalArgumentException("Torneo non trovato.");
        }
        torneoCorrente = tornei.get(codTorneo);
        System.out.println("Torneo selezionato: " + torneoCorrente.getNome());
    }

    // Metodo per inserire un mazzo
    public void inserimentoMazzo(String nome) {
        if (giocatoreCorrente == null) {
            throw new IllegalStateException("Nessun giocatore corrente.");
        }
        Mazzo nuovoMazzo = new Mazzo(nome);
        giocatoreCorrente.aggiungiMazzo(nuovoMazzo);
        System.out.println("Mazzo inserito per " + giocatoreCorrente.getNome() + ": " + nome);
    }

    // Metodo per selezionare un tipo di mazzo
    public void selezionaTipo(int codice) {
        if (torneoCorrente == null) {
            throw new IllegalStateException("Nessun torneo selezionato.");
        }
        Map<Integer, TipoMazzo> tipiConsentiti = torneoCorrente.getFormato().getTipiMazzo();
        if (!tipiConsentiti.containsKey(codice)) {
            throw new IllegalArgumentException("Tipo mazzo non consentito.");
        }
        System.out.println("Tipo mazzo selezionato: " + tipiConsentiti.get(codice).getNome());
    }

    // Metodo per ottenere tipi di mazzi consentiti
    public Map<Integer, TipoMazzo> getTipiMazziConsentiti() {
        if (torneoCorrente == null) {
            throw new IllegalStateException("Nessun torneo selezionato.");
        }
        return torneoCorrente.getFormato().getTipiMazzo();
    }
}
