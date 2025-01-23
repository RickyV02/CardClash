package com.cardclash;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
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

    public Torneo(String nome, LocalDate data, LocalTime orario, String luogo) {
        this.nome = nome;
        this.data = data;
        this.orario = orario;
        this.luogo = luogo;
        this.giocatori = new HashMap<>();
        this.mazziTorneo = new HashMap<>();
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

    public boolean isAperto() {
        // per il momento controlliamo solo la data
        LocalDate oggi = LocalDate.now();
        return data.isAfter(oggi);
    }

    // Metodo per aggiungere un mazzo (con codice come chiave)
    public void aggiungiMazzo(Integer codice, Mazzo m) {
        mazziTorneo.put(codice, m);
    }

    // Metodo per aggiungere un giocatore (con email come chiave)
    public void aggiungiGiocatore(String email, Giocatore g) {
        giocatori.put(email, g);
    }

    // Metodo per ottenere un giocatore dato l'email
    public Giocatore getGiocatore(String email) {
        return giocatori.get(email);
    }

    public Map<String, Giocatore> getGiocatori() {
        return giocatori;
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
