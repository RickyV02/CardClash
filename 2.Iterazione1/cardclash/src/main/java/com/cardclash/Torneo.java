package com.cardclash;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.security.SecureRandom;

public class Torneo {

    private int codice;
    private final String nome;
    private final Date data;
    private final String orario;
    private final String luogo;
    private final Map<String, Giocatore> giocatori;
    private final Map<Integer, Mazzo> mazziTorneo;
    private FormatoTorneo formato;

    public Torneo(String nome, Date data, String orario, String luogo) {
        this.nome = nome;
        this.data = data;
        this.orario = orario;
        this.luogo = luogo;
        this.giocatori = new HashMap<>();
        this.mazziTorneo = new HashMap<>();
        generaCodice();
    }

    private void generaCodice() {
        SecureRandom random = new SecureRandom();
        this.codice = random.nextInt(999999);
    }

    public void setCodice() {
        this.generaCodice();
    }

    // Getter per il codice
    public int getCodice() {
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

    // Metodo per verificare se il torneo è aperto
    public Boolean isAperto() {
        // Esempio: limite massimo di giocatori
        return giocatori.size() < 16;
    }

    // Metodo per aggiungere un mazzo (con codice come chiave)
    public void aggiungiMazzo(int codice, Mazzo m) {
        if (!mazziTorneo.containsKey(codice)) {
            mazziTorneo.put(codice, m);
        }
    }

    // Metodo per aggiungere un giocatore (con email come chiave)
    public void aggiungiGiocatore(String email, Giocatore g) {
        if (!giocatori.containsKey(email)) {
            giocatori.put(email, g);
        }
    }

    // Metodo per ottenere un giocatore dato l'email
    public Giocatore getGiocatore(String email) {
        return giocatori.get(email);
    }

    // Metodo per ottenere un mazzo dato il codice
    public Mazzo getMazzo(int codice) {
        return mazziTorneo.get(codice);
    }

    @Override
    public String toString() {
        return "Torneo{" +
                "codice=" + codice +
                ", nome='" + nome + '\'' +
                ", data=" + data +
                ", orario='" + orario + '\'' +
                ", luogo='" + luogo + '\'' +
                ", numero giocatori=" + giocatori.size() +
                ", numero mazzi=" + mazziTorneo.size() +
                ", formato=" + formato.getNome() +
                '}';
    }
}
