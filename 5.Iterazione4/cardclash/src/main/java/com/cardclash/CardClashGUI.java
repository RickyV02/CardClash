package com.cardclash;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

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

    private JPanel containerPanel;
    private CardLayout cardLayout;
    private LoginPanel loginPanel;
    private MainPanel mainPanel;
    private TesseramentoPanel tesseramentoPanel;
    private PersistenceHandler PersistenceHandler;
    private CardClash cardClash;

    public CardClashGUI() {
        super("CardClash");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        PersistenceHandler = new PersistenceHandler();
        cardClash = cardClash.getInstance();

        // Carica gli utenti o crea il file XML di default
        PersistenceHandler.loadUsers();

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        mainPanel = new MainPanel(this);
        tesseramentoPanel = new TesseramentoPanel(this);

        containerPanel.add(loginPanel, "LOGIN");
        containerPanel.add(mainPanel, "MAIN");
        containerPanel.add(tesseramentoPanel, "TESSERAMENTO");

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CardClashGUI::new);
    }

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
                String role = PersistenceHandler.authenticate(email, password);

                if (role != null) {
                    Giocatore giocatore = cardClash.getGiocatore(email);
                    if (giocatore != null) {
                        cardClash.setGiocatoreCorrente(giocatore);
                        parentFrame.showMainPanel(role);
                    } else {
                        JOptionPane.showMessageDialog(this, "Giocatore non trovato!", "Errore", JOptionPane.ERROR_MESSAGE);
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
            add(new JButton("UC3: Iscrizione Torneo"));
            add(new JButton("UC7: Visualizza Classifica"));

            eloLabel = new JLabel();
            add(eloLabel);
        }
    }

    private class OrganizerPanel extends JPanel {

        public OrganizerPanel() {
            setLayout(new GridLayout(4, 2, 10, 10));
            add(new JButton("UC1: Creazione Torneo"));
            add(new JButton("UC4: Inserimento Tipologia Mazzo"));
            add(new JButton("UC5: Creazione Tabellone"));
            add(new JButton("UC6: Gestisci Eliminazione"));
            add(new JButton("UC8: Aggiorna ELO"));
            add(new JButton("UC9: Aggiungi Formato Torneo"));
            add(new JButton("UC7: Visualizza Classifica"));
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
                    PersistenceHandler.registerUser("user", email, password);
                    JOptionPane.showMessageDialog(this, "Registrazione completata!", "Successo", JOptionPane.INFORMATION_MESSAGE);
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
}
