package com.cardclash;

import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;

public class PlayerPanel extends JPanel {
    private JLabel eloLabel;
    private JButton iscrizioneTorneiButton;
    private CardClashGUI parentFrame;

    public PlayerPanel(CardClashGUI parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridLayout(4, 1, 10, 10));
        iscrizioneTorneiButton = new JButton("Iscrizione Tornei");
        iscrizioneTorneiButton.addActionListener(e -> iscrizioneTorneo());
        add(iscrizioneTorneiButton);
        eloLabel = new JLabel("ELO: ");
        add(eloLabel);
    }

    public void updateEloLabel(Float elo) {
        eloLabel.setText("ELO: " + elo);
    }

    private void iscrizioneTorneo() {
        Giocatore giocatore = parentFrame.cardClash.getGiocatoreCorrente();
        if (giocatore == null) {
            JOptionPane.showMessageDialog(this, "Nessun giocatore loggato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Torneo> torneiDisponibili = parentFrame.cardClash.mostraTorneiDisponibili();
        List<Torneo> torneiNonIscritti = new java.util.ArrayList<>();
        for (Torneo t : torneiDisponibili)
            if (!t.getGiocatori().containsKey(giocatore.getEmail()))
                torneiNonIscritti.add(t);
        if (torneiNonIscritti.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono tornei disponibili!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] tournamentOptions = new String[torneiNonIscritti.size()];
        for (int i = 0; i < torneiNonIscritti.size(); i++) {
            Torneo t = torneiNonIscritti.get(i);
            tournamentOptions[i] = t.getCodice() + " - " + t.getNome() + " (" + t.getData().toString() + ")";
        }
        String selectedTournament = (String) JOptionPane.showInputDialog(this, "Seleziona il torneo a cui iscriverti:", "Iscrizione Torneo", JOptionPane.PLAIN_MESSAGE, null, tournamentOptions, tournamentOptions[0]);
        if (selectedTournament == null)
            return;
        String[] parts = selectedTournament.split(" - ");
        int tournamentCode = Integer.parseInt(parts[0].trim());
        parentFrame.cardClash.selezionaTorneo(tournamentCode);
        Torneo torneoSelezionato = parentFrame.cardClash.getTorneoCorrente();
        JOptionPane.showMessageDialog(this, "Torneo selezionato: " + torneoSelezionato.getNome() + "\nData: " + torneoSelezionato.getData(), "Informazione", JOptionPane.INFORMATION_MESSAGE);
        String mazzoNome = JOptionPane.showInputDialog(this, "Inserisci il nome del mazzo:");
        if (mazzoNome == null || mazzoNome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il nome del mazzo è obbligatorio!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        parentFrame.cardClash.inserimentoMazzo(mazzoNome);
        java.util.Map<Integer, TipoMazzo> tipiMazzi = parentFrame.cardClash.getTipiMazziConsentiti();
        if (tipiMazzi == null || tipiMazzi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non sono disponibili tipi di mazzo per questo torneo!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] tipologiaOptions = new String[tipiMazzi.size()];
        int index = 0;
        for (Integer codice : tipiMazzi.keySet()) {
            String nomeTipologia = tipiMazzi.get(codice).getNome();
            tipologiaOptions[index++] = codice + " - " + nomeTipologia;
        }
        String selectedTipologia = (String) JOptionPane.showInputDialog(this, "Seleziona il tipo di mazzo:", "Selezione Tipologia", JOptionPane.PLAIN_MESSAGE, null, tipologiaOptions, tipologiaOptions[0]);
        if (selectedTipologia == null) {
            JOptionPane.showMessageDialog(this, "Selezione del tipo di mazzo annullata!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] partsTip = selectedTipologia.split(" - ");
        int tipologiaCodice = Integer.parseInt(partsTip[0].trim());
        parentFrame.cardClash.selezionaTipo(tipologiaCodice);
        int conferma = JOptionPane.showConfirmDialog(this, "Confermi l'iscrizione al torneo?", "Conferma Iscrizione", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            parentFrame.cardClash.confermaIscrizione();
            JOptionPane.showMessageDialog(this, "Iscrizione al torneo completata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Iscrizione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void updateElo(Float elo) {
        eloLabel.setText("ELO: " + elo);
    }
}
