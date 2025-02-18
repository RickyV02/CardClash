package com.cardclash;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CardClashGUI extends JFrame {

    JPanel containerPanel;
    CardLayout cardLayout;
    LoginPanel loginPanel;
    MainPanel mainPanel;
    TesseramentoPanel tesseramentoPanel;
    TournamentCreationPanel tournamentCreationPanel; // Nuovo pannello per la creazione del torneo
    PersistenceHandler persistenceHandler;
    CardClash cardClash;

    public CardClashGUI() {
        super("CardClash");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        persistenceHandler = new PersistenceHandler();
        cardClash = CardClash.getInstance();

        // Carica gli utenti o crea il file XML di default
        persistenceHandler.loadUsers(cardClash);
        persistenceHandler.loadTournaments(cardClash);

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        mainPanel = new MainPanel(this);
        tesseramentoPanel = new TesseramentoPanel(this);
        tournamentCreationPanel = new TournamentCreationPanel(); // inizializza il pannello per il torneo

        containerPanel.add(loginPanel, "LOGIN");
        containerPanel.add(mainPanel, "MAIN");
        containerPanel.add(tesseramentoPanel, "TESSERAMENTO");
        containerPanel.add(tournamentCreationPanel, "CREA_TORNEO");

        cardLayout.show(containerPanel, "LOGIN");
        add(containerPanel);
        setVisible(true);
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
        SwingUtilities.invokeLater(CardClashGUI::new);
    }

    // ======================= Inner Classes =======================

    private class LoginPanel extends JPanel {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private JButton tesseramentoButton;
        private CardClashGUI parentFrame;

        public LoginPanel(CardClashGUI parentFrame) {
            this.parentFrame = parentFrame;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel emailLabel = new JLabel("Email:");
            JLabel passLabel = new JLabel("Password:");

            emailField = new JTextField(15);
            passwordField = new JPasswordField(15);
            loginButton = new JButton("Login");
            tesseramentoButton = new JButton("Tesseramento");

            loginButton.addActionListener(e -> {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String role = persistenceHandler.authenticate(email, password);

                if (role != null) {
                    Giocatore giocatore = cardClash.getGiocatore(email);
                    if (giocatore != null) {
                        mainPanel.updateEloLabel(giocatore.getELO());
                        parentFrame.showMainPanel(role);
                    } else {
                        JOptionPane.showMessageDialog(this, "Giocatore non trovato!", "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Credenziali errate!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            });

            tesseramentoButton.addActionListener(e -> parentFrame.showTesseramentoPanel());

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(emailLabel, gbc);
            gbc.gridx = 1;
            add(emailField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(passLabel, gbc);
            gbc.gridx = 1;
            add(passwordField, gbc);

            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 2;
            add(loginButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            add(tesseramentoButton, gbc);
        }
    }

    private class MainPanel extends JPanel {
        private JTabbedPane tabbedPane;
        private PlayerPanel playerPanel;
        private OrganizerPanel organizerPanel;

        public MainPanel(CardClashGUI parentFrame) {
            initComponents();
        }

        public void updateEloLabel(Float elo) {
            playerPanel.updateElo(elo);
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenuItem logoutItem = new JMenuItem("Logout");
            JMenuItem exitItem = new JMenuItem("Esci");

            logoutItem.addActionListener(e -> cardLayout.show(containerPanel, "LOGIN"));
            exitItem.addActionListener(e -> System.exit(0));

            fileMenu.add(logoutItem);
            fileMenu.add(exitItem);
            menuBar.add(fileMenu);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(menuBar, BorderLayout.NORTH);

            tabbedPane = new JTabbedPane();
            playerPanel = new PlayerPanel();
            organizerPanel = new OrganizerPanel();

            tabbedPane.addTab("Giocatore", playerPanel);
            tabbedPane.addTab("Organizzatore", organizerPanel);

            add(topPanel, BorderLayout.NORTH);
            add(tabbedPane, BorderLayout.CENTER);
        }

        public void setupRole(String role) {
            if ("user".equalsIgnoreCase(role)) {
                tabbedPane.setEnabledAt(0, true);
                tabbedPane.setEnabledAt(1, false);
                tabbedPane.setSelectedIndex(0);
            } else {
                tabbedPane.setEnabledAt(0, false);
                tabbedPane.setEnabledAt(1, true);
                tabbedPane.setSelectedIndex(1);
            }
        }
    }

    private class PlayerPanel extends JPanel {
        private JLabel eloLabel;

        public PlayerPanel() {
            setLayout(new GridLayout(4, 1, 10, 10));
            add(new JButton("Iscrizione Tornei"));

            eloLabel = new JLabel();
            add(eloLabel);
        }

        public void updateElo(Float elo) {
            eloLabel.setText("ELO: " + elo);
        }
    }

    private class OrganizerPanel extends JPanel {
        public OrganizerPanel() {
            setLayout(new GridLayout(4, 2, 10, 10));

            JButton creaTorneoButton = new JButton("Crea Torneo");
            creaTorneoButton.addActionListener(e -> {
                // Mostra il pannello per la creazione del torneo
                cardLayout.show(containerPanel, "CREA_TORNEO");
            });
            add(creaTorneoButton);

            add(new JButton("Inserimento Tipo Mazzo"));
            add(new JButton("Genera Tabellone"));
            add(new JButton("Elimina giocatori"));
            add(new JButton("Visualizza Classifica"));
            add(new JButton("Aggiorna ELO"));
            add(new JButton("Aggiungi formato torneo"));
        }
    }

    private class TesseramentoPanel extends JPanel {
        private JTextField nomeField;
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nicknameField;
        private JButton registraButton;
        private JButton backButton;
        private CardClashGUI parentFrame;

        public TesseramentoPanel(CardClashGUI parentFrame) {
            this.parentFrame = parentFrame;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel nomeLabel = new JLabel("Nome:");
            JLabel emailLabel = new JLabel("Email:");
            JLabel passwordLabel = new JLabel("Password:");
            JLabel nicknameLabel = new JLabel("Nickname:");

            nomeField = new JTextField(15);
            emailField = new JTextField(15);
            passwordField = new JPasswordField(15);
            nicknameField = new JTextField(15);
            registraButton = new JButton("Registrati");
            backButton = new JButton("Indietro");

            registraButton.addActionListener(e -> {
                String nome = nomeField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String nickname = nicknameField.getText();

                try {
                    cardClash.registraGiocatore(nome, email, password, nickname);
                    cardClash.confermaRegistrazione();
                    persistenceHandler.registerUser("user", nome, email, password, nickname);
                    JOptionPane.showMessageDialog(this, "Registrazione completata!", "Successo",
                            JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.cardLayout.show(parentFrame.containerPanel, "LOGIN");
                } catch (GiocatoreGiaRegistratoException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            });

            backButton.addActionListener(e -> parentFrame.cardLayout.show(parentFrame.containerPanel, "LOGIN"));

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(nomeLabel, gbc);
            gbc.gridx = 1;
            add(nomeField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(emailLabel, gbc);
            gbc.gridx = 1;
            add(emailField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            add(passwordLabel, gbc);
            gbc.gridx = 1;
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            add(nicknameLabel, gbc);
            gbc.gridx = 1;
            add(nicknameField, gbc);

            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 4;
            add(registraButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            add(backButton, gbc);
        }
    }

    // ======================= Pannello per la Creazione del Torneo
    // =======================

    private class TournamentCreationPanel extends JPanel {
        private JTextField nomeField;
        private JTextField dataField;
        private JTextField orarioField;
        private JTextField luogoField;
        // Campo formato rimosso: verrà richiesto tramite popup
        private JButton creaButton;
        private JButton backButton;
        private JButton confermaButton; // Bottone aggiuntivo per confermare la creazione

        public TournamentCreationPanel() {
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
            confermaButton.setVisible(false); // inizialmente nascosto
            add(confermaButton, gbc);

            creaButton.addActionListener(e -> creaTorneo());
            backButton.addActionListener(e -> cardLayout.show(containerPanel, "MAIN"));
            confermaButton.addActionListener(e -> confermaTorneo());
        }

        private void creaTorneo() {
            String nome = nomeField.getText().trim();
            String dataStr = dataField.getText().trim();
            String orarioStr = orarioField.getText().trim();
            String luogo = luogoField.getText().trim();

            if (nome.isEmpty() || dataStr.isEmpty() || orarioStr.isEmpty() || luogo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tutti i campi sono obbligatori!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate data;
            LocalTime orario;
            try {
                data = LocalDate.parse(dataStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato data non valido! Usa yyyy-MM-dd", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                orario = LocalTime.parse(orarioStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato orario non valido! Usa HH:mm", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (data.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La data del torneo non può essere nel passato!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                cardClash.creaTorneo(nome, data, orarioStr, luogo);
                if (cardClash.getTorneoCorrente() == null) {
                    JOptionPane.showMessageDialog(this, "Errore durante la creazione del torneo",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (DataGiaPresenteException dpe) {
                JOptionPane.showMessageDialog(this, dpe.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Errore durante la creazione del torneo: " + exception.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Popup per inserire il codice del formato
            String formatoInput = JOptionPane.showInputDialog(this, "Inserisci il codice del formato:");
            if (formatoInput == null || formatoInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Il formato deve essere inserito!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int formatoCodice = Integer.parseInt(formatoInput.trim());
                cardClash.selezionaFormato(formatoCodice);
                if (cardClash.getTorneoCorrente().getFormato() == null) {
                    JOptionPane.showMessageDialog(this, "Formato non trovato!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Il formato deve essere un codice numerico!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ora mostra il bottone aggiuntivo per confermare la creazione
            confermaButton.setVisible(true);
            JOptionPane.showMessageDialog(this,
                    "Formato selezionato correttamente! Clicca 'Conferma Creazione' per completare.", "Informazione",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        private void confermaTorneo() {
            PersistenceHandler.saveTournament(cardClash.getTorneoCorrente());
            cardClash.confermaCreazione();
            JOptionPane.showMessageDialog(this, "Torneo creato con successo!", "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
            // Ripulisco i campi e nascondo il bottone di conferma
            nomeField.setText("");
            dataField.setText("");
            orarioField.setText("");
            luogoField.setText("");
            confermaButton.setVisible(false);
            cardLayout.show(containerPanel, "MAIN");
        }
    }

}