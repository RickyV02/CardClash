package com.cardclash;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Torneo {

    private Integer codice;
    private final String nome;
    private final LocalDate data;
    private final LocalTime orario;
    private final String luogo;
    private final Map<String, Giocatore> giocatori;
    private final Map<Integer, Mazzo> mazziTorneo;
    private FormatoTorneo formato;
    private Integer numGiocatori;
    private Tabellone tabellone;
    private Tabellone tabelloneCorrente;

    public Torneo(String nome, LocalDate data, LocalTime orario, String luogo) {
        this.nome = nome;
        this.data = data;
        this.orario = orario;
        this.luogo = luogo;
        this.giocatori = new HashMap<>();
        this.mazziTorneo = new HashMap<>();
        this.numGiocatori = 0;
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    public void setCodice() {
        this.generaCodice();
    }

    // Getter per il codice
    public Integer getCodice() {
        return codice;
    }

    public String getNome() {
        return nome;
    }

    // Setter per il formato
    public void setFormato(FormatoTorneo formato) {
        this.formato = formato;
    }

    // Getter per il formato
    public FormatoTorneo getFormato() {
        return formato;
    }

    public Tabellone getTabellone() {
        return tabellone;
    }

    public boolean isAperto() {
        LocalDate oggi = LocalDate.now();
        LocalTime adesso = LocalTime.now();
        return (data.isAfter(oggi) || (data.isEqual(oggi) && orario.isAfter(adesso)))
                && numGiocatori < formato.getNumMaxGiocatori();
    }

    public boolean isPotenzaDiDue(int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }

    // Metodo per aggiungere un mazzo (con codice come chiave)
    public void aggiungiMazzo(Integer codice, Mazzo m) {
        mazziTorneo.put(codice, m);
    }

    // Metodo per aggiungere un giocatore (con email come chiave)
    public void aggiungiGiocatore(String email, Giocatore g) {
        giocatori.put(email, g);
        addGiocatore();
    }

    public Tabellone creaTabellone() throws GiocatoriNotPotenzaDiDueException {
        List<Giocatore> giocatoriIscritti = getGiocatoriList();
        inizializzaPunteggi(giocatoriIscritti);
        boolean checkPotenza = isPotenzaDiDue(giocatoriIscritti.size());
        if (checkPotenza) {
            tabelloneCorrente = new Tabellone(giocatoriIscritti);
            return tabelloneCorrente;
        } else {
            throw new GiocatoriNotPotenzaDiDueException(giocatoriIscritti.size());
        }
    }

    public void confermaTabellone() {
        tabelloneCorrente.setCodice();
        setTabellone();
    }

    public void eliminaGiocatore(String email) {
        tabelloneCorrente.eliminaGiocatore(email);
    }

    public void aggiornaTabellone() {
        tabelloneCorrente.aggiornaTabellone();
    }

    public void aggiornaPunteggi(Float punteggio) {
        tabelloneCorrente.aggiornaPunteggi(codice, punteggio);
    }

    public void setTabellone() {
        tabellone = tabelloneCorrente;
    }

    public void setTabelloneCorrente(Tabellone tabelloneCorrente) {
        this.tabelloneCorrente = tabelloneCorrente;
    }

    public void addGiocatore() {
        numGiocatori++;
    }

    // Metodo per ottenere un giocatore dato l'email
    public Giocatore getGiocatore(String email) {
        return giocatori.get(email);
    }

    public Map<String, Giocatore> getGiocatori() {
        return giocatori;
    }

    public List<Giocatore> getGiocatoriList() {
        List<Giocatore> giocatoriIscritti = new ArrayList<>();
        giocatoriIscritti.addAll(giocatori.values());
        return giocatoriIscritti;
    }

    public void inizializzaPunteggi(List<Giocatore> giocatoriIscritti) {
        for (Iterator<Giocatore> iterator = giocatoriIscritti.iterator(); iterator.hasNext();) {
            Giocatore giocatore = iterator.next();
            giocatore.setPunteggio(this.codice);
        }
    }

    public Map<Integer, Mazzo> getMazzi() {
        return mazziTorneo;
    }

    // Metodo per ottenere un mazzo dato il codice
    public Mazzo getMazzo(Integer codice) {
        return mazziTorneo.get(codice);
    }

    @Override
    public String toString() {
        return "Torneo{"
                + "codice=" + codice
                + ", nome='" + nome + '\''
                + ", data=" + data
                + ", orario=" + orario
                + ", luogo='" + luogo + '\''
                + ", numero giocatori=" + giocatori.size()
                + ", numero mazzi=" + mazziTorneo.size()
                + ", formato=" + formato.getNome()
                + '}';
    }
}
