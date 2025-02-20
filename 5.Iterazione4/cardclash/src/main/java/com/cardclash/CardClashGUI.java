package com.cardclash;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CardClashGUI extends JFrame {

    JPanel containerPanel;
    CardLayout cardLayout;
    LoginPanel loginPanel;
    MainPanel mainPanel;
    TesseramentoPanel tesseramentoPanel;
    TournamentCreationPanel tournamentCreationPanel;
    CardClash cardClash;

    public CardClashGUI() {
        super("CardClash");
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(900, 600);
            setLocationRelativeTo(null);
            cardClash = CardClash.getInstance();
            System.out.println("Inizio il caricamento dei dati...");

            PersistenceHandler.loadFormats(cardClash);
            System.out.println("Formati caricati correttamente.");
            System.out.println("Formats: " + cardClash.getFormati().toString());

            PersistenceHandler.loadUsers(cardClash);
            System.out.println("Utenti caricati correttamente.");
            
            PersistenceHandler.loadTournaments(cardClash);
            System.out.println("Tornei caricati correttamente.");
            System.out.println("Tornei: " + cardClash.getTornei().toString());

            cardLayout = new CardLayout();
            containerPanel = new JPanel(cardLayout);
            loginPanel = new LoginPanel(this);
            mainPanel = new MainPanel(this);
            tesseramentoPanel = new TesseramentoPanel(this);
            tournamentCreationPanel = new TournamentCreationPanel(this);
            containerPanel.add(loginPanel, "LOGIN");
            containerPanel.add(mainPanel, "MAIN");
            containerPanel.add(tesseramentoPanel, "TESSERAMENTO");
            containerPanel.add(tournamentCreationPanel, "CREA_TORNEO");
            cardLayout.show(containerPanel, "LOGIN");
            add(containerPanel);
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMainPanel(String role) {
        mainPanel.setupRole(role);
        cardLayout.show(containerPanel, "MAIN");
    }

    public void showTesseramentoPanel() {
        cardLayout.show(containerPanel, "TESSERAMENTO");
    }

    public void showTournamentCreationPanel() {
        cardLayout.show(containerPanel, "CREA_TORNEO");
    }

    public static void main(String[] args) {
        System.out.println("Avvio dell'applicazione...");
        SwingUtilities.invokeLater(CardClashGUI::new);
    }
}
