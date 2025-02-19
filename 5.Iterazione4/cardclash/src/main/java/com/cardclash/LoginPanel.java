package com.cardclash;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, tesseramentoButton;
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
            String role = parentFrame.persistenceHandler.authenticate(email, password);
            if (role != null) {
                Giocatore giocatore = parentFrame.cardClash.getGiocatore(email);
                if (giocatore != null) {
                    parentFrame.cardClash.setGiocatoreCorrente(giocatore);
                    parentFrame.mainPanel.updateEloLabel(giocatore.getELO());
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
