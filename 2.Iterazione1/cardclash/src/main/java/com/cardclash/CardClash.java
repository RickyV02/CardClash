package com.cardclash;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public void creaTorneo(String nome, LocalDate data, String orario, String luogo) {
        LocalTime o = LocalTime.parse(orario);
        this.torneoCorrente = new Torneo(nome, data, o, luogo);
        System.out.println("Torneo creato: " + torneoCorrente.getNome());
    }

    public void selezionaFormato(Integer codice) {
        FormatoTorneo f = formati.get(codice);
        if (f != null) {
            System.out.println("Formato selezionato: " + f.toString());
            torneoCorrente.setFormato(f);
        }
    }

    public void confermaCreazione() {
        this.torneoCorrente.setCodice();
        Integer code = this.torneoCorrente.getCodice();
        this.tornei.put(code, torneoCorrente);
    }

    public void registraGiocatore(String nome, String mail, String password, String nickname)
            throws GiocatoreGiaRegistratoException {
        /* Estensione 1.A del caso d'uso UC2 */
        if (giocatori.containsKey(mail)) {
            throw new GiocatoreGiaRegistratoException("Giocatore già registrato con l'email: " + mail);
        }
        this.giocatoreCorrente = new Giocatore(nome, mail, password, nickname);
    }

    public void confermaRegistrazione() {
        String mail = giocatoreCorrente.getEmail();
        giocatori.put(mail, giocatoreCorrente);
    }

    public List<Torneo> mostraTorneiDisponibili() {
        List<Torneo> elencoTornei = getTornei();
        for (Iterator<Torneo> iterator = elencoTornei.iterator(); iterator.hasNext();) {
            Torneo t = iterator.next();
            if (!t.isAperto()) {
                iterator.remove();
            }
        }

        return elencoTornei;
    }

    public List<Torneo> getTornei() {
        List<Torneo> elencoTornei = new ArrayList<>();
        elencoTornei.addAll(tornei.values());
        return elencoTornei;
    }

    public Torneo selezionaTorneo(Integer codTorneo) {
        Torneo t = tornei.get(codTorneo);
        setTorneoCorrente(t);
        return t;
    }

    public void inserimentoMazzo(String nome) {
        if (giocatoreCorrente == null) {
            System.out.println("Nessun giocatore selezionato.");
        }
        Mazzo nuovoMazzo = new Mazzo(nome);
        giocatoreCorrente.setMazzoCorrente(nuovoMazzo);
    }

    public Map<Integer, TipoMazzo> getTipiMazziConsentiti() {
        if (torneoCorrente == null) {
            System.out.println("Nessun torneo selezionato.");
        }
        FormatoTorneo f = torneoCorrente.getFormato();
        return f.getTipiMazzo();
    }

    public void selezionaTipo(Integer codice) {
        if (torneoCorrente == null) {
            System.out.println("Nessun torneo selezionato.");
        }
        Map<Integer, TipoMazzo> tipiMazziConsentiti = getTipiMazziConsentiti();
        TipoMazzo tm = tipiMazziConsentiti.get(codice);
        Mazzo m = giocatoreCorrente.getMazzoCorrente();
        m.setTipo(tm);
    }

    public void confermaIscrizione() {
        Mazzo m = giocatoreCorrente.getMazzoCorrente();
        m.setCodice();
        Integer codice = m.getCodice();
        giocatoreCorrente.aggiungiMazzo(codice, m);
        torneoCorrente.aggiungiMazzo(codice, m);
        String email = giocatoreCorrente.getEmail();
        torneoCorrente.aggiungiGiocatore(email, giocatoreCorrente);
    }

    public void setTorneoCorrente(Torneo t) {
        torneoCorrente = t;
    }

    public void setGiocatoreCorrente(Giocatore g) {
        giocatoreCorrente = g;
    }

    public Map<Integer, FormatoTorneo> getFormati() {
        return formati;
    }

    public Torneo getTorneoCorrente() {
        return torneoCorrente;
    }

    public Map<String, Giocatore> getGiocatori() {
        return giocatori;
    }

    public Giocatore getGiocatoreCorrente() {
        return giocatoreCorrente;
    }
}
