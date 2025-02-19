package com.cardclash;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.*;

public class TournamentCreationPanel extends JPanel {
    private JTextField nomeField, dataField, orarioField, luogoField;
    private JButton creaButton, backButton, confermaButton;
    private CardClashGUI parentFrame;

    public TournamentCreationPanel(CardClashGUI parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nome Torneo:"), gbc);
        gbc.gridx = 1;
        nomeField = new JTextField(20);
        add(nomeField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Data (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dataField = new JTextField(20);
        add(dataField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Orario (HH:mm):"), gbc);
        gbc.gridx = 1;
        orarioField = new JTextField(20);
        add(orarioField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Luogo:"), gbc);
        gbc.gridx = 1;
        luogoField = new JTextField(20);
        add(luogoField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        creaButton = new JButton("Crea Torneo");
        add(creaButton, gbc);
        gbc.gridx = 1;
        backButton = new JButton("Indietro");
        add(backButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        confermaButton = new JButton("Conferma Creazione");
        confermaButton.setVisible(false);
        add(confermaButton, gbc);
        creaButton.addActionListener(e -> creaTorneo());
        backButton.addActionListener(e -> parentFrame.cardLayout.show(parentFrame.containerPanel, "MAIN"));
        confermaButton.addActionListener(e -> confermaTorneo());
    }

    private void creaTorneo() {
        String nome = nomeField.getText().trim();
        String dataStr = dataField.getText().trim();
        String orarioStr = orarioField.getText().trim();
        String luogo = luogoField.getText().trim();
        if (nome.isEmpty() || dataStr.isEmpty() || orarioStr.isEmpty() || luogo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tutti i campi sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalDate data;
        LocalTime orario;
        try {
            data = LocalDate.parse(dataStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato data non valido! Usa yyyy-MM-dd", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            orario = LocalTime.parse(orarioStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato orario non valido! Usa HH:mm", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (data.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "La data del torneo non può essere nel passato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            parentFrame.cardClash.creaTorneo(nome, data, orarioStr, luogo);
            if (parentFrame.cardClash.getTorneoCorrente() == null) {
                JOptionPane.showMessageDialog(this, "Esiste già un torneo con la data selezionata.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore durante la creazione del torneo: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Map<Integer, FormatoTorneo> formati = parentFrame.cardClash.getFormati();
        if (formati == null || formati.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non sono disponibili formati per il torneo!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] formatoOptions = new String[formati.size()];
        int index = 0;
        for (Integer codice : formati.keySet()) {
            FormatoTorneo f = formati.get(codice);
            formatoOptions[index++] = codice + " - " + f.getNome() + " (" + f.getGioco() + ")";
        }
        String selectedFormato = (String) JOptionPane.showInputDialog(this, "Seleziona il formato del torneo:", "Selezione Formato", JOptionPane.PLAIN_MESSAGE, null, formatoOptions, formatoOptions[0]);
        if (selectedFormato == null) {
            JOptionPane.showMessageDialog(this, "Selezione del formato annullata!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] partsFormato = selectedFormato.split(" - ");
        int formatoCodice = Integer.parseInt(partsFormato[0].trim());
        parentFrame.cardClash.selezionaFormato(formatoCodice);
        if (parentFrame.cardClash.getTorneoCorrente().getFormato() == null) {
            JOptionPane.showMessageDialog(this, "Formato non trovato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        confermaButton.setVisible(true);
        JOptionPane.showMessageDialog(this, "Formato selezionato correttamente! Clicca 'Conferma Creazione' per completare.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confermaTorneo() {
        PersistenceHandler.saveTournament(parentFrame.cardClash.getTorneoCorrente());
        parentFrame.cardClash.confermaCreazione();
        JOptionPane.showMessageDialog(this, "Torneo creato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        nomeField.setText("");
        dataField.setText("");
        orarioField.setText("");
        luogoField.setText("");
        confermaButton.setVisible(false);
        parentFrame.cardLayout.show(parentFrame.containerPanel, "MAIN");
    }
}
