package com.cardclash;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardClash {

    private static CardClash cardClash;

    private final Map<Integer, Torneo> tornei;
    private final Map<String, Giocatore> giocatori;
    private final Map<Integer, FormatoTorneo> formati;

    private Torneo torneoCorrente;
    private Giocatore giocatoreCorrente;
    private FormatoTorneo formatoCorrente;

    private CardClash() {
        this.formati = new HashMap<>();
        loadFormati();
        this.tornei = new HashMap<>();
        this.giocatori = new HashMap<>();
    }

    private void loadFormati() {
        FormatoTorneo formato1 = new FormatoTorneoPauper(1, "Pauper", Gioco.MAGIC, 16, 2.0f, 3.0f);
        FormatoTorneo formato2 = new FormatoTorneoMonotype(2, "Monotype", Gioco.POKEMON, 16, 2.0f, 3.0f);
        FormatoTorneo formato3 = new FormatoTorneo1v1(3, "1v1", Gioco.YUGIOH, 16, 1.0f, 3.0f);

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
        } else {
            System.out.println("Formato non trovato per il codice: " + codice);
        }
    }

    public void confermaCreazione() {
        do {
            this.torneoCorrente.setCodice();
        } while (this.tornei.containsKey(this.torneoCorrente.getCodice()));
        Integer code = this.torneoCorrente.getCodice();
        this.tornei.put(code, this.torneoCorrente);
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
        List<Torneo> elencoTornei = getTorneiList();
        for (Iterator<Torneo> iterator = elencoTornei.iterator(); iterator.hasNext();) {
            Torneo t = iterator.next();
            if (t.isTerminato() || !t.isAperto()) {
                iterator.remove();
            }
        }
        return elencoTornei;
    }

    public List<Torneo> getTorneiList() {
        List<Torneo> elencoTornei = new ArrayList<>();
        elencoTornei.addAll(tornei.values());
        return elencoTornei;
    }

    public Map<Integer, Torneo> getTornei() {
        return tornei;
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
        do {
            m.setCodice();
        } while (torneoCorrente.getGiocatori().values().stream()
                .anyMatch(g -> g.getMazzi().containsKey(m.getCodice())));
        Integer codice = m.getCodice();
        giocatoreCorrente.aggiungiMazzo(codice, m);
        torneoCorrente.aggiungiMazzo(codice, m);
        String email = giocatoreCorrente.getEmail();
        torneoCorrente.aggiungiGiocatore(email, giocatoreCorrente);
    }

    public void selezioneFormato(Integer codice) {
        FormatoTorneo f = formati.get(codice);
        if (f != null) {
            System.out.println("Formato selezionato: " + f.toString());
            this.setFormatoCorrente(f);
        }
    }

    public void inserimentoTipoMazzo(String nome) {
        formatoCorrente.inserisciTipoMazzo(nome);
    }

    public void confermaInserimentoTipo() {
        this.formatoCorrente.confermaInserimento();
    }

    // Estensione 5.a implementata (UC5)
    public Tabellone creaTabellone(Integer codTorneo) {
        Torneo t = tornei.get(codTorneo);
        if (t.isTerminato()) {
            System.out.println("Il torneo è già terminato, non è possibile creare un nuovo tabellone.");
            return null;
        }
        setTorneoCorrente(t);
        try {
            return t.creaTabellone();
        } catch (GiocatoriNotPotenzaDiDueException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void confermaTabellone() {
        torneoCorrente.confermaTabellone();
    }

    public Tabellone visualizzaTabellone(Integer codTorneo) {
        Torneo t = tornei.get(codTorneo);
        setTorneoCorrente(t);
        Tabellone tab = t.getTabellone();
        t.setTabelloneCorrente(tab);
        return tab;
    }

    public void eliminaGiocatore(String email) {
        torneoCorrente.eliminaGiocatore(email);
    }

    public void aggiornaTabellone() {
        torneoCorrente.aggiornaTabellone();
        torneoCorrente.setTabellone();
    }

    public void aggiornaPunteggio() {
        FormatoTorneo f = torneoCorrente.getFormato();
        torneoCorrente.aggiornaPunteggi(f.getVictoryScore());
    }

    public List<Giocatore> visualizzaClassifica(Integer codTorneo) {
        return tornei.get(codTorneo).getClassifica();
    }

    public Map<Integer, Torneo> getTorneiDaConcludere() {
        return tornei.entrySet().stream()
                .filter(entry -> !entry.getValue().isTerminato())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void aggiornaELO(Integer codTorneo) {
        Torneo t = tornei.get(codTorneo);
        torneoCorrente = t;
        t.aggiornaELO();
    }

    public void setVincitore() {
        torneoCorrente.concludiTorneo();
    }

    // Estensione 9.b e 9.c implementata
    public FormatoTorneo creaNuovoFormato(Integer codice, String nome, String gioco, Integer numMaxGiocatori,
            Float victoryScore, Float penaltyScore) throws GiocoNonSupportatoException {
        if (formati.containsKey(codice)) {
            System.out.println("Il codice " + codice + " è già stato utilizzato per un altro formato.");
            return null;
        }

        Gioco giocoEnum;
        try {
            giocoEnum = Gioco.valueOf(gioco);
        } catch (IllegalArgumentException e) {
            throw new GiocoNonSupportatoException(gioco);
        }

        formatoCorrente = new FormatoTorneoPersonalizzato(codice, nome, giocoEnum, numMaxGiocatori, victoryScore,
                penaltyScore);
        return formatoCorrente;
    }

    public void confermaFormato() {
        formati.put(formatoCorrente.getCodice(), formatoCorrente);
    }

    public Float showPlayerELO() {
        return giocatoreCorrente.getELO();
    }

    public FormatoTorneo getFormatoCorrente() {
        return formatoCorrente;
    }

    public void setTorneoCorrente(Torneo t) {
        torneoCorrente = t;
    }

    public void setGiocatoreCorrente(Giocatore g) {
        giocatoreCorrente = g;
    }

    public void setFormatoCorrente(FormatoTorneo f) {
        formatoCorrente = f;
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

    public Giocatore getGiocatore(String email) {
        return giocatori.get(email);
    }

}
