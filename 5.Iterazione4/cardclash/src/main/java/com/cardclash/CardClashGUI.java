package com.cardclash;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CardClashGUI extends JFrame {

    JPanel containerPanel;
    CardLayout cardLayout;
    LoginPanel loginPanel;
    MainPanel mainPanel;
    TesseramentoPanel tesseramentoPanel;
    TournamentCreationPanel tournamentCreationPanel; // Pannello per la creazione del torneo
    PersistenceHandler persistenceHandler;
    CardClash cardClash;

    public CardClashGUI() {
        super("CardClash");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        persistenceHandler = new PersistenceHandler();
        cardClash = CardClash.getInstance();

        // Carica gli utenti e i tornei dal file XML
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
                        cardClash.setGiocatoreCorrente(giocatore);
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

    // Pannello del giocatore con il bottone per l'iscrizione al torneo (UC3)
    private class PlayerPanel extends JPanel {

        private JLabel eloLabel;
        private JButton iscrizioneTorneiButton;

        public PlayerPanel() {
            setLayout(new GridLayout(4, 1, 10, 10));
            iscrizioneTorneiButton = new JButton("Iscrizione Tornei");
            iscrizioneTorneiButton.addActionListener(e -> iscrizioneTorneo());
            add(iscrizioneTorneiButton);
            eloLabel = new JLabel("ELO: ");
            add(eloLabel);
        }

        public void updateElo(Float elo) {
            eloLabel.setText("ELO: " + elo);
        }

        private void iscrizioneTorneo() {
            // Recupera il giocatore loggato
            Giocatore giocatore = cardClash.getGiocatoreCorrente();
            if (giocatore == null) {
                JOptionPane.showMessageDialog(this, "Nessun giocatore loggato!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1. Ottieni i tornei disponibili
            List<Torneo> torneiDisponibili = cardClash.mostraTorneiDisponibili();

            // Rimuovi i tornei ai quali il giocatore è già iscritto
            List<Torneo> torneiNonIscritti = new java.util.ArrayList<>();
            for (Torneo t : torneiDisponibili) {
                // Supponiamo che getGiocatori() restituisca una mappa con le email dei partecipanti
                if (!t.getGiocatori().containsKey(giocatore.getEmail())) {
                    torneiNonIscritti.add(t);
                }
            }

            if (torneiNonIscritti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non ci sono tornei disponibili a cui non sei già iscritto!",
                        "Informazione", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Crea un array di opzioni (mostrando codice, nome e data) per i tornei non iscritti
            String[] tournamentOptions = new String[torneiNonIscritti.size()];
            for (int i = 0; i < torneiNonIscritti.size(); i++) {
                Torneo t = torneiNonIscritti.get(i);
                tournamentOptions[i] = t.getCodice() + " - " + t.getNome() + " (" + t.getData().toString() + ")";
            }

            String selectedTournament = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleziona il torneo a cui iscriverti:",
                    "Iscrizione Torneo",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    tournamentOptions,
                    tournamentOptions[0]
            );
            if (selectedTournament == null) {
                return; // L'utente ha annullato
            }

            // Estrai il codice del torneo
            String[] parts = selectedTournament.split(" - ");
            int tournamentCode = Integer.parseInt(parts[0].trim());

            // 2. Seleziona il torneo
            cardClash.selezionaTorneo(tournamentCode);
            Torneo torneoSelezionato = cardClash.getTorneoCorrente();
            JOptionPane.showMessageDialog(this, "Torneo selezionato: " + torneoSelezionato.getNome()
                    + "\nData: " + torneoSelezionato.getData(), "Informazione", JOptionPane.INFORMATION_MESSAGE);

            // 3. Richiedi il nome del mazzo
            String mazzoNome = JOptionPane.showInputDialog(this, "Inserisci il nome del mazzo:");
            if (mazzoNome == null || mazzoNome.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Il nome del mazzo è obbligatorio!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            cardClash.inserimentoMazzo(mazzoNome);

            // 4. Seleziona la tipologia del mazzo tra quelle disponibili
            Map<Integer, TipoMazzo> tipiMazzi = cardClash.getTipiMazziConsentiti();
            if (tipiMazzi == null || tipiMazzi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non sono disponibili tipi di mazzo per questo torneo!",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] tipologiaOptions = new String[tipiMazzi.size()];
            int index = 0;
            for (Integer codice : tipiMazzi.keySet()) {
                String nomeTipologia = tipiMazzi.get(codice).getNome();
                tipologiaOptions[index++] = codice + " - " + nomeTipologia;
            }
            String selectedTipologia = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleziona il tipo di mazzo:",
                    "Selezione Tipologia",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    tipologiaOptions,
                    tipologiaOptions[0]
            );
            if (selectedTipologia == null) {
                JOptionPane.showMessageDialog(this, "Selezione del tipo di mazzo annullata!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] partsTip = selectedTipologia.split(" - ");
            int tipologiaCodice = Integer.parseInt(partsTip[0].trim());
            cardClash.selezionaTipo(tipologiaCodice);

            // 5. Conferma l'iscrizione
            int conferma = JOptionPane.showConfirmDialog(this, "Confermi l'iscrizione al torneo?",
                    "Conferma Iscrizione", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                cardClash.confermaIscrizione();
                JOptionPane.showMessageDialog(this, "Iscrizione al torneo completata con successo!",
                        "Successo", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Elenco giocatori: " + torneoSelezionato.getGiocatori().toString());
            } else {
                JOptionPane.showMessageDialog(this, "Iscrizione annullata.", "Informazione",
                        JOptionPane.INFORMATION_MESSAGE);
            }
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

            // Bottone per UC4: Inserimento Tipo Mazzo
            JButton inserimentoTipoButton = new JButton("Inserimento Tipo Mazzo");
            inserimentoTipoButton.addActionListener(e -> inserimentoTipoMazzo());
            add(inserimentoTipoButton);

            // UC5: Genera Tabellone
            JButton generaTabelloneButton = new JButton("Genera Tabellone");
            generaTabelloneButton.addActionListener(e -> generaTabellone());
            add(generaTabelloneButton);

            // UC6: Elimina giocatori
            JButton eliminaGiocatoriButton = new JButton("Elimina giocatori");
            eliminaGiocatoriButton.addActionListener(e -> eliminaGiocatori());
            add(eliminaGiocatoriButton);

            add(new JButton("Visualizza Classifica"));
            add(new JButton("Aggiorna ELO"));
            add(new JButton("Aggiungi formato torneo"));
        }

        /**
         * UC4: Inserimento tipologia mazzo
         */
        private void inserimentoTipoMazzo() {
            // 1. Ottieni i formati disponibili
            Map<Integer, FormatoTorneo> formati = cardClash.getFormati();
            if (formati == null || formati.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non ci sono formati disponibili!",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crea un array di opzioni (codice - nome formato (gioco))
            String[] formatoOptions = new String[formati.size()];
            int index = 0;
            for (Map.Entry<Integer, FormatoTorneo> entry : formati.entrySet()) {
                FormatoTorneo formato = entry.getValue();
                // Aggiungiamo anche il gioco tra parentesi
                formatoOptions[index++] = entry.getKey() + " - "
                        + formato.getNome() + " (" + formato.getGioco() + ")";
            }

            // Popup per selezionare il formato
            String selectedFormato = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleziona il formato in cui inserire la nuova tipologia di mazzo:",
                    "Inserimento Tipo Mazzo",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    formatoOptions,
                    formatoOptions[0]
            );
            if (selectedFormato == null) {
                JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Estrai il codice dal risultato (prima parte, prima del ' - ')
            int formatoCodice = Integer.parseInt(selectedFormato.split(" - ")[0].trim());

            // 2. Seleziona il formato
            cardClash.selezioneFormato(formatoCodice);

            // 3. Inserisci il nome della nuova tipologia di mazzo
            String nomeTipologia = JOptionPane.showInputDialog(this, "Inserisci il nome della nuova tipologia di mazzo:");
            if (nomeTipologia == null || nomeTipologia.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome tipologia mancante.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Validazione e salvataggio
            try {
                cardClash.inserimentoTipoMazzo(nomeTipologia);
                if (cardClash.getFormatoCorrente() == null) {
                    JOptionPane.showMessageDialog(this, "Tipologia già esistente",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                cardClash.confermaInserimentoTipo();
                JOptionPane.showMessageDialog(this, "Nuova tipologia di mazzo inserita con successo!",
                        "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Estensione 4a: dati incompleti o non validi
                JOptionPane.showMessageDialog(this, "Errore nell'inserimento della tipologia: " + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void generaTabellone() {
            // 1. Ottieni tutti i tornei
            Map<Integer, Torneo> tuttiTornei = cardClash.getTornei();
            if (tuttiTornei == null || tuttiTornei.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non ci sono tornei disponibili!", "Informazione", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2. Filtra i tornei con data da oggi in poi
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

            // 3. Mostra i tornei filtrati in un popup a tendina
            String[] torneoOptions = new String[torneiFuturi.size()];
            for (int i = 0; i < torneiFuturi.size(); i++) {
                Torneo tor = torneiFuturi.get(i);
                torneoOptions[i] = tor.getCodice() + " - " + tor.getNome() + " (" + tor.getData() + ")";
            }

            String selectedTorneo = (String) JOptionPane.showInputDialog(
                    this, "Seleziona il torneo per generare il tabellone:", "Genera Tabellone",
                    JOptionPane.PLAIN_MESSAGE, null, torneoOptions, torneoOptions[0]
            );

            if (selectedTorneo == null) {
                JOptionPane.showMessageDialog(this, "Operazione annullata.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Estrai il codice del torneo
            String[] parts = selectedTorneo.split(" - ");
            int torneoCode = Integer.parseInt(parts[0].trim());

            // 4. Genera il tabellone
            Tabellone tabellone = cardClash.creaTabellone(torneoCode);
            if (tabellone == null) {
                JOptionPane.showMessageDialog(this, "Impossibile generare il tabellone per il torneo selezionato. "
                        + "Verifica che il numero di giocatori sia corretto.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Mostra il tabellone con il metodo riutilizzabile
            mostraTabellone(tabellone, "Tabellone Generato", torneoCode);

            // 6. Conferma il tabellone
            int conferma = JOptionPane.showConfirmDialog(
                    this, "Confermi il tabellone?", "Conferma Tabellone", JOptionPane.YES_NO_OPTION
            );
            if (conferma == JOptionPane.YES_OPTION) {
                cardClash.confermaTabellone();
                JOptionPane.showMessageDialog(this, "Tabellone confermato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Tabellone non confermato.", "Informazione", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void eliminaGiocatori() {
            // 1. Seleziona un torneo
            Map<Integer, Torneo> tuttiTornei = cardClash.getTornei();
            if (tuttiTornei == null || tuttiTornei.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Non ci sono tornei disponibili!",
                        "Informazione",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Crea un array di opzioni (codice - nome - data)
            String[] torneoOptions = new String[tuttiTornei.size()];
            int idx = 0;
            for (Torneo t : tuttiTornei.values()) {
                torneoOptions[idx++] = t.getCodice() + " - " + t.getNome() + " (" + t.getData() + ")";
            }

            String selectedTorneo = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleziona il torneo in cui eliminare giocatori:",
                    "Elimina giocatori",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    torneoOptions,
                    torneoOptions[0]
            );
            if (selectedTorneo == null) {
                JOptionPane.showMessageDialog(this,
                        "Operazione annullata.",
                        "Informazione",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Estrai il codice
            int torneoCode = Integer.parseInt(selectedTorneo.split(" - ")[0].trim());

            // 2. Recupera il tabellone e lo stato iniziale dei giocatori
            Tabellone tabellone = cardClash.visualizzaTabellone(torneoCode);
            if (tabellone == null) {
                JOptionPane.showMessageDialog(this,
                        "Impossibile visualizzare il tabellone per questo torneo.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Salviamo i giocatori iniziali in una HashMap per poterli ripristinare in caso di errore
            Map<String, Giocatore> giocatoriIniziali = new HashMap<>();
            for (Giocatore g : cardClash.getTorneoCorrente().getGiocatori().values()) {
                giocatoriIniziali.put(g.getEmail(), g);
            }

            // Mostra il tabellone iniziale
            mostraTabellone(tabellone, "Tabellone Iniziale", torneoCode);

            // 3. Loop di rimozione giocatori
            while (true) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Vuoi rimuovere un giocatore?",
                        "Elimina giocatore",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) {
                    break;
                }

                // Richiedi l'email del giocatore da rimuovere
                String emailToRemove = JOptionPane.showInputDialog(
                        this,
                        "Inserisci l'email del giocatore da eliminare:"
                );
                if (emailToRemove == null || emailToRemove.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Nessuna email inserita, annullo l'operazione.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Controlla se il giocatore è presente
                boolean giocatorePresente = tabellone.getPartite().values().stream()
                        .anyMatch(partita -> (partita.getGiocatore1() != null && partita.getGiocatore1().getEmail().equals(emailToRemove))
                        || (partita.getGiocatore2() != null && partita.getGiocatore2().getEmail().equals(emailToRemove)));

                if (!giocatorePresente) {
                    JOptionPane.showMessageDialog(this,
                            "Il giocatore con l'email inserita non è presente nel tabellone.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // 4. Elimina giocatore
                cardClash.eliminaGiocatore(emailToRemove);
            }

            // 5. Controlla se il numero di giocatori rimasti è una potenza di due
            int numGiocatori = cardClash.getTorneoCorrente().getGiocatori().size();
            if (!isPotenzaDiDue(numGiocatori)) {
                // Ripristina i giocatori originali
                cardClash.getTorneoCorrente().setGiocatori(new HashMap<>(giocatoriIniziali));
                JOptionPane.showMessageDialog(this,
                        "Errore! Il numero di giocatori rimasti (" + numGiocatori + ") non è una potenza di due.\n"
                        + "L'eliminazione è stata annullata e i giocatori originali sono stati ripristinati.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 6. Aggiorna tabellone e punteggi SOLO se il numero è corretto
            cardClash.aggiornaTabellone();
            cardClash.aggiornaPunteggio();

            Tabellone newTabellone = cardClash.getTorneoCorrente().getTabellone();
            mostraTabellone(newTabellone, "Tabellone Aggiornato", torneoCode);
        }

        // Funzione di supporto per verificare se un numero è una potenza di due
        private boolean isPotenzaDiDue(int n) {
            return (n > 0) && ((n & (n - 1)) == 0);
        }

        private void mostraTabellone(Tabellone tabellone, String title, Integer codTorneo) {
            String[] columnNames = {"codPartita", "Giocatore1", "Punti G1", "Giocatore2", "Punti G2"};
            Object[][] data = new Object[tabellone.getPartite().size()][5];

            int row = 0;
            for (Map.Entry<Integer, Partita> entry : tabellone.getPartite().entrySet()) {
                Partita partita = entry.getValue();
                Giocatore g1 = partita.getGiocatore1();
                Giocatore g2 = partita.getGiocatore2();

                data[row][0] = entry.getKey();  // Codice partita
                data[row][1] = (g1 != null) ? g1.getEmail() : "N/A";
                data[row][2] = (g1 != null) ? g1.getPunteggio(codTorneo) : "N/A";  // Mostra il punteggio
                data[row][3] = (g2 != null) ? g2.getEmail() : "N/A";
                data[row][4] = (g2 != null) ? g2.getPunteggio(codTorneo) : "N/A";  // Mostra il punteggio
                row++;
            }

            JTable table = new JTable(data, columnNames);

            // Se il tabellone non è ancora confermato, non mostriamo il codice
            Integer tabCode = tabellone.getCodice();
            String codiceTabellone = (tabCode != null) ? tabCode.toString() : "non confermato";

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Tabellone (" + codiceTabellone + ")"), BorderLayout.NORTH);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
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

    // ======================= Pannello per la Creazione del Torneo =======================
    private class TournamentCreationPanel extends JPanel {

        private JTextField nomeField;
        private JTextField dataField;
        private JTextField orarioField;
        private JTextField luogoField;
        // Campo formato rimosso: verrà richiesto tramite popup con scelta a tendina
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
                    JOptionPane.showMessageDialog(this, "Esiste già un torneo con la data selezionata.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
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

            // Popup per selezionare il formato tramite scelta a tendina
            Map<Integer, FormatoTorneo> formati = cardClash.getFormati();
            if (formati == null || formati.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non sono disponibili formati per il torneo!",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] formatoOptions = new String[formati.size()];
            int index = 0;
            for (Integer codice : formati.keySet()) {
                FormatoTorneo f = formati.get(codice);
                // Mostriamo anche il gioco tra parentesi
                formatoOptions[index++] = codice + " - " + f.getNome() + " (" + f.getGioco() + ")";
            }
            String selectedFormato = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleziona il formato del torneo:",
                    "Selezione Formato",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    formatoOptions,
                    formatoOptions[0]
            );
            if (selectedFormato == null) {
                JOptionPane.showMessageDialog(this, "Selezione del formato annullata!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] partsFormato = selectedFormato.split(" - ");
            int formatoCodice = Integer.parseInt(partsFormato[0].trim());
            cardClash.selezionaFormato(formatoCodice);
            if (cardClash.getTorneoCorrente().getFormato() == null) {
                JOptionPane.showMessageDialog(this, "Formato non trovato!", "Errore", JOptionPane.ERROR_MESSAGE);
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
