package com.cardclash;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class TesseramentoPanel extends JPanel {
    private JTextField nomeField, emailField, nicknameField;
    private JPasswordField passwordField;
    private JButton registraButton, backButton;
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
                parentFrame.cardClash.registraGiocatore(nome, email, password, nickname);
                parentFrame.cardClash.confermaRegistrazione();
                PersistenceHandler.registerUser("user", nome, email, password, nickname);
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
