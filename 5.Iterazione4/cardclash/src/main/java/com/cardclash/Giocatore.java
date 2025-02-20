package com.cardclash;

import java.util.HashMap;
import java.util.Map;

public class Giocatore {

    private String nome;
    private final String email;
    private final String password;
    private String nickname;
    private Mazzo mazzoCorrente;
    private Float elo;
    private final HashMap<Integer, Mazzo> mazziGiocatore;
    private final HashMap<Integer, Float> punteggi;

    public Giocatore(String nome, String email, String password, String nickname) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.elo = 0.0f;
        this.mazziGiocatore = new HashMap<>();
        this.punteggi = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void aggiungiMazzo(Integer codice, Mazzo mazzo) {
        mazziGiocatore.put(codice, mazzo);
    }

    public void setMazzoCorrente(Mazzo mazzo) {
        mazzoCorrente = mazzo;
    }

    public Mazzo getMazzoCorrente() {
        return mazzoCorrente;
    }

    public void setPunteggio(Integer codTorneo) {
        punteggi.put(codTorneo, 0.0f);
    }

    public void addPunteggio(Integer codTorneo, Float punteggio) {
        Float oldPunteggio = punteggi.get(codTorneo);
        punteggi.put(codTorneo, oldPunteggio + punteggio);
    }

    public Map<Integer, Float> getPunteggi() {
        return punteggi;
    }

    public Float getPunteggio(Integer codTorneo) {
        return punteggi.get(codTorneo);
    }

    public Map<Integer, Mazzo> getMazzi() {
        return mazziGiocatore;
    }

    public Map<Integer, Mazzo> getMazziGiocatore() {
        return mazziGiocatore;
    }

    public Float getELO() {
        return elo;
    }

    public void setELO(Float elo) {
        this.elo = elo;
    }

    public void aggiornaELO(Integer codice) {
        Float currentELO = getELO();
        Float punteggio = getPunteggio(codice);
        setELO(currentELO + punteggio);
    }

    @Override
    public String toString() {
        return "Giocatore{"
                + "nome='" + nome + '\''
                + ", email='" + email + '\''
                + ", nickname='" + nickname + '\''
                + ", mazzoCorrente=" + (mazzoCorrente != null ? mazzoCorrente.getNome() : "Nessun mazzo")
                + ", numeroMazzi=" + mazziGiocatore.size()
                + '}';
    }
}
