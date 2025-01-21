package com.cardclash;

import java.util.LinkedList;
import java.util.List;

public class Giocatore {

    private String nome;
    private final String email;
    private final String password;
    private String nickname;
    private Mazzo mazzoCorrente;
    private final LinkedList<Mazzo> mazziGiocatore;

    // Costruttore
    public Giocatore(String nome, String email, String password, String nickname) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.mazziGiocatore = new LinkedList<>();
    }

    // Getter per l'email
    public String getEmail() {
        return email;
    }

    // Getter per il nome
    public String getNome() {
        return nome;
    }

    // Setter per il nome
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter per il nickname
    public String getNickname() {
        return nickname;
    }

    // Setter per il nickname
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Metodo per aggiungere un mazzo
    public void aggiungiMazzo(Mazzo mazzo) {
        if (!mazziGiocatore.contains(mazzo)) {
            mazziGiocatore.add(mazzo);
        }
    }

    // Metodo per impostare il mazzo corrente
    public void setMazzoCorrente(Mazzo mazzo) {
        if (mazziGiocatore.contains(mazzo)) {
            this.mazzoCorrente = mazzo;
        } else {
            System.out.println("Il mazzo non appartiene al giocatore.");
        }
    }

    // Getter per il mazzo corrente
    public Mazzo getMazzoCorrente() {
        return mazzoCorrente;
    }

    // Metodo per ottenere tutti i mazzi del giocatore
    public List<Mazzo> getMazziGiocatore() {
        return mazziGiocatore;
    }

    @Override
    public String toString() {
        return "Giocatore{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mazzoCorrente=" + (mazzoCorrente != null ? mazzoCorrente.getNome() : "Nessun mazzo") +
                ", numeroMazzi=" + mazziGiocatore.size() +
                '}';
    }
}
