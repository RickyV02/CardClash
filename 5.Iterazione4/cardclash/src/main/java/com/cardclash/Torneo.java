package com.cardclash;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private boolean terminato;
    private Map<String, Giocatore> giocatori;
    private final Map<Integer, Mazzo> mazziTorneo;
    private Giocatore vincitore;
    private FormatoTorneo formato;
    private Integer numGiocatori;
    private Tabellone tabellone;
    private Tabellone tabelloneCorrente;

    public Torneo(String nome, LocalDate data, LocalTime orario, String luogo) {
        this.nome = nome;
        this.data = data;
        this.orario = orario;
        this.luogo = luogo;
        this.terminato = false;
        this.giocatori = new HashMap<>();
        this.mazziTorneo = new HashMap<>();
        this.numGiocatori = 0;
        this.vincitore = null;
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    public void setCodice() {
        this.generaCodice();
    }

    public Integer getCodice() {
        return codice;
    }

    public String getNome() {
        return nome;
    }

    public boolean isTerminato() {
        return terminato;
    }

    public void setFormato(FormatoTorneo formato) {
        this.formato = formato;
    }

    public FormatoTorneo getFormato() {
        return formato;
    }

    public Tabellone getTabellone() {
        return tabellone;
    }

    public Tabellone getTabelloneCorrente() {
        return tabelloneCorrente;
    }

    public boolean isAperto() {
        LocalDate oggi = LocalDate.now();
        LocalTime adesso = LocalTime.now();
        return (data.isAfter(oggi) || (data.isEqual(oggi) && orario.isAfter(adesso)))
                && numGiocatori < this.formato.getNumMaxGiocatori() && getTabellone() == null;
    }

    public boolean isPotenzaDiDue(int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }

    public void aggiungiMazzo(Integer codice, Mazzo m) {
        mazziTorneo.put(codice, m);
    }

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

    public void aggiornaTabellone() throws GiocatoriNotPotenzaDiDueException {
        boolean checkPotenza = isPotenzaDiDue(tabelloneCorrente.getGiocatori().size());
        if (checkPotenza) {
            tabelloneCorrente.aggiornaTabellone();
        } else {
            throw new GiocatoriNotPotenzaDiDueException(tabelloneCorrente.getGiocatori().size());
        }
    }

    public void aggiornaPunteggi(Float punteggio) {
        tabelloneCorrente.aggiornaPunteggi(codice, punteggio);
    }

    public List<Giocatore> getClassifica() {
        List<Giocatore> classifica = new ArrayList<>(giocatori.values());
        classifica.sort(Comparator.comparingDouble((Giocatore g) -> g.getPunteggio(codice)).reversed());
        return classifica;
    }

    public void aggiornaELO() {
        List<Giocatore> listaGiocatori = getGiocatoriList();

        for (Iterator<Giocatore> iterator = listaGiocatori.iterator(); iterator.hasNext();) {
            Giocatore g = iterator.next();
            g.aggiornaELO(codice);
        }
    }

    public void concludiTorneo() {
        terminato = true;
        if (!getClassifica().isEmpty()) {
            vincitore = getClassifica().get(0);
        } else {
            vincitore = null;
        }
    }

    public Giocatore getVincitore() {
        return vincitore;
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

    public Mazzo getMazzo(Integer codice) {
        return mazziTorneo.get(codice);
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getOrario() {
        return orario;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setGiocatori(Map<String, Giocatore> giocatori) {
        this.giocatori = giocatori;
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
