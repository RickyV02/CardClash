package com.cardclash;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class OrganizerPanel extends JPanel {

    private final CardClashGUI parentFrame;

    public OrganizerPanel(CardClashGUI parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridLayout(4, 2, 10, 10));
        JButton creaTorneoButton = new JButton("Crea Torneo");
        creaTorneoButton.addActionListener(e -> parentFrame.showTournamentCreationPanel());
        add(creaTorneoButton);
        JButton inserimentoTipoButton = new JButton("Inserimento Tipo Mazzo");
        inserimentoTipoButton.addActionListener(e -> inserimentoTipoMazzo());
        add(inserimentoTipoButton);
        JButton generaTabelloneButton = new JButton("Genera Tabellone");
        generaTabelloneButton.addActionListener(e -> generaTabellone());
        add(generaTabelloneButton);
        JButton eliminaGiocatoriButton = new JButton("Elimina giocatori");
        eliminaGiocatoriButton.addActionListener(e -> eliminaGiocatori());
        add(eliminaGiocatoriButton);
        JButton visualizzaClassificaButton = new JButton("Visualizza Classifica");
        visualizzaClassificaButton.addActionListener(e -> visualizzaClassifica());
        add(visualizzaClassificaButton);
        JButton aggiornaELOButton = new JButton("Aggiorna ELO");
        aggiornaELOButton.addActionListener(e -> aggiornaELO());
        add(aggiornaELOButton);
        JButton aggiungiFormatoButton = new JButton("Aggiungi formato torneo");
        aggiungiFormatoButton.addActionListener(e -> aggiungiFormatoTorneo());
        add(aggiungiFormatoButton);
    }

    private void inserimentoTipoMazzo() {
        Map<Integer, FormatoTorneo> formati = parentFrame.cardClash.getFormati();
        if (formati == null || formati.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono formati disponibili!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] formatoOptions = new String[formati.size()];
        int index = 0;
        for (Map.Entry<Integer, FormatoTorneo> entry : formati.entrySet()) {
            FormatoTorneo formato = entry.getValue();
            formatoOptions[index++] = entry.getKey() + " - " + formato.getNome() + " (" + formato.getGioco() + ")";
        }
        String selectedFormato = (String) JOptionPane.showInputDialog(this, "Seleziona il formato in cui inserire la nuova tipologia di mazzo:", "Inserimento Tipo Mazzo", JOptionPane.PLAIN_MESSAGE, null, formatoOptions, formatoOptions[0]);
        if (selectedFormato == null) {
            JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int formatoCodice = Integer.parseInt(selectedFormato.split(" - ")[0].trim());
        parentFrame.cardClash.selezioneFormato(formatoCodice);
        String nomeTipologia = JOptionPane.showInputDialog(this, "Inserisci il nome della nuova tipologia di mazzo:");
        if (nomeTipologia == null || nomeTipologia.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome tipologia mancante.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            parentFrame.cardClash.inserimentoTipoMazzo(nomeTipologia);
            if (parentFrame.cardClash.getFormatoCorrente() == null) {
                JOptionPane.showMessageDialog(this, "Tipologia già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            parentFrame.cardClash.confermaInserimentoTipo();
            JOptionPane.showMessageDialog(this, "Nuova tipologia di mazzo inserita con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore nell'inserimento della tipologia: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generaTabellone() {
        Map<Integer, Torneo> tuttiTornei = parentFrame.cardClash.getTornei();
        if (tuttiTornei == null || tuttiTornei.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono tornei disponibili!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<Torneo> torneiFuturi = new ArrayList<>();
        for (Torneo t : tuttiTornei.values()) {
            if (!t.getData().isBefore(LocalDate.now())) {
                torneiFuturi.add(t);
            }
        }
        if (torneiFuturi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono tornei futuri disponibili!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] torneoOptions = new String[torneiFuturi.size()];
        for (int i = 0; i < torneiFuturi.size(); i++) {
            Torneo tor = torneiFuturi.get(i);
            torneoOptions[i] = tor.getCodice() + " - " + tor.getNome() + " (" + tor.getData() + ")";
        }
        String selectedTorneo = (String) JOptionPane.showInputDialog(this, "Seleziona il torneo per generare il tabellone:", "Genera Tabellone", JOptionPane.PLAIN_MESSAGE, null, torneoOptions, torneoOptions[0]);
        if (selectedTorneo == null) {
            JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int torneoCode = Integer.parseInt(selectedTorneo.split(" - ")[0].trim());
        Tabellone tabellone = parentFrame.cardClash.creaTabellone(torneoCode);
        if (tabellone == null) {
            JOptionPane.showMessageDialog(this, "Impossibile generare il tabellone per il torneo selezionato. Verifica che il numero di giocatori sia corretto.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostraTabellone(tabellone, "Tabellone Generato", torneoCode);
        int conferma = JOptionPane.showConfirmDialog(this, "Confermi il tabellone?", "Conferma Tabellone", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            parentFrame.cardClash.confermaTabellone();
            JOptionPane.showMessageDialog(this, "Tabellone confermato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Tabellone non confermato.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void eliminaGiocatori() {
        Map<Integer, Torneo> tuttiTornei = parentFrame.cardClash.getTornei();
        if (tuttiTornei == null || tuttiTornei.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono tornei disponibili!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] torneoOptions = new String[tuttiTornei.size()];
        int idx = 0;
        for (Torneo t : tuttiTornei.values()) {
            torneoOptions[idx++] = t.getCodice() + " - " + t.getNome() + " (" + t.getData() + ")";
        }
        String selectedTorneo = (String) JOptionPane.showInputDialog(this, "Seleziona il torneo in cui eliminare giocatori:", "Elimina giocatori", JOptionPane.PLAIN_MESSAGE, null, torneoOptions, torneoOptions[0]);
        if (selectedTorneo == null) {
            JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int torneoCode = Integer.parseInt(selectedTorneo.split(" - ")[0].trim());
        Tabellone tabellone = parentFrame.cardClash.visualizzaTabellone(torneoCode);
        if (tabellone == null) {
            JOptionPane.showMessageDialog(this, "Impossibile visualizzare il tabellone per questo torneo.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Giocatore> listaGiocatori = parentFrame.cardClash.getTorneoCorrente().getTabelloneCorrente().getGiocatori();
        Map<String, Giocatore> giocatoriIniziali = new HashMap<>();
        for (Giocatore g : listaGiocatori) {
            giocatoriIniziali.put(g.getEmail(), g);
        }
        int finalSize = parentFrame.cardClash.getTorneoCorrente().getTabelloneCorrente().getGiocatori().size();
        if (finalSize == 1) {
            JOptionPane.showMessageDialog(this, "Il torneo è già concluso! Impossibile eliminare altri giocatori.", "Errore!", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        mostraTabellone(tabellone, "Tabellone Iniziale", torneoCode);
        while (finalSize != 1) {
            int choice = JOptionPane.showConfirmDialog(this, "Vuoi rimuovere un giocatore?", "Elimina giocatore", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                break;
            }
            String emailToRemove = JOptionPane.showInputDialog(this, "Inserisci l'email del giocatore da eliminare:");
            if (emailToRemove == null || emailToRemove.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nessuna email inserita, annullo l'operazione per questo giocatore.", "Errore", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            boolean giocatorePresente = tabellone.getPartite().values().stream()
                    .anyMatch(partita -> (partita.getGiocatore1() != null && partita.getGiocatore1().getEmail().equals(emailToRemove))
                    || (partita.getGiocatore2() != null && partita.getGiocatore2().getEmail().equals(emailToRemove)));
            if (!giocatorePresente) {
                JOptionPane.showMessageDialog(this, "Il giocatore con l'email inserita non è presente nel tabellone.", "Errore", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            parentFrame.cardClash.eliminaGiocatore(emailToRemove);
            finalSize--;
        }
        int initialSize = giocatoriIniziali.size();
        if (initialSize == finalSize) {
            JOptionPane.showMessageDialog(this, "Nessun giocatore è stato eliminato. L'operazione è annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int numGiocatori = parentFrame.cardClash.getTorneoCorrente().getTabelloneCorrente().getGiocatori().size();
        if (!isPotenzaDiDue(numGiocatori)) {
            parentFrame.cardClash.getTorneoCorrente().setGiocatori(new HashMap<>(giocatoriIniziali));
            JOptionPane.showMessageDialog(this, "Errore! Il numero di giocatori rimasti (" + numGiocatori + ") non è una potenza di due.\nL'eliminazione è stata annullata e i giocatori originali sono stati ripristinati.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (finalSize == 1) {
            Giocatore vincitore = parentFrame.cardClash.getTorneoCorrente().getTabelloneCorrente().getGiocatori().iterator().next();
            JOptionPane.showMessageDialog(this, "Il torneo è concluso! Il vincitore è: " + vincitore.getNickname(), "Vincitore", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.cardClash.aggiornaPunteggio();
            return;
        }
        parentFrame.cardClash.aggiornaTabellone();
        parentFrame.cardClash.aggiornaPunteggio();
        Tabellone newTabellone = parentFrame.cardClash.getTorneoCorrente().getTabellone();
        mostraTabellone(newTabellone, "Tabellone Aggiornato", torneoCode);
    }

    private void visualizzaClassifica() {
        Map<Integer, Torneo> tuttiTornei = parentFrame.cardClash.getTornei();
        if (tuttiTornei == null || tuttiTornei.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono tornei disponibili!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] torneoOptions = new String[tuttiTornei.size()];
        int idx = 0;
        for (Torneo t : tuttiTornei.values()) {
            torneoOptions[idx++] = t.getCodice() + " - " + t.getNome() + " (" + t.getData() + ")";
        }
        String selectedTorneo = (String) JOptionPane.showInputDialog(this, "Seleziona il torneo per visualizzare la classifica:", "Visualizza Classifica", JOptionPane.PLAIN_MESSAGE, null, torneoOptions, torneoOptions[0]);
        if (selectedTorneo == null) {
            JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int torneoCode = Integer.parseInt(selectedTorneo.split(" - ")[0].trim());
        List<Giocatore> classifica = parentFrame.cardClash.visualizzaClassifica(torneoCode);
        if (classifica == null || classifica.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La classifica è vuota.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object[][] data = new Object[classifica.size()][4];
        for (int i = 0; i < classifica.size(); i++) {
            Giocatore g = classifica.get(i);
            data[i][0] = i + 1;
            data[i][1] = g.getEmail();
            data[i][2] = g.getNickname();
            data[i][3] = g.getPunteggio(torneoCode);
        }
        String[] columnNames = {"Posizione", "Email", "Nickname", "Punteggio"};
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, "Classifica", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aggiornaELO() {
        Map<Integer, Torneo> torneiDaConcludere = parentFrame.cardClash.getTorneiDaConcludere();
        if (torneiDaConcludere == null || torneiDaConcludere.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono tornei da concludere!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] torneoOptions = new String[torneiDaConcludere.size()];
        int idx = 0;
        for (Torneo torneo : torneiDaConcludere.values()) {
            torneoOptions[idx++] = torneo.getCodice() + " - " + torneo.getNome() + " (" + torneo.getData() + ")";
        }
        String selectedTorneo = (String) JOptionPane.showInputDialog(this, "Seleziona il torneo da concludere:", "Aggiorna ELO", JOptionPane.PLAIN_MESSAGE, null, torneoOptions, torneoOptions[0]);
        if (selectedTorneo == null) {
            JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        //Estensioni 8.a e 8.b implementate
        int torneoCode = Integer.parseInt(selectedTorneo.split(" - ")[0].trim());
        if (parentFrame.cardClash.getTornei().get(torneoCode).getTabellone() == null) {
            JOptionPane.showMessageDialog(this, "Il torneo non è ancora iniziato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (parentFrame.cardClash.getTornei().get(torneoCode).getTabellone().getGiocatori().size() != 1) {
            JOptionPane.showMessageDialog(this, "Il torneo non è ancora finito!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        parentFrame.cardClash.aggiornaELO(torneoCode);
        List<Giocatore> giocatori = parentFrame.cardClash.getTornei().get(torneoCode).getTabellone().getGiocatori();
        for (Giocatore giocatore : giocatori) {
            PersistenceHandler.updateUserElo(giocatore.getEmail(), giocatore.getELO());
        }
        parentFrame.cardClash.setVincitore();
        JOptionPane.showMessageDialog(this, "Le informazioni sono state aggiornate correttamente!", "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aggiungiFormatoTorneo() {
        String codiceStr = JOptionPane.showInputDialog(this, "Inserisci il codice univoco per il nuovo formato:");
        if (codiceStr == null || codiceStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Codice univoco mancante!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer codice;
        try {
            codice = Integer.valueOf(codiceStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Codice univoco non valido!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nome = JOptionPane.showInputDialog(this, "Inserisci il nome del nuovo formato:");
        if (nome == null || nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome mancante!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String gioco = JOptionPane.showInputDialog(this, "Inserisci il nome del gioco di carte associato:");
        if (gioco == null || gioco.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome del gioco mancante!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String numMaxStr = JOptionPane.showInputDialog(this, "Inserisci il numero massimo di partecipanti:");
        int numMax;
        try {
            numMax = Integer.parseInt(numMaxStr.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Numero massimo di partecipanti non valido!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String victoryScoreStr = JOptionPane.showInputDialog(this, "Inserisci il punteggio di vittoria:");
        Float victoryScore;
        try {
            victoryScore = Float.valueOf(victoryScoreStr.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Punteggio di vittoria non valido!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String penaltyScoreStr = JOptionPane.showInputDialog(this, "Inserisci il punteggio di penalità:");
        Float penaltyScore;
        try {
            penaltyScore = Float.valueOf(penaltyScoreStr.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Punteggio di penalità non valido!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FormatoTorneo nuovoFormato;
        try {
            nuovoFormato = parentFrame.cardClash.creaNuovoFormato(codice, nome, gioco, numMax, victoryScore, penaltyScore);
            if (nuovoFormato == null) {
                JOptionPane.showMessageDialog(this, "Errore nella creazione del nuovo formato!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (GiocoNonSupportatoException ex) {
            JOptionPane.showMessageDialog(this, "Il gioco specificato non è supportato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String info = "Codice: " + nuovoFormato.getCodice() + "\n"
                + "Nome: " + nuovoFormato.getNome() + "\n"
                + "Gioco: " + nuovoFormato.getGioco() + "\n"
                + "Numero massimo di partecipanti: " + nuovoFormato.getNumMaxGiocatori() + "\n"
                + "Punteggio di vittoria: " + nuovoFormato.getVictoryScore() + "\n"
                + "Punteggio di penalità: " + nuovoFormato.getPenaltyScore() + "\n";
        int conferma = JOptionPane.showConfirmDialog(this, info + "\nConfermi l'inserimento del nuovo formato?", "Conferma Nuovo Formato", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            parentFrame.cardClash.confermaFormato();
            PersistenceHandler.saveFormat(nuovoFormato);
            JOptionPane.showMessageDialog(this, "Nuovo formato aggiunto con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostraTabellone(Tabellone tabellone, String title, Integer codTorneo) {
        String[] columnNames = {"codPartita", "Giocatore1", "Punti G1", "Giocatore2", "Punti G2"};
        Object[][] data = new Object[tabellone.getPartite().size()][5];
        int row = 0;
        for (Map.Entry<Integer, Partita> entry : tabellone.getPartite().entrySet()) {
            Partita partita = entry.getValue();
            Giocatore g1 = partita.getGiocatore1();
            Giocatore g2 = partita.getGiocatore2();
            data[row][0] = entry.getKey();
            data[row][1] = (g1 != null) ? g1.getEmail() : "N/A";
            data[row][2] = (g1 != null) ? g1.getPunteggio(codTorneo) : "N/A";
            data[row][3] = (g2 != null) ? g2.getEmail() : "N/A";
            data[row][4] = (g2 != null) ? g2.getPunteggio(codTorneo) : "N/A";
            row++;
        }
        JTable table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Integer tabCode = tabellone.getCodice();
        String codiceTabellone = (tabCode != null) ? tabCode.toString() : "non confermato";
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Tabellone (" + codiceTabellone + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isPotenzaDiDue(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }
}
